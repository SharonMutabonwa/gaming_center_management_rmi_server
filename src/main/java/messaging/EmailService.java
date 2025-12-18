/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package messaging;

/**
 *
 * @author Odeth
 */

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * EmailService - Sends emails using JavaMail API
 * Configure with your email provider (Gmail, Outlook, etc.)
 */
public class EmailService {
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    // Email Configuration - UPDATE THESE WITH YOUR CREDENTIALS
    private static final String SMTP_HOST = "smtp.gmail.com";
    private static final String SMTP_PORT = "587";
    private static final String SENDER_EMAIL = "sharonmutabonwa@gmail.com"; // Your email
    private static final String SENDER_PASSWORD = "fhsf digs jzcz dlce";      // Your app password
    private static final String SENDER_NAME = "Gaming Center Management";

    private final Properties properties;

    public EmailService() {
        properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", SMTP_HOST);
        properties.put("mail.smtp.port", SMTP_PORT);
        properties.put("mail.smtp.ssl.trust", SMTP_HOST);
    }

    /**
     * Send OTP email
     */
    public void sendOTPEmail(String recipientEmail, String username, String otp) {
        String subject = "Your Gaming Center OTP Code";
        String htmlContent = buildOTPEmailTemplate(username, otp);
        sendEmail(recipientEmail, subject, htmlContent);
    }

    /**
     * Send booking confirmation email
     */
    public void sendBookingConfirmation(String recipientEmail, String customerName, 
                                       String stationName, String date, String time) {
        String subject = "Booking Confirmation - Gaming Center";
        String htmlContent = buildBookingEmailTemplate(customerName, stationName, date, time);
        sendEmail(recipientEmail, subject, htmlContent);
    }

    /**
     * Send tournament registration confirmation
     */
    public void sendTournamentConfirmation(String recipientEmail, String customerName, 
                                          String tournamentName, String startDate) {
        String subject = "Tournament Registration Confirmed";
        String htmlContent = buildTournamentEmailTemplate(customerName, tournamentName, startDate);
        sendEmail(recipientEmail, subject, htmlContent);
    }

    /**
     * Send generic email
     */
    public void sendGenericEmail(String recipientEmail, String subject, String content) {
        String htmlContent = buildGenericEmailTemplate(content);
        sendEmail(recipientEmail, subject, htmlContent);
    }

