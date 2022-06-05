/**
 * Copyright 2022, Xiaomi.
 * All rights reserved.
 * Author: wangfan8@xiaomi.com
 */

package org.apache.rocketmq.schema.registry.core.service.impl;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.apache.rocketmq.schema.registry.core.common.RequestContext;
import org.apache.rocketmq.schema.registry.core.common.dto.SchemaDto;
import org.apache.rocketmq.schema.registry.core.manager.ContextManager;
import org.apache.rocketmq.schema.registry.core.service.SchemaService;

import com.sun.javafx.font.Metrics;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SchemaServiceImpl implements SchemaService {


    public SchemaServiceImpl(

    ) {

        log.info("curreent: " + (System.currentTimeMillis()-1000));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchemaDto register(String tenant, String schemaName, SchemaDto dto) {
        final RequestContext requestContext = ContextManager.getContext();
        log.info("register get requect context: " + requestContext.toString());
        validate(name);
        this.authorizationService.checkPermission(metacatRequestContext.getUserName(),
            tableDto.getName(), MetacatOperation.CREATE);
        //check the metadata which is valid or not.
        checkSetMetadata(tableDto);
        //
        // Set the owner,if null, with the session user name.
        //
        setOwnerIfNull(tableDto, metacatRequestContext.getUserName());
        log.info("Creating table {}", name);
        eventBus.post(new MetacatCreateTablePreEvent(name, metacatRequestContext, this, tableDto));

        connectorTableServiceProxy.create(name, converterUtil.fromTableDto(tableDto));

        if (tableDto.getDataMetadata() != null || tableDto.getDefinitionMetadata() != null) {
            log.info("Saving user metadata for table {}", name);
            final long start = registry.clock().wallTime();
            userMetadataService.saveMetadata(metacatRequestContext.getUserName(), tableDto, true);
            final long duration = registry.clock().wallTime() - start;
            log.info("Time taken to save user metadata for table {} is {} ms", name, duration);
            registry.timer(registry.createId(Metrics.TimerSaveTableMetadata.getMetricName()).withTags(name.parts()))
                .record(duration, TimeUnit.MILLISECONDS);
            tag(name, tableDto.getDefinitionMetadata());
        }

        TableDto dto = tableDto;
        try {
            dto = get(name, GetTableServiceParameters.builder()
                .disableOnReadMetadataIntercetor(false)
                .includeInfo(true)
                .includeDataMetadata(true)
                .includeDefinitionMetadata(true)
                .build()).orElse(tableDto);
        } catch (Exception e) {
            handleExceptionOnCreate(name, "getTable", e);
        }

        try {
            eventBus.post(new MetacatCreateTablePostEvent(name, metacatRequestContext, this, dto));
        } catch (Exception e) {
            handleExceptionOnCreate(name, "postEvent", e);
        }
        return dto;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchemaDto update(String tenant, String schemaName, SchemaDto dto) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchemaDto delete(String tenant, String schemaName) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<SchemaDto> get(String tenant, String schemaName) {
        return Optional.empty();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(String namespace, String schemaName) {
        return false;
    }
}
