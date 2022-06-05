/**
 * Copyright 2022, Xiaomi.
 * All rights reserved.
 * Author: wangfan8@xiaomi.com
 */

package org.apache.rocketmq.schema.registry.core.common.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Information required to create a new schema.
 */
@ApiModel(description = "Data transport to create a new schema")
@Data
@EqualsAndHashCode(callSuper = false)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterSchemaDto extends BaseDto {
    private static final long serialVersionUID = -7155784580371873659L;

    @ApiModelProperty(value = "the namespace of this schema", required = true)
    private String namespace;

    @ApiModelProperty(value = "the name of this schema", required = true)
    @JsonProperty
    private String name;

    @ApiModelProperty(value = "the type of the connector of this catalog", required = true)
    private String type;

    private Map<String, String> props;

}
