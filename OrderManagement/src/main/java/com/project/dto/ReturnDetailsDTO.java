package com.project.dto;

import lombok.Data;

import java.util.Date;

@Data
public class ReturnDetailsDTO {
    private Long returnId;
    private Long orderId;
    private String returnReason;
    private Date returnRequestedDate;
    private Date returnApprovedDate;
    private Date returnRejectedDate;
}