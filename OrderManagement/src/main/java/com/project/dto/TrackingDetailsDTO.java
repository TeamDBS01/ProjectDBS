package com.project.dto;

import lombok.Data;

import java.util.Date;

@Data
public class TrackingDetailsDTO {
    private Long trackingId;
    private Long orderId;
    private String trackingNumber;
    private String shippingCarrier;
    private Date estimatedDeliveryDate;
}
