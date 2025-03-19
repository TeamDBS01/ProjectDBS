package com.project.services;

/**
 * Service interface for sending emails.
 */
public interface EmailService {

    /**
     * Sends a low stock alert email for a specific book.
     * @param bookID the ID of the book.
     * @param quantity the current quantity of the book.
     */
    void sendLowStockAlert(String bookID, int quantity);
}

