package com.dbh.training.rest.config;

import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * OpenAPI configuration for the REST API.
 * 
 * This class configures Swagger/OpenAPI documentation for the entire API.
 * The annotations here provide global API information and security schemes.
 */
@OpenAPIDefinition(
    info = @Info(
        title = "DBH REST Training API",
        version = "2.0.0",
        description = "RESTful API built with Jersey for DBH training. " +
                     "This API demonstrates REST principles, Jersey framework, " +
                     "Jackson JSON processing, and JWT authentication.",
        contact = @Contact(
            name = "Thomas Becker",
            email = "training@dbh.com",
            url = "https://dbh.com/training"
        ),
        license = @License(
            name = "Apache 2.0",
            url = "https://www.apache.org/licenses/LICENSE-2.0.html"
        )
    ),
    servers = {
        @Server(
            url = "http://localhost:8080/api",
            description = "Local development server"
        ),
        @Server(
            url = "https://api.dbh-training.com",
            description = "Production server"
        )
    },
    security = {
        @SecurityRequirement(name = "bearerAuth")
    }
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "JWT authentication. Enter the token without 'Bearer' prefix."
)
public class OpenApiConfig {
    // This class is just for OpenAPI annotations
    // The actual configuration is handled by JerseyConfig
}