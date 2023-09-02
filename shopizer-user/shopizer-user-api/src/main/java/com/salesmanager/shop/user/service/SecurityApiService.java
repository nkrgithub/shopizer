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
package com.salesmanager.shop.user.service;


import com.salesmanager.shop.security.v2.api.grpc.EmptyRequest;
import com.salesmanager.shop.security.v2.api.grpc.GroupNameRequest;
import com.salesmanager.shop.security.v2.api.grpc.ReadableGroup;
import com.salesmanager.shop.security.v2.api.grpc.ReadableGroupResponse;
import com.salesmanager.shop.security.v2.api.grpc.ReadablePermission;
import com.salesmanager.shop.security.v2.api.grpc.ReadablePermissionResponse;
import com.salesmanager.shop.security.v2.api.grpc.SecurityApi;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * {@link SecurityApi} Service
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since 1.0.0
 */
@DubboService
public class SecurityApiService implements SecurityApi {

    @Autowired
    private com.salesmanager.shop.security.v2.api.SecurityApi securityApi;

    @Override
    public ReadablePermissionResponse listPermissions(GroupNameRequest request) {
        String group = request.getName();
        List<com.salesmanager.shop.security.model.ReadablePermission> permissions = securityApi.listPermissions(group);
        return responsePermissions(permissions);
    }

    private ReadablePermission buildReadablePermission(com.salesmanager.shop.security.model.ReadablePermission permission) {
        return ReadablePermission.newBuilder()
                .setId(String.valueOf(permission.getId()))
                .setName(permission.getName())
                .build();
    }

    @Override
    public ReadablePermissionResponse permissions(EmptyRequest request) {
        List<com.salesmanager.shop.security.model.ReadablePermission> permissions = securityApi.permissions();
        return responsePermissions(permissions);
    }

    private ReadablePermissionResponse responsePermissions(List<com.salesmanager.shop.security.model.ReadablePermission> permissions) {
        ReadablePermissionResponse.Builder builder = ReadablePermissionResponse.newBuilder();
        permissions.stream().map(this::buildReadablePermission).forEach(builder::addPermissions);
        return builder.build();
    }

    @Override
    public ReadableGroupResponse groups(EmptyRequest request) {
        List<com.salesmanager.shop.security.model.ReadableGroup> readableGroups = securityApi.groups();
        ReadableGroupResponse.Builder builder = ReadableGroupResponse.newBuilder();
        readableGroups.stream().map(this::buildReadableGroup).forEach(builder::addGroups);
        return builder.build();
    }

    private ReadableGroup buildReadableGroup(com.salesmanager.shop.security.model.ReadableGroup group) {
        return ReadableGroup.newBuilder()
                .setId(String.valueOf(group.getId()))
                .setName(group.getName())
                .setType(group.getType())
                .build();
    }
}
