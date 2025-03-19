package com.project.services;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the EmailServiceImpl class.
 */
public class EmailServiceTest {

    /**
     * Mock instance of JavaMailSender.
     */
    @Mock
    private JavaMailSender mailSender;

    /**
     * Injected instance of EmailServiceImpl.
     */
    @InjectMocks
    private EmailServiceImpl emailService;

    /**
     * Initializes mock objects before each test.
     */
    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests the positive scenario for sending an email.
     * Verifies that the send method of JavaMailSender is called once.
     */
    @Test
    void testSendMail_Positive(){

        emailService.sendLowStockAlert("B1001", 5);
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    /**
     * Tests the negative scenario for sending an email.
     * Simulates a MailSendException and verifies that the exception is thrown.
     */
    @Test
    void testSendMail_Negative(){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sathishvarunt@gmail.com");
        message.setTo("sathishvarunt@gmail.com");
        message.setSubject("Low Stock Alert");
        message.setText("The stock for book ID " + "B1001" + " is low. Current quantity: " + 5);

        doThrow(new MailSendException("Failed to send email")).when(mailSender).send(message);

        assertThrows(MailSendException.class, () -> {
            emailService.sendLowStockAlert("B1001", 5);
        });

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

}