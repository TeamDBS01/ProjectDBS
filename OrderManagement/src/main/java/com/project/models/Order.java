package com.project.models;

import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
//import lombok.Getter;
import lombok.NoArgsConstructor;
//import lombok.Setter;

@Entity
@Data
//@Getter
//@Setter
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
	@Column(name="status")
	private String status;
	@Column(name="user_id")
	private Long userId;
	
	@ElementCollection
	private List<String> bookIds;

	public Order(Date orderDate, Double totalAmount, String status, Long userId, List<String> bookIds) {
		super();
		this.orderDate = orderDate;
		this.totalAmount = totalAmount;
		this.status = status;
		this.userId = userId;
		this.bookIds = bookIds;
	}
	
	
	
//	public Order(Date orderDate,double totalAmount,String status,long userId,List<String> bookIds) {}
	
}
