package com.project.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.dto.BookDTO;
import com.project.dto.CartItem;
import com.project.dto.CartItem;
import com.project.dto.OrderDTO;
import com.project.dto.UserDTO;
import com.project.exception.InsufficientStockException;
import com.project.exception.ResourceNotFoundException;
import com.project.feign.BookClient;
//import com.project.feign.InventoryClient;
import com.project.feign.UserClient;
import com.project.models.Order;
import com.project.repositories.OrderRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTestCase {

	@Mock
	private OrderRepository orderRepository;
	
	@Mock
	private UserClient userClient;
	
	@Mock
	private BookClient bookClient;
	
//	@Mock
//	private InventoryClient inventoryClient;
	
	@InjectMocks
	private OrderServiceImpl orderService;
	
	private UserDTO userDTO;
	private BookDTO bookDTO1,bookDTO2;
	private Order order;

	@BeforeEach
	void setUp() {
		userDTO = new UserDTO();
		userDTO.setUserId(1L);
		userDTO.setName("Daya");
		
		bookDTO1 = new BookDTO();
		bookDTO1.setBookID("E112");
		bookDTO1.setTitle("Wings of Fire");
		bookDTO1.setPrice(20.0);
		bookDTO1.setStockQuantity(10);
		
		bookDTO2 = new BookDTO();
		bookDTO2.setBookID("E113");
		bookDTO2.setTitle("MS Dhoni");
		bookDTO2.setPrice(30.0);
		bookDTO2.setStockQuantity(5);
		
		order = new Order();
		order.setOrderId(1L);
		order.setUserId(1L);
		order.setStatus("Pending");
		order.setOrderDate(new Date());
		order.setTotalAmount(50.0);
		List<String> bookIds = new ArrayList<>();
		bookIds.add("E112");
		bookIds.add("E113");
		order.setBookIds(bookIds);
	}
	
	@Test
	void addToCart_userNotFound() {
		when(userClient.getUserById(1L)).thenReturn(new ResponseEntity<>(null,HttpStatus.NOT_FOUND));
		try {
			orderService.addToCart(1L, "E112", 1);
			assertTrue(false,"Expected ResourceNotFoundException ");
		}catch(ResourceNotFoundException e) {
			assertTrue(true);
		}
	}
	
	@Test
	void addToCart_bookNotFound() {
		when(userClient.getUserById(1L)).thenReturn(new ResponseEntity<>(userDTO,HttpStatus.OK));
		when(bookClient.getBookById("E112")).thenReturn(null);
		try {
			orderService.addToCart(1L, "E112", 1);
			assertTrue(false,"Expected ResourceNotFoundException");
		}catch(ResourceNotFoundException e) {
			assertEquals("Book not found for ID: E112",e.getMessage());
		}
	}
	
	@Test
	void getCartItems_emptyCart() {
		List<CartItem> cartItems = orderService.getCartItems(1L);
		assertTrue(cartItems.isEmpty());
	}
	
	@Test
	void clearCart_success() {
		when(userClient.getUserById(1L)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
		when(bookClient.getBookById("E112")).thenReturn(bookDTO1);
		when(bookClient.getBookStockQuantity("E112")).thenReturn(10);
		orderService.addToCart(1L, "E112", 1);
		orderService.clearCart(1L);
		List<CartItem> cartItems = orderService.getCartItems(1L);
		assertTrue(cartItems.isEmpty());
	}
	
	@Test
	void placeOrder_success() {
		ResponseEntity<UserDTO> responseEntity = new ResponseEntity<>(userDTO, HttpStatus.OK);
		when(userClient.getUserById(1L)).thenReturn(responseEntity);
		when(bookClient.getBookById("E112")).thenReturn(bookDTO1);
		when(bookClient.getBookById("E113")).thenReturn(bookDTO2);
		when(bookClient.getBookStockQuantity("E112")).thenReturn(10);
		when(bookClient.getBookStockQuantity("E113")).thenReturn(5);
		orderService.addToCart(1L, "E112", 2);
		orderService.addToCart(1L, "E113", 1);
		when(bookClient.updateBookStock("E112", 2)).thenReturn(ResponseEntity.ok("Book stock updated successfully"));
		when(bookClient.updateBookStock("E113", 1)).thenReturn(ResponseEntity.ok("Book stock updated successfully"));
		OrderDTO orderDTO = orderService.placeOrder(1L);
		assertNotNull(orderDTO);
		assertEquals(70.0,orderDTO.getTotalAmount());
		verify(bookClient,times(1)).updateBookStock("E112",2);
		verify(bookClient,times(1)).updateBookStock("E113",1);
		verify(orderRepository,times(1)).save(any(Order.class));
	}
	
	@Test
	void placeOrder_userNotFound() {
		when(userClient.getUserById(1L)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
		when(bookClient.getBookById("E112")).thenReturn(bookDTO1);
		when(bookClient.getBookStockQuantity("E112")).thenReturn(10);
		orderService.addToCart(1L, "E112", 2);
		ResponseEntity<UserDTO> responseEntity = new ResponseEntity<>(null,HttpStatus.NOT_FOUND);
		when(userClient.getUserById(1L)).thenReturn(responseEntity);
		
		try {
			orderService.placeOrder(1L);
			assertTrue(false,"Expected ResourceNotFoundException to be thrown");
		}catch(ResourceNotFoundException e) {
			assertEquals("User not found for ID: 1",e.getMessage());
		}
	}
	
	
	@Test
	void placeOrder_bookNotFound() {
		
		ResponseEntity<UserDTO> responseEntity = new ResponseEntity<>(userDTO, HttpStatus.OK);
		when(userClient.getUserById(1L)).thenReturn(responseEntity);
		when(bookClient.getBookById("E112")).thenReturn(bookDTO1);
		when(bookClient.getBookStockQuantity("E112")).thenReturn(10);
		orderService.addToCart(1L, "E112", 2);
		when(bookClient.getBookById("E112")).thenReturn(null);
		try {
			orderService.placeOrder(1L);
			assertTrue(false,"Expected ResourceNotFoundException to be thrown");
		}catch(ResourceNotFoundException e) {
			assertTrue(true);
		}
	}
	
	@Test
	void placeOrder_insufficientStock() {
		bookDTO1.setStockQuantity(10);
		when(userClient.getUserById(1L)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
		when(bookClient.getBookById("E112")).thenReturn(bookDTO1);
		try {
			orderService.addToCart(1L, "E112", 22);
			assertTrue(false,"Expected InsufficientStockException to be thrown");
		}catch(InsufficientStockException e) {
			assertTrue(true); 
		}
	}
	
	@Test
	void updateOrderStatus_success() {
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
		OrderDTO orderDTO = orderService.updateOrderStatus(1L, "Shipped");
		assertNotNull(orderDTO);
		assertEquals("Shipped",orderDTO.getStatus());
		verify(orderRepository,times(1)).save(order);
	}
	
	@Test
	void updateOrderStatus_orderNotFound() {
		when(orderRepository.findById(1L)).thenReturn(Optional.empty());
		try {
			orderService.updateOrderStatus(1L, "Shipped");
			assertTrue(false);
		}catch(ResourceNotFoundException e) {
			assertTrue(true);
		}
	}
	
	@Test
	void getUserOrders_success() {
		List<Order> orderList = new ArrayList<>();
		orderList.add(order);
		when(orderRepository.findByUserId(1L)).thenReturn(orderList);
		List<OrderDTO> orderDTOs = orderService.getUserOrders(1L);
		assertFalse(orderDTOs.isEmpty());
		assertEquals(1,orderDTOs.size());
		assertEquals(1L,orderDTOs.get(0).getOrderId());
	}
	
	@Test
	void getOrderDetails_success() {
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
		OrderDTO orderDTO = orderService.getOrderDetails(1L);
		assertNotNull(orderDTO);
		assertEquals(1L,orderDTO.getOrderId());
	}
	
	@Test
	void getOrderDetails_orderNotFound() {
		when(orderRepository.findById(1L)).thenReturn(Optional.empty());
		try {
			orderService.getOrderDetails(1L);
			assertTrue(false);
		}catch(ResourceNotFoundException e) {
			assertEquals("Order not found: 1",e.getMessage());
		}
	}
	
	@Test
	void getBooksByOrderId_success() {
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
		when(bookClient.getBookById("E112")).thenReturn(bookDTO1);
		when(bookClient.getBookById("E113")).thenReturn(bookDTO2);
		List<BookDTO> bookDTOs = orderService.getBooksByOrderId(1L);
		assertFalse(bookDTOs.isEmpty());
		assertEquals(2,bookDTOs.size());
		assertEquals("E112",bookDTOs.get(0).getBookID());
	}
	
	@Test
	void getBooksByOrderId_orderNotFound() {
		when(orderRepository.findById(1L)).thenReturn(Optional.empty());
		try {
			orderService.getBooksByOrderId(1L);
			assertTrue(false);
		}catch(ResourceNotFoundException e) {
			assertEquals("Order not found: 1",e.getMessage());
		}
	}
	
}

