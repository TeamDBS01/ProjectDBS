package com.project.models;

import java.util.Date;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name="order")
public class Order {
	@Id
	@Column(name="order_id")
	private Long orderId;
	@Column(name="order_date")
	private Date orderDate;
	@Column(name="total_amount")
	private Double totalAmount;
	@Column(name="status")
	private String status;
	
//	@ManyToOne(fetch=FetchType.EAGER)
//	private User user;
	
}
