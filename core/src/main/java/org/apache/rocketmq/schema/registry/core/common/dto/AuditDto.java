/**
 * Copyright 2022, Xiaomi.
 * All rights reserved.
 * Author: wangfan8@xiaomi.com
 */

package org.apache.rocketmq.schema.registry.core.common.dto;

import java.util.Date;

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
public class AuditDto extends BaseDto {
    private static final long serialVersionUID = -2306105985602090836L;

    @ApiModelProperty(value = "The user who creates the resource")
    private String createdBy;

    @ApiModelProperty(value = "The date the resource was created")
    private Date createdDate;

    @ApiModelProperty(value = "The user who updates the resource")
    private String lastModifiedBy;

    @ApiModelProperty(value = "The date the resource was updated")
    private Date lastModifiedDate;
}
