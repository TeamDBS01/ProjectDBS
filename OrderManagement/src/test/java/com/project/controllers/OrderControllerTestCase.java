package com.project.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dto.*;
import com.project.exception.CartEmptyException;
import com.project.exception.InsufficientCreditsException;
import com.project.exception.InsufficientStockException;
import com.project.exception.ResourceNotFoundException;
import com.project.feign.UserClient;
import com.project.service.OrderServiceImpl;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderControllerTestCase {

	@Mock
	private OrderServiceImpl orderService;

	@Mock
	private UserClient userClient;
	
	@InjectMocks
	private OrderController orderController;
	
	private MockMvc mockMvc;

	private ObjectMapper objectMapper;
	
	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(orderController).build();
		objectMapper = new ObjectMapper();
	}

	
	@Test 
	void addToCart_success() {
		try {
			List<CartItem> cartItems =new ArrayList<>();
			cartItems.add(new CartItem("E112",2));
			when(orderService.addToCart(1L, "E112", 2)).thenReturn(cartItems);
			ResponseEntity<?> actual = orderController.addToCart(1L, "E112", 2);
			assertEquals(cartItems,actual.getBody());
			assertEquals(HttpStatus.CREATED,actual.getStatusCode());
		}catch(ResourceNotFoundException | InsufficientStockException e) {
			fail("Should not have thrown an exception");
		}
	}
	
	@Test
	void addToCart_userNotFound() {
		try {
		when(orderService.addToCart(1L, "E112", 2)).thenThrow(new ResourceNotFoundException("User not found"));
		ResponseEntity<Object> actual = orderController.addToCart(1L, "E112", 2);
		assertEquals("User not found",actual.getBody());
		assertEquals(HttpStatus.NOT_FOUND,actual.getStatusCode());
		}catch(ResourceNotFoundException | InsufficientStockException e) {
			fail("Should not have thrown an exception");
		}
	}

	
	@Test
	void addToCart_insufficientStock() {
		try {
		when(orderService.addToCart(1L, "E112", 2)).thenThrow(new InsufficientStockException("Insufficient stock"));
		ResponseEntity<Object> actual = orderController.addToCart(1L, "E112", 2);
		assertEquals("Insufficient stock",actual.getBody());
		assertEquals(HttpStatus.BAD_REQUEST,actual.getStatusCode());
		}catch(ResourceNotFoundException | InsufficientStockException e) {
			fail("Should not have thrown an exception");
		}
	}

	@Test
	void getCartItems_success() throws Exception {
		List<CartItem> cartItems = new ArrayList<>();
		cartItems.add(new CartItem("E112", 2));
		when(orderService.getCartItems(1L)).thenReturn(cartItems);
		ResponseEntity<List<CartItem>> actual = orderController.getCartItems(1L);
		assertEquals(HttpStatus.OK,actual.getStatusCode());
		assertEquals(objectMapper.writeValueAsString(cartItems),objectMapper.writeValueAsString(actual.getBody()));
	}
	
	@Test
	void getCartItems_emptyCart() throws Exception {
		when(orderService.getCartItems(1L)).thenReturn(new ArrayList<>());
		ResponseEntity<List<CartItem>> actual = orderController.getCartItems(1L);
		assertEquals("[]",objectMapper.writeValueAsString(actual.getBody()));
	}

	@Test
	void placeOrder_success() throws Exception{
		OrderDTO orderDTO = new OrderDTO();
		when(orderService.placeOrder(1L)).thenReturn(orderDTO);
		ResponseEntity<Object> actual = orderController.placeOrder(1L);
		assertEquals(HttpStatus.CREATED,actual.getStatusCode());
		assertEquals(objectMapper.writeValueAsString(orderDTO),objectMapper.writeValueAsString(actual.getBody()));
	}
	
	@Test
	void placeOrder_userNotFound(){
		when(orderService.placeOrder(1L)).thenThrow(new ResourceNotFoundException("User not found"));
		ResponseEntity<Object> actual = orderController.placeOrder(1L);
		assertEquals(HttpStatus.BAD_REQUEST,actual.getStatusCode());
		assertEquals("User not found",actual.getBody());
	}

	@Test
	void placeOrder_cartEmpty(){
		when(orderService.placeOrder(1L)).thenThrow(new CartEmptyException("Cart is empty"));
		ResponseEntity<Object> actual = orderController.placeOrder(1L);
		assertEquals(HttpStatus.BAD_REQUEST,actual.getStatusCode());
		assertEquals("Cart is empty",actual.getBody());
	}

	@Test
	void clearCartItem_success(){
		Long userId = 1L;
		String bookId = "E112";
		UserDTO userDTO = new UserDTO();
		ResponseEntity<UserDTO> userResponse = new ResponseEntity<>(userDTO, HttpStatus.OK);
		when(userClient.getUserById(userId)).thenReturn(userResponse);
		doNothing().when(orderService).clearCartItem(userId, bookId);
		ResponseEntity<Object> actual = orderController.clearCartItem(userId, bookId);
		assertEquals(HttpStatus.OK, actual.getStatusCode());
		assertEquals("Cart Item cleared successfully", actual.getBody());
		verify(orderService).clearCartItem(userId, bookId);
	}



	@Test
	void updateOrderStatus_success() throws Exception{
		OrderDTO orderDTO = new OrderDTO();
		when(orderService.updateOrderStatus(1L, "Shipped",2L)).thenReturn(orderDTO);
		ResponseEntity<Object> actual = orderController.updateOrderStatus(1L, "Shipped",2L);
		assertEquals(HttpStatus.OK,actual.getStatusCode());
		assertEquals(objectMapper.writeValueAsString(orderDTO),objectMapper.writeValueAsString(actual.getBody()));
	}
	
	@Test
	void updateOrderStatus_orderNotFound(){
		when(orderService.updateOrderStatus(1L, "Shipped",2L)).thenThrow(new ResourceNotFoundException("Order not found"));
		ResponseEntity<Object> actual = orderController.updateOrderStatus(1L, "Shipped",2L);
		assertEquals(HttpStatus.NOT_FOUND,actual.getStatusCode());
		assertEquals("Order not found",actual.getBody());
	}

	@Test
	void getUserOrders_success() {
		List<OrderDTO> orderDTOs = new ArrayList<>();
		when(orderService.getUserOrders(1L)).thenReturn(orderDTOs);
		ResponseEntity<Object> actual = orderController.getUserOrders(1L);
		assertEquals(HttpStatus.OK,actual.getStatusCode());
		assertEquals(orderDTOs,actual.getBody());
	}
	
	@Test 
	void getUserOrders_noOrderFound() {
		when(orderService.getUserOrders(1L)).thenReturn(new ArrayList<>());
		ResponseEntity<Object> actual = orderController.getUserOrders(1L);
		assertEquals(HttpStatus.OK,actual.getStatusCode());
		assertEquals(new ArrayList<>(),actual.getBody());
	}

	@Test
	void getOrderDetails_success() throws Exception{
		OrderDTO orderDTO = new OrderDTO();
		when(orderService.getOrderDetails(1L)).thenReturn(orderDTO);
		ResponseEntity<Object> actual = orderController.getOrderDetails(1L);
		assertEquals(HttpStatus.OK,actual.getStatusCode());
		assertEquals(objectMapper.writeValueAsString(orderDTO),objectMapper.writeValueAsString(actual.getBody()));
	}
	
	@Test
	void getOrderDetails_orderNotFound() {
		when(orderService.getOrderDetails(1L)).thenThrow(new ResourceNotFoundException("Order not found"));
		ResponseEntity<Object> actual = orderController.getOrderDetails(1L);
		assertEquals(HttpStatus.NOT_FOUND,actual.getStatusCode());
		assertEquals("Order not found",actual.getBody());
	}
	
	
	@Test
	void getBooksByOrderId_success() throws Exception{
		List<BookDTO> bookDTOs = new ArrayList<>();
		when(orderService.getBooksByOrderId(1L)).thenReturn(bookDTOs);
		ResponseEntity<Object> actual = orderController.getBooksByOrderId(1L);
		assertEquals(HttpStatus.OK,actual.getStatusCode());
		assertEquals(objectMapper.writeValueAsString(bookDTOs),objectMapper.writeValueAsString(actual.getBody()));
	}
	
	void getBooksByOrderId_orderNotFound(){
		when(orderService.getBooksByOrderId(1L)).thenThrow(new ResourceNotFoundException("Order not found"));
		ResponseEntity<Object> actual = orderController.getBooksByOrderId(1L);
		assertEquals(HttpStatus.NOT_FOUND,actual.getStatusCode());
		assertEquals("Order not found",actual.getBody());
	}
	
	@Test
	void addToCart_uri_positive() throws Exception {
		 List<CartItem> cartItems = new ArrayList<>();
	     cartItems.add(new CartItem("E112", 2));
		when(orderService.addToCart(1L, "E112", 2)).thenReturn(cartItems);
		mockMvc.perform(post("/dbs/order/1/cart/add")
				.param("bookId", "E112")
				.param("quantity", "2")).andExpect(status().isCreated());
	}
	
	@Test
	void addToCart_uri_userNotFound() throws Exception{
		when(orderService.addToCart(1L, "E112", 2)).thenThrow(new ResourceNotFoundException("User not found"));
		mockMvc.perform(post("/dbs/order/1/cart/add")
				.param("bookId", "E112")
				.param("quantity", "2")).andExpect(status().isNotFound());
	}
	
	@Test
	void addToCart_uri_insufficientStock() throws Exception{
		when(orderService.addToCart(1L,"E112",2)).thenThrow(new InsufficientStockException("Insufficient stock"));
		mockMvc.perform(post("/dbs/order/1/cart/add")
				.param("bookId", "E112")
				.param("quantity", "2")).andExpect(status().isBadRequest());
	}

	@Test
	void clearCartItem_uri_positive() throws Exception {
		Long userId = 1L;
		String bookId = "E112";
		UserDTO userDTO = new UserDTO();
		ResponseEntity<UserDTO> userResponse = new ResponseEntity<>(userDTO, HttpStatus.OK);
		when(userClient.getUserById(userId)).thenReturn(userResponse);
		mockMvc.perform(delete("/dbs/order/1/cart/clear")
						.param("bookId", bookId))
				.andExpect(status().isOk());
	}
	
	 @Test
	    void placeOrder_uri_positive() throws Exception {
	        when(orderService.placeOrder(1L)).thenReturn(new OrderDTO());
	        mockMvc.perform(post("/dbs/order/1"))
	                .andExpect(status().isCreated());
	    }

	    @Test
	    void placeOrder_uri_userNotFound() throws Exception {
	        when(orderService.placeOrder(1L)).thenThrow(new ResourceNotFoundException("User not found"));
	        mockMvc.perform(post("/dbs/order/1"))
	                .andExpect(status().isBadRequest());
	    }
	    
	    @Test
	    void placeOrder_uri_emptyCart() throws Exception{
	    	when(orderService.placeOrder(1L)).thenThrow(new ResourceNotFoundException("Cart is empty"));
	    	mockMvc.perform(post("/dbs/order/1"))
	    	.andExpect(status().isBadRequest());
	    }
	    
	    @Test
	    void updateOrderStatus_uri_positive() throws Exception{
	    	when(orderService.updateOrderStatus(1L, "Shipped",2L)).thenReturn(new OrderDTO());
	    	mockMvc.perform(put("/dbs/order/1/Shipped/2")).andExpect(status().isOk());
	    }

		@Test
	void processPayment_success() throws Exception {
		PaymentDetailsDTO paymentDetails = new PaymentDetailsDTO();
		when(orderService.processPayment(1L,1L)).thenReturn(paymentDetails);
		mockMvc.perform(post("/dbs/order/1/payment/1"))
				.andExpect(status().isOk());
	}

	@Test
	void processPayment_orderNotFound() throws Exception{
		when(orderService.processPayment(1L,1L)).thenThrow(new ResourceNotFoundException("Order not found"));
		mockMvc.perform(post("/dbs/order/1/payment/1"))
				.andExpect(status().isNotFound());
	}

	@Test
	void processPayment_userNotFound() throws Exception {
		when(orderService.processPayment(1L, 1L)).thenThrow(new ResourceNotFoundException("User not found"));
		mockMvc.perform(post("/dbs/order/1/payment/1"))
				.andExpect(status().isNotFound());
	}

	@Test
	void processPayment_insufficientCredits() throws Exception {
		when(orderService.processPayment(1L, 1L)).thenThrow(new InsufficientCreditsException("Insufficient credits"));
		mockMvc.perform(post("/dbs/order/1/payment/1"))
				.andExpect(status().isBadRequest());
	}


	@Test
	void cancelOrder_success() throws Exception{
		OrderDTO orderDTO = new OrderDTO();
		when(orderService.cancelOrder(1L,1L)).thenReturn(orderDTO);
		mockMvc.perform(put("/dbs/order/1/cancel/1")).andExpect(status().isOk());
	}

	@Test
	void cancelOrder_orderNotFound() throws Exception{
		when(orderService.cancelOrder(1L,1L)).thenThrow(new ResourceNotFoundException("Order not found"));
		mockMvc.perform(put("/dbs/order/1/cancel/1")).andExpect(status().isNotFound());
	}

	@Test
	void cancelOrder_forbidden() throws  Exception{
		when(orderService.cancelOrder(1L,1L)).thenThrow(new SecurityException("Forbidden"));
		mockMvc.perform(put("/dbs/order/1/cancel/1")).andExpect(status().isForbidden());
	}

	@Test
	 void clearCartItem_ValidUser_ValidBookId() {
		Long userId = 1L;
		String bookId = "BOOK123";
		UserDTO userDTO = new UserDTO();
		ResponseEntity<UserDTO> userResponse = new ResponseEntity<>(userDTO, HttpStatus.OK);
		when(userClient.getUserById(userId)).thenReturn(userResponse);
		ResponseEntity<Object> response = orderController.clearCartItem(userId, bookId);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertEquals("Cart Item cleared successfully", response.getBody());
		verify(orderService, times(1)).clearCartItem(userId, bookId);
	}


	@Test
	 void clearCartItem_InvalidUser() {
		Long userId = 1L;
		String bookId = "BOOK123";
		ResponseEntity<UserDTO> userResponse = new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
		when(userClient.getUserById(userId)).thenReturn(userResponse);
		ResponseEntity<Object> response = orderController.clearCartItem(userId, bookId);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
		assertEquals("User not found.", response.getBody());
	}


}

