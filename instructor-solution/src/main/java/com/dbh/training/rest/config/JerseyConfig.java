package com.dbh.training.rest.config;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.ServerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbh.training.rest.filters.CORSFilter;
import com.dbh.training.rest.filters.LoggingFilter;
import com.dbh.training.rest.mappers.ValidationExceptionMapper;
import com.dbh.training.rest.resources.UserResourceV1;
import com.dbh.training.rest.resources.UserResourceV2;
import com.dbh.training.rest.resources.HealthResource;
import org.glassfish.jersey.server.validation.ValidationFeature;

/**
 * Jersey configuration class that sets up:
 * - Package scanning for resources
 * - Jackson JSON provider
 * - Bean Validation
 * - Exception mappers
 * - Filters (CORS, Logging)
 * 
 * This replaces web.xml configuration in traditional servlet deployments.
 */
public class JerseyConfig extends ResourceConfig {
    
    private static final Logger logger = LoggerFactory.getLogger(JerseyConfig.class);
    
    public JerseyConfig() {
        logger.info("Initializing Jersey configuration...");
        
        // Exercise 05: Explicitly register both API versions
        // Both versions coexist at different paths
        register(UserResourceV1.class);  // /v1/users - deprecated
        register(UserResourceV2.class);  // /v2/users - current
        register(HealthResource.class);  // /health
        
        // Register Jackson for JSON processing
        register(JacksonFeature.class);
        register(JacksonConfig.class);
        
        // Register Bean Validation (Exercise 04)
        register(ValidationFeature.class);
        register(ValidationExceptionMapper.class);
        property(ServerProperties.BV_SEND_ERROR_IN_RESPONSE, true);
        
        // Register filters
        register(CORSFilter.class);
        register(LoggingFilter.class);
        
        // Disable automatic Wadl generation (optional)
        property(ServerProperties.WADL_FEATURE_DISABLE, true);
        
        // Enable JSON processing errors to be sent to client (helpful for debugging)
        property(ServerProperties.PROCESSING_RESPONSE_ERRORS_ENABLED, true);
        
        logger.info("Jersey configuration initialized successfully");
        logger.info("API versions registered: V1 (deprecated), V2 (current)");
        logger.info("Features enabled: Jackson JSON, Bean Validation, CORS, Request/Response Logging");
    }
}