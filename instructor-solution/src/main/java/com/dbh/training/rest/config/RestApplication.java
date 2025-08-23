package com.dbh.training.rest.config;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * JAX-RS Application configuration.
 * 
 * The @ApplicationPath annotation defines the base URI for all REST resources.
 * This ensures that OpenAPI/Swagger correctly includes the /api prefix in generated documentation.
 */
@ApplicationPath("/api")
public class RestApplication extends Application {
    // JerseyConfig handles all the actual configuration
    // This class just defines the application path for JAX-RS
}