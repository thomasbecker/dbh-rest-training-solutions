package com.dbh.training.rest;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dbh.training.rest.config.JerseyConfig;

/**
 * Main application class that starts an embedded Jetty server with Jersey REST endpoints.
 * 
 * This class demonstrates how to run Jersey without Spring Boot, using plain Java
 * with an embedded Jetty server.
 */
public class Application {
    
    private static final Logger logger = LoggerFactory.getLogger(Application.class);
    
    // Default configuration
    private static final String DEFAULT_CONTEXT_PATH = "/";
    private static final String API_PATH_SPEC = "/api/*";
    private static final int DEFAULT_PORT = 8080;
    
    private Server server;
    
    public Application() {
        this(DEFAULT_PORT);
    }
    
    public Application(int port) {
        this.server = new Server(port);
    }
    
    /**
     * Configures and starts the embedded Jetty server.
     */
    public void start() throws Exception {
        logger.info("Starting DBH REST Training Server...");
        
        // Create servlet context handler with sessions disabled (REST is stateless)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath(DEFAULT_CONTEXT_PATH);
        
        server.setHandler(context);
        
        // Configure Jersey servlet
        ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, API_PATH_SPEC);
        jerseyServlet.setInitOrder(0);
        
        // Tell Jersey where to find the configuration
        jerseyServlet.setInitParameter(
            "javax.ws.rs.Application",
            JerseyConfig.class.getName()
        );
        
        // Optional: Enable request/response logging
        jerseyServlet.setInitParameter(
            "jersey.config.server.provider.classnames",
            "org.glassfish.jersey.filter.LoggingFilter"
        );
        
        try {
            server.start();
            logger.info("Server started successfully!");
            logger.info("Server running at http://localhost:{}", getPort());
            logger.info("API endpoint: http://localhost:{}/api", getPort());
            logger.info("Health check: http://localhost:{}/api/health", getPort());
            logger.info("Press Ctrl+C to stop the server...");
            
            server.join();
        } catch (Exception e) {
            logger.error("Error starting server", e);
            throw e;
        }
    }
    
    /**
     * Stops the embedded Jetty server.
     */
    public void stop() throws Exception {
        if (server != null) {
            logger.info("Stopping server...");
            server.stop();
            server.destroy();
            logger.info("Server stopped.");
        }
    }
    
    /**
     * Gets the port the server is configured to run on.
     */
    public int getPort() {
        return server.getURI().getPort();
    }
    
    /**
     * Starts a configured server for testing purposes.
     * 
     * @param port The port to run the server on
     * @return Started Jetty server
     * @throws Exception if server fails to start
     */
    public static Server startServer(int port) throws Exception {
        Server server = createServer(port);
        server.start();
        return server;
    }
    
    /**
     * Creates a configured server for testing purposes.
     * 
     * @param port The port to run the server on
     * @return Configured Jetty server
     */
    public static Server createServer(int port) {
        Server server = new Server(port);
        
        // Create servlet context handler with sessions disabled (REST is stateless)
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.NO_SESSIONS);
        context.setContextPath(DEFAULT_CONTEXT_PATH);
        
        server.setHandler(context);
        
        // Configure Jersey servlet
        ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, API_PATH_SPEC);
        jerseyServlet.setInitOrder(0);
        
        // Tell Jersey where to find the configuration
        jerseyServlet.setInitParameter(
            "javax.ws.rs.Application",
            JerseyConfig.class.getName()
        );
        
        return server;
    }
    
    /**
     * Main entry point for the application.
     * 
     * @param args Command line arguments (optional: port number)
     */
    public static void main(String[] args) {
        // Parse port from command line arguments or environment variable
        int port = DEFAULT_PORT;
        
        // Check command line argument
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
                logger.info("Using port from command line: {}", port);
            } catch (NumberFormatException e) {
                logger.warn("Invalid port number: {}, using default: {}", args[0], DEFAULT_PORT);
            }
        }
        
        // Check environment variable (overrides command line)
        String envPort = System.getenv("PORT");
        if (envPort != null) {
            try {
                port = Integer.parseInt(envPort);
                logger.info("Using port from environment variable: {}", port);
            } catch (NumberFormatException e) {
                logger.warn("Invalid PORT environment variable: {}, using: {}", envPort, port);
            }
        }
        
        // Create and start the application
        Application app = new Application(port);
        
        // Add shutdown hook for graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                app.stop();
            } catch (Exception e) {
                logger.error("Error during shutdown", e);
            }
        }));
        
        try {
            app.start();
        } catch (Exception e) {
            logger.error("Failed to start application", e);
            System.exit(1);
        }
    }
}