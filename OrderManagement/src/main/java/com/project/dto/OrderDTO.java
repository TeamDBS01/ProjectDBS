package com.project.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
	private Long orderId;
	private Date orderDate;
	private Double totalAmount;
	private String status;
//	private UserDTO user;
}
