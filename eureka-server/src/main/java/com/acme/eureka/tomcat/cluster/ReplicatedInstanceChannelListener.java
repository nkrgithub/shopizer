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
package com.acme.eureka.tomcat.cluster;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.converters.wrappers.CodecWrapper;
import com.netflix.eureka.EurekaServerContext;
import com.netflix.eureka.cluster.protocol.ReplicationInstance;
import com.netflix.eureka.registry.PeerAwareInstanceRegistry;
import com.netflix.eureka.registry.PeerAwareInstanceRegistryImpl;
import com.netflix.eureka.resources.ServerCodecs;
import org.apache.catalina.tribes.ChannelListener;
import org.apache.catalina.tribes.Member;
import org.apache.catalina.tribes.tipis.AbstractReplicatedMap;

import javax.servlet.ServletContext;
import java.io.IOException;
import java.io.Serializable;

/**
 * TODO Comment
 *
 * @author <a href="mailto:mercyblitz@gmail.com">Mercy</a>
 * @since TODO
 */
public class ReplicatedInstanceChannelListener implements ChannelListener {

    private static ServletContext servletContext;

    public static void setServletContext(ServletContext servletContext) {
        ReplicatedInstanceChannelListener.servletContext = servletContext;
        servletContext.log("ReplicatedInstanceChannelListener set ServletContext");
    }

    @Override
    public void messageReceived(Serializable msg, Member sender) {
        if (!(msg instanceof AbstractReplicatedMap.MapMessage)) {
            return;
        }
        AbstractReplicatedMap.MapMessage mapMessage = (AbstractReplicatedMap.MapMessage) msg;
        Object key = mapMessage.getKey();

        if (key instanceof String) {
            String name = (String) key;
            if (name.startsWith("ReplicationInstance-")) {
                String json = (String) mapMessage.getValue();
                CodecWrapper codecWrapper = getCodecWrapper();
                try {
                    ReplicationInstance replicationInstance = codecWrapper.decode(json, ReplicationInstance.class);
                    PeerAwareInstanceRegistryImpl.Action action = replicationInstance.getAction();
                    switch (action) {
                        case Register:
                            doRegister(replicationInstance);
                            break;
                        case Cancel:
                            break;
                        case Heartbeat:
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegister(ReplicationInstance replicationInstance) {
        InstanceInfo instanceInfo = replicationInstance.getInstanceInfo();
        PeerAwareInstanceRegistry registry = getRegistry();
        registry.register(instanceInfo,true);
    }

    private CodecWrapper getCodecWrapper() {
        EurekaServerContext eurekaServerContext = (EurekaServerContext)
                servletContext.getAttribute(EurekaServerContext.class.getName());
        ServerCodecs serverCodecs = eurekaServerContext.getServerCodecs();
        return serverCodecs.getCompactJsonCodec();
    }

    private PeerAwareInstanceRegistry getRegistry() {
        EurekaServerContext eurekaServerContext = (EurekaServerContext)
                servletContext.getAttribute(EurekaServerContext.class.getName());
        return eurekaServerContext.getRegistry();
    }


    @Override
    public boolean accept(Serializable msg, Member sender) {
        return msg instanceof AbstractReplicatedMap.MapMessage;
    }
}
