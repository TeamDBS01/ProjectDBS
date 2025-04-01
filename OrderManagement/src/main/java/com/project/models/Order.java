package com.project.models;

import java.util.Date;
import java.util.List;

import com.project.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an order placed by a user.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name="order_table")
public class Order {
	/**
	 * The unique identifier for the order.
	 */
	@Id
	@Column(name="order_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long orderId;
	/**
	 * The date and time when the order was placed.
	 */
	@Column(name="order_date")
	private Date orderDate;
	/**
	 * The total amount of the order.
	 */
	@Column(name="total_amount")
	private Double totalAmount;
	/**
	 * The payment status of the order.
	 */
	@Enumerated(EnumType.STRING)
	@Column(name="payment_status")
	private PaymentStatus paymentStatus;
	/**
	 * The current status of the order (e.g., "Pending", "Shipped", "Delivered").
	 */
	@Column(name="status")
	private String status;
	/**
	 * The ID of the user who placed the order.
	 */
	@Column(name="user_id")
	private Long userId;
	/**
	 * The list of book IDs included in the order, along with their quantities.
	 * Each string in the list is formatted as "bookId:quantity".
	 */
	@ElementCollection
	@CollectionTable(name="order_book_ids",joinColumns=@JoinColumn(name="order_order_id"))
	@Column(name="book_ids")
	private List<String> bookIds;

	/**
	 * Constructs a new Order object with the specified attributes.
	 *
	 * @param orderDate     The date and time when the order was placed.
	 * @param totalAmount   The total amount of the order.
	 * @param paymentStatus The payment status of the order.
	 * @param status        The current status of the order.
	 * @param userId        The ID of the user who placed the order.
	 * @param bookIds       The list of book IDs included in the order.
	 */
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
