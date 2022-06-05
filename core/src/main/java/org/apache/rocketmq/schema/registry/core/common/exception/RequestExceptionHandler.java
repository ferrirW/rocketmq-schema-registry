/**
 * Copyright 2022, Xiaomi.
 * All rights reserved.
 * Author: wangfan8@xiaomi.com
 */

package org.apache.rocketmq.schema.registry.core.common.exception;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class RequestExceptionHandler {
    /**
     * Handle Schema service Exceptions.
     *
     * @param response The HTTP response
     * @param e        The exception to handle
     * @throws IOException on error in sending error
     */
    @ExceptionHandler({SchemaException.class})
    public void handleException(
        final HttpServletResponse response,
        final SchemaException e
    ) throws IOException {
        final int status;

        if (e instanceof SchemaNotFoundException) {
            status = HttpStatus.NOT_FOUND.value();
        } else  {
            status = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }

        response.sendError(status, e.getMessage());
    }

}
