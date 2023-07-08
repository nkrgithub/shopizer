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

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Serializable;

/**
 * Replicated Instance {@link ChannelListener}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ChannelListener
 * @since 1.0.0
 */
public class ReplicatedInstanceChannelListener implements ChannelListener {

    private final ServletContext servletContext;

    private EurekaServerContext eurekaServerContext;

    private CodecWrapper codecWrapper;

    private PeerAwareInstanceRegistry registry;

    public ReplicatedInstanceChannelListener(ServletContext servletContext) {
        this.servletContext = servletContext;
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
            if (name.startsWith("ReplicationInstance-")) {
                String json = (String) mapMessage.getValue();
                CodecWrapper codecWrapper = getCodecWrapper();
                if (codecWrapper == null) {
                    return;
                }
                try {
                    ReplicationInstance replicationInstance = codecWrapper.decode(json, ReplicationInstance.class);
                    PeerAwareInstanceRegistryImpl.Action action = replicationInstance.getAction();
                    switch (action) {
                        case Register:
                            doRegister(replicationInstance);
                            break;
                        case Cancel:
                            doCancel(replicationInstance);
                            break;
                        case Heartbeat:
                            doRenew(replicationInstance);
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegister(ReplicationInstance replicationInstance) {
        InstanceInfo instanceInfo = replicationInstance.getInstanceInfo();
        PeerAwareInstanceRegistry registry = getRegistry();
        if (registry == null) {
            return;
        }
        registry.register(instanceInfo, true);
    }

    private void doCancel(ReplicationInstance replicationInstance) {
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

    private void doRenew(ReplicationInstance replicationInstance) {
        synchronized (this) {
            doCancel(replicationInstance);
            doRegister(replicationInstance);
        }
    }

    @Override
    public boolean accept(Serializable msg, Member sender) {
        return msg instanceof AbstractReplicatedMap.MapMessage;
    }

    private CodecWrapper getCodecWrapper() {
        CodecWrapper codecWrapper = this.codecWrapper;
        if (codecWrapper == null) {
            EurekaServerContext eurekaServerContext = getEurekaServerContext();
            if (eurekaServerContext != null) {
                ServerCodecs serverCodecs = eurekaServerContext.getServerCodecs();
                codecWrapper = serverCodecs.getCompactJsonCodec();
                this.codecWrapper = codecWrapper;
            }
        }
        return codecWrapper;
    }

    private PeerAwareInstanceRegistry getRegistry() {
        PeerAwareInstanceRegistry registry = this.registry;
        if (registry == null) {
            EurekaServerContext eurekaServerContext = getEurekaServerContext();
            if (eurekaServerContext != null) {
                registry = eurekaServerContext.getRegistry();
                this.registry = registry;
            }
        }
        return eurekaServerContext.getRegistry();
    }

    private EurekaServerContext getEurekaServerContext() {
        EurekaServerContext eurekaServerContext = this.eurekaServerContext;
        if (eurekaServerContext == null) {
            eurekaServerContext = (EurekaServerContext)
                    servletContext.getAttribute(EurekaServerContext.class.getName());
            this.eurekaServerContext = eurekaServerContext;
        }
        return eurekaServerContext;
    }

}
