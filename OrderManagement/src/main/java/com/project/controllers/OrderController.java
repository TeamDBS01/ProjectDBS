package com.project.controllers;


import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.project.dto.*;
import com.project.exception.*;
import com.project.feign.BookClient;
import com.project.feign.UserClient;
import com.project.models.CartItem;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.service.OrderServiceImpl;

/**
 * REST controller for handling order-related operations.
 * This class provides endpoints for adding items to cart, placing orders,
 * processing payments, updating order status, and retrieving order details.
 */

@RestController
//@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("dbs/order")
public class OrderController {

	private final OrderServiceImpl orderService;
	private final UserClient userClient;
	private final BookClient bookClient;

	/**
	 * Constructs a new OrderController with the specified OrderServiceImpl and UserClient.
	 *
	 * @param orderService The service layer for order-related operations.
	 * @param userClient   The Feign client for user-related operations.
	 */
	@Autowired
	public OrderController(OrderServiceImpl orderService, UserClient userClient, BookClient bookClient) {
		this.orderService = orderService;
		this.userClient = userClient;
        this.bookClient = bookClient;
    }

	/**
	 * Adds a book to the user's cart.
	 *
	 * @param userId   The ID of the user.
	 * @param bookId   The ID of the book to add.
	 * @param quantity The quantity of the book to add.
	 * @return ResponseEntity containing the updated cart or an error message.
	 * @throws ResourceNotFoundException  If the user or book is not found.
	 * @throws InsufficientStockException If the requested quantity exceeds available stock.
	 */
	@Operation(summary = "Add book to cart", description = "Adds a book to the user's cart.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Book added to cart successfully"),
			@ApiResponse(responseCode = "404", description = "User or book not found")
	})
	@PostMapping("/{userId}/cart/add")
	public ResponseEntity<Object> addToCart(
			@PathVariable Long userId,
			@RequestParam String bookId,
			@RequestParam int quantity) {
		List<CartItem> updatedCart = orderService.addToCart(userId, bookId, quantity);
		return ResponseEntity.status(HttpStatus.CREATED).body(updatedCart);
	}

	/**
	 * Retrieves the items in the user's cart.
	 *
	 * @param userId The ID of the user.
	 * @return ResponseEntity containing the list of cart items.
	 */
	@Operation(summary = "Get cart items", description = "Retrieves the items in the user's cart.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved cart items")
	})
	@GetMapping("/{userId}/cart")
	public ResponseEntity<List<CartItem>> getCartItems(@PathVariable Long userId) {
		return ResponseEntity.ok(orderService.getCartItems(userId));
	}


	/**
	 * Places an order for the items in the user's cart.
	 *
	 * @param userId The ID of the user.
	 * @return ResponseEntity containing the placed order or an error message.
	 * @throws ResourceNotFoundException  If the user is not found.
	 * @throws CartEmptyException         If the user's cart is empty.
	 * @throws InsufficientStockException If there is insufficient stock for any item.
	 */
	@Operation(summary = "Place order", description = "Places an order for the items in the user's cart.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Order placed successfully"),
			@ApiResponse(responseCode = "400", description = "User not found or invalid request")
	})
	@PostMapping("/{userId}")
	public ResponseEntity<Object> placeOrder(@PathVariable Long userId,@RequestBody ShippingDetailsDTO shippingDetails) {
		OrderDTO order = orderService.placeOrder(userId,shippingDetails);
		return new ResponseEntity<>(order, HttpStatus.CREATED);
	}

	/**
	 * Processes payment for a specific order.
	 *
	 * @param orderId The ID of the order.
	 * @param userId  The ID of the user making the payment.
	 * @return ResponseEntity containing payment details or an error message.
	 * @throws ResourceNotFoundException    If the order or user is not found.
	 * @throws OrderAlreadyPaidException    If the order is already paid.
	 * @throws InsufficientCreditsException If the user has insufficient credits.
	 * @throws SecurityException            if the user making the payment does not match the order's user.
	 */
	@Operation(summary = "Process payment for an order", description = "Processes payment for a specific order.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Payment processed successfully"),
			@ApiResponse(responseCode = "404", description = "Order or user not found"),
			@ApiResponse(responseCode = "400", description = "Order already paid or insufficient credits")
	})

	@PostMapping("/{orderId}/payment/{userId}")
	public ResponseEntity<Object> processPayment(@PathVariable Long orderId, @PathVariable Long userId) {
		try {
			PaymentDetailsDTO paymentDetails = orderService.processPayment(orderId, userId);
			return new ResponseEntity<>(paymentDetails, HttpStatus.OK);
		} catch (InsufficientCreditsException ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
		}
	}


	/**
	 * Updates the status of an order.
	 *
	 * @param orderId     The ID of the order.
	 * @param status      The new status of the order.
	 * @param adminUserId The ID of the admin user performing the update.
	 * @return ResponseEntity containing the updated order or an error message.
	 * @throws ResourceNotFoundException If the order or admin user is not found.
	 * @throws SecurityException         If the admin user does not have permission to update the order status.
	 */

	@Operation(summary = "Update order status", description = "Updates the status of an order.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Order status updated successfully"),
			@ApiResponse(responseCode = "404", description = "Order not found or admin user not found"),
			@ApiResponse(responseCode = "403", description = "Forbidden: User does not have permission")
	})
	@PutMapping("/{orderId}/{status}/{adminUserId}")
	public ResponseEntity<Object> updateOrderStatus(@PathVariable Long orderId,
													@PathVariable String status,
													@PathVariable Long adminUserId) {
		OrderDTO order = orderService.updateOrderStatus(orderId, status, adminUserId);
		return new ResponseEntity<>(order, HttpStatus.OK);
	}


	/**
	 * Cancels a specific order.
	 *
	 * @param orderId The ID of the order.
	 * @param userId  The ID of the user requesting the cancellation.
	 * @return ResponseEntity containing the cancelled order or an error message.
	 * @throws ResourceNotFoundException If the order is not found.
	 * @throws SecurityException         If the user is not authorized to cancel the order.
	 * @throws Exception                 If any other error occurs during cancellation.
	 */
	@Operation(summary = "Cancel an order", description = "Cancels a specific order.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
			@ApiResponse(responseCode = "404", description = "Order not found"),
			@ApiResponse(responseCode = "403", description = "Forbidden: User not authorized"),
			@ApiResponse(responseCode = "400", description = "Bad request: Order already paid or other cancellation error")
	})
	@PutMapping("/{orderId}/cancel/{userId}")
	public ResponseEntity<Object> cancelOrder(@PathVariable Long orderId, @PathVariable Long userId) {
		OrderDTO cancelledOrder = orderService.cancelOrder(orderId, userId);
		return new ResponseEntity<>(cancelledOrder, HttpStatus.OK);
	}

	/**
	 * Requests a return for an order.
	 *
	 * @param orderId The ID of the order to return.
	 * @param userId  The ID of the user requesting the return.
	 * @param reason  The reason for the return.
	 * @return A ResponseEntity containing the updated OrderDTO and HTTP status.
	 */
	@Operation(summary = "Request a return", description = "Requests a return for an order.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Return request submitted successfully"),
			@ApiResponse(responseCode = "404", description = "Order not found"),
			@ApiResponse(responseCode = "403", description = "Forbidden: User not authorized"),
			@ApiResponse(responseCode = "400", description = "Bad request: Invalid order status")
	})
	@PutMapping("/{orderId}/return/{userId}")
	public ResponseEntity<Object> requestReturn(@PathVariable Long orderId,
												@PathVariable Long userId,
												@RequestParam String reason) {
		OrderDTO returnedOrder = orderService.requestReturn(orderId, userId, reason);
		return new ResponseEntity<>(returnedOrder, HttpStatus.OK);
	}

	/**
	 * Admin processes a return for an order.
	 *
	 * @param orderId     The ID of the order.
	 * @param adminUserId The ID of the admin user processing the return.
	 * @param action      The action to take ("approve" or "reject").
	 * @return A ResponseEntity containing the updated OrderDTO and HTTP status.
	 */
	@Operation(summary = "Admin process a return", description = "Admin processes a return for an order.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Return processed successfully"),
			@ApiResponse(responseCode = "404", description = "Order or user not found"),
			@ApiResponse(responseCode = "403", description = "Forbidden: User not authorized"),
			@ApiResponse(responseCode = "400", description = "Bad request: Invalid action")
	})
	@PutMapping("/{orderId}/adminReturn/{adminUserId}")
	public ResponseEntity<Object> adminProcessReturn(@PathVariable Long orderId,
													 @PathVariable Long adminUserId,
													 @RequestParam String action) {
		OrderDTO returnedOrder = orderService.adminProcessReturn(orderId, adminUserId, action);
		return new ResponseEntity<>(returnedOrder, HttpStatus.OK);
	}


	/**
	 * Updates the tracking details for an order.
	 *
	 * @param orderId              The ID of the order to update.
	 * @param shippingCarrier      The shipping carrier (e.g., "UPS", "FedEx").
	 * @param estimatedDeliveryDate The estimated delivery date.
	 * @param adminUserId          The ID of the admin user performing the update.
	 * @return A ResponseEntity containing the updated OrderDTO and HTTP status.
	 */
	@Operation(summary = "Update tracking details for an order", description = "Updates the tracking details of an order.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Tracking details updated successfully"),
			@ApiResponse(responseCode = "404", description = "Order not found")
	})
	@PutMapping("/{orderId}/tracking")
	public ResponseEntity<Object> updateTracking(@PathVariable Long orderId,
												 @RequestParam String shippingCarrier,
												 @RequestParam  @DateTimeFormat(pattern = "yyyy-MM-dd") Date estimatedDeliveryDate,
												 @RequestParam Long adminUserId) {

		OrderDTO updatedOrder = orderService.updateTracking(orderId, shippingCarrier, estimatedDeliveryDate,adminUserId);
		return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
	}

	/**
	 * Clears a specific item from the user's cart.
	 *
	 * @param userId The ID of the user.
	 * @param bookId The ID of the book to clear.
	 * @return ResponseEntity indicating success or an error message.
	 * @throws ResourceNotFoundException If the user is not found.
	 */
	@Operation(summary = "Clear cart item", description = "Clears a specific item from the user's cart.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Cart item cleared succesfully"),
			@ApiResponse(responseCode = "404", description = "User or book not found")
	})
	@DeleteMapping("/{userId}/cart/clear")
	public ResponseEntity<Object> clearCartItem(@PathVariable Long userId, @RequestParam String bookId) {
		ResponseEntity<UserDTO> userResponse = userClient.getUserById(userId);
		if (userResponse.getStatusCode() != HttpStatus.OK || userResponse.getBody() == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
		}
		orderService.clearCartItem(userId, bookId);
		return ResponseEntity.ok("Cart Item cleared successfully");
	}


	/**
	 * Retrieves all orders for a user.
	 *
	 * @param userId The ID of the user.
	 * @return ResponseEntity containing the list of user orders or an error message.
	 * @throws ResourceNotFoundException If user orders are not found.
	 */
	@Operation(summary = "Get user orders", description = "Retrieves all orders for a user.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved user orders"),
			@ApiResponse(responseCode = "404", description = "User orders not found")
	})
	@GetMapping("/user/{userId}")
	public ResponseEntity<Object> getUserOrders(@PathVariable Long userId) {
		List<OrderDTO> orders = orderService.getUserOrders(userId);
		return new ResponseEntity<>(orders, HttpStatus.OK);
	}

	/**
	 * Retrieves details of a specific order.
	 *
	 * @param orderId The ID of the order.
	 *                * @return ResponseEntity containing the order details or an error message.
	 *                * @throws ResourceNotFoundException If the order is not found.
	 */
	@Operation(summary = "Get order details", description = "Retrieves details of a specific order.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved order details"),
			@ApiResponse(responseCode = "404", description = "Order not found")
	})

	@GetMapping("/{orderId}")
	public ResponseEntity<Object> getOrderDetails(@PathVariable Long orderId) {
		OrderDTO orderDTO = orderService.getOrderDetails(orderId);
		return new ResponseEntity<>(orderDTO, HttpStatus.OK);
	}

	/**
	 * Retrieves all books associated with an order.
	 *
	 * @param orderId The ID of the order.
	 * @return ResponseEntity containing the list of books or an error message.
	 * @throws ResourceNotFoundException If the order or books are not found.
	 */
	@Operation(summary = "Get books by order ID", description = "Retrieves all books associated with an order.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved books"),
			@ApiResponse(responseCode = "404", description = "Order or books not found")
	})
	@GetMapping("/orders/{orderId}/books")
	public ResponseEntity<Object> getBooksByOrderId(@PathVariable Long orderId) {
		List<BookDTO> books = orderService.getBooksByOrderId(orderId);
		return new ResponseEntity<>(books, HttpStatus.OK);

	}


	/**
	 * Updates the quantity of a specific item in the user's cart.
	 *
	 * @param userId   The ID of the user.
	 * @param bookId   The ID of the book to update.
	 * @param quantity The new quantity of the book.
	 * @return ResponseEntity containing the updated cart or an error message.
	 * @throws ResourceNotFoundException If the user or book is not found in the cart.
	 */
	@Operation(summary = "Update cart item quantity", description = "Updates the quantity of a specific item in the user's cart.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Cart item quantity updated successfully"),
			@ApiResponse(responseCode = "404", description = "User or book not found in cart"),
			@ApiResponse(responseCode = "400", description = "Invalid quantity")
	})
	@PutMapping("/{userId}/cart/update")
	public ResponseEntity<List<CartItem>> updateCartItem(
			@PathVariable Long userId,
			@RequestParam String bookId,
			@RequestParam int quantity) {
		try {
			if (quantity < 1) {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
			}
			List<CartItem> updatedCart = orderService.updateCartItem(userId, bookId, quantity);
			return ResponseEntity.ok(updatedCart);
		} catch (ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
		}
	}

	@GetMapping("books/{bookId}")
	public ResponseEntity<Object> getBookById(@PathVariable("bookId") String bookId) {
		try {
			BookDTO bookDTO = orderService.getBookById(bookId);
			return new ResponseEntity<>(bookDTO, HttpStatus.OK);
		} catch (ResourceNotFoundException e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ERROR: " + e.getMessage());
		}
	}

	@GetMapping("books/available-book-ids")
	public ResponseEntity<List<String>> getAvailableBookIds() {
		// Fetch all books from the Book Service (without pagination for simplicity here)
		ResponseEntity<List<BookDTO>> response = bookClient.getAllBooks(0, Integer.MAX_VALUE);

		if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
			List<String> bookIds = response.getBody().stream()
					.map(BookDTO::getBookID)
					.collect(Collectors.toList());
			return ResponseEntity.ok(bookIds);
		} else {
			return ResponseEntity.status(response.getStatusCode()).body(null);
		}
	}

	@PostMapping("/{orderId}/cash-on-delivery/{userId}")
	public ResponseEntity<Object> cashOnDelivery(@PathVariable Long orderId, @PathVariable Long userId) {
		try {
			orderService.processCashOnDelivery(orderId, userId);
			return ResponseEntity.ok().body("Cash on Delivery order placed successfully.");
		} catch (ResourceNotFoundException ex) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
		} catch (SecurityException ex) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
		}
	}

}
