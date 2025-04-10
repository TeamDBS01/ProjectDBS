package com.project.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReturnDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long returnId;

    private Long orderId;

    private String returnReason;
    private Date returnRequestedDate;
    private Date returnApprovedDate;
    private Date returnRejectedDate;
}