/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package utils;

/**
 *
 * @author Odeth
 */

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * HibernateUtil - Singleton SessionFactory
 * Manages Hibernate SessionFactory lifecycle
 */
public class HibernateUtil {
    private static final Logger logger = LoggerFactory.getLogger(HibernateUtil.class);
    private static SessionFactory sessionFactory;

    static {
        try {
            // Create SessionFactory from hibernate.cfg.xml
            Configuration configuration = new Configuration();
            configuration.configure("hibernate.cfg.xml");
            
            sessionFactory = configuration.buildSessionFactory();
            logger.info("Hibernate SessionFactory created successfully!");
            
        } catch (Exception e) {
            logger.error("Error creating SessionFactory: " + e.getMessage(), e);
            throw new ExceptionInInitializerError("Failed to create SessionFactory: " + e);
        }
    }

    /**
     * Get the SessionFactory instance
     * @return SessionFactory
     */
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            logger.warn("SessionFactory is null or closed. Recreating...");
            initSessionFactory();
        }
        return sessionFactory;
    }

    /**
     * Initialize SessionFactory (for testing or reset)
     */
    private static synchronized void initSessionFactory() {
        if (sessionFactory == null || sessionFactory.isClosed()) {
            try {
                Configuration configuration = new Configuration();
                configuration.configure("hibernate.cfg.xml");
                sessionFactory = configuration.buildSessionFactory();
                logger.info("SessionFactory reinitialized successfully!");
            } catch (Exception e) {
                logger.error("Error reinitializing SessionFactory: " + e.getMessage(), e);
                throw new RuntimeException("Failed to reinitialize SessionFactory", e);
            }
        }
    }

    /**
     * Shutdown SessionFactory (call when application closes)
     */
    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            try {
                sessionFactory.close();
                logger.info("SessionFactory closed successfully!");
            } catch (Exception e) {
                logger.error("Error closing SessionFactory: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Check if SessionFactory is active
     * @return true if active, false otherwise
     */
    public static boolean isActive() {
        return sessionFactory != null && !sessionFactory.isClosed();
    }
}
