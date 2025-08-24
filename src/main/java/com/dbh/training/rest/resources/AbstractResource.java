package com.dbh.training.rest.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

/**
 * Base class for all REST resources.
 * 
 * Provides common functionality for all resources:
 * - Logging
 * - URI building helpers
 * - Common response patterns
 */
public abstract class AbstractResource {
    
    protected final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Context
    protected UriInfo uriInfo;
    
    /**
     * Build a location URI for a newly created resource.
     * 
     * @param resourceClass The resource class
     * @param id The ID of the created entity
     * @return URI for the new resource
     */
    protected URI buildLocationUri(Class<?> resourceClass, Object id) {
        return UriBuilder.fromResource(resourceClass)
            .path(String.valueOf(id))
            .build();
    }
    
    /**
     * Build a location URI using the current request path.
     * 
     * @param id The ID to append to the path
     * @return URI for the new resource
     */
    protected URI buildLocationUri(Object id) {
        return uriInfo.getAbsolutePathBuilder()
            .path(String.valueOf(id))
            .build();
    }
    
    /**
     * Create a 201 Created response with Location header.
     * 
     * @param entity The created entity to return
     * @param id The ID of the created entity
     * @return Response with 201 status and Location header
     */
    protected Response created(Object entity, Object id) {
        return Response.created(buildLocationUri(id))
            .entity(entity)
            .build();
    }
    
    /**
     * Create a 200 OK response with entity.
     * 
     * @param entity The entity to return
     * @return Response with 200 status
     */
    protected Response ok(Object entity) {
        return Response.ok(entity).build();
    }
    
    /**
     * Create a 204 No Content response.
     * 
     * @return Response with 204 status
     */
    protected Response noContent() {
        return Response.noContent().build();
    }
    
    /**
     * Create a paginated response with headers.
     * 
     * @param items The page of items
     * @param page Current page number
     * @param size Page size
     * @param total Total number of items
     * @return Response with pagination headers
     */
    protected Response paginated(Object items, int page, int size, long total) {
        int totalPages = (int) Math.ceil((double) total / size);
        
        return Response.ok(items)
            .header("X-Page", page)
            .header("X-Page-Size", size)
            .header("X-Total-Count", total)
            .header("X-Total-Pages", totalPages)
            .build();
    }
    
    /**
     * Log method entry for debugging.
     * 
     * @param methodName The method being entered
     * @param params Parameters to log
     */
    protected void logEntry(String methodName, Object... params) {
        if (logger.isDebugEnabled()) {
            logger.debug("Entering {}.{} with params: {}", 
                getClass().getSimpleName(), methodName, params);
        }
    }
    
    /**
     * Log method exit for debugging.
     * 
     * @param methodName The method being exited
     * @param result The result being returned
     */
    protected void logExit(String methodName, Object result) {
        if (logger.isDebugEnabled()) {
            logger.debug("Exiting {}.{} with result: {}", 
                getClass().getSimpleName(), methodName, result);
        }
    }
}
