package com.dbh.training.rest.filters;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;

/**
 * CORS (Cross-Origin Resource Sharing) filter to allow cross-origin requests.
 * 
 * This is essential for REST APIs that will be consumed by web applications
 * running on different domains/ports.
 * 
 * In production, you should configure this more restrictively.
 */
@Provider
public class CORSFilter implements ContainerResponseFilter {
    
    @Override
    public void filter(ContainerRequestContext requestContext, 
                      ContainerResponseContext responseContext) throws IOException {
        
        // Allow requests from any origin (for development/training)
        // In production, specify actual allowed origins
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        
        // Allow specific HTTP methods
        responseContext.getHeaders().add("Access-Control-Allow-Methods", 
            "GET, POST, PUT, DELETE, OPTIONS, HEAD, PATCH");
        
        // Allow specific headers in requests
        responseContext.getHeaders().add("Access-Control-Allow-Headers",
            "Content-Type, Accept, X-Requested-With, Authorization, X-API-Key");
        
        // Expose specific headers to the client
        responseContext.getHeaders().add("Access-Control-Expose-Headers",
            "Location, Content-Disposition, X-Total-Count");
        
        // Allow credentials (cookies, authorization headers)
        // Note: When using credentials, Access-Control-Allow-Origin cannot be "*"
        // responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
        
        // Cache preflight requests for 1 hour
        responseContext.getHeaders().add("Access-Control-Max-Age", "3600");
    }
}