package com.dbh.training.rest.config;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.IOException;

/**
 * Serves Swagger UI static files.
 * 
 * This resource makes Swagger UI available at /swagger-ui
 * The UI will automatically load the OpenAPI spec from /openapi.json
 */
@Path("/swagger-ui")
public class SwaggerUIResource {
    
    private static final String SWAGGER_UI_PATH = "META-INF/resources/webjars/swagger-ui/4.15.5/";
    
    @GET
    @Produces("text/html")
    public Response getSwaggerUI() {
        return getStaticFile("index.html");
    }
    
    @GET
    @Path("{path:.*}")
    public Response getStaticFile(@PathParam("path") String path) {
        // Security: prevent directory traversal
        if (path.contains("..")) {
            return Response.status(Response.Status.BAD_REQUEST).build();
        }
        
        // Default to index.html if no path specified
        if (path == null || path.isEmpty()) {
            path = "index.html";
        }
        
        // Load resource from Swagger UI webjar
        InputStream resource = getClass().getClassLoader()
            .getResourceAsStream(SWAGGER_UI_PATH + path);
        
        if (resource == null) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        
        // Determine content type based on file extension
        String contentType = getContentType(path);
        
        // Special handling for index.html to configure the title
        if (path.equals("index.html")) {
            try {
                // Java 8 compatible way to read all bytes
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[1024];
                while ((nRead = resource.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                String html = new String(buffer.toByteArray());
                
                // Configure page title
                html = html.replace("<title>Swagger UI</title>", 
                                  "<title>DBH REST API Documentation</title>");
                return Response.ok(html).type(contentType).build();
            } catch (IOException e) {
                return Response.serverError().entity("Error loading Swagger UI").build();
            }
        }
        
        // Special handling for swagger-initializer.js to configure the OpenAPI URL
        if (path.equals("swagger-initializer.js")) {
            try {
                // Java 8 compatible way to read all bytes
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                int nRead;
                byte[] data = new byte[1024];
                while ((nRead = resource.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }
                String js = new String(buffer.toByteArray());
                
                // Configure Swagger UI to load our OpenAPI spec
                js = js.replace("https://petstore.swagger.io/v2/swagger.json", 
                               "/api/openapi.json");
                return Response.ok(js).type(contentType).build();
            } catch (IOException e) {
                return Response.serverError().entity("Error loading Swagger initializer").build();
            }
        }
        
        return Response.ok(resource).type(contentType).build();
    }
    
    private String getContentType(String path) {
        if (path.endsWith(".html")) return "text/html";
        if (path.endsWith(".js")) return "application/javascript";
        if (path.endsWith(".css")) return "text/css";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".svg")) return "image/svg+xml";
        if (path.endsWith(".ico")) return "image/x-icon";
        if (path.endsWith(".json")) return "application/json";
        return "application/octet-stream";
    }
}