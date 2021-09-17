package com.example.testexample.controllers;

import java.util.Date;
import java.util.UUID;

import com.example.testexample.controllers.exceptions.BadRequestException;
import com.example.testexample.controllers.exceptions.ResourceNotFoundException;
import lombok.Getter;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestControllerExceptionHandler
        extends ResponseEntityExceptionHandler {

    private static final String TRACE_ID_MESSAGE_TEMPLATE =
            "Exception trace id: \"%s\". Contact administrator to resolve this issue: %s";
    private static final String STACK_TRACE_TEMPLATE =
            "Exception. Trace id: \"%s\".\n%s";

    @ExceptionHandler(value = {BadRequestException.class, IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected void handleBadRequest(RuntimeException ex) {
        // should we log it?
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    protected void handleNotFound(RuntimeException ex) {
        // should we log it?
    }

    @ExceptionHandler(value = {Exception.class})
    protected ResponseEntity<Object> handleServerErrors(RuntimeException ex) {

        return handleInternal(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity handleInternal(Exception ex, HttpStatus httpStatus) {
        String traceId = UUID.randomUUID().toString();
        String errorMessage = String.format(TRACE_ID_MESSAGE_TEMPLATE, traceId, ex.getMessage());
        String errorLogMessage = String.format(STACK_TRACE_TEMPLATE, traceId, ExceptionUtils.getStackTrace(ex));

        System.out.println(errorLogMessage);
        ErrorResponse response = new ErrorResponse(traceId, errorMessage);

        return new ResponseEntity<>(response, httpStatus);
    }

    @Getter
    private class ErrorResponse {

        private final String id;
        private String message;
        private final Date date = new Date();

        public ErrorResponse(String id, String message) {
            this.id = id;
            this.message = message;
        }
    }
}