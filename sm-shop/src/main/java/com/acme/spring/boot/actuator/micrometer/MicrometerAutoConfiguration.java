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
package com.acme.spring.boot.actuator.micrometer;

import com.acme.spring.boot.actuator.micrometer.sentinel.spring.webmvc.SpringWebMvcSentinelMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.env.Environment;

/**
 * Micrometer Auto-Configuration
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@Configuration(proxyBeanMethods = false)
@Import(SpringWebMvcSentinelMetrics.class)
public class MicrometerAutoConfiguration {

    @Bean
    public MeterRegistryCustomizer<MeterRegistry> meterRegistryCustomizer(Environment environment) {
        return meterRegistry -> {
            String applicationName = environment.getProperty("spring.application.name", "default");
            meterRegistry.config().commonTags("application", applicationName);
        };
    }

}
