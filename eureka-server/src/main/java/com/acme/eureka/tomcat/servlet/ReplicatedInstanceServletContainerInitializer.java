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
package com.acme.eureka.tomcat.servlet;

import com.acme.eureka.tomcat.cluster.ReplicatedInstanceChannelListener;
import org.apache.catalina.Cluster;
import org.apache.catalina.Context;
import org.apache.catalina.core.ApplicationContext;
import org.apache.catalina.core.ContainerBase;
import org.apache.catalina.ha.CatalinaCluster;
import org.apache.catalina.tribes.Channel;

import javax.servlet.ServletContainerInitializer;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import java.util.Set;

import static org.springframework.util.ReflectionUtils.doWithFields;

/**
 * Replicated Instance {@link ServletContainerInitializer}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ServletContainerInitializer
 * @since 1.0.0
 */
public class ReplicatedInstanceServletContainerInitializer implements ServletContainerInitializer {

    @Override
    public void onStartup(Set<Class<?>> c, ServletContext servletContext) throws ServletException {
        Class<?> servletContextClass = servletContext.getClass();
        String className = servletContextClass.getName();
        if ("org.apache.catalina.core.ApplicationContextFacade".equals(className)) {
            doWithFields(servletContextClass, field -> {
                field.setAccessible(true);
                ApplicationContext applicationContext = (ApplicationContext) field.get(servletContext);
                doWithFields(applicationContext.getClass(), f -> {
                            f.setAccessible(true);
                            Context context = (Context) f.get(applicationContext);
                            if (context instanceof ContainerBase) {
                                ContainerBase containerBase = (ContainerBase) context;
                                Cluster cluster = containerBase.getCluster();
                                if (cluster instanceof CatalinaCluster) {
                                    CatalinaCluster catalinaCluster = (CatalinaCluster) cluster;
                                    Channel channel = catalinaCluster.getChannel();
                                    channel.addChannelListener(new ReplicatedInstanceChannelListener(servletContext));
                                }
                            }
                        },
                        f -> "context".equals(f.getName())
                                && Context.class.isAssignableFrom(f.getType()));
            }, field -> "context".equals(field.getName())
                    && ApplicationContext.class.isAssignableFrom(field.getType()));
        }

    }
}
