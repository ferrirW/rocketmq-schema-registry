/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.rocketmq.schema.registry.core.service;

import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.schema.registry.common.QualifiedName;
import org.apache.rocketmq.schema.registry.common.context.RequestContext;
import org.apache.rocketmq.schema.registry.common.auth.AccessControlService;
import org.apache.rocketmq.schema.registry.common.exception.SchemaException;
import org.apache.rocketmq.schema.registry.common.exception.SchemaExistException;
import org.apache.rocketmq.schema.registry.common.exception.SchemaCompatibilityException;
import org.apache.rocketmq.schema.registry.common.model.Dependency;
import org.apache.rocketmq.schema.registry.common.model.SchemaRecordInfo;
import org.apache.rocketmq.schema.registry.common.properties.GlobalConfig;
import org.apache.rocketmq.schema.registry.common.dto.SchemaDto;
import org.apache.rocketmq.schema.registry.common.model.SchemaInfo;
import org.apache.rocketmq.schema.registry.common.model.SchemaOperation;
import org.apache.rocketmq.schema.registry.common.exception.SchemaNotFoundException;
import org.apache.rocketmq.schema.registry.common.context.RequestContextManager;
import org.apache.rocketmq.schema.registry.common.utils.IdGenerator;
import org.apache.rocketmq.schema.registry.core.dependency.DependencyService;
import org.apache.rocketmq.schema.registry.common.storage.StorageServiceProxy;
import org.apache.rocketmq.schema.registry.common.utils.CommonUtil;
import org.apache.rocketmq.schema.registry.common.utils.StorageUtil;

@Slf4j
public class SchemaServiceImpl implements SchemaService<SchemaDto> {

    private final GlobalConfig config;

    private final AccessControlService accessController;
    private final StorageServiceProxy storageServiceProxy;
    private final StorageUtil storageUtil;

    private final DependencyService dependencyService;

    private IdGenerator idGenerator;

