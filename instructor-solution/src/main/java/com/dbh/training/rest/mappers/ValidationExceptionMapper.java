package com.dbh.training.rest.mappers;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;
import java.util.*;

/**
 * Exception mapper for Bean Validation errors
 * 
 * Exercise 04: Bean Validation
 * Converts ConstraintViolationException to user-friendly error responses
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
            String field = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.add(field + ": " + message);
        }
        response.put("errors", errors);
        
        return Response.status(Response.Status.BAD_REQUEST)
                      .entity(response)
                      .build();
    }
}