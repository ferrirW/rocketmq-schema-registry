/**
 * Copyright 2022, Xiaomi.
 * All rights reserved.
 * Author: wangfan8@xiaomi.com
 */

package org.apache.rocketmq.schema.registry.core.common;

import javax.annotation.Nullable;
import java.io.Serializable;

import lombok.NonNull;

public class QualifiedName implements Serializable {
    private static final long serialVersionUID = 2266514833942841209L;

    private final String namespace;
    private final String name;

    private QualifiedName(
        @Nullable final String namespace,
        @NonNull final String name
    ) {
        this.namespace= namespace;
        this.name = name;
    }
}
