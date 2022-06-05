/**
 * Copyright 2022, Xiaomi.
 * All rights reserved.
 * Author: wangfan8@xiaomi.com
 */

package org.apache.rocketmq.schema.registry.core.common.json;

import org.apache.rocketmq.schema.registry.core.common.exception.SchemaException;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class JsonConverterImpl implements JsonConverter {

    @Override
    public ObjectNode fromJson(String s) throws SchemaException {
        return null;
    }

    @Override
    public <T> T fromJson(String s, Class<T> clazz) {
        return null;
    }

    @Override
    public <T> T fromJson(byte[] s, Class<T> clazz) {
        return null;
    }

    @Override
    public byte[] toJsonAsBytes(Object o) {
        return new byte[0];
    }

    @Override
    public ObjectNode toJsonAsObjectNode(Object o) {
        return null;
    }

    @Override
    public String toJson(Object o) {
        return null;
    }

    @Override
    public String toString(Object o) {
        return null;
    }
}
