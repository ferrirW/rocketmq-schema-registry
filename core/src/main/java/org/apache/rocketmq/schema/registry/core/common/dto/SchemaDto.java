/**
 * Copyright 2022, Xiaomi.
 * All rights reserved.
 * Author: wangfan8@xiaomi.com
 */

package org.apache.rocketmq.schema.registry.core.common.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@ApiModel(description = "Schema detail information")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SchemaDto extends BaseDto {
    private static final long serialVersionUID = -441512542075118183L;

    @ApiModelProperty(value = "Information about schema changes")
    private AuditDto audit;

    @ApiModelProperty(value = "Information about schema meta")
    private SchemaMetaDto meta;

    @ApiModelProperty(value = "Information about schema details")
    private SchemaDetailDto details;

    @ApiModelProperty(value = "Information about schema persistence")
    private SchemaStorageDto storage;
}
