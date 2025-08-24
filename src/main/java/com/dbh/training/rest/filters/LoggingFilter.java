package com.dbh.training.rest.filters;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.UUID;

/**
 * Request/Response logging filter for debugging and monitoring.
 * 
 * Logs:
 * - HTTP method and path
 * - Request headers (optional)
 * - Response status
 * - Execution time
 */
@Provider
public class LoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(LoggingFilter.class);
    private static final String REQUEST_ID_PROPERTY = "request.id";
    private static final String START_TIME_PROPERTY = "request.startTime";
    
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        // Generate unique request ID for tracking
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        requestContext.setProperty(REQUEST_ID_PROPERTY, requestId);
        requestContext.setProperty(START_TIME_PROPERTY, System.currentTimeMillis());
        
        // Log request details
        logger.info("[{}] {} {} from {}",
            requestId,
            requestContext.getMethod(),
            requestContext.getUriInfo().getPath(),
            requestContext.getHeaderString("User-Agent")
        );
        
        // Optional: Log headers for debugging (be careful with sensitive data)
        if (logger.isDebugEnabled()) {
            requestContext.getHeaders().forEach((key, value) -> {
                // Skip sensitive headers
                if (!key.equalsIgnoreCase("Authorization") && 
                    !key.equalsIgnoreCase("X-API-Key")) {
                    logger.debug("[{}] Header: {} = {}", requestId, key, value);
                }
            });
        }
    }
    
    @Override
    public void filter(ContainerRequestContext requestContext,
                      ContainerResponseContext responseContext) throws IOException {
        
        String requestId = (String) requestContext.getProperty(REQUEST_ID_PROPERTY);
        Long startTime = (Long) requestContext.getProperty(START_TIME_PROPERTY);
        
        if (startTime != null) {
            long duration = System.currentTimeMillis() - startTime;
            
            // Log response details
            logger.info("[{}] Response: {} - {} ms",
                requestId != null ? requestId : "unknown",
                responseContext.getStatus(),
                duration
            );
            
            // Add request ID to response headers for client correlation
            if (requestId != null) {
                responseContext.getHeaders().add("X-Request-Id", requestId);
            }
            
            // Warn if request took too long
            if (duration > 1000) {
                logger.warn("[{}] Slow request detected: {} ms for {} {}",
                    requestId,
                    duration,
                    requestContext.getMethod(),
                    requestContext.getUriInfo().getPath()
                );
            }
        }
    }
}