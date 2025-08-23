package com.dbh.training.rest.exceptions;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Maps validation exceptions to proper HTTP responses
 * 
 * Exercise 03: Bean Validation
 * Converts ConstraintViolationExceptions into 400 Bad Request responses
 * with structured error messages.
 */
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 400);
        response.put("error", "Validation Failed");
        
        List<String> errors = new ArrayList<>();
        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            errors.add(violation.getMessage());
        }
        response.put("errors", errors);
        
        return Response.status(Response.Status.BAD_REQUEST)
                .entity(response)
                .build();
    }
}