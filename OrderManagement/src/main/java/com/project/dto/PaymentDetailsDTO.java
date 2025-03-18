package com.project.dto;

import com.project.enums.PaymentStatus;
import lombok.Data;

import java.util.Date;

@Data
public class PaymentDetailsDTO {
    private Long orderId;
    private Long userId;
    private Double amount;
    private Date paymentDate;
    private PaymentStatus paymentStatus;
    private Double remainingCredits;
}
