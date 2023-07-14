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
package com.acme.eureka.tomcat.filter;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncEvent;
import javax.servlet.AsyncListener;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ReadListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.WriteListener;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_SERVICE_UNAVAILABLE;

/**
 * TODO Comment
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since TODO
 */
@WebFilter(
        asyncSupported = true, // 激活异步特性
        filterName = "asyncFilter", // Servlet 名字
        urlPatterns = "/async/*"
)
public class AsyncFilter implements Filter {

    private ServletContext servletContext;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.servletContext = filterConfig.getServletContext();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        this.println("AsyncFilter.doFilter()");
        // 判断是否支持异步
        if (request.isAsyncSupported()) {
            this.println("request.startAsync()");
            // 创建 AsyncContext
            AsyncContext asyncContext = request.startAsync(request, response);
            // 设置超时时间
            asyncContext.setTimeout(50 * 1000L);
            asyncContext.addListener(new AsyncListener() {
                @Override
                public void onComplete(AsyncEvent event) throws IOException {
                    println("执行完成");
                }

                @Override
                public void onTimeout(AsyncEvent event) throws IOException {
                    HttpServletResponse servletResponse = (HttpServletResponse) event.getSuppliedResponse();
                    servletResponse.setStatus(SC_SERVICE_UNAVAILABLE);
                    println("执行超时");
                }

                @Override
                public void onError(AsyncEvent event) throws IOException {
                    println("执行错误");
                }

                @Override
                public void onStartAsync(AsyncEvent event) throws IOException {
                    println("开始执行");
                    ServletRequest request = event.getSuppliedRequest();
                    ServletResponse response = event.getSuppliedResponse();
                    try {
                        ServletInputStream servletInputStream = request.getInputStream();
                        servletInputStream.setReadListener(new ReadListener() {
                            @Override
                            public void onDataAvailable() throws IOException {
                                println("onDataAvailable()");
                            }

                            @Override
                            public void onAllDataRead() throws IOException {
                                println("onAllDataRead()");
                            }

                            @Override
                            public void onError(Throwable t) {
                                println("onError() : " + t.getMessage());
                            }
                        });
                        ServletOutputStream servletOutputStream = response.getOutputStream();
                        servletOutputStream.setWriteListener(new WriteListener() {
                            @Override
                            public void onWritePossible() throws IOException {
                                println("onWritePossible()");
                            }

                            @Override
                            public void onError(Throwable t) {
                                println("onError() : " + t.getMessage());
                            }
                        });
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });

            asyncContext.start(() -> {
                println("AsyncContext.start()");
            });
            asyncContext.dispatch(servletContext, "/async-servlet");
        }
    }

    private void println(Object object) {
        String threadName = Thread.currentThread().getName();
        System.err.println("AsyncFilter[" + threadName + "]: " + object);
    }
}
