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
import com.netflix.eureka.EurekaServerContext;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import com.netflix.eureka.registry.PeerAwareInstanceRegistryImpl;
import com.netflix.eureka.resources.ServerCodecs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceCanceledEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRenewedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

import static com.acme.eureka.tomcat.cluster.ReplicatedInstanceListener.ACTION_METADATA_KEY;
import static com.acme.eureka.tomcat.cluster.ReplicatedInstanceListener.REPLICATION_INSTANCE_NAME_PREFIX;
import static com.netflix.eureka.registry.PeerAwareInstanceRegistryImpl.Action.Cancel;
import static org.springframework.web.context.request.RequestContextHolder.getRequestAttributes;

/**
 * Customized EurekaServer {@link Configuration @Configuration}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
public class EurekaServerConfiguration implements ServletContextAttributeListener {

    private static final Logger logger = LoggerFactory.getLogger(EurekaServerApplication.class);

    private PeerAwareInstanceRegistry registry;

    private CodecWrapper codecWrapper;

    @Autowired
    public void init(EurekaServerContext eurekaServerContext, WebApplicationContext context) {
        ServletContext servletContext = context.getServletContext();
        initEurekaServerContext(eurekaServerContext, servletContext);
        initCodecWrapper(eurekaServerContext);
        initPeerAwareInstanceRegistry(eurekaServerContext);
    }

    private void initEurekaServerContext(EurekaServerContext eurekaServerContext,
                                         ServletContext servletContext) {
        String name = EurekaServerContext.class.getName();
        servletContext.setAttribute(name, eurekaServerContext);
        logger.info("The EurekaServerContext has been initialized into the ServletContext with name : {}", name);
    }

    private void initCodecWrapper(EurekaServerContext eurekaServerContext) {
        ServerCodecs serverCodecs = eurekaServerContext.getServerCodecs();
        this.codecWrapper = serverCodecs.getFullJsonCodec();
        logger.info("The CodecWrapper has been initialized");
    }

    private void initPeerAwareInstanceRegistry(EurekaServerContext eurekaServerContext) {
        this.registry = eurekaServerContext.getRegistry();
        logger.info("The PeerAwareInstanceRegistry has been initialized");
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
        InstanceInfo instance = registry.getInstanceByAppAndId(appName, serviceInstanceId);
        replicateInstanceInfo(instance, Cancel);
    }

    @EventListener(EurekaInstanceRenewedEvent.class)
    public void onEurekaInstanceRenewedEvent(EurekaInstanceRenewedEvent event) throws Throwable {
        if (event.isReplication()) {
            return;
        }
        InstanceInfo instance = event.getInstanceInfo();
        replicateInstanceInfo(instance, PeerAwareInstanceRegistryImpl.Action.Heartbeat);
    }

    @Override
    public void attributeAdded(ServletContextAttributeEvent event) {
        ServletContext servletContext = event.getServletContext();
        String name = event.getName();
        Object value = event.getValue();
        servletContext.log("ServletContext's attribute[name : " + name + " , value : " + value + "] was added");
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent event) {
        ServletContext servletContext = event.getServletContext();
        String name = event.getName();
        Object value = event.getValue();
        servletContext.log("ServletContext's attribute[name : " + name + " , value : " + value + "] was removed");
    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent event) {
        ServletContext servletContext = event.getServletContext();
        String name = event.getName();
        Object value = event.getValue();
        servletContext.log("ServletContext's attribute[name : " + name + " , value : " + value + "] was replaced");
    }

    private void replicateInstanceInfo(InstanceInfo instance, PeerAwareInstanceRegistryImpl.Action action) throws IOException {
        if (instance == null) {
            return;
        }
        HttpServletRequest request = getHttpServletRequest();
        if (request == null) {
            return;
        }

        Map<String, String> metadata = instance.getMetadata();
        metadata.put(ACTION_METADATA_KEY, action.name());

        ServletContext servletContext = request.getServletContext();
        String json = codecWrapper.encode(instance);
        String name = REPLICATION_INSTANCE_NAME_PREFIX + instance.getId();
        servletContext.setAttribute(name, json);
        // remove "action" metadata after replication
        metadata.remove(ACTION_METADATA_KEY);
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
