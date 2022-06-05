/**
 * Copyright 2022, Xiaomi.
 * All rights reserved.
 * Author: wangfan8@xiaomi.com
 */

package org.apache.rocketmq.schema.registry.core.common.dto;

import java.util.Map;

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
public class SchemaStorageDto extends BaseDto {
    private static final long serialVersionUID = -3298771958844258686L;

    @ApiModelProperty(value = "Input format of the schema data stored")
    private String inputFormat;

    @ApiModelProperty(value = "Output format of the table data stored")
    private String outputFormat;

    @ApiModelProperty(value = "Serialization library of the data")
    private String serializationLib;

    @ApiModelProperty(value = "Extra storage parameters")
    private Map<String, String> parameters;
}
