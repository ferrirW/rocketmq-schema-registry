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

package org.apache.rocketmq.schema.registry.storage.rocketmq;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPullConsumer;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.PullResult;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.schema.registry.common.exception.SchemaException;
import org.apache.rocketmq.schema.registry.common.exception.SchemaExistException;
import org.apache.rocketmq.schema.registry.common.exception.SchemaNotFoundException;
import org.apache.rocketmq.schema.registry.common.json.JsonConverter;
import org.apache.rocketmq.schema.registry.common.json.JsonConverterImpl;
import org.apache.rocketmq.schema.registry.common.model.SchemaInfo;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.ColumnFamilyHandle;
import org.rocksdb.DBOptions;
import org.rocksdb.Options;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;

import static org.apache.rocketmq.schema.registry.storage.rocketmq.configs.RocketmqConfigConstants.STORAGE_LOCAL_CACHE_PATH;
import static org.apache.rocketmq.schema.registry.storage.rocketmq.configs.RocketmqConfigConstants.STORAGE_LOCAL_CACHE_PATH_DEFAULT;
import static org.apache.rocketmq.schema.registry.storage.rocketmq.configs.RocketmqConfigConstants.STORAGE_ROCKETMQ_CONSUMER_GROUP;
import static org.apache.rocketmq.schema.registry.storage.rocketmq.configs.RocketmqConfigConstants.STORAGE_ROCKETMQ_CONSUMER_GROUP_DEFAULT;
import static org.apache.rocketmq.schema.registry.storage.rocketmq.configs.RocketmqConfigConstants.STORAGE_ROCKETMQ_NAMESRV;
import static org.apache.rocketmq.schema.registry.storage.rocketmq.configs.RocketmqConfigConstants.STORAGE_ROCKETMQ_NAMESRV_DEFAULT;
import static org.apache.rocketmq.schema.registry.storage.rocketmq.configs.RocketmqConfigConstants.STORAGE_ROCKETMQ_PRODUCER_GROUP;
import static org.apache.rocketmq.schema.registry.storage.rocketmq.configs.RocketmqConfigConstants.STORAGE_ROCKETMQ_PRODUCER_GROUP_DEFAULT;
import static org.apache.rocketmq.schema.registry.storage.rocketmq.configs.RocketmqConfigConstants.STORAGE_ROCKETMQ_TOPIC;
import static org.apache.rocketmq.schema.registry.storage.rocketmq.configs.RocketmqConfigConstants.STORAGE_ROCKETMQ_TOPIC_DEFAULT;
import static org.apache.rocketmq.schema.registry.storage.rocketmq.configs.RocketmqConfigConstants.STORAGE_ROCKSDB_SCHEMA_COLUMN_FAMILY;
import static org.apache.rocketmq.schema.registry.storage.rocketmq.configs.RocketmqConfigConstants.STORAGE_ROCKSDB_SUBJECT_COLUMN_FAMILY;

@Slf4j
public class RocketmqClient {

    private Properties properties;
    private DefaultMQProducer producer;
    private DefaultMQPushConsumer scheduleConsumer;
    private String storageTopic;
    private String cachePath;
    private JsonConverter converter;
    private final List<ColumnFamilyHandle> cfHandleList = new ArrayList<>();
    private final List<ColumnFamilyDescriptor> cfDescriptors = new ArrayList<>();
    private final Map<String, ColumnFamilyHandle> cfHandleMap = new HashMap<>();


    /**
     * RocksDB for cache
     */
    // TODO setCreateMissingColumnFamilies
    private final Options options = new Options().setCreateIfMissing(true);
    private final DBOptions dbOptions = new DBOptions().setCreateIfMissing(true);
    private RocksDB cache;

    public RocketmqClient(Properties props) {
        init(props);
        startRemoteStorage();
        startLocalCache();
    }