    /**
     * Core email sending method
     */
    private void sendEmail(String recipientEmail, String subject, String htmlContent) {
        try {
            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, SENDER_PASSWORD);
                }
            });

            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(SENDER_EMAIL, SENDER_NAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject(subject);
            message.setContent(htmlContent, "text/html; charset=utf-8");

            Transport.send(message);
            logger.info("Email sent successfully to: {}", recipientEmail);

        } catch (Exception e) {
            logger.error("Failed to send email to {}: {}", recipientEmail, e.getMessage(), e);
        }
    }

    // ============================================
    // EMAIL TEMPLATES
    // ============================================

    private String buildOTPEmailTemplate(String username, String otp) {
        return "<!DOCTYPE html>" +
               "<html>" +
               "<head>" +
               "    <style>" +
               "        body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }" +
               "        .container { background-color: #ffffff; max-width: 600px; margin: 0 auto; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
               "        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; }" +
               "        .otp-code { font-size: 36px; font-weight: bold; color: #667eea; text-align: center; padding: 20px; background-color: #f0f0f0; border-radius: 5px; margin: 20px 0; letter-spacing: 5px; }" +
               "        .content { padding: 20px; line-height: 1.6; }" +
               "        .footer { text-align: center; color: #666; font-size: 12px; margin-top: 20px; }" +
               "    </style>" +
               "</head>" +
               "<body>" +
               "    <div class='container'>" +
               "        <div class='header'>" +
               "            <h1>🎮 Gaming Center</h1>" +
               "        </div>" +
               "        <div class='content'>" +
               "            <h2>Hello " + username + ",</h2>" +
               "            <p>Your One-Time Password (OTP) for login is:</p>" +
               "            <div class='otp-code'>" + otp + "</div>" +
               "            <p><strong>⚠️ Important:</strong></p>" +
               "            <ul>" +
               "                <li>This OTP is valid for <strong>5 minutes</strong></li>" +
               "                <li>Do not share this code with anyone</li>" +
               "                <li>If you didn't request this, please ignore this email</li>" +
               "            </ul>" +
               "        </div>" +
               "        <div class='footer'>" +
               "            <p>© 2024 Gaming Center Management System. All rights reserved.</p>" +
               "        </div>" +
               "    </div>" +
               "</body>" +
               "</html>";
    }

    private String buildBookingEmailTemplate(String customerName, String stationName, 
                                            String date, String time) {
        return "<!DOCTYPE html>" +
               "<html>" +
               "<head>" +
               "    <style>" +
               "        body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }" +
               "        .container { background-color: #ffffff; max-width: 600px; margin: 0 auto; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
               "        .header { background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%); color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; }" +
               "        .booking-details { background-color: #f8f8f8; padding: 20px; border-left: 4px solid #11998e; margin: 20px 0; }" +
               "        .content { padding: 20px; line-height: 1.6; }" +
               "        .detail-row { padding: 10px 0; border-bottom: 1px solid #e0e0e0; }" +
               "        .detail-label { font-weight: bold; color: #555; }" +
               "        .detail-value { color: #333; float: right; }" +
               "        .footer { text-align: center; color: #666; font-size: 12px; margin-top: 20px; }" +
               "    </style>" +
               "</head>" +
               "<body>" +
               "    <div class='container'>" +
               "        <div class='header'>" +
               "            <h1>✅ Booking Confirmed!</h1>" +
               "        </div>" +
               "        <div class='content'>" +
               "            <h2>Hello " + customerName + ",</h2>" +
               "            <p>Your gaming session has been successfully booked!</p>" +
               "            <div class='booking-details'>" +
               "                <h3>Booking Details:</h3>" +
               "                <div class='detail-row'>" +
               "                    <span class='detail-label'>🎮 Gaming Station:</span>" +
               "                    <span class='detail-value'>" + stationName + "</span>" +
               "                    <div style='clear:both;'></div>" +
               "                </div>" +
               "                <div class='detail-row'>" +
               "                    <span class='detail-label'>📅 Date:</span>" +
               "                    <span class='detail-value'>" + date + "</span>" +
               "                    <div style='clear:both;'></div>" +
               "                </div>" +
               "                <div class='detail-row'>" +
               "                    <span class='detail-label'>⏰ Time:</span>" +
               "                    <span class='detail-value'>" + time + "</span>" +
               "                    <div style='clear:both;'></div>" +
               "                </div>" +
               "            </div>" +
               "            <p>Please arrive 5 minutes before your scheduled time. Have a great gaming session!</p>" +
               "        </div>" +
               "        <div class='footer'>" +
               "            <p>© 2024 Gaming Center Management System. All rights reserved.</p>" +
               "        </div>" +
               "    </div>" +
               "</body>" +
               "</html>";
    }

    private String buildTournamentEmailTemplate(String customerName, String tournamentName, 
                                               String startDate) {
        return "<!DOCTYPE html>" +
               "<html>" +
               "<head>" +
               "    <style>" +
               "        body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }" +
               "        .container { background-color: #ffffff; max-width: 600px; margin: 0 auto; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
               "        .header { background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%); color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; }" +
               "        .content { padding: 20px; line-height: 1.6; }" +
               "        .tournament-info { background-color: #fff5f5; padding: 20px; border-left: 4px solid #f5576c; margin: 20px 0; }" +
               "        .footer { text-align: center; color: #666; font-size: 12px; margin-top: 20px; }" +
               "    </style>" +
               "</head>" +
               "<body>" +
               "    <div class='container'>" +
               "        <div class='header'>" +
               "            <h1>🏆 Tournament Registration Confirmed!</h1>" +
               "        </div>" +
               "        <div class='content'>" +
               "            <h2>Hello " + customerName + ",</h2>" +
               "            <p>You've successfully registered for the tournament!</p>" +
               "            <div class='tournament-info'>" +
               "                <h3>Tournament Details:</h3>" +
               "                <p><strong>🎮 Tournament:</strong> " + tournamentName + "</p>" +
               "                <p><strong>📅 Start Date:</strong> " + startDate + "</p>" +
               "            </div>" +
               "            <p>Good luck! May the best gamer win!</p>" +
               "        </div>" +
               "        <div class='footer'>" +
               "            <p>© 2024 Gaming Center Management System. All rights reserved.</p>" +
               "        </div>" +
               "    </div>" +
               "</body>" +
               "</html>";
    }

    private String buildGenericEmailTemplate(String content) {
        return "<!DOCTYPE html>" +
               "<html>" +
               "<head>" +
               "    <style>" +
               "        body { font-family: Arial, sans-serif; background-color: #f4f4f4; padding: 20px; }" +
               "        .container { background-color: #ffffff; max-width: 600px; margin: 0 auto; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
               "        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; }" +
               "        .content { padding: 20px; line-height: 1.6; }" +
               "        .footer { text-align: center; color: #666; font-size: 12px; margin-top: 20px; }" +
               "    </style>" +
               "</head>" +
               "<body>" +
               "    <div class='container'>" +
               "        <div class='header'>" +
               "            <h1>🎮 Gaming Center Notification</h1>" +
               "        </div>" +
               "        <div class='content'>" +
               "            <p>" + content + "</p>" +
               "        </div>" +
               "        <div class='footer'>" +
               "            <p>© 2024 Gaming Center Management System. All rights reserved.</p>" +
               "        </div>" +
               "    </div>" +
               "</body>" +
               "</html>";
    }

    /**
     * Test method to verify email configuration
     */
    public static void main(String[] args) {
        EmailService emailService = new EmailService();
        
        // Test OTP email
        emailService.sendOTPEmail(
            "test@example.com",
            "TestUser",
            "123456"
        );
        
        System.out.println("Test email sent! Check your inbox.");
    }
}
