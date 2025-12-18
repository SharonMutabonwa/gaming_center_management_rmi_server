/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messaging;

/**
 *
 * @author Odeth
 */

import org.apache.activemq.ActiveMQConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jms.*;

/**
 * MessageProducer - Sends messages to ActiveMQ queue
 * Used for sending OTP and notifications via email
 */
public class MessageProducer {
    private static final Logger logger = LoggerFactory.getLogger(MessageProducer.class);
    
    private static final String BROKER_URL = "tcp://localhost:61616";
    private static final String OTP_QUEUE = "OTP_QUEUE";
    private static final String NOTIFICATION_QUEUE = "NOTIFICATION_QUEUE";

    /**
     * Send OTP email via message queue
     */
    public void sendOTPEmail(String email, String username, String otp) {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(OTP_QUEUE);
            javax.jms.MessageProducer producer = session.createProducer(destination);

            // Create message with OTP details
            String messageText = String.format("TYPE:OTP|EMAIL:%s|USERNAME:%s|OTP:%s", email, username, otp);
            TextMessage message = session.createTextMessage(messageText);
            
            producer.send(message);
            logger.info("OTP message sent to queue for: {}", email);

            connection.close();

        } catch (Exception e) {
            logger.error("Error sending OTP message: {}", e.getMessage(), e);
        }
    }

    /**
     * Send booking confirmation email
     */
    public void sendBookingConfirmation(String email, String customerName, String stationName, 
                                       String date, String time) {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(NOTIFICATION_QUEUE);
            javax.jms.MessageProducer producer = session.createProducer(destination);

            String messageText = String.format(
                "TYPE:BOOKING_CONFIRMATION|EMAIL:%s|NAME:%s|STATION:%s|DATE:%s|TIME:%s",
                email, customerName, stationName, date, time
            );
            
            TextMessage message = session.createTextMessage(messageText);
            producer.send(message);
            logger.info("Booking confirmation sent to queue for: {}", email);

            connection.close();

        } catch (Exception e) {
            logger.error("Error sending booking confirmation: {}", e.getMessage(), e);
        }
    }

    /**
     * Send tournament registration confirmation
     */
    public void sendTournamentConfirmation(String email, String customerName, String tournamentName, 
                                          String startDate) {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(NOTIFICATION_QUEUE);
            javax.jms.MessageProducer producer = session.createProducer(destination);

            String messageText = String.format(
                "TYPE:TOURNAMENT_REGISTRATION|EMAIL:%s|NAME:%s|TOURNAMENT:%s|START_DATE:%s",
                email, customerName, tournamentName, startDate
            );
            
            TextMessage message = session.createTextMessage(messageText);
            producer.send(message);
            logger.info("Tournament confirmation sent to queue for: {}", email);

            connection.close();

        } catch (Exception e) {
            logger.error("Error sending tournament confirmation: {}", e.getMessage(), e);
        }
    }

    /**
     * Send generic notification
     */
    public void sendNotification(String email, String subject, String content) {
        try {
            ConnectionFactory factory = new ActiveMQConnectionFactory(BROKER_URL);
            Connection connection = factory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Destination destination = session.createQueue(NOTIFICATION_QUEUE);
            javax.jms.MessageProducer producer = session.createProducer(destination);

            String messageText = String.format(
                "TYPE:GENERIC|EMAIL:%s|SUBJECT:%s|CONTENT:%s",
                email, subject, content
            );
            
            TextMessage message = session.createTextMessage(messageText);
            producer.send(message);
            logger.info("Generic notification sent to queue for: {}", email);

            connection.close();

        } catch (Exception e) {
            logger.error("Error sending notification: {}", e.getMessage(), e);
        }
    }
}