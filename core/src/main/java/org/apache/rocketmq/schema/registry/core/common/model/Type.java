/**
 * Copyright 2022, Xiaomi.
 * All rights reserved.
 * Author: wangfan8@xiaomi.com
 */

package org.apache.rocketmq.schema.registry.core.common.model;

public enum Type {

    /**
     * Avro type
     */
    AVRO("avro"),

    /**
     * Thrift type
     */
    THRIFT("thrift"),

    /**
     * Json type
     */
    JSON("json"),

    /**
     * Protobuf type
     */
    PROTOBUF("protobuf");

    private final String value;

    Type(final String value) {
        this.value = value;
    }

}
