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
package org.microsphere.spring.cloud.gateway.filter;

import io.microsphere.spring.web.metadata.WebEndpointMapping;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.gateway.event.RefreshRoutesResultEvent;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static io.microsphere.spring.cloud.client.service.util.ServiceInstanceUtils.getWebEndpointMappings;
import static org.springframework.cloud.gateway.filter.ReactiveLoadBalancerClientFilter.LOAD_BALANCER_CLIENT_FILTER_ORDER;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;
import static org.springframework.util.StringUtils.commaDelimitedListToSet;
import static org.springframework.web.reactive.result.method.RequestMappingInfo.paths;

/**
 * {@link WebEndpointMapping}  {@link GlobalFilter}
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @see ReactiveLoadBalancerClientFilter
 * @since 1.0.0
 */
public class WebEndpointMappingGlobalFilter implements GlobalFilter, ApplicationListener<RefreshRoutesResultEvent>, Ordered {

    public static final String SCHEME = "we";

    public static final String ALL_SERVICES = "all";

    private final RouteLocator routeLocator;

    private final DiscoveryClient discoveryClient;

    private volatile Map<RequestMappingInfo, List<String>> requestMappingBasePaths;

    public WebEndpointMappingGlobalFilter(RouteLocator routeLocator, DiscoveryClient discoveryClient) {
        this.routeLocator = routeLocator;
        this.discoveryClient = discoveryClient;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
        if (url == null || !SCHEME.equals(url.getScheme())) {
            return chain.filter(exchange);
        }


        for (Map.Entry<RequestMappingInfo, List<String>> entry : requestMappingBasePaths.entrySet()) {
            RequestMappingInfo requestMappingInfo = entry.getKey();
            if (requestMappingInfo.getMatchingCondition(exchange) != null) {
                List<String> basePaths = entry.getValue();
                String basePath = choose(basePaths);
                if (basePath != null) {
                    StringBuilder targetURIBuilder = new StringBuilder();
                    targetURIBuilder.append(basePath)
                            .append(url.getPath());
                    URI targetURI = URI.create(targetURIBuilder.toString());
                    exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, targetURI);
                    break;
                }
            }
        }

        return chain.filter(exchange);
    }

    private String choose(List<String> basePaths) {
        // TODO Add Load Balancing rules
        return basePaths.isEmpty() ? null : basePaths.get(0);
    }

    @Override
    public int getOrder() {
        return LOAD_BALANCER_CLIENT_FILTER_ORDER - 1;
    }

    @Override
    public void onApplicationEvent(RefreshRoutesResultEvent event) {
        if (event.isSuccess()) {
            Set<String> services = new HashSet<>();
            Flux<Route> routes = routeLocator.getRoutes();
            Map<RequestMappingInfo, List<String>> requestMappingBasePaths = new HashMap<>();
            routes.toStream()
                    .map(Route::getUri)
                    .filter(uri -> SCHEME.equals(uri.getScheme()))
                    .forEach(uri -> {
                        String host = uri.getHost();
                        if (ALL_SERVICES.equals(host)) {
                            services.addAll(discoveryClient.getServices());
                        } else {
                            services.addAll(commaDelimitedListToSet(host));
                        }
                    });

            services.stream()
                    .map(discoveryClient::getInstances)
                    .flatMap(List::stream)
                    .forEach(serviceInstance -> {
                        String basePath = buildBasePath(serviceInstance);
                        getWebEndpointMappings(serviceInstance)
                                .stream()
                                .map(WebEndpointMappingGlobalFilter::buildRequestMappingInfo)
                                .forEach(info -> {
                                    List<String> basePaths = requestMappingBasePaths.computeIfAbsent(info, i -> new LinkedList<>());
                                    basePaths.add(basePath);
                                });
                    });

            this.requestMappingBasePaths = requestMappingBasePaths;
        }
    }

    private String buildBasePath(ServiceInstance serviceInstance) {
        StringBuilder basePathBuilder = new StringBuilder();
        basePathBuilder.append(serviceInstance.isSecure() ? "https://" : "http://")
                .append(serviceInstance.getHost())
                .append(":")
                .append(serviceInstance.getPort());
        // TODO append the context path
        return basePathBuilder.toString();
    }

    private static RequestMappingInfo buildRequestMappingInfo(WebEndpointMapping webEndpointMapping) {
        RequestMethod[] methods = buildRequestMethods(webEndpointMapping);
        return paths(webEndpointMapping.getPatterns())
                .methods(methods)
//                .params(webEndpointMapping.getParams())
//                .headers(webEndpointMapping.getHeaders())
//                .consumes(webEndpointMapping.getConsumes())
//                .produces(webEndpointMapping.getProduces())
                .build();

    }

    private static RequestMethod[] buildRequestMethods(WebEndpointMapping webEndpointMapping) {
        String[] methods = webEndpointMapping.getMethods();
        return methods == null ? null : Stream.of(webEndpointMapping.getMethods())
                .map(RequestMethod::valueOf)
                .toArray(RequestMethod[]::new);
    }
}
