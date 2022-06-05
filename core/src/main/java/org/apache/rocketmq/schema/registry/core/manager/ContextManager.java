/**
 * Copyright 2022, Xiaomi.
 * All rights reserved.
 * Author: wangfan8@xiaomi.com
 */

package org.apache.rocketmq.schema.registry.core.manager;

import org.apache.rocketmq.schema.registry.core.common.RequestContext;

public class ContextManager {

    private static ThreadLocal<RequestContext> contexts = new ThreadLocal<>();

    private ContextManager() {
    }

    public static void removeContext() {
        contexts.remove();
    }

    public static RequestContext getContext() {
        RequestContext result = contexts.get();
        if (result == null) {
            result = new RequestContext();
            putContext(result);
        }
        return result;
    }

    public static void putContext(final RequestContext context) {
        contexts.set(context);
    }
}
