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

package org.apache.rocketmq.schema.registry.common.context;

import java.util.Properties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.rocketmq.schema.registry.common.exception.SchemaException;
import org.apache.rocketmq.schema.registry.common.model.StorageType;
import org.apache.rocketmq.schema.registry.common.properties.GlobalConfig;
import org.springframework.context.ApplicationContext;

@Data
@AllArgsConstructor
public class StoragePluginContext {

    /**
     * global config
     */
    private final GlobalConfig config;

//    public StoragePluginContext(
//        GlobalConfig config,
//        Properties properties
//    ) {
//        if (properties.getProperty("storage.type", "rocketmq") == null) {
//            throw new SchemaException("");
//        }
//
//        String storageType = properties.getProperty("storage.type", "ROCKETMQ");
//        this.storageType = StorageType.valueOf(storageType.toUpperCase());
//        this.config = config;
//        this.properties = properties;
//
//        String namesrv = properties.getProperty("namesrv");
//        String ak = properties.getProperty("access.key");
//        String sk = properties.getProperty("secret.key");
//    }
}
