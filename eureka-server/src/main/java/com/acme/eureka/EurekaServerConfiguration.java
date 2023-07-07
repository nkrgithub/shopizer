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

import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceCanceledEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRegisteredEvent;
import org.springframework.cloud.netflix.eureka.server.event.EurekaInstanceRenewedEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

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

    @EventListener(EurekaInstanceRegisteredEvent.class)
    public void onEurekaInstanceRegisteredEvent(EurekaInstanceRegisteredEvent event) {
        
    }

    @EventListener(EurekaInstanceCanceledEvent.class)
    public void onEurekaInstanceCanceledEvent(EurekaInstanceCanceledEvent event) {

    }

    @EventListener(EurekaInstanceRenewedEvent.class)
    public void onEurekaInstanceRenewedEvent(EurekaInstanceRenewedEvent event) {

    }
}
