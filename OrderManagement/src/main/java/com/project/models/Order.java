package com.project.models;

import com.project.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="order_table")
public class Order {
	@Id
	@Column(name="order_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long orderId;
	@Column(name="order_date")
	private Date orderDate;
	@Column(name="total_amount")
	private Double totalAmount;

	@Enumerated(EnumType.STRING)
	@Column(name="payment_status")
	private PaymentStatus paymentStatus;

	@Column(name="status")
	private String status;
	@Column(name="user_id")
	private Long userId;
	
	@ElementCollection
	@CollectionTable(name="order_book_ids",joinColumns=@JoinColumn(name="order_order_id"))
	@Column(name="book_ids")
	private List<String> bookIds;



	public Order(Date orderDate, Double totalAmount,PaymentStatus paymentStatus, String status, Long userId, List<String> bookIds) {
		super();
		this.orderDate = orderDate;
		this.totalAmount = totalAmount;
		this.paymentStatus = paymentStatus;
		this.status = status;
		this.userId = userId;
		this.bookIds = bookIds;
	}
}
