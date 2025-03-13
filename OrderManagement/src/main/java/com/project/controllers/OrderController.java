package com.project.controllers;

import java.util.List;
//import java.util.Map;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.dto.BookDTO;
import com.project.dto.CartItem;
import com.project.dto.OrderDTO;
import com.project.service.OrderServiceImpl;

@RestController
@RequestMapping("dbs/orders")
public class OrderController {
	@Autowired
	private OrderServiceImpl orderService;
	
	@GetMapping("/test1")
	public String testEndpoint() {
	    return "Order service test";
	}


	@Operation(summary="Add book to cart", description = "Adds a book to the user's cart.")
	@ApiResponses(value={
			@ApiResponse(responseCode="201",description = "Book added to cart successfully"),
			@ApiResponse(responseCode = "404",description = "User or book not found")
	})
	
	 @PostMapping("/{userId}/cart/add")
    public ResponseEntity<List<CartItem>> addToCart(
            @PathVariable Long userId,
            @RequestParam String bookId,
            @RequestParam int quantity) {
        try {
            List<CartItem> updatedCart = orderService.addToCart(userId, bookId, quantity);
            return ResponseEntity.status(HttpStatus.CREATED).body(updatedCart);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }


	@Operation(summary = "Get cart items", description = "Retrieves the items in the user's cart.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved cart items")
	})
	@GetMapping("/{userId}/cart")
	public ResponseEntity<?> getCartItems(@PathVariable Long userId){
		return ResponseEntity.ok(orderService.getCartItems(userId));
	}
	
//	@PostMapping("/{userId}")
//	public ResponseEntity<?> placeOrder(@PathVariable Long userId,@RequestBody Map<String,Integer> bookQuantities){
//		try {
//			OrderDTO order = orderService.placeOrder(userId, bookQuantities);
//			return new ResponseEntity<>(order,HttpStatus.CREATED);
//		}catch(RuntimeException e) {
//			return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
//		}
//	}

@Operation(summary = "Place order", description = "Places an order for the items in the user's cart.")
@ApiResponses(value = {
		@ApiResponse(responseCode = "201", description = "Order placed successfully"),
		@ApiResponse(responseCode = "400", description = "User not found or invalid request")
})
	@PostMapping("/{userId}")
	public ResponseEntity<?> placeOrder(@PathVariable Long userId){
		try {
			OrderDTO order = orderService.placeOrder(userId);
			return new ResponseEntity<>(order,HttpStatus.CREATED);
		}catch(RuntimeException e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.BAD_REQUEST);
		}
	}

	@Operation(summary = "Update order status", description = "Updates the status of an order.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Order status updated successfully"),
			@ApiResponse(responseCode = "404", description = "Order not found")
	})
	@PutMapping("/{orderId}/{status}")
	public ResponseEntity<?> updateOrderStatus(@PathVariable Long orderId, @PathVariable String status){
		try {
			OrderDTO order = orderService.updateOrderStatus(orderId, status);
			return new ResponseEntity<>(order,HttpStatus.OK);
		}catch(RuntimeException e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
		}
	}

	@Operation(summary = "Get user orders", description = "Retrieves all orders for a user.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved user orders"),
			@ApiResponse(responseCode = "404", description = "User orders not found")
	})
	@GetMapping("/user/{userId}")
	public ResponseEntity<List<OrderDTO>> getUserOrders(@PathVariable Long userId){
		try {
			List<OrderDTO> orders = orderService.getUserOrders(userId);
			return new ResponseEntity<>(orders,HttpStatus.OK);
		} catch(RuntimeException e) {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	@Operation(summary = "Get order details", description = "Retrieves details of a specific order.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved order details"),
			@ApiResponse(responseCode = "404", description = "Order not found")
	})
	@GetMapping("/{orderId}")
	public ResponseEntity<?> getOrderDetails(@PathVariable Long orderId){
		try {
			OrderDTO orderDTO = orderService.getOrderDetails(orderId);
			return new ResponseEntity<>(orderDTO,HttpStatus.OK);
		}catch(RuntimeException e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
		}
	}

	@Operation(summary = "Get books by order ID", description = "Retrieves all books associated with an order.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved books"),
			@ApiResponse(responseCode = "404", description = "Order or books not found")
	})
	@GetMapping("/order/{orderId}/books")
	public ResponseEntity<?> getBooksByOrderId(@PathVariable Long orderId){
		try {
			List<BookDTO> books = orderService.getBooksByOrderId(orderId);
			return new ResponseEntity<>(books,HttpStatus.OK);
		}catch(RuntimeException e) {
			return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
		}
	}
	
}
