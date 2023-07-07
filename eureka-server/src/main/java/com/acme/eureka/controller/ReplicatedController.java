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
package com.acme.eureka.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextAttributeEvent;
import javax.servlet.ServletContextAttributeListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Replicated Controller
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@RestController
@RequestMapping("/replicated")
public class ReplicatedController implements ServletContextAttributeListener, HttpSessionAttributeListener {

    @GetMapping("/context/set/{name}/{value}")
    public String setServletContextAttributes(@PathVariable String name, @PathVariable String value,
                                              HttpServletRequest request) {

        ServletContext servletContext = request.getServletContext();
        servletContext.setAttribute(name, value);
        return "ServletContext";
    }

    @GetMapping(value = "/context/attributes", produces = "application/json")
    public Map<String, String> getServletContextAttributes(HttpServletRequest request) {
        ServletContext servletContext = request.getServletContext();
        return getAttributes(servletContext.getAttributeNames(), servletContext::getAttribute);
    }

    @GetMapping("/session/set/{name}/{value}")
    public String setHttpSessionAttributes(@PathVariable String name, @PathVariable String value,
                                           HttpServletRequest request) {
        HttpSession session = request.getSession();
        session.setAttribute(name, value);
        return "HttpSession";
    }

    @GetMapping(value = "/session/attributes", produces = "application/json")
    public Map<String, String> getHttpSessionAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession();
        return getAttributes(session.getAttributeNames(), session::getAttribute);
    }

    private Map<String, String> getAttributes(Enumeration<String> attributeNames, Function<String, Object> mapper) {
        Map<String, String> attributes = new HashMap<>();
        while (attributeNames.hasMoreElements()) {
            String attributeName = attributeNames.nextElement();
            Object value = mapper.apply(attributeName);
            if (value instanceof String) {
                attributes.put(attributeName, (String) value);
            }
        }
        return attributes;
    }

    @Override
    public void attributeAdded(ServletContextAttributeEvent event) {
        ServletContext servletContext = event.getServletContext();
        String name = event.getName();
        Object value = event.getValue();
        servletContext.log("ServletContext attribute name : " + name + " , value : " + value);
    }

    @Override
    public void attributeRemoved(ServletContextAttributeEvent event) {
        // DO NOTHING
    }

    @Override
    public void attributeReplaced(ServletContextAttributeEvent event) {
        // DO NOTHING
    }

    @Override
    public void attributeAdded(HttpSessionBindingEvent event) {
        HttpSession session = event.getSession();
        ServletContext servletContext = session.getServletContext();
        String name = event.getName();
        Object value = event.getValue();
        servletContext.log("HttpSession attribute name : " + name + " , value : " + value);
    }


    @Override
    public void attributeRemoved(HttpSessionBindingEvent event) {
        // DO NOTHING
    }

    @Override
    public void attributeReplaced(HttpSessionBindingEvent event) {
        // DO NOTHING
    }
}
