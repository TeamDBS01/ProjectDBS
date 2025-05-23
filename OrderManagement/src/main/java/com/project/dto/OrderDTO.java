package com.project.dto;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.project.enums.PaymentStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class OrderDTO {
	private Long orderId;
	private Date orderDate;
	@NotNull(message="Total amount cannot be null")
	@Min(value=0,message="Total amount must be greater than or equal to 0")
	private Double totalAmount;
	@NotBlank(message="Status cannot be blank")
	private String status;
	@NotNull(message="User Id cannot be null")
	private Long userId;
	@NotEmpty(message="Book IDs cannot be empty")
	private List<String> bookIds;
	private PaymentStatus paymentStatus;
	private TrackingDetailsDTO trackingDetails;
	private ReturnDetailsDTO returnDetails;
}
