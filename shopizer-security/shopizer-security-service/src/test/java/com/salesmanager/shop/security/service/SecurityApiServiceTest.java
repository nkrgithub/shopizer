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
package com.salesmanager.shop.security.service;

import com.salesmanager.shop.security.v2.api.grpc.EmptyRequest;
import com.salesmanager.shop.security.v2.api.grpc.ReadableGroupResponse;
import com.salesmanager.shop.security.v2.api.grpc.SecurityApi;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * {@link SecurityApiService} Test
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = SecurityApiServiceTest.class,
        properties = {
                "dubbo.application.name=test",
                "dubbo.application.parameters.unicast=false",
                "dubbo.registry.address=multicast://228.0.0.4:1234",
                "dubbo.protocol.name=tri",
                "dubbo.protocol.proxy=nativestub"
        }
)
@EnableDubbo(scanBasePackages = "no.scan")
public class SecurityApiServiceTest {

    @DubboReference
    private SecurityApi securityApi;

    @Test
    public void test() {
        ReadableGroupResponse readableGroupResponse = securityApi.groups(EmptyRequest.newBuilder().build());
        System.out.println("securityApi : " + readableGroupResponse.getGroupsList());
    }
}
