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
package com.acme.eureka;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.converters.wrappers.CodecWrapper;
import com.netflix.eureka.cluster.protocol.ReplicationInstance;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import com.netflix.eureka.registry.PeerAwareInstanceRegistryImpl;
import com.netflix.eureka.resources.ServerCodecs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceCanceledEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRenewedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static com.acme.eureka.tomcat.cluster.ReplicatedInstanceChannelListener.REPLICATION_INSTANCE_NAME_PREFIX;
import static com.netflix.eureka.cluster.protocol.ReplicationInstance.ReplicationInstanceBuilder.aReplicationInstance;
import static org.springframework.web.context.request.RequestContextHolder.getRequestAttributes;

/**
 * Customized EurekaServer {@link Configuration @Configuration}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class EurekaServerConfiguration {

    @Autowired
    private PeerAwareInstanceRegistry instanceRegistry;

    @Autowired
    private ServerCodecs serverCodecs;

    private CodecWrapper codecWrapper;

    @PostConstruct
    public void init() {
        this.codecWrapper = serverCodecs.getCompactJsonCodec();
    }

    @EventListener(EurekaInstanceRegisteredEvent.class)
    public void onEurekaInstanceRegisteredEvent(EurekaInstanceRegisteredEvent event) throws Throwable {
        if (event.isReplication()) {
            return;
        }
        InstanceInfo instance = event.getInstanceInfo();
        replicateInstanceInfo(instance, PeerAwareInstanceRegistryImpl.Action.Register);
    }

    @EventListener(EurekaInstanceCanceledEvent.class)
    public void onEurekaInstanceCanceledEvent(EurekaInstanceCanceledEvent event) throws Throwable {
        if (event.isReplication()) {
            return;
        }
        String appName = event.getAppName();
        String serviceInstanceId = event.getServerId();
        InstanceInfo instance = instanceRegistry.getInstanceByAppAndId(appName, serviceInstanceId);
        replicateInstanceInfo(instance, PeerAwareInstanceRegistryImpl.Action.Cancel);
    }

    @EventListener(EurekaInstanceRenewedEvent.class)
    public void onEurekaInstanceRenewedEvent(EurekaInstanceRenewedEvent event) throws Throwable {
        if (event.isReplication()) {
            return;
        }
        InstanceInfo instance = event.getInstanceInfo();
        replicateInstanceInfo(instance, PeerAwareInstanceRegistryImpl.Action.Heartbeat);
    }

    private void replicateInstanceInfo(InstanceInfo instance, PeerAwareInstanceRegistryImpl.Action action) throws IOException {
        if (instance == null) {
            return;
        }
        HttpServletRequest request = getHttpServletRequest();
        if (request == null) {
            return;
        }
        ServletContext servletContext = request.getServletContext();
        ReplicationInstance replicationInstance = buildReplicationInstance(instance, action);
        String json = codecWrapper.encode(replicationInstance);
        String name = REPLICATION_INSTANCE_NAME_PREFIX + instance.getId();
        servletContext.setAttribute(name, json);
    }

    private ReplicationInstance buildReplicationInstance(InstanceInfo instance, PeerAwareInstanceRegistryImpl.Action action) {
        ReplicationInstance replicationInstance = aReplicationInstance()
                .withAppName(instance.getAppName())
                .withId(instance.getId())
                .withLastDirtyTimestamp(instance.getLastDirtyTimestamp())
                .withStatus(instance.getStatus().name())
                .withInstanceInfo(instance)
                .withAction(action)
                .build();
        return replicationInstance;
    }

    private HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = attributes.getRequest();
            return request;
        }
        // Non-Web Request
        return null;
    }
}
