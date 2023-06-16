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
package com.acme.spring.boot.actuator.micrometer.sentinel.spring.webmvc;

import com.alibaba.csp.sentinel.node.ClusterNode;
import io.micrometer.core.instrument.Tag;
import io.microsphere.micrometer.instrument.binder.sentinel.AbstractSentinelMetrics;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.AbstractHandlerMethodMapping;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.PatternsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static org.springframework.beans.factory.BeanFactoryUtils.beansOfTypeIncludingAncestors;

/**
 * Sentinel Micrometer Metrics for Spring Web MVC
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
public class SpringWebMvcSentinelMetrics extends AbstractSentinelMetrics<Object> implements ApplicationListener<ContextRefreshedEvent> {

    private Map<String, Object> resourceMetadataCache;

    @Override
    protected void initResourceMetadataCache(Map<String, Object> resourceMetadataCache) {
        resourceMetadataCache.put("/api/v1/private/login", Boolean.TRUE);
        this.resourceMetadataCache = resourceMetadataCache;
    }

    @Override
    protected String getMetricsNamePrefix() {
        return "sentinel.spring.webmvc.";
    }

    @Override
    protected void addTags(String resourceName, Object metadata, ClusterNode clusterNode, List<Tag> tags) {
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();

        Map<String, AbstractHandlerMethodMapping> handlerMappingsMap =
                beansOfTypeIncludingAncestors(applicationContext, AbstractHandlerMethodMapping.class);

        for (AbstractHandlerMethodMapping handlerMapping : handlerMappingsMap.values()) {
            if (handlerMapping instanceof RequestMappingInfoHandlerMapping) {
                RequestMappingInfoHandlerMapping requestMappingInfoHandlerMapping = (RequestMappingInfoHandlerMapping) handlerMapping;
                Map<RequestMappingInfo, HandlerMethod> handlerMethods = requestMappingInfoHandlerMapping.getHandlerMethods();
                for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethods.entrySet()) {
                    RequestMappingInfo requestMappingInfo = entry.getKey();
                    HandlerMethod handlerMethod = entry.getValue();
                    Method method = handlerMethod.getMethod();
                    PathPatternsRequestCondition pathPatternsRequestCondition = requestMappingInfo.getPathPatternsCondition();
                    if (pathPatternsRequestCondition != null) {
                        for (PathPattern pathPattern : pathPatternsRequestCondition.getPatterns()) {
                            String url = pathPattern.getPatternString();
                            resourceMetadataCache.put(url, method);
                        }
                    }
                    PatternsRequestCondition patternsRequestCondition = requestMappingInfo.getPatternsCondition();
                    if (patternsRequestCondition != null) {
                        for (String pathPattern : patternsRequestCondition.getPatterns()) {
                            resourceMetadataCache.put(pathPattern, method);
                        }
                    }
                }
            }
        }

        logger.info("resourceMetadataCache : {} ", resourceMetadataCache);
    }
}
