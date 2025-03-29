package com.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * Implementation of EmailService for sending emails.
 */
@Service
public class EmailServiceImpl implements EmailService {


    private JavaMailSender mailSender;
    /**
     * Constructs an EmailServiceImpl with the specified JavaMailSender.
     * @param mailSender the JavaMailSender to use for sending emails.
     */
    @Autowired
    public EmailServiceImpl(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendLowStockAlert(String bookID, int quantity) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sathishvarunt@gmail.com");
        message.setTo("sathishvarunt@gmail.com");
        message.setSubject("Low Stock Alert");
        message.setText("The stock for book ID " + bookID + " is low. Current quantity: " + quantity);
        mailSender.send(message);

        System.out.println("Mail sent successfully");
    }
}
