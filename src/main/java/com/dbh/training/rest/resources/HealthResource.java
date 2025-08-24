package com.dbh.training.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health check endpoint for monitoring the API status.
 * 
 * This is a simple endpoint that returns the current status of the API.
 * Useful for load balancers, monitoring tools, and initial testing.
 */
@Path("/health")
@Produces(MediaType.APPLICATION_JSON)
public class HealthResource {
    
    /**
     * Basic health check endpoint.
     * 
     * @return JSON response with status and timestamp
     */
    @GET
    public Response health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "DBH REST Training API");
        health.put("version", "1.0.0");
        
        return Response.ok(health).build();
    }
    
    /**
     * Detailed health check with additional information.
     * 
     * @return JSON response with detailed health information
     */
    @GET
    @Path("/details")
    public Response healthDetails() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", LocalDateTime.now());
        health.put("service", "DBH REST Training API");
        health.put("version", "1.0.0");
        
        // JVM information
        Map<String, Object> jvm = new HashMap<>();
        jvm.put("version", System.getProperty("java.version"));
        jvm.put("vendor", System.getProperty("java.vendor"));
        jvm.put("uptime", getUptime());
        
        // Memory information
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> memory = new HashMap<>();
        memory.put("total", runtime.totalMemory() / 1024 / 1024 + " MB");
        memory.put("free", runtime.freeMemory() / 1024 / 1024 + " MB");
        memory.put("used", (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024 + " MB");
        memory.put("max", runtime.maxMemory() / 1024 / 1024 + " MB");
        
        health.put("jvm", jvm);
        health.put("memory", memory);
        
        return Response.ok(health).build();
    }
    
    private String getUptime() {
        long uptimeMillis = System.currentTimeMillis() - 
            java.lang.management.ManagementFactory.getRuntimeMXBean().getStartTime();
        long uptimeSeconds = uptimeMillis / 1000;
        long hours = uptimeSeconds / 3600;
        long minutes = (uptimeSeconds % 3600) / 60;
        long seconds = uptimeSeconds % 60;
        
        return String.format("%d hours, %d minutes, %d seconds", hours, minutes, seconds);
    }
}