    private void startLocalCache() {
        try {
            List<byte[]> cfs = RocksDB.listColumnFamilies(options, cachePath);
            if (cfs.size() <= 1) {
                List<byte[]> columnFamilies = Arrays.asList(STORAGE_ROCKSDB_SCHEMA_COLUMN_FAMILY,
                    STORAGE_ROCKSDB_SUBJECT_COLUMN_FAMILY);
                // TODO: add default cf in handles when needed
                cache = org.rocksdb.RocksDB.open(options, cachePath);
                cfDescriptors.addAll(columnFamilies.stream()
                    .map(ColumnFamilyDescriptor::new)
                    .collect(Collectors.toList()));
                cfHandleList.addAll(cache.createColumnFamilies(cfDescriptors));
            } else {
                cfDescriptors.addAll(cfs.stream()
                    .map(ColumnFamilyDescriptor::new)
                    .collect(Collectors.toList()));
                cache = org.rocksdb.RocksDB.open(dbOptions, cachePath, cfDescriptors, cfHandleList);
            }

            cfHandleMap.putAll(
                cfHandleList.stream().collect(Collectors.toMap(h -> {
                    try {
                        return new String(h.getName());
                    } catch (RocksDBException e) {
                        throw new SchemaException("Failed to open RocksDB", e);
                    }
                }, h -> h)));

            assert cfHandleList.size() >= 2;
        } catch (RocksDBException e) {
            throw new SchemaException("Failed to open RocksDB", e);
        }
    }

    public void startRemoteStorage() {
        try {
            producer.start();

            scheduleConsumer.subscribe(storageTopic, "*");
            scheduleConsumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    ColumnFamilyHandle cfHandle = cfHandleMap.get(new String(STORAGE_ROCKSDB_SCHEMA_COLUMN_FAMILY));
                    msgs.forEach(msg -> {
                        byte[] key = msg.getKeys().getBytes(StandardCharsets.UTF_8);
                        byte[] value = msg.getBody();

                        synchronized (this) {
                            try {
                                if (value == null) {
                                    cache.delete(cfHandle, key);
                                } else {
                                    byte[] result = cache.get(cfHandle, key);
                                    if (result == null) {
                                        // first register
                                        cache.put(cfHandle, key, value);
                                    } else {
                                        // update
                                        SchemaInfo current = converter.fromJson(result, SchemaInfo.class);
                                        SchemaInfo update = converter.fromJson(value, SchemaInfo.class);
                                        if (current.getLastRecordVersion() >= update.getLastRecordVersion()) {
                                            throw new SchemaException("Schema version is invalid, update: "
                                                + update.getLastRecordVersion() + ", but current: " + current.getLastRecordVersion());
                                        }
                                        cache.put(cfHandle, key, value);
                                    }
                                }
                            } catch (RocksDBException e) {
                                throw new SchemaException("put schema to cache failed", e);
                            }
                        }

                    });
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            scheduleConsumer.start();
        } catch (MQClientException e) {
            throw new SchemaException("Rocketmq client start failed", e);
        }
    }

    // TODO: next query on other machine may can't found schema in cache since send is async with receive
    public SchemaInfo registerSchema(String key, SchemaInfo value) {
        ColumnFamilyHandle cfHandle = cfHandleMap.get(new String(STORAGE_ROCKSDB_SCHEMA_COLUMN_FAMILY));
        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] valueBytes = converter.toJsonAsBytes(value);

