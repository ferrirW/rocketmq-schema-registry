/**
 * Copyright 2022, Xiaomi.
 * All rights reserved.
 * Author: wangfan8@xiaomi.com
 */

package org.apache.rocketmq.schema.registry.core.common.exception;

public class SchemaException extends RuntimeException {

    /** Constructor. */
    public SchemaException() {
        super();
    }

    /**
     * Constructor.
     *
     * @param msg The error message
     */
    public SchemaException(final String msg) {
        super(msg);
    }

    /**
     * Constructor.
     *
     * @param msg The error message
     * @param cause The cause of the error
     */
    public SchemaException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
