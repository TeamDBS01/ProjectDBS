package com.project.controllers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dto.BookDTO;
import com.project.dto.CartItem;
import com.project.dto.OrderDTO;
import com.project.exception.InsufficientStockException;
import com.project.exception.ResourceNotFoundException;
import com.project.service.OrderServiceImpl;

@ExtendWith(MockitoExtension.class)
class OrderControllerTestCase {

	@Mock
	private OrderServiceImpl orderService;
	
	@InjectMocks
	private OrderController orderController;
	
	private MockMvc mockMvc;

	private ObjectMapper objectMapper;
	
	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
		objectMapper = new ObjectMapper();
	}
	
	// Add to cart Endpoint Tests
	
	@Test 
	void addToCart_success() {
		try {
			List<CartItem> cartItems =new ArrayList<>();
			cartItems.add(new CartItem("E112",2));
			when(orderService.addToCart(1L, "E112", 2)).thenReturn(cartItems);
			ResponseEntity<List<CartItem>> actual = orderController.addToCart(1L, "E112", 2);
			assertEquals(cartItems,actual.getBody());
			assertEquals(HttpStatus.CREATED,actual.getStatusCode());
		}catch(ResourceNotFoundException | InsufficientStockException e) {
			assertFalse(true,"Should not have thrown an exception");
		}
	}
	
	@Test
	void addToCart_userNotFound() {
		try {
		when(orderService.addToCart(1L, "E112", 2)).thenThrow(new ResourceNotFoundException("User not found"));
		ResponseEntity<?> actual = orderController.addToCart(1L, "E112", 2);
//		assertEquals("User not found",actual.getBody());
		assertEquals(HttpStatus.NOT_FOUND,actual.getStatusCode());
		}catch(ResourceNotFoundException | InsufficientStockException e) {
			assertFalse(true,"Should not have thrown an exception");
		}
	}

	
	@Test
	void addToCart_insufficientStock() {
		try {
		when(orderService.addToCart(1L, "E112", 2)).thenThrow(new InsufficientStockException("Insufficient stock"));
		ResponseEntity<?> actual = orderController.addToCart(1L, "E112", 2);
//		assertEquals("Insufficient stock",actual.getBody());
		assertEquals(HttpStatus.NOT_FOUND,actual.getStatusCode());
		}catch(ResourceNotFoundException | InsufficientStockException e) {
			assertFalse(true,"Should not have thrown an exception");
		}
	}
	
