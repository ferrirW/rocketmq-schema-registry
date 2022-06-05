/**
 * Copyright 2022, Xiaomi.
 * All rights reserved.
 * Author: wangfan8@xiaomi.com
 */

package org.apache.rocketmq.schema.registry.core.api;

import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class RequestProcessor {

    @Autowired
    public RequestProcessor() {

    }

    /**
     * Request wrapper to to process request.
     *
     * @param namespace                name
     * @param schemaName request name
     * @param requestName request name
     * @param supplier            supplier
     * @param <R>                 response
     * @return response of supplier
     */
    public <R> R processRequest(
        final String namespace,
        final String schemaName,
        final String requestName,
        final Supplier<R> supplier) {

        return supplier.get();
    }
}