        try {
            synchronized (this) {
                if (cache.get(cfHandle, keyBytes) != null) {
                    throw new SchemaExistException("key: " + key + " is exist.");
                }

                SendResult result = producer.send(new Message(storageTopic, "", key, valueBytes));
                if (!result.getSendStatus().equals(SendStatus.SEND_OK)) {
                    throw new SchemaException("Persist key: " + key + " failed");
                }

                if (cache.get(cfHandle, keyBytes) == null) {
                    if (cache.get(cfHandle, keyBytes) == null) {
                        cache.put(cfHandle, keyBytes, valueBytes);
                    }
                }
            }

            return value;
        } catch (SchemaException e) {
            throw e;
        } catch (Exception e) {
            throw new SchemaException("register schema failed", e);
        }
    }

    public byte[] delete(String key) {
        ColumnFamilyHandle schemaHandle = cfHandleMap.get(new String(STORAGE_ROCKSDB_SCHEMA_COLUMN_FAMILY));
        ColumnFamilyHandle subjectHandle = cfHandleMap.get(new String(STORAGE_ROCKSDB_SUBJECT_COLUMN_FAMILY));

        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        try {
            synchronized (this) {
                // empty message mean delete
                Message msg = new Message(storageTopic, "", key, null);
                SendResult result = producer.send(msg);
                if (!result.getSendStatus().equals(SendStatus.SEND_OK)) {
                    throw new SchemaException("Persist key: " + key + " failed");
                }
                cache.delete(schemaHandle, keyBytes);
                // TODO: delete subject -> schema immediately
            }
            return null;
        } catch (Exception e) {
            log.error("", e);
            throw new SchemaException("", e);
        }
    }

    public SchemaInfo updateSchema(String key, SchemaInfo value) {
        ColumnFamilyHandle cfHandle = cfHandleMap.get(new String(STORAGE_ROCKSDB_SCHEMA_COLUMN_FAMILY));

        byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
        byte[] valueBytes = converter.toJsonAsBytes(value);
        try {
            synchronized (this) {
                Message msg = new Message(storageTopic, "", key, valueBytes);
                SendResult result = producer.send(msg);

                if (result.getSendStatus() != SendStatus.SEND_OK) {
                    throw new SchemaException("Update " + key + " failed when store schema: " + result.getSendStatus());
                }

                cache.put(cfHandle, keyBytes, valueBytes);
            }
            return value;
        } catch (SchemaException e) {
            throw e;
        } catch (Exception e) {
            throw new SchemaException("Update schema " + key + " failed", e);
        }
    }

    public byte[] getSchema(String key) {
        ColumnFamilyHandle cfHandle = cfHandleMap.get(new String(STORAGE_ROCKSDB_SCHEMA_COLUMN_FAMILY));
        try {
            // TODO: get from rocketmq topic if cache not contain
            return cache.get(cfHandle, key.getBytes(StandardCharsets.UTF_8));
        } catch (RocksDBException e) {
            throw new SchemaException("Get schema " + key + " failed", e);
        }
    }

    public byte[] getBySubject(String key) {
        ColumnFamilyHandle cfHandle = cfHandleMap.get(new String(STORAGE_ROCKSDB_SUBJECT_COLUMN_FAMILY));
        try {
            return cache.get(cfHandle, key.getBytes(StandardCharsets.UTF_8));
        } catch (RocksDBException e) {
            throw new SchemaException("Get schema " + key + " failed", e);
        }
    }

    private void init(Properties props) {
        this.properties = props;
        this.storageTopic = props.getProperty(STORAGE_ROCKETMQ_TOPIC, STORAGE_ROCKETMQ_TOPIC_DEFAULT);
        this.cachePath = props.getProperty(STORAGE_LOCAL_CACHE_PATH, STORAGE_LOCAL_CACHE_PATH_DEFAULT);

        this.producer = new DefaultMQProducer(
            props.getProperty(STORAGE_ROCKETMQ_PRODUCER_GROUP, STORAGE_ROCKETMQ_PRODUCER_GROUP_DEFAULT)
        );

        this.producer.setNamesrvAddr(
            props.getProperty(STORAGE_ROCKETMQ_NAMESRV, STORAGE_ROCKETMQ_NAMESRV_DEFAULT)
        );

        this.scheduleConsumer = new DefaultMQPushConsumer(
            props.getProperty(STORAGE_ROCKETMQ_CONSUMER_GROUP, STORAGE_ROCKETMQ_CONSUMER_GROUP_DEFAULT)
        );

        this.scheduleConsumer.setNamesrvAddr(
            props.getProperty(STORAGE_ROCKETMQ_NAMESRV, STORAGE_ROCKETMQ_NAMESRV_DEFAULT)
        );

        this.converter = new JsonConverterImpl();
    }
}
