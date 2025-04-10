package com.project.dto;

import jakarta.persistence.Entity;
import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingDetailsDTO {
    private String shippingName;
    private String shippingEmail;
    private String shippingPhone;
    private String shippingAddress;
    private String shippingCity;
    private String shippingState;
    private String shippingZipCode;
}