/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.acme.eureka.tomcat.cluster;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.converters.wrappers.CodecWrapper;
import com.netflix.eureka.EurekaServerContext;
import com.netflix.eureka.cluster.protocol.ReplicationInstance;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import com.netflix.eureka.registry.PeerAwareInstanceRegistryImpl;
import com.netflix.eureka.resources.ServerCodecs;
import org.apache.catalina.tribes.ChannelListener;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.tipis.AbstractReplicatedMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Replicated Instance {@link ChannelListener}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ChannelListener
 * @since 1.0.0
 */
public class ReplicatedInstanceChannelListener implements ChannelListener {

    public static final String REPLICATION_INSTANCE_NAME_PREFIX = "ReplicationInstance-";

    private static final Logger logger = LoggerFactory.getLogger(ReplicatedInstanceChannelListener.class);

    private static final String THREADS_PARAM_NAME = "microsphere.eureka.replication.instance.messages.threads";

    private static final String CAPACITY_PARAM_NAME = "microsphere.eureka.replication.instance.messages.capacity";

    private final ServletContext servletContext;

    private final ExecutorService executorService;

    private final Object mutex = new Object();

    private volatile EurekaServerContext eurekaServerContext;


    public ReplicatedInstanceChannelListener(ServletContext servletContext) {
        this.servletContext = servletContext;
        this.executorService = buildExecutorService(servletContext);
    }

    private ExecutorService buildExecutorService(ServletContext servletContext) {
        String value = servletContext.getInitParameter(THREADS_PARAM_NAME);
        int size = 1;
        if (value != null) {
            size = Integer.valueOf(value);
        }

        BlockingQueue<Runnable> blockingQueue = buildBlockingQueue(servletContext);
        CustomizableThreadFactory threadFactory = new CustomizableThreadFactory("Eureka-Replication-Instance-Messages-Thread-");

        threadFactory.setDaemon(true);
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(size, size,
                0, TimeUnit.MILLISECONDS,
                blockingQueue,
                threadFactory,
                new ThreadPoolExecutor.DiscardOldestPolicy()
        );
        return threadPoolExecutor;
    }

    private BlockingQueue<Runnable> buildBlockingQueue(ServletContext servletContext) {
        String value = servletContext.getInitParameter(CAPACITY_PARAM_NAME);
        int capacity = 100;
        if (value != null) {
            capacity = Integer.valueOf(value);
        }
        return new ArrayBlockingQueue<>(capacity);
    }

    @Override
    public void messageReceived(Serializable msg, Member sender) {
        if (!(msg instanceof AbstractReplicatedMap.MapMessage)) {
            return;
        }

        AbstractReplicatedMap.MapMessage mapMessage = (AbstractReplicatedMap.MapMessage) msg;
        if (mapMessage.getMsgType() != AbstractReplicatedMap.MapMessage.MSG_COPY) {
            return;
        }

        Object key = mapMessage.getKey();

        if (key instanceof String) {
            String name = (String) key;

            synchronized (mutex) {
                if (getEurekaServerContext() != null) {
                    logger.info("EurekaServerContext is ready , Handling {} will be resumed", name);
                    mutex.notifyAll();
                }
            }

            if (name.startsWith(REPLICATION_INSTANCE_NAME_PREFIX)) {
                executorService.execute(() -> {
                    synchronized (mutex) {
                        while ((getEurekaServerContext()) == null) {
                            try {
                                logger.info("EurekaServerContext is not ready , Handling {} will be blocked", name);
                                mutex.wait();
                            } catch (InterruptedException e) {
                                logger.error(e.getMessage(), e);
                            }
                        }
                    }

                    CodecWrapper codecWrapper = getCodecWrapper();
                    String json = (String) mapMessage.getValue();
                    try {
                        ReplicationInstance replicationInstance = codecWrapper.decode(json, ReplicationInstance.class);
                        execute(replicationInstance, json);
                    } catch (IOException e) {
                        logger.error("ReplicationInstance can't be decoded from the JSON : {}", json);
                    }
                });


            }
        }
    }

    private void execute(ReplicationInstance replicationInstance, String json) {
        PeerAwareInstanceRegistryImpl.Action action = replicationInstance.getAction();
        switch (action) {
            case Register:
                doRegister(replicationInstance, json);
                break;
            case Cancel:
                doCancel(replicationInstance, json);
                break;
            case Heartbeat:
                doRenew(replicationInstance, json);
                break;
        }
    }

    private void doRegister(ReplicationInstance replicationInstance, String json) {
        InstanceInfo instanceInfo = replicationInstance.getInstanceInfo();
        if (instanceInfo == null) {
            logger.warn("Why instanceInfo is null ? JSON : {}", json);
            return;
        }
        PeerAwareInstanceRegistry registry = getRegistry();
        if (registry == null) {
            return;
        }
        registry.register(instanceInfo, true);
    }

    private void doCancel(ReplicationInstance replicationInstance, String json) {
        PeerAwareInstanceRegistry registry = getRegistry();
        if (registry == null) {
            return;
        }
        String appName = replicationInstance.getAppName();
        String serviceInstanceId = replicationInstance.getId();
        InstanceInfo instanceInfo = registry.getInstanceByAppAndId(appName, serviceInstanceId);
        if (instanceInfo == null) {
            return;
        }
        registry.cancel(appName, serviceInstanceId, true);
    }

    private void doRenew(ReplicationInstance replicationInstance, String json) {
        synchronized (this) {
            doCancel(replicationInstance, json);
            doRegister(replicationInstance, json);
        }
    }

    @Override
    public boolean accept(Serializable msg, Member sender) {
        return true;
    }

    private CodecWrapper getCodecWrapper() {
        EurekaServerContext eurekaServerContext = getEurekaServerContext();
        CodecWrapper codecWrapper = null;
        if (eurekaServerContext != null) {
            ServerCodecs serverCodecs = eurekaServerContext.getServerCodecs();
            codecWrapper = serverCodecs.getCompactJsonCodec();
        }
        return codecWrapper;
    }

    private PeerAwareInstanceRegistry getRegistry() {
        EurekaServerContext eurekaServerContext = getEurekaServerContext();
        PeerAwareInstanceRegistry registry = null;
        if (eurekaServerContext != null) {
            registry = eurekaServerContext.getRegistry();
        }
        return registry;
    }

    private EurekaServerContext getEurekaServerContext() {
        EurekaServerContext eurekaServerContext = this.eurekaServerContext;
        if (eurekaServerContext == null) {
            eurekaServerContext = (EurekaServerContext) servletContext.getAttribute(EurekaServerContext.class.getName());
        }
        return eurekaServerContext;
    }

}
