/**
 * Copyright 2022, Xiaomi.
 * All rights reserved.
 * Author: wangfan8@xiaomi.com
 */

package org.apache.rocketmq.schema.registry.core.common.dto;

import java.util.Map;

import org.apache.rocketmq.schema.registry.core.common.model.Type;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SchemaMetaDto extends BaseDto {
    private static final long serialVersionUID = -4377140936300258473L;

    @ApiModelProperty(value = "The type of the schema")
    private Type type;

    @ApiModelProperty(value = "The namespace of the schema")
    private String namespace;

    @ApiModelProperty(value = "The name of the schema")
    private String schemaName;

    @ApiModelProperty(value = "The unique id of the schema")
    private String uniqueId;

    @ApiModelProperty(value = "Owner of the table")
    private String owner;

    private Map<String, String> properties;
}
