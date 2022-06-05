/**
 * Copyright 2022, Xiaomi.
 * All rights reserved.
 * Author: wangfan8@xiaomi.com
 */

package org.apache.rocketmq.schema.registry.core.common;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.UUID;

import lombok.Builder;

@Builder

public class RequestContext implements Serializable {
    private static final long serialVersionUID = 1772214628830653791L;

    public static final String UNKNOWN = "UNKNOWN";
    private final long timestamp = System.currentTimeMillis();
    private final String id = UUID.randomUUID().toString();
    private String userName;
    private final String clientAppName;
    private final String clientId;
    private final String jobId;
    private final String dataTypeContext;
    private final String apiUri;
    private final String scheme;
    private final String metaAccount;


    public RequestContext() {
        this.userName = null;
        this.clientAppName = null;
        this.clientId = null;
        this.jobId = null;
        this.dataTypeContext = null;
        this.apiUri = UNKNOWN;
        this.scheme = UNKNOWN;
        this.metaAccount = null;
    }

    protected RequestContext(
        @Nullable final String userName,
        @Nullable final String clientAppName,
        @Nullable final String clientId,
        @Nullable final String jobId,
        @Nullable final String dataTypeContext,
        final String apiUri,
        final String scheme,
        final String metaAccount
    ) {
        this.userName = userName;
        this.clientAppName = clientAppName;
        this.clientId = clientId;
        this.jobId = jobId;
        this.dataTypeContext = dataTypeContext;
        this.apiUri = apiUri;
        this.scheme = scheme;
        this.metaAccount = metaAccount;
    }

    @Override
    public String toString() {
        return "RequestContext{" +
            "timestamp=" + timestamp +
            ", id='" + id + '\'' +
            ", userName='" + userName + '\'' +
            ", clientAppName='" + clientAppName + '\'' +
            ", clientId='" + clientId + '\'' +
            ", jobId='" + jobId + '\'' +
            ", dataTypeContext='" + dataTypeContext + '\'' +
            ", apiUri='" + apiUri + '\'' +
            ", scheme='" + scheme + '\'' +
            ", metaAccount='" + metaAccount + '\'' +
            '}';
    }
}
