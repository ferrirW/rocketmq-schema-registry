/**
 * Copyright 2022, Xiaomi.
 * All rights reserved.
 * Author: wangfan8@xiaomi.com
 */

package org.apache.rocketmq.schema.registry.core.common.config;

import org.apache.rocketmq.schema.registry.core.common.filter.RequestFilter;
import org.apache.rocketmq.schema.registry.core.service.SchemaService;
import org.apache.rocketmq.schema.registry.core.service.impl.SchemaServiceImpl;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfig {

    @Bean
    public SchemaService schemaService() {
        return new SchemaServiceImpl();
    }

    @Bean
    public FilterRegistrationBean requestFilter() {
        final FilterRegistrationBean registrationBean = new FilterRegistrationBean();
        final RequestFilter filter = new RequestFilter();
        registrationBean.setFilter(filter);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
