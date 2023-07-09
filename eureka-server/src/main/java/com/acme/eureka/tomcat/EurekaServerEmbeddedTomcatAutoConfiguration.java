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
package com.acme.eureka.tomcat;

import com.acme.eureka.tomcat.listener.ReplicatedInstanceListener;
import org.apache.catalina.Context;
import org.apache.catalina.Host;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.ha.ClusterRuleSet;
import org.apache.catalina.ha.tcp.SimpleTcpCluster;
import org.apache.catalina.tribes.Channel;
import org.apache.catalina.tribes.tipis.AbstractReplicatedMap;
import org.apache.catalina.tribes.tipis.ReplicatedMap;
import org.apache.tomcat.util.digester.Digester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.SearchStrategy;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringValueResolver;
import org.xml.sax.InputSource;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Eureka Server Tomcat {@link Configuration @Configuration}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ServletWebServerFactoryAutoConfiguration
 * @see TomcatServletWebServerFactory
 * @since 1.0.0
 */
@ConditionalOnClass(
        value = {Servlet.class},
        name = {
                "org.apache.catalina.startup.Tomcat",
                "org.apache.catalina.ha.tcp.SimpleTcpCluster",
                "org.apache.catalina.ha.ClusterRuleSet"
        })
@ConditionalOnBean(value = ServletWebServerFactory.class, search = SearchStrategy.CURRENT)
@AutoConfigureAfter(ServletWebServerFactoryAutoConfiguration.class)
public class EurekaServerEmbeddedTomcatAutoConfiguration implements EmbeddedValueResolverAware, BeanClassLoaderAware,
        BeanNameAware,
        AbstractReplicatedMap.MapOwner {

    private static final Logger logger = LoggerFactory.getLogger(EurekaServerEmbeddedTomcatAutoConfiguration.class);

    @Value("classpath:/META-INF/conf/cluster.xml")
    private Resource resource;

    @Value("${microsphere.eureka.replication.timeout:15000}")
    private int replicationTimeout;

    private StringValueResolver resolver;

    private ClassLoader classLoader;

    private ReplicatedMap<String, Object> replicatedMap;

    private ReplicatedInstanceListener replicatedInstanceListener;

    private String beanName;

    @Bean
    public TomcatContextCustomizer simpleTcpClusterCustomizer(ConfigurableListableBeanFactory beanFactory) {
        return context -> {
            String[] beanNames = beanFactory.getBeanNamesForType(ServletWebServerFactory.class, false, false);
            if (ObjectUtils.isEmpty(beanNames)) {
                // Standard Tomcat
                logger.info("Current Eureka Server is running on the standard Tomcat Web Server");
                return;
            }
            logger.info("Current Eureka Server is initializing on the embedded Tomcat Web Server[name : {}]", beanNames[0]);
            // Embedded Tomcat
            initEmbeddedTomcat(context);
            beanFactory.registerSingleton(beanName + ".listener", new Listener());
        };
    }

    private class Listener implements ServletContextInitializer, ServletContextListener, ServletContextAttributeListener {

        @Override
        public void onStartup(ServletContext servletContext) throws ServletException {
            servletContext.addListener(replicatedInstanceListener);
            servletContext.addListener(this);
        }

        @Override
        public void contextInitialized(ServletContextEvent sce) {
        }

        @Override
        public void contextDestroyed(ServletContextEvent sce) {
            if (replicatedMap != null) {
                replicatedMap.breakdown();
            }
        }

        @Override
        public void attributeAdded(ServletContextAttributeEvent event) {
            processAttribute(event, false);
        }

        @Override
        public void attributeRemoved(ServletContextAttributeEvent event) {
            processAttribute(event, true);
        }

        @Override
        public void attributeReplaced(ServletContextAttributeEvent event) {
            processAttribute(event, false);
        }

        private void processAttribute(ServletContextAttributeEvent event, boolean removed) {
            if (replicatedMap == null) {
                logger.warn("The ReplicatedMap is not ready!");
                return;
            }
            Object value = event.getValue();
            if (!(value instanceof Serializable)) {
                return;
            }

            String name = event.getName();

            if (removed) {
                replicatedMap.remove(name, value);
            } else {
                replicatedMap.put(name, value);
            }
        }
    }

    private void initEmbeddedTomcat(Context context) {
        Host host = (Host) context.getParent();
        try {
            SimpleTcpCluster cluster = buildCluster();
            host.setCluster(cluster);
            initReplicatedInstanceListener(context, cluster);
            initReplicatedMap(context, cluster);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
    }

    private void initReplicatedInstanceListener(Context context, SimpleTcpCluster cluster) {
        ServletContext servletContext = context.getServletContext();
        ReplicatedInstanceListener listener = new ReplicatedInstanceListener(servletContext);
        Channel channel = cluster.getChannel();
        channel.addChannelListener(listener);
        this.replicatedInstanceListener = listener;
    }

    private void initReplicatedMap(Context context, SimpleTcpCluster cluster) {
        Channel channel = cluster.getChannel();
        String name = context.getName();
        ClassLoader[] classLoaders = getClassLoaders();
        ReplicatedMap<String, Object> replicatedMap = new ReplicatedMap<>(this, channel, replicationTimeout, name, classLoaders);
        replicatedMap.setChannelSendOptions(cluster.getChannelSendOptions());
        this.replicatedMap = replicatedMap;
    }

    private ClassLoader[] getClassLoaders() {
        ClassLoader classLoader = this.classLoader;
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        if (classLoader == Thread.currentThread().getContextClassLoader()) {
            return new ClassLoader[]{classLoader};
        } else {
            return new ClassLoader[]{classLoader, Thread.currentThread().getContextClassLoader()};
        }
    }

    private SimpleTcpCluster buildCluster() throws Throwable {
        SimpleTcpCluster cluster = new SimpleTcpCluster();
        parseCluster(cluster);
        return cluster;
    }

    private void parseCluster(SimpleTcpCluster cluster) throws Throwable {
        try (InputStream inputStream = resource.getInputStream()) {
            String xmlContent = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
            Digester digester = createStartDigester();
            String resolvedXmlContent = resolver.resolveStringValue(xmlContent);
            InputSource inputSource = new InputSource(resource.getURI().toURL().toString());
            inputSource.setCharacterStream(new StringReader(resolvedXmlContent));
            digester.push(cluster);
            digester.parse(inputSource);
        }
    }

    private Digester createStartDigester() {
        // Initialize the digester
        Digester digester = new Digester();
        digester.setValidating(false);
        digester.setRulesValidation(true);
        Map<Class<?>, List<String>> fakeAttributes = new HashMap<>();
        // Ignore className on all elements
        List<String> objectAttrs = new ArrayList<>();
        objectAttrs.add("className");
        fakeAttributes.put(Object.class, objectAttrs);
        // Ignore attribute added by Eclipse for its internal tracking
        List<String> contextAttrs = new ArrayList<>();
        contextAttrs.add("source");
        fakeAttributes.put(StandardContext.class, contextAttrs);
        // Ignore Connector attribute used internally but set on Server
        List<String> connectorAttrs = new ArrayList<>();
        connectorAttrs.add("portOffset");
        fakeAttributes.put(Connector.class, connectorAttrs);
        digester.setFakeAttributes(fakeAttributes);
        digester.setUseContextClassLoader(true);

        // Configure the actions we will be using
        ClusterRuleSet clusterRuleSet = new ClusterRuleSet("Cluster/");
        digester.addRuleSet(clusterRuleSet);
        return digester;

    }

    @Override
    public void setEmbeddedValueResolver(StringValueResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setBeanName(String name) {
        this.beanName = name;
    }

    @Override
    public void objectMadePrimary(Object key, Object value) {
        // DO NOTHING
    }

}