//  getCartItems Endpoint Test
	@Test
	void getCartItems_success() throws Exception {
		List<CartItem> cartItems = new ArrayList<>();
		cartItems.add(new CartItem("E112", 2));
		when(orderService.getCartItems(1L)).thenReturn(cartItems);
		ResponseEntity<?> actual = orderController.getCartItems(1L);
		assertEquals(HttpStatus.OK,actual.getStatusCode());
		assertEquals(objectMapper.writeValueAsString(cartItems),objectMapper.writeValueAsString(actual.getBody()));
	}
	
	@Test
	void getCartItems_emptyCart() throws Exception {
		when(orderService.getCartItems(1L)).thenReturn(new ArrayList<>());
		ResponseEntity<?> actual = orderController.getCartItems(1L);
		assertEquals("[]",objectMapper.writeValueAsString(actual.getBody()));
	}
	
	// placeOrder Endpoint tests
	@Test
	void placeOrder_success() throws Exception{
		OrderDTO orderDTO = new OrderDTO();
		when(orderService.placeOrder(1L)).thenReturn(orderDTO);
		ResponseEntity<?> actual = orderController.placeOrder(1L);
		assertEquals(HttpStatus.CREATED,actual.getStatusCode());
		assertEquals(objectMapper.writeValueAsString(orderDTO),objectMapper.writeValueAsString(actual.getBody()));
	}
	
	@Test
	void placeOrder_userNotFound() throws Exception{
		when(orderService.placeOrder(1L)).thenThrow(new ResourceNotFoundException("User not found"));
		ResponseEntity<?> actual = orderController.placeOrder(1L);
		assertEquals(HttpStatus.BAD_REQUEST,actual.getStatusCode());
		assertEquals("User not found",actual.getBody());
	}
	
	
	// update order status endpoint tests
	
	@Test
	void updateOrderStatus_success() throws Exception{
		OrderDTO orderDTO = new OrderDTO();
		when(orderService.updateOrderStatus(1L, "Shipped")).thenReturn(orderDTO);
		ResponseEntity<?> actual = orderController.updateOrderStatus(1L, "Shipped");
		assertEquals(HttpStatus.OK,actual.getStatusCode());
		assertEquals(objectMapper.writeValueAsString(orderDTO),objectMapper.writeValueAsString(actual.getBody()));
	}
	
	@Test
	void updateOrderStatus_orderNotFound(){
		when(orderService.updateOrderStatus(1L, "Shipped")).thenThrow(new ResourceNotFoundException("Order not found"));
		ResponseEntity<?> actual = orderController.updateOrderStatus(1L, "Shipped");
		assertEquals(HttpStatus.NOT_FOUND,actual.getStatusCode());
		assertEquals("Order not found",actual.getBody());
	}
	
	// getUserOrders Endpoints Tests
	@Test
	void getUserOrders_success() {
		List<OrderDTO> orderDTOs = new ArrayList<>();
		when(orderService.getUserOrders(1L)).thenReturn(orderDTOs);
		ResponseEntity<List<OrderDTO>> actual = orderController.getUserOrders(1L);
		assertEquals(HttpStatus.OK,actual.getStatusCode());
		assertEquals(orderDTOs,actual.getBody());
	}
	
	@Test 
	void getUserOrders_noOrderFound() {
		when(orderService.getUserOrders(1L)).thenReturn(new ArrayList<>());
		ResponseEntity<List<OrderDTO>> actual = orderController.getUserOrders(1L);
		assertEquals(HttpStatus.OK,actual.getStatusCode());
		assertEquals(new ArrayList<>(),actual.getBody());
	}
	
	
	// get Order Details Endpoints Tests
	@Test
	void getOrderDetails_success() throws Exception{
		OrderDTO orderDTO = new OrderDTO();
		when(orderService.getOrderDetails(1L)).thenReturn(orderDTO);
		ResponseEntity<?> actual = orderController.getOrderDetails(1L);
		assertEquals(HttpStatus.OK,actual.getStatusCode());
		assertEquals(objectMapper.writeValueAsString(orderDTO),objectMapper.writeValueAsString(actual.getBody()));
	}
	
	@Test
	void getOrderDetails_orderNotFound() {
		when(orderService.getOrderDetails(1L)).thenThrow(new ResourceNotFoundException("Order not found"));
		ResponseEntity<?> actual = orderController.getOrderDetails(1L);
		assertEquals(HttpStatus.NOT_FOUND,actual.getStatusCode());
		assertEquals("Order not found",actual.getBody());
	}
	
	
	@Test
	void getBooksByOrderId_success() throws Exception{
		List<BookDTO> bookDTOs = new ArrayList<>();
		when(orderService.getBooksByOrderId(1L)).thenReturn(bookDTOs);
		ResponseEntity<?> actual = orderController.getBooksByOrderId(1L);
		assertEquals(HttpStatus.OK,actual.getStatusCode());
		assertEquals(objectMapper.writeValueAsString(bookDTOs),objectMapper.writeValueAsString(actual.getBody()));
	}
	
	void getBooksByOrderId_orderNotFound() throws Exception{
		when(orderService.getBooksByOrderId(1L)).thenThrow(new ResourceNotFoundException("Order not found"));
		ResponseEntity<?> actual = orderController.getBooksByOrderId(1L);
		assertEquals(HttpStatus.NOT_FOUND,actual.getStatusCode());
		assertEquals("Order not found",actual.getBody());
	}
	
	@Test
	void addToCart_uri_positive() throws Exception {
		 List<CartItem> cartItems = new ArrayList<>();
	     cartItems.add(new CartItem("E112", 2));
		when(orderService.addToCart(1L, "E112", 2)).thenReturn(cartItems);
		mockMvc.perform(post("/dbs/orders/1/cart/add")
				.param("bookId", "E112")
				.param("quantity", "2")).andExpect(status().isCreated());
	}
	
	@Test
	void addToCart_uri_userNotFound() throws Exception{
		when(orderService.addToCart(1L, "E112", 2)).thenThrow(new ResourceNotFoundException("User not found"));
		mockMvc.perform(post("/dbs/orders/1/cart/add")
				.param("bookId", "E112")
				.param("quantity", "2")).andExpect(status().isNotFound());
	}
	
	@Test
	void addToCart_uri_insufficientStock() throws Exception{
		when(orderService.addToCart(1L,"E112",2)).thenThrow(new InsufficientStockException("Insufficient stock"));
		mockMvc.perform(post("/dbs/orders/1/cart/add")
				.param("bookId", "E112")
				.param("quantity", "2")).andExpect(status().isNotFound());
	}
	
	 @Test
	    void placeOrder_uri_positive() throws Exception {
	        when(orderService.placeOrder(1L)).thenReturn(new OrderDTO());
	        mockMvc.perform(post("/dbs/orders/1"))
	                .andExpect(status().isCreated());
	    }

	    @Test
	    void placeOrder_uri_userNotFound() throws Exception {
	        when(orderService.placeOrder(1L)).thenThrow(new ResourceNotFoundException("User not found"));
	        mockMvc.perform(post("/dbs/orders/1"))
	                .andExpect(status().isBadRequest());
	    }
	    
	    @Test
	    void placeOrder_uri_emptyCart() throws Exception{
	    	when(orderService.placeOrder(1L)).thenThrow(new ResourceNotFoundException("Cart is empty"));
	    	mockMvc.perform(post("/dbs/orders/1"))
	    	.andExpect(status().isBadRequest());
	    }
	    
	    @Test
	    void updateOrderStatus_uri_positive() throws Exception{
	    	when(orderService.updateOrderStatus(1L, "Shipped")).thenReturn(new OrderDTO());
	    	mockMvc.perform(put("/dbs/orders/1/Shipped")).andExpect(status().isOk());
	    }
	
}
