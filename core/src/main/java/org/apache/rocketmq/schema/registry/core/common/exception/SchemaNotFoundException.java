/**
 * Copyright 2022, Xiaomi.
 * All rights reserved.
 * Author: wangfan8@xiaomi.com
 */

package org.apache.rocketmq.schema.registry.core.common.exception;

public class SchemaNotFoundException extends SchemaException {

    public SchemaNotFoundException(final String namespace, final String schemaName) {
        this(String.format("Schema: %s/%s not found, please check your configuration.", namespace, schemaName));
    }

    public SchemaNotFoundException(final String msg) {
        super(msg);
    }

    public SchemaNotFoundException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
