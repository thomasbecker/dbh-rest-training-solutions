package com.dbh.training.rest.config;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.dbh.training.rest.config.jackson.ThirdPartyUserMixIn;
import com.dbh.training.rest.thirdparty.ThirdPartyUser;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

/**
 * Jackson ObjectMapper configuration for JSON serialization/deserialization.
 * 
 * This class configures how Jackson handles JSON in the REST API:
 * - Date/time formatting
 * - Null value handling
 * - Pretty printing
 * - Java 8 time support
 */
@Provider
public class JacksonConfig implements ContextResolver<ObjectMapper> {
    
    private final ObjectMapper objectMapper;
    
    public JacksonConfig() {
        objectMapper = new ObjectMapper();
        
        // Java 8 time support (LocalDate, LocalDateTime, etc.)
        objectMapper.registerModule(new JavaTimeModule());
        
        // Date formatting
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        objectMapper.setDateFormat(dateFormat);
        
        // Serialization features
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);  // Pretty print
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        
        // Deserialization features
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        
        // Include only non-null values in JSON output
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        
        // For training purposes, we'll keep pretty printing enabled
        // In production, you might want to disable it for performance
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        // Exercise 07: Jackson Advanced - Mix-ins
        // Register mix-in to control third-party class serialization
        objectMapper.addMixIn(ThirdPartyUser.class, ThirdPartyUserMixIn.class);
        
        // Exercise 07: Enable JSON Views
        objectMapper.enable(MapperFeature.DEFAULT_VIEW_INCLUSION);
    }
    
    @Override
    public ObjectMapper getContext(Class<?> type) {
        return objectMapper;
    }
}