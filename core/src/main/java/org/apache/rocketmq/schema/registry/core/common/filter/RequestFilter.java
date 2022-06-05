/**
 * Copyright 2022, Xiaomi.
 * All rights reserved.
 * Author: wangfan8@xiaomi.com
 */

package org.apache.rocketmq.schema.registry.core.common.filter;


import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import org.apache.rocketmq.schema.registry.core.common.RequestContext;
import org.apache.rocketmq.schema.registry.core.manager.ContextManager;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RequestFilter implements Filter {


    public RequestFilter() {

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doFilter(
        ServletRequest request,
        ServletResponse response,
        FilterChain chain
    ) throws IOException, ServletException {
        // Pre-processing
        if (!(request instanceof HttpServletRequest)) {
            throw new ServletException("Expected an HttpServletRequest but didn't get one");
        }
        final HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        final String method = httpServletRequest.getMethod();

        // TODO: get request Authorization from http header
//        final String requestAuth =
//            httpServletRequest.getHeader(RequestContext.MICOMPUTE_REQUEST_HEADER_AUTHORIZATION);
//        final String metaAccount = StringUtils.isNotBlank(requestAuth)
//            ? requestAuth.replaceAll("@<", "\\{").replaceAll("@>", "\\}")
//            : requestAuth;
        final RequestContext context = RequestContext.builder().build();
        ContextManager.putContext(context);
        log.info("filter " + context.toString());

        // Do the rest of the chain
        chain.doFilter(request, response);

        // Post processing
        ContextManager.removeContext();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
