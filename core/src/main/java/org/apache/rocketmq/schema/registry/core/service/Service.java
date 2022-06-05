/**
 * Copyright 2022, Xiaomi.
 * All rights reserved.
 * Author: wangfan8@xiaomi.com
 */

package org.apache.rocketmq.schema.registry.core.service;

import java.util.Optional;

import org.apache.rocketmq.schema.registry.core.common.dto.BaseDto;

public interface Service<T extends BaseDto> {

    /**
     * Register the schema.
     *
     * @param tenant tenant of the schema
     * @param schemaName name of the schema
     * @param dto register information
     * @return registered schema object
     */
    T register(String tenant, String schemaName, T dto);

    /**
     * Register the schema.
     *
     * @param tenant tenant of the schema
     * @param schemaName name of the schema
     * @param dto update information
     * @return updated schema object
     */
    T update(String tenant, String schemaName, T dto);

    /**
     * Deletes the schema.
     *
     * @param tenant tenant of the schema
     * @param schemaName name of the schema
     * @return deleted schema object
     */
    T delete(String tenant, String schemaName);

    /**
     * Query the schema object with the given name.
     *
     * @param tenant tenant of the schema
     * @param schemaName name of the schema
     * @return schema object with the schemaName
     */
    Optional<T> get(String tenant, String schemaName);
}
