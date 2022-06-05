/**
 * Copyright 2022, Xiaomi.
 * All rights reserved.
 * Author: wangfan8@xiaomi.com
 */

package org.apache.rocketmq.schema.registry.core.service;

import org.apache.rocketmq.schema.registry.core.common.dto.SchemaDto;

public interface SchemaService extends Service<SchemaDto> {

    /**
     * Query if the schema object exists with the given name
     *
     * @param namespace namespace of the schema
     * @param schemaName name of the schema
     * @return boolean
     */
    boolean exists(String namespace, String schemaName);
}
