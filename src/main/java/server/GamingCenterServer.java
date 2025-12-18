/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package server;

/**
 *
 * @author Odeth
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import messaging.MessageConsumer;
import rmi.RemoteService;
import rmi.RemoteServiceImpl;
import utils.HibernateUtil;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 * GamingCenterServer - Main Server Application
 * Starts RMI server and message consumers on port 3500
 * 
 * To run: java -jar GamingCenterServer{studentId}.jar
 */
public class GamingCenterServer {
    private static final Logger logger = LoggerFactory.getLogger(GamingCenterServer.class);
    
    // Server configuration
    private static final int RMI_PORT = 3500; // Within required range 3000-4000
    private static final String SERVICE_NAME = "GamingCenterService";

    public static void main(String[] args) {
        logger.info("==========================================");
        logger.info("  Gaming Center Management System Server ");
        logger.info("==========================================");
        
        try {
            // Step 1: Initialize Hibernate
            logger.info("Initializing Hibernate...");
            if (!HibernateUtil.isActive()) {
                logger.error("Failed to initialize Hibernate SessionFactory!");
                System.exit(1);
            }
            logger.info("✅ Hibernate initialized successfully");

            // Step 2: Create RMI Registry
            logger.info("Creating RMI registry on port {}...", RMI_PORT);
            Registry registry = LocateRegistry.createRegistry(RMI_PORT);
            logger.info("✅ RMI Registry created successfully");

            // Step 3: Create and bind Remote Service
            logger.info("Creating Remote Service implementation...");
            RemoteService service = new RemoteServiceImpl();
            registry.rebind(SERVICE_NAME, service);
            logger.info("✅ Remote Service bound to registry as '{}'", SERVICE_NAME);

            // Step 4: Start Message Consumers (for OTP and notifications)
            logger.info("Starting ActiveMQ message consumers...");
            MessageConsumer messageConsumer = new MessageConsumer();
            messageConsumer.startOTPConsumer();
            messageConsumer.startNotificationConsumer();
            logger.info("✅ Message consumers started successfully");

            // Server is ready
            logger.info("==========================================");
            logger.info("✅ Server started successfully!");
            logger.info("==========================================");
            logger.info("RMI Port: {}", RMI_PORT);
            logger.info("Service Name: {}", SERVICE_NAME);
            logger.info("Server is ready to accept client connections...");
            logger.info("");
            logger.info("📊 Available Services:");
            logger.info("  - User Authentication (with OTP)");
            logger.info("  - Customer Management");
            logger.info("  - Gaming Station Management");
            logger.info("  - Booking Management");
            logger.info("  - Tournament Management");
            logger.info("  - Transaction Management");
            logger.info("  - Report Generation (PDF/Excel/CSV)");
            logger.info("==========================================");
            logger.info("Press Ctrl+C to stop the server");
            logger.info("==========================================");

            // Add shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                logger.info("");
                logger.info("==========================================");
                logger.info("Shutting down Gaming Center Server...");
                logger.info("==========================================");
                
                try {
                    // Unbind from registry
                    registry.unbind(SERVICE_NAME);
                    logger.info("✅ Service unbound from registry");
                    
                    // Close Hibernate SessionFactory
                    HibernateUtil.shutdown();
                    logger.info("✅ Hibernate SessionFactory closed");
                    
                    logger.info("==========================================");
                    logger.info("Server shut down successfully. Goodbye!");
                    logger.info("==========================================");
                    
                } catch (Exception e) {
                    logger.error("Error during shutdown: {}", e.getMessage(), e);
                }
            }));

            // Keep server running
            while (true) {
                Thread.sleep(1000);
                // Server continues to run and handle client requests
            }

        } catch (Exception e) {
            logger.error("==========================================");
            logger.error("❌ Server startup failed!");
            logger.error("==========================================");
            logger.error("Error: {}", e.getMessage(), e);
            System.exit(1);
        }
    }

    /**
     * Test method - Verify server configuration
     */
    private static void runServerDiagnostics() {
        logger.info("");
        logger.info("Running server diagnostics...");
        logger.info("------------------------------------------");
        
        // Check Hibernate
        logger.info("Checking Hibernate configuration...");
        if (HibernateUtil.isActive()) {
            logger.info("✅ Hibernate: OK");
        } else {
            logger.error("❌ Hibernate: Failed");
        }
        
        // Check RMI port
        logger.info("Checking RMI port availability...");
        try {
            Registry registry = LocateRegistry.getRegistry(RMI_PORT);
            logger.info("✅ RMI Port {}: Available", RMI_PORT);
        } catch (Exception e) {
            logger.error("❌ RMI Port {}: Unavailable - {}", RMI_PORT, e.getMessage());
        }
        
        // Check ActiveMQ connection
        logger.info("Checking ActiveMQ connection...");
        logger.info("⚠️  Make sure ActiveMQ is running on tcp://localhost:61616");
        
        logger.info("------------------------------------------");
        logger.info("Diagnostics complete");
        logger.info("");
    }
}