    public SchemaServiceImpl(
        final GlobalConfig config,
        final AccessControlService accessController,
        final StorageServiceProxy storageServiceProxy,
        final StorageUtil storageUtil,
        final DependencyService dependencyService,
        final IdGenerator idGenerator
    ) {
        this.config = config;
        this.accessController = accessController;
        this.storageServiceProxy = storageServiceProxy;
        this.storageUtil = storageUtil;
        this.dependencyService = dependencyService;
        this.idGenerator = idGenerator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchemaDto register(QualifiedName qualifiedName, SchemaDto schemaDto) {
        final RequestContext requestContext = RequestContextManager.getContext();
        log.info("register get request context: " + requestContext);

        checkSchemaValid(schemaDto);
        checkSchemaExist(qualifiedName);

        // TODO: add user and ak sk
        accessController.checkPermission("", qualifiedName.getTenant(), SchemaOperation.REGISTER);

        SchemaInfo schemaInfo = storageUtil.convertFromSchemaDto(schemaDto);
        schemaInfo.setUniqueId(idGenerator.nextId());
        schemaInfo.setLastRecordVersion(1L);
        schemaInfo.getLastRecord().addTopic(qualifiedName.getSubject());

        if (config.isUploadEnabled()) {
            // TODO: async upload to speed up register operation and keep atomic with register
            Dependency dependency = dependencyService.compile(schemaInfo);
            schemaInfo.setLastRecordDependency(dependency);
        }

        log.info("Creating schema info {}: {}", qualifiedName, schemaInfo);
        storageServiceProxy.register(qualifiedName, schemaInfo);
        return storageUtil.convertToSchemaDto(schemaInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchemaDto update(QualifiedName qualifiedName, SchemaDto schemaDto) {
        final RequestContext requestContext = RequestContextManager.getContext();
        log.info("update get request context: " + requestContext);

        checkSchemaValid(schemaDto);

        this.accessController.checkPermission("", "", SchemaOperation.UPDATE);

        SchemaInfo current = storageServiceProxy.get(qualifiedName, config.isCacheEnabled());
        if (current == null) {
            throw new SchemaNotFoundException("Schema " + qualifiedName + " not exist, ignored update.");
        }

        SchemaInfo update = storageUtil.convertFromSchemaDto(schemaDto);

        CommonUtil.validateCompatibility(update, current, current.getMeta().getCompatibility());

        SchemaRecordInfo updateRecord = update.getDetails().lastRecord();
        updateRecord.setVersion(current.getLastRecordVersion() + 1);
        updateRecord.addTopic(qualifiedName.getSubject());
        current.getLastRecord().removeTopic(qualifiedName.getSubject());

        List<SchemaRecordInfo> currentRecords = new ArrayList<>(current.getDetails().getSchemaRecords());
        currentRecords.add(updateRecord);
        update.getDetails().setSchemaRecords(currentRecords);

        log.info("Updating schema info {}: {}", qualifiedName, update);
        storageServiceProxy.update(qualifiedName, update);
        return storageUtil.convertToSchemaDto(update);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SchemaDto delete(QualifiedName qualifiedName) {
        final RequestContext requestContext = RequestContextManager.getContext();
        log.info("delete request context: " + requestContext);

        this.accessController.checkPermission("", qualifiedName.getTenant(), SchemaOperation.DELETE);

        SchemaInfo current = storageServiceProxy.get(qualifiedName, config.isCacheEnabled());
        if (current == null) {
            throw new SchemaNotFoundException("Schema " + qualifiedName + " not exist, ignored update.");
        }

        log.info("delete schema {}", qualifiedName);
        storageServiceProxy.delete(qualifiedName);
        return storageUtil.convertToSchemaDto(current);
    }

    // TODO add get last record query
    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<SchemaDto> get(QualifiedName qualifiedName) {
        final RequestContext requestContext = RequestContextManager.getContext();
        log.info("register get request context: " + requestContext);

        CommonUtil.validateName(qualifiedName);

        this.accessController.checkPermission("", qualifiedName.getTenant(), SchemaOperation.GET);

        SchemaInfo schemaInfo = storageServiceProxy.get(qualifiedName, config.isCacheEnabled());
        SchemaDto schemaDto = storageUtil.convertToSchemaDto(schemaInfo);

        log.info("get schema {}/{}", qualifiedName, schemaDto);
        return Optional.ofNullable(schemaDto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<SchemaDto> getBySubject(QualifiedName qualifiedName) {
        final RequestContext requestContext = RequestContextManager.getContext();
        log.info("register get request context: " + requestContext);

//        CommonUtil.validateName(qualifiedName);
        this.accessController.checkPermission("", qualifiedName.getSubject(), SchemaOperation.GET);

        log.info("get schema by subject: {}", qualifiedName.getSubject());
        SchemaDto schemaDto = null;
        SchemaInfo schemaInfo = null;

        try {
            schemaInfo = storageServiceProxy.getBySubject(qualifiedName, config.isCacheEnabled());

            schemaDto = storageUtil.convertToSchemaDto(schemaInfo);
        } catch (Exception e) {
            throw new SchemaException("", e);
        }

        log.info("get schema {}/{}", schemaInfo, schemaDto);
        return Optional.of(schemaDto);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exists(QualifiedName qualifiedName) {
        return get(qualifiedName).isPresent();
    }


    private void checkSchemaExist(final QualifiedName qualifiedName) {
        if (storageServiceProxy.get(qualifiedName, config.isCacheEnabled()) != null) {
            throw new SchemaExistException(qualifiedName);
        }
    }

    private void checkSchemaValid(final SchemaDto schemaDto) {
        CommonUtil.validateName(schemaDto.getQualifiedName());

        // TODO: check and set namespace from idl
        if (Strings.isNullOrEmpty(schemaDto.getMeta().getNamespace())) {
            throw new SchemaCompatibilityException("Schema namespace is null, please check your config.");
        }

        if (schemaDto.getDetails().getSchemaRecords().size() > 1) {
            throw new SchemaCompatibilityException("Can not register schema with multi records.");
        }
    }
}
