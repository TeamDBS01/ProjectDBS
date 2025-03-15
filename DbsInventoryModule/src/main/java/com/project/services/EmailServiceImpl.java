package com.project.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendLowStockAlert(String bookID, int quantity) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sathishvarunt@gmail.com");
        message.setTo("sathishvarunt@gmail.com");
        message.setText("The stock for book ID " + bookID + " is low. Current quantity: " + quantity);
        mailSender.send(message);

        System.out.println("Mail sent successfully");
    }
}
