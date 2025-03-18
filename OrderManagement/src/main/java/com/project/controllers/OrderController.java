package com.project.controllers;


import java.util.List;
import com.project.dto.*;
import com.project.exception.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.project.service.OrderServiceImpl;

@RestController
@RequestMapping("dbs/order")
public class OrderController {

	private final OrderServiceImpl orderService;

	@Autowired
	public OrderController(OrderServiceImpl orderService){
		this.orderService = orderService;
	}

	@Operation(summary="Add book to cart", description = "Adds a book to the user's cart.")
	@ApiResponses(value={
			@ApiResponse(responseCode="201",description = "Book added to cart successfully"),
			@ApiResponse(responseCode = "404",description = "User or book not found")
	})
	 @PostMapping("/{userId}/cart/add")
    public ResponseEntity<Object> addToCart(
            @PathVariable Long userId,
            @RequestParam String bookId,
            @RequestParam int quantity) {
        try {
            List<CartItem> updatedCart = orderService.addToCart(userId, bookId, quantity);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedCart);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }catch(InsufficientStockException e){
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
		}
    }


	@Operation(summary = "Get cart items", description = "Retrieves the items in the user's cart.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved cart items")
	})
	@GetMapping("/{userId}/cart")
	public ResponseEntity<List<CartItem>> getCartItems(@PathVariable Long userId){
		return ResponseEntity.ok(orderService.getCartItems(userId));
	}


	@Operation(summary = "Clear cart item", description = "Clears a specific item from the user's cart.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200",description = "Cart item cleared succesfully"),
			@ApiResponse(responseCode = "404",description = "User or book not found")
	})
	@DeleteMapping("/{userId}/cart/clear")
	public ResponseEntity<Object> clearCartItem(@PathVariable Long userId, @RequestParam String bookId){
		try{
			orderService.clearCartItem(userId,bookId);
			return ResponseEntity.ok("Cart Item cleared successfully");
		}catch(ResourceNotFoundException e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
		}
	}

@Operation(summary = "Place order", description = "Places an order for the items in the user's cart.")
@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Order placed successfully"),
		@ApiResponse(responseCode = "400", description = "User not found or invalid request")
})
	@PostMapping("/{userId}")
	public ResponseEntity<Object> placeOrder(@PathVariable Long userId){
		try {
			OrderDTO order = orderService.placeOrder(userId);
			return new ResponseEntity<>(order,HttpStatus.CREATED);
		}catch(ResourceNotFoundException | CartEmptyException | InsufficientStockException e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(summary = "Update order status", description = "Updates the status of an order.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Order status updated successfully"),
			@ApiResponse(responseCode = "404", description = "Order not found or admin user not found"),
			@ApiResponse(responseCode = "403",description = "Forbidden: User does not have permission")
	})

	@PutMapping("/{orderId}/{status}/{adminUserId}")
	public ResponseEntity<Object> updateOrderStatus(@PathVariable Long orderId,
			@PathVariable String status,
			@PathVariable Long adminUserId){
		try {
			OrderDTO order = orderService.updateOrderStatus(orderId, status,adminUserId);
			return new ResponseEntity<>(order,HttpStatus.OK);
		}catch( ResourceNotFoundException | SecurityException e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
		}
	}


	@Operation(summary = "Get user orders", description = "Retrieves all orders for a user.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved user orders"),
			@ApiResponse(responseCode = "404", description = "User orders not found")
	})
	@GetMapping("/user/{userId}")
	public ResponseEntity<Object> getUserOrders(@PathVariable Long userId){
		try {
			List<OrderDTO> orders = orderService.getUserOrders(userId);
			return new ResponseEntity<>(orders,HttpStatus.OK);
		} catch(ResourceNotFoundException e) {
			return new ResponseEntity<>("User orders not found.", HttpStatus.NOT_FOUND);
		}
	}

	@Operation(summary = "Get order details", description = "Retrieves details of a specific order.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved order details"),
			@ApiResponse(responseCode = "404", description = "Order not found")
	})
	@GetMapping("/{orderId}")
	public ResponseEntity<Object> getOrderDetails(@PathVariable Long orderId){
		try {
			OrderDTO orderDTO = orderService.getOrderDetails(orderId);
			return new ResponseEntity<>(orderDTO,HttpStatus.OK);
		}catch(ResourceNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
		}
	}


	@Operation(summary = "Get books by order ID", description = "Retrieves all books associated with an order.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved books"),
			@ApiResponse(responseCode = "404", description = "Order or books not found")
	})
	@GetMapping("/orders/{orderId}/books")
	public ResponseEntity<Object> getBooksByOrderId(@PathVariable Long orderId){
		try {
			List<BookDTO> books = orderService.getBooksByOrderId(orderId);
			return new ResponseEntity<>(books,HttpStatus.OK);
		}catch(ResourceNotFoundException e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
		}

	}


	@Operation(summary = "Process payment for an order", description = "Processes payment for a specific order.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Payment processed successfully"),
			@ApiResponse(responseCode = "404", description = "Order or user not found"),
			@ApiResponse(responseCode = "400", description = "Order already paid or insufficient credits")
	})
	@PostMapping("/{orderId}/payment/{userId}")
	public ResponseEntity<Object> processPayment(@PathVariable Long orderId, @PathVariable Long userId){
		try{
			PaymentDetailsDTO paymentDetails = orderService.processPayment(orderId,userId);
			return new ResponseEntity<>(paymentDetails,HttpStatus.OK);
		}catch(ResourceNotFoundException e){
			return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
		}catch(OrderAlreadyPaidException | InsufficientCreditsException | SecurityException e){
			return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}


	@Operation(summary = "Cancel an order", description = "Cancels a specific order.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Order cancelled successfully"),
			@ApiResponse(responseCode = "404", description = "Order not found"),
			@ApiResponse(responseCode = "403", description = "Forbidden: User not authorized"),
			@ApiResponse(responseCode = "400", description = "Bad request: Order already paid or other cancellation error")
	})
	@PutMapping("/{orderId}/cancel/{userId}")
	public ResponseEntity<Object> cancelOrder(@PathVariable Long orderId,@PathVariable Long userId){
		try{
			OrderDTO cancelledOrder = orderService.cancelOrder(orderId,userId);
			return new ResponseEntity<>(cancelledOrder,HttpStatus.OK);
		}catch(ResourceNotFoundException  e){
			return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
		}catch(SecurityException e){
			return new ResponseEntity<>(e.getMessage(),HttpStatus.FORBIDDEN);
		}catch(Exception e){
			return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}


}
