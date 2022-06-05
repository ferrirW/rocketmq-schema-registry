/**
 * Copyright 2022, Xiaomi.
 * All rights reserved.
 * Author: wangfan8@xiaomi.com
 */

package org.apache.rocketmq.schema.registry.core.api.v1;

import java.net.HttpURLConnection;
import java.util.Optional;

import org.apache.rocketmq.schema.registry.core.api.RequestProcessor;
import org.apache.rocketmq.schema.registry.core.common.dto.SchemaDto;
import org.apache.rocketmq.schema.registry.core.common.exception.SchemaNotFoundException;
import org.apache.rocketmq.schema.registry.core.service.SchemaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;

/**
 * RocketMQ schema registry V1 API implementation.
 */
@RestController
@RequestMapping(
    path = "/schema/v1",
    produces = MediaType.APPLICATION_JSON_VALUE
)
@Api(
    value = "SchemaRegistryV1",
    produces = MediaType.APPLICATION_JSON_VALUE,
    consumes = MediaType.APPLICATION_JSON_VALUE
)
@Slf4j
public class SchemaController {

    private final String x = "";

    private final RequestProcessor requestProcessor;
    private final SchemaService schemaService;

    /**
     * Constructor.
     *
     * @param requestProcessor request processor
     * @param schemaService schema service
     */
    @Autowired
    public SchemaController(
        final RequestProcessor requestProcessor,
        final SchemaService schemaService
    ) {
        this.requestProcessor = requestProcessor;
        this.schemaService = schemaService;
    }

    @RequestMapping(
        method = RequestMethod.GET,
        path = "/tenant/{tenant-name}/schema/{schema-name}"
    )
    @ApiOperation(
        position = 1,
        value = "Schema information",
        notes = "Schema information for the given schema name under the tenant")
    @ApiResponses(
        {
            @ApiResponse(
                code = HttpURLConnection.HTTP_OK,
                message = "The schema is returned"
            ),
            @ApiResponse(
                code = HttpURLConnection.HTTP_NOT_FOUND,
                message = "The requested tenant or schema cannot be found"
            )
        }
    )
    public SchemaDto getSchema(
        @ApiParam(value = "The name of the tenant", required = true)
        @PathVariable("tenant-name") String tenant,
        @ApiParam(value = "The name of the schema", required = true)
        @PathVariable("schema-name") String schemaName
    ) {
        log.info(String.format("Request for get schema: %s/%s", tenant, schemaName));
        return this.requestProcessor.processRequest(
            tenant,
            schemaName,
            "getSchema",
            () -> {
                Optional<SchemaDto> schema = schemaService.get(tenant, schemaName);
                return schema.orElseThrow(() -> new SchemaNotFoundException(tenant, schemaName));
            }
        );
    }

    @RequestMapping(
        method = RequestMethod.POST,
        path = "/tenant/{tenant-name}/schema/{schema-name}",
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseStatus(HttpStatus.CREATED)
    @ApiOperation(
        position = 2,
        value = "Register a new schema",
        notes = "Return success if there were no errors registering the schema"
    )
    @ApiResponses(
        {
            @ApiResponse(
                code = HttpURLConnection.HTTP_CREATED,
                message = "The schema was registered"
            ),
            @ApiResponse(
                code = HttpURLConnection.HTTP_NOT_FOUND,
                message = "The requested schema cannot be registered"
            )
        }
    )
    public SchemaDto registerSchema(
        @ApiParam(value = "The tenant of the schema", required = true)
        @PathVariable("tenant-name") final String tenant,
        @ApiParam(value = "The name of the schema", required = true)
        @PathVariable("schema-name") final String schemaName,
        @ApiParam(value = "The schema detail", required = true)
//        @Valid
        @RequestBody final SchemaDto schemaDto
    ) {
        // TODO: register by sql
        return this.requestProcessor.processRequest(
            tenant,
            schemaName,
            "register",
            () -> {
                // TODO do something
                return this.schemaService.register(tenant, schemaName, schemaDto);
            }
        );
    }

    @RequestMapping(
        path = "/tenant/{tenant-name}/schema/{schema-name}",
        method = RequestMethod.PUT,
        consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiOperation(
        position = 3,
        value = "Update schema and generate new schema version",
        notes = "Update the given schema"
    )
    @ApiResponses(
        {
            @ApiResponse(
                code = HttpURLConnection.HTTP_OK,
                message = "Update schema success"
            ),
            @ApiResponse(
                code = HttpURLConnection.HTTP_NOT_FOUND,
                message = "The requested schema cannot be found"
            )
        }
    )
    public SchemaDto updateSchema(
        @ApiParam(value = "The tenant of the schema", required = true)
        @PathVariable("tenant-name") final String tenant,
        @ApiParam(value = "The name of the schema", required = true)
        @PathVariable("schema-name") final String schemaName,
        @ApiParam(value = "The schema detail", required = true)
        @RequestBody final SchemaDto schemaDto
    ) {
        return this.requestProcessor.processRequest(
            tenant,
            schemaName,
            "updateSchema",
            () -> {
                // TODO check schema name equals older
                return this.schemaService.update(tenant, schemaName, schemaDto);
            }
        );
    }

    @RequestMapping(
        path = "/tenant/{tenant-name}/schema/{schema-name}",
        method = RequestMethod.DELETE
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiOperation(
        position = 4,
        value = "Delete schema",
        notes = "Delete the schema under the given tenant"
    )
    @ApiResponses(
        {
            @ApiResponse(
                code = HttpURLConnection.HTTP_OK,
                message = "Schema deleted success"
            ),
            @ApiResponse(
                code = HttpURLConnection.HTTP_NOT_FOUND,
                message = "The requested schema cannot be found or it's still been used"
            )
        }
    )
    public SchemaDto deleteSchema(
        @ApiParam(value = "The tenant of the schema", required = true)
        @PathVariable("tenant-name") final String tenant,
        @ApiParam(value = "The name of the schema", required = true)
        @PathVariable("schema-name") final String schemaName
    ) {
        // TODO permission check

        return this.requestProcessor.processRequest(
            tenant,
            schemaName,
            "deleteSchema",
            () -> this.schemaService.delete(tenant, schemaName)
        );
    }





}
