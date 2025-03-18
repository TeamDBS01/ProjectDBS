package com.project.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.project.dto.*;
import com.project.enums.PaymentStatus;
import com.project.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.enums.Role;
import com.project.feign.BookClient;
import com.project.feign.UserClient;
import com.project.models.Order;
import com.project.repositories.OrderRepository;

import jakarta.transaction.Transactional;

@Service
public class OrderServiceImpl implements  OrderService{

	private final OrderRepository orderRepository;
	private final UserClient userClient;
	private final BookClient bookClient;
	private final Map<Long,Cart> cartStorage = new HashMap<>();

	@Autowired
	public OrderServiceImpl(OrderRepository orderRepository,UserClient userClient, BookClient bookClient){
		this.orderRepository = orderRepository;
		this.userClient = userClient;
		this.bookClient = bookClient;
	}
	public List<CartItem> addToCart(Long userId,String bookId, int quantity) {
		ResponseEntity<UserDTO> response = userClient.getUserById(userId);
		if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
			BookDTO bookDTO = bookClient.getBookById(bookId);
			if (bookDTO == null) {
				throw new ResourceNotFoundException("Book not found for ID: " + bookId);
			}
			int stockQuantity = bookClient.getBookStockQuantity(bookId);

			if (stockQuantity < quantity) {
				throw new InsufficientStockException("Requested quantity exceeds available stock.");
			}
			CartItem cartItem = new CartItem(bookId, quantity);
			Cart userCart = cartStorage.computeIfAbsent(userId,k->new Cart());
			userCart.addItem(cartItem);
			return userCart.getItems();
		} else {
			throw new ResourceNotFoundException("User not found for ID: " + userId);
		}
	}
	
	public List<CartItem> getCartItems(Long userId){
		Cart userCart = cartStorage.getOrDefault(userId, new Cart());
		return userCart.getItems();
	}
	
	public void clearCart(Long userId) {
		Cart userCart = cartStorage.get(userId);
		if(userCart != null) {
			userCart.clear();
		}
	}

	public void clearCartItem(Long userId,String bookId){
		ResponseEntity<UserDTO> response = userClient.getUserById(userId);
		if(response.getStatusCode() == HttpStatus.OK && response.getBody() != null){
			Cart userCart = cartStorage.get(userId);
			if(userCart != null){
				userCart.removeItem(bookId);
			}
		}else{
			throw new ResourceNotFoundException("User not found for ID: " + userId);
		}
	}

	@Transactional
	public OrderDTO placeOrder(Long userId) {
		ResponseEntity<UserDTO> response = userClient.getUserById(userId);

		if(response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
			Cart userCart = cartStorage.getOrDefault(userId, new Cart());
			List<CartItem> cartItems = userCart.getItems();
			if (cartItems.isEmpty()) {
				throw new CartEmptyException("Cart is empty for user ID: " + userId);
			}
			Order order = new Order();
			order.setOrderDate(new Date());
			order.setStatus("Pending");
			order.setUserId(userId);
			order.setPaymentStatus(PaymentStatus.PENDING);
			List<String> bookIds = new ArrayList<>();
			double totalAmount = 0.0;

			for (CartItem item : cartItems) {
				String bookId = item.getBookId();
				int quantity = item.getQuantity();

				BookDTO bookDTO = bookClient.getBookById(bookId);
				if (bookDTO == null) {
					throw new ResourceNotFoundException("Book not found for ID: " + bookId);
				}
				int stockQuantity = bookClient.getBookStockQuantity(bookId);
				if (stockQuantity < quantity) {
					throw new InsufficientStockException("Insufficient stock for book ID: " + bookId);
				}
				totalAmount += bookDTO.getPrice() * quantity;
			    bookClient.updateBookStock(bookId, quantity);
				bookIds.add(bookId + ":" + quantity);

			}
			order.setBookIds(bookIds);
			order.setTotalAmount(totalAmount);
			System.out.println("Before saving the order : " + order);
            orderRepository.save(order);
			System.out.println("After saving the order: " + order);
            clearCart(userId);
			return convertToOrderDTO(order);
		}else{
			throw new ResourceNotFoundException("User not found for ID: " + userId);
		}
	}



    @Transactional
    public PaymentDetailsDTO processPayment(Long orderId,Long userId){
		Order order = orderRepository.findById(orderId)
				.orElseThrow(()->new ResourceNotFoundException("Order not found"));

		if(!order.getUserId().equals(userId)){
			throw new SecurityException("User ID does not match the order's user ID.");
		}
		if(order.getPaymentStatus() == PaymentStatus.PAID){
			throw new OrderAlreadyPaidException("Order already paid");
		}
		ResponseEntity<UserDTO> userResponse = userClient.getUserById(userId);
		if(userResponse.getBody() == null){
			throw new ResourceNotFoundException("User not found");
		}

		double amount = order.getTotalAmount();
		ResponseEntity<UserCreditDTO> debitResponse = userClient.debitCredits(userId,amount);
		if(debitResponse.getStatusCode() == HttpStatus.BAD_REQUEST){
			order.setPaymentStatus(PaymentStatus.FAILED);
			orderRepository.save(order);
			throw new InsufficientCreditsException("Insufficient credits");
		}
		order.setPaymentStatus(PaymentStatus.PAID);
		order.setStatus("Shipped");
		orderRepository.save(order);
		clearCart(userId);

		PaymentDetailsDTO paymentDetails = new PaymentDetailsDTO();
		paymentDetails.setOrderId(orderId);
		paymentDetails.setUserId(userId);
		paymentDetails.setAmount(amount);
		paymentDetails.setPaymentDate(new Date());
		paymentDetails.setPaymentStatus(PaymentStatus.PAID);
		paymentDetails.setRemainingCredits(debitResponse.getBody().getCredits());
		return paymentDetails;
    }

	@Transactional
	public OrderDTO cancelOrder(Long orderId,Long userId){
		Order order = orderRepository.findById(orderId).orElseThrow(()->new ResourceNotFoundException("Order not found"));
		if(!order.getUserId().equals(userId)){
			throw new SecurityException("You are not authorized to cancel this order.");
		}
		List<String> bookIds = order.getBookIds();
		if(order.getPaymentStatus() == PaymentStatus.PAID) {
			double refundAmount = order.getTotalAmount();
			userClient.addCredits(userId, refundAmount);
			order.setTotalAmount(0.0);
			if (bookIds != null) {
				for (String bookIdQuantity : bookIds) {
					String[] parts = bookIdQuantity.split(":");
					if (parts.length == 2) {
						String bookId = parts[0];
						int quantity = Integer.parseInt(parts[1]);
						System.out.println("Processing bookId: " + bookId + ", quantity: " + quantity);
						bookClient.updateBookStock(bookId, quantity);
					}
				}
			}
		}else{
				if (bookIds != null) {
					for (String bookIdQuantity : bookIds) {
							String[] parts = bookIdQuantity.split(":");
							if (parts.length == 2) {
								String bookId = parts[0];
								int quantity = Integer.parseInt(parts[1]);
								bookClient.updateBookStock(bookId, quantity);
							}
					}
				}
			}
		order.setPaymentStatus(PaymentStatus.CANCELLED);
		order.setStatus("Cancelled");
		orderRepository.save(order);
		return convertToOrderDTO(order);
	}
		
	private OrderDTO convertToOrderDTO(Order order) {
		OrderDTO orderDTO = new OrderDTO();
		orderDTO.setOrderId(order.getOrderId());
		orderDTO.setOrderDate(order.getOrderDate());
		orderDTO.setTotalAmount(order.getTotalAmount());
		orderDTO.setStatus(order.getStatus());
		orderDTO.setUserId(order.getUserId());
		orderDTO.setBookIds(order.getBookIds());
		orderDTO.setPaymentStatus(order.getPaymentStatus());
		return orderDTO;
	}

	@Transactional
	public OrderDTO updateOrderStatus(Long orderId,String status,Long adminUserId) {
		
		ResponseEntity<UserDTO> userResponse = userClient.getUserById(adminUserId);
		UserDTO adminUser = userResponse.getBody();
		
		if(adminUser == null) {
			throw new ResourceNotFoundException("Admin user not found: " + adminUserId);
		}
		
		if(adminUser.getRole() != Role.ADMIN) {
			throw new SecurityException("User does not have permission to update order status.");
		}
		Order order = orderRepository.findById(orderId)
				.orElseThrow(()-> new ResourceNotFoundException("Order not found for ID: " + orderId));
		order.setStatus(status);
		orderRepository.save(order);
		return convertToOrderDTO(order);
	}
	
	public List<OrderDTO> getUserOrders(Long userId){
		List<Order> orders = orderRepository.findByUserId(userId);
		List<OrderDTO> orderDTOs = new ArrayList<>();
		for(Order order : orders) {
			OrderDTO orderDTO = convertToOrderDTO(order);
			orderDTOs.add(orderDTO);
		}
		return orderDTOs;
	}

	public OrderDTO getOrderDetails(Long orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(()->new ResourceNotFoundException("Order not found: " + orderId));
		return convertToOrderDTO(order);
	}
	
	public List<BookDTO> getBooksByOrderId(Long orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));

		List<BookDTO> bookDTOs = new ArrayList<>();
		for(String bookIdQuantity : order.getBookIds()){
			String[] parts = bookIdQuantity.split(":");
			if(parts.length>0){
				String bookId = parts[0];
				try{
					BookDTO bookDTO = bookClient.getBookById(bookId);
					bookDTOs.add(bookDTO);
				}catch(Exception e){
					bookDTOs.add(null);
				}
			}
		}
		return bookDTOs;
	}
	
}


