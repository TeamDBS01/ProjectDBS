package com.project.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.project.dto.*;
import com.project.enums.PaymentStatus;
import com.project.exception.*;
import com.project.models.CartItem;
import com.project.models.ReturnDetails;
import com.project.models.TrackingDetails;
import com.project.repositories.ReturnDetailsRepository;
import com.project.repositories.ShippingInfoRepository;
import com.project.repositories.TrackingDetailsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.project.enums.Role;
import com.project.feign.BookClient;
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


	@Mock
	private ReturnDetailsRepository returnDetailsRepository;

	@Mock
	private ShippingInfoRepository shippingInfoRepository;
	@Mock
	private TrackingDetailsRepository trackingDetailsRepository;

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

		bookDTO2 = new BookDTO();
		bookDTO2.setBookID("E113");
		bookDTO2.setTitle("MS Dhoni");
		bookDTO2.setPrice(30.0);

		order = new Order();
		order.setOrderId(1L);
		order.setUserId(1L);
		order.setStatus("Pending");
		order.setOrderDate(new Date());
		order.setTotalAmount(50.0);
		order.setPaymentStatus(PaymentStatus.PENDING);
		List<String> bookIds = new ArrayList<>();
		bookIds.add("E112:2");
		bookIds.add("E113:1");
		order.setBookIds(bookIds);
	}

	@Test
	void addToCart_success(){
		when(userClient.getUserById(1L)).thenReturn(new ResponseEntity<>(userDTO,HttpStatus.OK));
		when(bookClient.getBookById("E112")).thenReturn(bookDTO1);
		when(bookClient.getBookStockQuantity("E112")).thenReturn(10);
		List<CartItem> cartItems = orderService.addToCart(1L,"E112",2);
		assertFalse(cartItems.isEmpty());
		assertEquals(1,cartItems.size());
		assertEquals("E112",cartItems.getFirst().getBookId());
		assertEquals(2,cartItems.getFirst().getQuantity());
	}

	@Test
	void addToCart_userNotFound() {
		when(userClient.getUserById(1L)).thenReturn(new ResponseEntity<>(null,HttpStatus.NOT_FOUND));
		assertThrows(ResourceNotFoundException.class,()->orderService.addToCart(1L,"E112",1));
	}

	@Test
	void addToCart_bookNotFound() {
		when(userClient.getUserById(1L)).thenReturn(new ResponseEntity<>(userDTO,HttpStatus.OK));
		when(bookClient.getBookById("E112")).thenReturn(null);
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,()->orderService.addToCart(1L,"E112",1));
		assertEquals("Book not found for ID: E112",exception.getMessage());
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
		ShippingDetailsDTO shippingDetailsDTO = new ShippingDetailsDTO();
		orderService.addToCart(1L, "E112", 2);
		orderService.addToCart(1L, "E113", 1);

		when(bookClient.updateInventoryAfterOrder(Arrays.asList("E112", "E113"), Arrays.asList(2, 1)))
				.thenReturn(ResponseEntity.ok("Book stock updated successfully"));

		OrderDTO orderDTO = orderService.placeOrder(1L,shippingDetailsDTO);
		assertNotNull(orderDTO);
		assertEquals(70.0,orderDTO.getTotalAmount());
		verify(bookClient, times(1)).updateInventoryAfterOrder(Arrays.asList("E112", "E113"), Arrays.asList(2, 1));
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
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class,()->orderService.placeOrder(1L,new ShippingDetailsDTO()));
		assertEquals("User not found for ID: 1",exception.getMessage());
	}


	@Test
	void placeOrder_bookNotFound() {

		ResponseEntity<UserDTO> responseEntity = new ResponseEntity<>(userDTO, HttpStatus.OK);
		when(userClient.getUserById(1L)).thenReturn(responseEntity);
		when(bookClient.getBookById("E112")).thenReturn(bookDTO1);
		when(bookClient.getBookStockQuantity("E112")).thenReturn(10);
		orderService.addToCart(1L, "E112", 2);
		when(bookClient.getBookById("E112")).thenReturn(null);
		assertThrows(ResourceNotFoundException.class, () -> orderService.placeOrder(1L,new ShippingDetailsDTO()));
	}

	@Test
	void placeOrder_insufficientStock() {
		when(userClient.getUserById(1L)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
		when(bookClient.getBookById("E112")).thenReturn(bookDTO1);
		assertThrows(InsufficientStockException.class, () -> orderService.addToCart(1L, "E112", 22));
	}

	@Test
	void updateOrderStatus_success_admin() {
		UserDTO adminUser = new UserDTO();
		adminUser.setUserId(2L);
		adminUser.setRole(Role.ADMIN);
		when(userClient.getUserById(2L)).thenReturn(new ResponseEntity<>(adminUser,HttpStatus.OK));
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

		OrderDTO orderDTO = orderService.updateOrderStatus(1L, "Shipped",2L);
		assertNotNull(orderDTO);
		assertEquals("Shipped",orderDTO.getStatus());
		verify(orderRepository,times(1)).save(order);
	}

	@Test
	void updateOrderStatus_userNotFound_admin() {
		when(userClient.getUserById(2L)).thenReturn(new ResponseEntity<>(null,HttpStatus.NOT_FOUND));
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrderStatus(1L, "Shipped", 2L));
		assertEquals("Admin user not found: 2", exception.getMessage());
	}

	@Test
	void updateOrderStatus_orderNotFound() {
		UserDTO adminUser = new UserDTO();
		adminUser.setUserId(2L);
		adminUser.setRole(Role.ADMIN);
		when(userClient.getUserById(2L)).thenReturn(new ResponseEntity<>(adminUser,HttpStatus.OK));
		when(orderRepository.findById(1L)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> orderService.updateOrderStatus(1L, "Shipped", 2L));
	}

	@Test
	void getUserOrders_success() {
		List<Order> orderList = new ArrayList<>();
		orderList.add(order);
		when(orderRepository.findByUserId(1L)).thenReturn(orderList);
		List<OrderDTO> orderDTOs = orderService.getUserOrders(1L);
		assertFalse(orderDTOs.isEmpty());
		assertEquals(1,orderDTOs.size());
		assertEquals(1L,orderDTOs.getFirst().getOrderId());
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
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> orderService.getOrderDetails(1L));
		assertEquals("Order not found: 1", exception.getMessage());

	}

	@Test
	void getBooksByOrderId_success() {
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
		when(bookClient.getBookById("E112")).thenReturn(bookDTO1);
		when(bookClient.getBookById("E113")).thenReturn(bookDTO2);
		Order modifiedOrder = new Order();
		modifiedOrder.setBookIds(Arrays.asList("E112", "E113"));
		when(orderRepository.findById(1L)).thenReturn(Optional.of(modifiedOrder));
		List<BookDTO> bookDTOs = orderService.getBooksByOrderId(1L);
		assertFalse(bookDTOs.isEmpty());
		assertEquals(2,bookDTOs.size());
		assertEquals("E112",bookDTOs.getFirst().getBookID());
	}

	@Test
	void getBooksByOrderId_orderNotFound() {
		when(orderRepository.findById(1L)).thenReturn(Optional.empty());
		ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> orderService.getBooksByOrderId(1L));
		assertEquals("Order not found: 1", exception.getMessage());
	}

	@Test
	void processPayment_success(){
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
		when(userClient.getUserById(1L)).thenReturn(new ResponseEntity<>(userDTO,HttpStatus.OK));
		when(userClient.debitCredits(1L,50.0)).thenReturn(new ResponseEntity<>(new UserCreditDTO(1L,100.0),HttpStatus.OK));
		PaymentDetailsDTO paymentDetails = orderService.processPayment(1L,1L);
		assertNotNull(paymentDetails);
		assertEquals(PaymentStatus.PAID,paymentDetails.getPaymentStatus());
		verify(orderRepository,times(1)).save(any(Order.class));
	}

	@Test
	void processPayment_orderNotFound(){
		when(orderRepository.findById(1L)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class,()->orderService.processPayment(1L,1L));
	}

	@Test
	void processPayment_orderAlreadyPaid(){
		order.setPaymentStatus(PaymentStatus.PAID);
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
		assertThrows(OrderAlreadyPaidException.class,()->orderService.processPayment(1L,1L));
	}

	@Test
	void processPayment_insufficientCredits(){
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
		when(userClient.getUserById(1L)).thenReturn(new ResponseEntity<>(userDTO,HttpStatus.OK));
		when(userClient.debitCredits(1L,50.0)).thenReturn(new ResponseEntity<>(null,HttpStatus.BAD_REQUEST));
		assertThrows(InsufficientCreditsException.class,()->orderService.processPayment(1L,1L));
		assertEquals(PaymentStatus.FAILED,order.getPaymentStatus());
	}

	@Test
	void cancelOrder_paidOrder_success(){
		order.setPaymentStatus(PaymentStatus.PAID);
		order.setStatus("Pending");
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
		when(userClient.addCredits(1L,50.0)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
		when(bookClient.updateInventoryAfterOrder(Arrays.asList("E112", "E113"), Arrays.asList(-2, -1)))
				.thenReturn(ResponseEntity.ok("Book stock updated successfully"));
		OrderDTO cancelledOrder= orderService.cancelOrder(1L,1L);
		assertEquals("Cancelled",cancelledOrder.getStatus());
		verify(userClient,times(1)).addCredits(1L,50.0);
		verify(bookClient, times(1)).updateInventoryAfterOrder(Arrays.asList("E112", "E113"), Arrays.asList(-2, -1));
	}

	@Test
	void cancelOrder_unpaidOrder_success(){
		order.setPaymentStatus(PaymentStatus.PENDING);
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
		when(bookClient.updateInventoryAfterOrder(Arrays.asList("E112", "E113"), Arrays.asList(-2, -1)))
				.thenReturn(ResponseEntity.ok("Book stock updated successfully"));
		OrderDTO cancelledOrder= orderService.cancelOrder(1L,1L);
		assertEquals("Cancelled",cancelledOrder.getStatus());
		verify(userClient,times(0)).addCredits(anyLong(),anyDouble());
		verify(bookClient, times(1)).updateInventoryAfterOrder(Arrays.asList("E112", "E113"), Arrays.asList(-2, -1));
	}

	@Test
	void cancelOrder_orderNotFound(){
		when(orderRepository.findById(1L)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class,()->orderService.cancelOrder(1L,1L));
		verify(userClient,times(0)).addCredits(anyLong(),anyDouble());
		verify(bookClient, times(0)).updateInventoryAfterOrder(Arrays.asList("E112", "E113"), Arrays.asList(2, 1));
	}

	@Test
	void clearCartItem_success() {
		when(userClient.getUserById(1L)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
		when(bookClient.getBookById("E112")).thenReturn(bookDTO1);
		when(bookClient.getBookStockQuantity("E112")).thenReturn(10);
		orderService.addToCart(1L, "E112", 2);
		orderService.clearCartItem(1L, "E112");
		assertTrue(orderService.getCartItems(1L).isEmpty());
	}

	@Test
	void placeOrder_cartEmpty() {
		when(userClient.getUserById(1L)).thenReturn(new ResponseEntity<>(userDTO, HttpStatus.OK));
		assertThrows(CartEmptyException.class, () -> orderService.placeOrder(1L,new ShippingDetailsDTO()));
	}
	@Test
	void processPayment_userNotFound() {
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
		when(userClient.getUserById(1L)).thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
		assertThrows(ResourceNotFoundException.class, () -> orderService.processPayment(1L, 1L));
	}

	@Test
	void processPayment_securityException() {
		Order differentOrder = new Order();
		differentOrder.setUserId(2L);
		when(orderRepository.findById(1L)).thenReturn(Optional.of(differentOrder));
		assertThrows(SecurityException.class, () -> orderService.processPayment(1L, 1L));
	}

	@Test
	void updateTracking_success() throws ParseException {
		UserDTO adminUser = new UserDTO();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = dateFormat.parse("2025-05-30");
		adminUser.setUserId(2L);
		adminUser.setRole(Role.ADMIN);
		when(userClient.getUserById(2L)).thenReturn(new ResponseEntity<>(adminUser, HttpStatus.OK));
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
		TrackingDetails trackingDetails = new TrackingDetails();
		trackingDetails.setTrackingId(10L);
		trackingDetails.setOrderId(1L);
		trackingDetails.setShippingCarrier("UPS");
		when(trackingDetailsRepository.findByOrderId(1L)).thenReturn(trackingDetails);
		OrderDTO updatedOrder = orderService.updateTracking(1L, "UPS", date, 2L);
		assertNotNull(updatedOrder.getTrackingDetails());
		assertEquals("UPS", updatedOrder.getTrackingDetails().getShippingCarrier());
		verify(trackingDetailsRepository, times(1)).save(any(TrackingDetails.class));
	}

	@Test
	void updateTracking_adminNotFound() {
		when(userClient.getUserById(2L)).thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));
		assertThrows(ResourceNotFoundException.class, () -> orderService.updateTracking(1L, "UPS", new Date(), 2L));
	}

	@Test
	void updateTracking_orderNotFound() throws ParseException {
		UserDTO adminUser = new UserDTO();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date = dateFormat.parse("2025-05-30");
		adminUser.setUserId(2L);
		adminUser.setRole(Role.ADMIN);
		when(userClient.getUserById(2L)).thenReturn(new ResponseEntity<>(adminUser, HttpStatus.OK));
		when(orderRepository.findById(1L)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> orderService.updateTracking(1L, "UPS",date, 2L));
	}


	@Test
	void requestReturn_success() {
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
		when(returnDetailsRepository.findByOrderId(1L)).thenReturn(null);
		order.setStatus("Delivered");
		OrderDTO returnedOrder = orderService.requestReturn(1L, 1L, "Damaged");
		assertEquals("Return Requested", returnedOrder.getStatus());
		verify(returnDetailsRepository, times(1)).save(any(ReturnDetails.class));
	}

	@Test
	void requestReturn_orderNotFound() {
		when(orderRepository.findById(1L)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> orderService.requestReturn(1L, 1L, "Damaged"));
	}

	@Test
	void adminProcessReturn_approve_success() {
		UserDTO adminUser = new UserDTO();
		adminUser.setUserId(2L);
		adminUser.setRole(Role.ADMIN);
		when(userClient.getUserById(2L)).thenReturn(new ResponseEntity<>(adminUser, HttpStatus.OK));
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
		when(returnDetailsRepository.findByOrderId(1L)).thenReturn(new ReturnDetails());
		order.setStatus("Return Requested");
		order.setPaymentStatus(PaymentStatus.PAID);
		when(userClient.addCredits(1L, 50.0)).thenReturn(new ResponseEntity<>(HttpStatus.OK));
		when(bookClient.updateInventoryAfterOrder(Arrays.asList("E112", "E113"), Arrays.asList(-2, -1)))
				.thenReturn(ResponseEntity.ok("Book stock updated successfully"));
		OrderDTO processedOrder = orderService.adminProcessReturn(1L, 2L, "approve");
		assertEquals("Return Approved", processedOrder.getStatus());
		verify(returnDetailsRepository, times(2)).save(any(ReturnDetails.class));
	}

	@Test
	void adminProcessReturn_reject_success() {
		UserDTO adminUser = new UserDTO();
		adminUser.setUserId(2L);
		adminUser.setRole(Role.ADMIN);
		when(userClient.getUserById(2L)).thenReturn(new ResponseEntity<>(adminUser, HttpStatus.OK));
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
		when(returnDetailsRepository.findByOrderId(1L)).thenReturn(new ReturnDetails());
		order.setStatus("Return Requested");
		OrderDTO processedOrder = orderService.adminProcessReturn(1L, 2L, "reject");
		assertEquals("Return Rejected", processedOrder.getStatus());
		verify(returnDetailsRepository, times(2)).save(any(ReturnDetails.class));
	}

	@Test
	void adminProcessReturn_orderNotFound() {
		when(orderRepository.findById(1L)).thenReturn(Optional.empty());
		assertThrows(ResourceNotFoundException.class, () -> orderService.adminProcessReturn(1L, 2L, "approve"));
	}

	@Test
	void adminProcessReturn_adminUserNotFound() {
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
		when(userClient.getUserById(2L)).thenReturn(new ResponseEntity<>(null, HttpStatus.NOT_FOUND));

		assertThrows(ResourceNotFoundException.class, () -> orderService.adminProcessReturn(1L, 2L, "approve"));
	}

	@Test
	void adminProcessReturn_userNotAdmin() {
		UserDTO nonAdminUser = new UserDTO();
		nonAdminUser.setUserId(2L);
		nonAdminUser.setRole(Role.CUSTOMER);
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
		when(userClient.getUserById(2L)).thenReturn(new ResponseEntity<>(nonAdminUser, HttpStatus.OK));

		assertThrows(SecurityException.class, () -> orderService.adminProcessReturn(1L, 2L, "approve"));
	}

}

