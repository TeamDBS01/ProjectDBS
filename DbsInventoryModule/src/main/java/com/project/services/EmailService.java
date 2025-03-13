package com.project.services;

import com.project.models.EmailDetails;

public interface EmailService {

    // Method to send a simple email
    void sendLowStockAlert(String bookID, int quantity);
}

