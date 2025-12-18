/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messaging;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Odeth
 */
 /**
 * MessageConsumer - Consumes messages from ActiveMQ and sends emails
 * This should run as a separate thread or background service
 */
public class MessageConsumer {
    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);
    
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String OTP_QUEUE = "OTP_QUEUE";
    private static final String NOTIFICATION_QUEUE = "NOTIFICATION_QUEUE";

    private final EmailService emailService;

    public MessageConsumer() {
        this.emailService = new EmailService();
    }

    /**
     * Start consuming OTP messages
     */
    public void startOTPConsumer() {
        Thread consumerThread = new Thread(() -> {
            try {
                ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
                Connection connection = factory.createConnection();
                connection.start();

                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination destination = session.createQueue(OTP_QUEUE);
                javax.jms.MessageConsumer consumer = session.createConsumer(destination);

                logger.info("OTP Message Consumer started and listening...");

                consumer.setMessageListener(message -> {
                    try {
                        if (message instanceof TextMessage) {
                            TextMessage textMessage = (TextMessage) message;
                            String text = textMessage.getText();
                            
                            // Parse message: TYPE:OTP|EMAIL:xxx|USERNAME:xxx|OTP:xxx
                            String[] parts = text.split("\\|");
                            String email = parts[1].split(":")[1];
                            String username = parts[2].split(":")[1];
                            String otp = parts[3].split(":")[1];

                            // Send email
                            emailService.sendOTPEmail(email, username, otp);
                            logger.info("OTP email sent successfully to: {}", email);
                        }
                    } catch (Exception e) {
                        logger.error("Error processing OTP message: {}", e.getMessage(), e);
                    }
                });

                // Keep thread alive
                Thread.currentThread().join();

            } catch (Exception e) {
                logger.error("Error in OTP consumer: {}", e.getMessage(), e);
            }
        });

        consumerThread.setDaemon(false);
        consumerThread.start();
    }

    /**
     * Start consuming notification messages
     */
    public void startNotificationConsumer() {
        Thread consumerThread = new Thread(() -> {
            try {
                ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
                Connection connection = factory.createConnection();
                connection.start();

                Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination destination = session.createQueue(NOTIFICATION_QUEUE);
                javax.jms.MessageConsumer consumer = session.createConsumer(destination);

                logger.info("Notification Message Consumer started and listening...");

                consumer.setMessageListener(message -> {
                    try {
                        if (message instanceof TextMessage) {
                            TextMessage textMessage = (TextMessage) message;
                            String text = textMessage.getText();
                            
                            String[] parts = text.split("\\|");
                            String type = parts[0].split(":")[1];

                            switch (type) {
                                case "BOOKING_CONFIRMATION":
                                    processBookingConfirmation(parts);
                                    break;
                                case "TOURNAMENT_REGISTRATION":
                                    processTournamentConfirmation(parts);
                                    break;
                                case "GENERIC":
                                    processGenericNotification(parts);
                                    break;
                                default:
                                    logger.warn("Unknown notification type: {}", type);
                            }
                        }
                    } catch (Exception e) {
                        logger.error("Error processing notification: {}", e.getMessage(), e);
                    }
                });

                // Keep thread alive
                Thread.currentThread().join();

            } catch (Exception e) {
                logger.error("Error in notification consumer: {}", e.getMessage(), e);
            }
        });

        consumerThread.setDaemon(false);
        consumerThread.start();
    }

    private void processBookingConfirmation(String[] parts) {
        String email = parts[1].split(":")[1];
        String name = parts[2].split(":")[1];
        String station = parts[3].split(":")[1];
        String date = parts[4].split(":")[1];
        String time = parts[5].split(":")[1];

        emailService.sendBookingConfirmation(email, name, station, date, time);
        logger.info("Booking confirmation email sent to: {}", email);
    }

    private void processTournamentConfirmation(String[] parts) {
        String email = parts[1].split(":")[1];
        String name = parts[2].split(":")[1];
        String tournament = parts[3].split(":")[1];
        String startDate = parts[4].split(":")[1];

        emailService.sendTournamentConfirmation(email, name, tournament, startDate);
        logger.info("Tournament confirmation email sent to: {}", email);
    }

    private void processGenericNotification(String[] parts) {
        String email = parts[1].split(":")[1];
        String subject = parts[2].split(":")[1];
        String content = parts[3].split(":")[1];

        emailService.sendGenericEmail(email, subject, content);
        logger.info("Generic notification email sent to: {}", email);
    }

    /**
     * Main method to start all consumers
     */
    public static void main(String[] args) {
        MessageConsumer consumer = new MessageConsumer();
        consumer.startOTPConsumer();
        consumer.startNotificationConsumer();
        
        logger.info("All message consumers started successfully!");
    }
}

