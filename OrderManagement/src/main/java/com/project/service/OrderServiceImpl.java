package com.project.service;

import java.util.*;

import com.project.dto.*;
import com.project.enums.PaymentStatus;
import com.project.exception.*;
import com.project.models.*;
import com.project.repositories.ReturnDetailsRepository;
import com.project.repositories.TrackingDetailsRepository;
import feign.FeignException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.enums.Role;
import com.project.feign.BookClient;
import com.project.feign.UserClient;
import com.project.repositories.OrderRepository;

import jakarta.transaction.Transactional;

/**
 * Implementation of the {@link OrderService} interface.
 * Provides services for managing orders, cart operations, payment processing,
 * tracking updates, returns, and order status modifications.
 */
@Service
public class OrderServiceImpl implements  OrderService{

	private final OrderRepository orderRepository;
	private final UserClient userClient;
	private final BookClient bookClient;
//	private final BookClient.InventoryClient inventoryClient;
	private final Map<Long,Cart> cartStorage = new HashMap<>();
	private final TrackingDetailsRepository trackingDetailsRepository;
	private final ReturnDetailsRepository returnDetailsRepository;

	private static final String BOOK_NOT_FOUND = "Book not found for ID: ";
	private static final String USER_NOT_FOUND = "User not found for ID: ";
	private static final String PENDING = "Pending";
	private static final String ORDER_NOT_FOUND = "Order not found";
	private static final String SHIPPED = "Shipped";
	/**
	 * Constructs a new OrderServiceImpl with the specified dependencies.
	 * This constructor initializes the service with repositories and Feign clients
	 * necessary for order management, user interaction, book information retrieval, inventory updates, tracking, and return processing.
	 * @param orderRepository          The repository for managing order data.
	 * @param userClient               The Feign client for interacting with user-related services.
	 * @param bookClient               The Feign client for retrieving book information.
	 * @param trackingDetailsRepository The repository for managing tracking details.
	 * @param returnDetailsRepository  The repository for managing return details.
	 */
	@Autowired
	public OrderServiceImpl(OrderRepository orderRepository,UserClient userClient, BookClient bookClient,TrackingDetailsRepository trackingDetailsRepository,ReturnDetailsRepository returnDetailsRepository){
		this.orderRepository = orderRepository;
		this.userClient = userClient;
		this.bookClient = bookClient;
		this.trackingDetailsRepository = trackingDetailsRepository;
		this.returnDetailsRepository = returnDetailsRepository;
	}

	/**
	 * Adds a book to the user's cart.
	 *
	 * @param userId   The ID of the user.
	 * @param bookId   The ID of the book to add.
	 * @param quantity The quantity of the book to add.
	 * @return The updated list of cart items.
	 * @throws ResourceNotFoundException If the user or book is not found.
	 * @throws InsufficientStockException If the requested quantity exceeds available stock.
	 */
	public List<CartItem> addToCart(Long userId, String bookId, int quantity) {
		ResponseEntity<UserDTO> response = userClient.getUserById(userId);
		if (response != null && response.getStatusCode() == HttpStatus.OK &&  response.getBody() != null && response.getBody().getUserId() != null) {
			try {
				BookDTO bookDTO = bookClient.getBookById(bookId);
				if (bookDTO == null) {
					throw new ResourceNotFoundException(BOOK_NOT_FOUND + bookId);
				}
				if (quantity <= 0) {
					throw new IllegalArgumentException("Quantity must be greater than zero.");
				}
				int stockQuantity = bookClient.getBookStockQuantity(bookId);
				if (stockQuantity < quantity) {
					throw new InsufficientStockException("Requested quantity exceeds available stock.");
				}
				CartItem cartItem = new CartItem(bookId, quantity);
				Cart userCart = cartStorage.computeIfAbsent(userId, k -> new Cart());
				userCart.addItem(cartItem);
				return userCart.getItems();
			} catch (FeignException.NotFound ex) {
				throw new ResourceNotFoundException(BOOK_NOT_FOUND + bookId);
			} catch (FeignException ex) {
				throw new RuntimeException("Error fetching book: " + ex.getMessage());
			}
		} else {
			throw new ResourceNotFoundException(USER_NOT_FOUND + userId);
		}
	}

	/**
	 * Places an order for the items in the user's cart.
	 *
	 * @param userId The ID of the user.
	 * @return The placed order details.
	 * @throws ResourceNotFoundException If the user is not found.
	 * @throws CartEmptyException If the user's cart is empty.
	 * @throws InsufficientStockException If there is insufficient stock for any item.
	 */
	@Transactional
	public OrderDTO placeOrder(Long userId) {
		ResponseEntity<UserDTO> response = userClient.getUserById(userId);
		if(response.getStatusCode() == HttpStatus.OK && response.getBody()!=null && response.getBody().getUserId() != null) {
			Cart userCart = cartStorage.getOrDefault(userId, new Cart());
			List<CartItem> cartItems = userCart.getItems();
			if (cartItems.isEmpty()) {
				throw new CartEmptyException("Cart is empty for user ID: " + userId);
			}
			Order order = new Order();
			order.setOrderDate(new Date());
			order.setStatus(PENDING);
			order.setUserId(userId);
			order.setPaymentStatus(PaymentStatus.PENDING);
			List<String> bookIds = new ArrayList<>();
			double totalAmount = 0.0;
			List<String> bookIDsUpdate = new ArrayList<>();
			List<Integer> quantitiesToUpdate = new ArrayList<>();

			for (CartItem item : cartItems) {
				String bookId = item.getBookId();
				int quantity = item.getQuantity();

				BookDTO bookDTO = bookClient.getBookById(bookId);
				if (bookDTO == null) {
					throw new ResourceNotFoundException(BOOK_NOT_FOUND + bookId);
				}
				int stockQuantity = bookClient.getBookStockQuantity(bookId);
				if (stockQuantity < quantity) {
					throw new InsufficientStockException("Insufficient stock for book ID: " + bookId);
				}
				totalAmount += bookDTO.getPrice() * quantity;
				bookIds.add(bookId + ":" + quantity);
				bookIDsUpdate.add(bookId);
				quantitiesToUpdate.add(quantity);
			}

			bookClient.updateInventoryAfterOrder(bookIDsUpdate,quantitiesToUpdate);
			order.setBookIds(bookIds);
			order.setTotalAmount(totalAmount);
			orderRepository.save(order);
			clearCart(userId);
			return convertToOrderDTO(order);
		}else{
			throw new ResourceNotFoundException(USER_NOT_FOUND + userId);
		}
	}

	/**
	 * Processes payment for a specific order.
	 *
	 * @param orderId The ID of the order.
	 * @param userId  The ID of the user making the payment.
	 * @return The payment details.
	 * @throws ResourceNotFoundException If the order or user is not found.
	 * @throws OrderAlreadyPaidException If the order is already paid.
	 * @throws InsufficientCreditsException If the user has insufficient credits.
	 * @throws SecurityException If the user making the payment does not match the order's user.
	 */
	@Transactional
	public PaymentDetailsDTO processPayment(Long orderId,Long userId){
		Order order = orderRepository.findById(orderId)
				.orElseThrow(()->new ResourceNotFoundException(ORDER_NOT_FOUND));

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
		ResponseEntity<UserCreditDTO> debitResponse;
		try {
			debitResponse = userClient.debitCredits(userId, amount);
		}catch(FeignException.BadRequest ex){
			order.setPaymentStatus(PaymentStatus.FAILED);
			orderRepository.save(order);
			throw new InsufficientCreditsException("Insufficient credits");
		}
		if(debitResponse == null || debitResponse.getBody() == null){
			order.setPaymentStatus(PaymentStatus.FAILED);
			orderRepository.save(order);
			throw new InsufficientCreditsException("Insufficient credits");
		}
		order.setPaymentStatus(PaymentStatus.PAID);
		order.setStatus(SHIPPED);
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

	/**
	 * Cancels a specific order.
	 *
	 * @param orderId The ID of the order.
	 * @param userId  The ID of the user requesting the cancellation.
	 * @return The cancelled order details.
	 * @throws ResourceNotFoundException If the order is not found.
	 * @throws SecurityException If the user is not authorized to cancel the order.
	 */

	@Transactional
	public OrderDTO cancelOrder(Long orderId,Long userId){
		Order order = orderRepository.findById(orderId).orElseThrow(()->new ResourceNotFoundException(ORDER_NOT_FOUND));
		if(!order.getUserId().equals(userId)){
			throw new SecurityException("You are not authorized to cancel this order.");
		}
		if (!order.getStatus().equalsIgnoreCase(SHIPPED) && !order.getStatus().equalsIgnoreCase(PENDING)) {
			throw new IllegalStateException("Cannot cancel an order that has already been delivered or Cancelled or Returned");
		}
		List<String> bookIds = order.getBookIds();
		if(order.getPaymentStatus() == PaymentStatus.PAID) {
			double refundAmount = order.getTotalAmount();
			userClient.addCredits(userId, refundAmount);
			order.setTotalAmount(0.0);
			if (bookIds != null) {
				List<String> bookIDsToUpdate = new ArrayList<>();
				List<Integer> quantitiesToUpdate =  new ArrayList<>();
				for (String bookIdQuantity : bookIds) {
					String[] parts = bookIdQuantity.split(":");
					if (parts.length == 2) {
						String bookId = parts[0];
						int quantity = Integer.parseInt(parts[1]);
						bookIDsToUpdate.add(bookId);
						quantitiesToUpdate.add(-quantity);
					}
				}
				bookClient.updateInventoryAfterOrder(bookIDsToUpdate,quantitiesToUpdate);
				order.setPaymentStatus(PaymentStatus.REFUNDED);
			}
		}else{
			if (bookIds != null) {
				List<String> bookIDsToUpdate = new ArrayList<>();
				List<Integer> quantitiesToUpdate =  new ArrayList<>();
				for (String bookIdQuantity : bookIds) {
					String[] parts = bookIdQuantity.split(":");
					if (parts.length == 2) {
						String bookId = parts[0];
						int quantity = Integer.parseInt(parts[1]);
						bookIDsToUpdate.add(bookId);
						quantitiesToUpdate.add(-quantity);
					}
				}
				bookClient.updateInventoryAfterOrder(bookIDsToUpdate,quantitiesToUpdate);
			}
		}
		order.setStatus("Cancelled");
		orderRepository.save(order);
		return convertToOrderDTO(order);
	}

	/**
	 * Updates the status of an order.
	 *
	 * @param orderId     The ID of the order.
	 * @param status      The new status of the order.
	 * @param adminUserId The ID of the admin user performing the update.
	 * @return The updated order details.
	 * @throws ResourceNotFoundException If the order or admin user is not found.
	 * @throws SecurityException If the admin user does not have permission to update the order status.
	 */
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
				.orElseThrow(()-> new ResourceNotFoundException(ORDER_NOT_FOUND + " for ID: " + orderId));
		if (order.getStatus().equalsIgnoreCase("Return Approved") ||
				order.getStatus().equalsIgnoreCase("Return Rejected")||
				order.getStatus().equalsIgnoreCase("Cancelled")||
				order.getStatus().equalsIgnoreCase("Delivered")) {
			throw new IllegalStateException("Cannot update status of this order");
		}
		order.setStatus(status);
		orderRepository.save(order);
		return convertToOrderDTO(order);
	}


	/**
	 * Updates the tracking information for an order.
	 *
	 * @param orderId              The ID of the order to update.
	 * @param shippingCarrier      The shipping carrier (e.g., "UPS", "FedEx").
	 * @param estimatedDeliveryDate The estimated delivery date.
	 * @param adminUserId          The ID of the admin user performing the update.
	 * @return The updated OrderDTO.
	 * @throws ResourceNotFoundException If the order or admin user is not found.
	 * @throws SecurityException         If the user does not have permission to update tracking.
	 */
	@Transactional
	public OrderDTO updateTracking(Long orderId, String shippingCarrier, Date estimatedDeliveryDate,Long adminUserId) {
		ResponseEntity<UserDTO> adminResponse = userClient.getUserById(adminUserId);
		if (adminResponse.getStatusCode() != HttpStatus.OK || adminResponse.getBody() == null) {
			throw new ResourceNotFoundException("Admin user not found for ID: " + adminUserId);
		}

		UserDTO adminUser = adminResponse.getBody();
		if (adminUser.getRole() != Role.ADMIN) {
			throw new SecurityException("User does not have permission to update tracking.");
		}
		if (estimatedDeliveryDate == null || estimatedDeliveryDate.before(new Date())) {
			throw new IllegalArgumentException("Estimated delivery date must be in the future.");
		}
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND));
		TrackingDetails trackingDetails = trackingDetailsRepository.findByOrderId(orderId);
		if (trackingDetails == null) {
			trackingDetails = new TrackingDetails();
			trackingDetails.setOrderId(orderId);
		}
		String trackingNumber = generateTrackingNumber();
		trackingDetails.setTrackingNumber(trackingNumber);
		trackingDetails.setShippingCarrier(shippingCarrier);
		trackingDetails.setEstimatedDeliveryDate(estimatedDeliveryDate);
		trackingDetailsRepository.save(trackingDetails);
		return convertToOrderDTO(order);
	}

	/**
	 * Requests a return for an order.
	 *
	 * @param orderId The ID of the order to return.
	 * @param userId  The ID of the user requesting the return.
	 * @param reason  The reason for the return.
	 * @return The updated OrderDTO.
	 * @throws ResourceNotFoundException If the order is not found.
	 * @throws SecurityException         If the user is not authorized to return the order.
	 * @throws IllegalStateException     If the order is not in a deliverable state.
	 */
	@Transactional
	public OrderDTO requestReturn(Long orderId, Long userId, String reason) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND));
		if (!order.getUserId().equals(userId)) {
			throw new SecurityException("You are not authorized to return this order.");
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(order.getOrderDate());
		cal.add(Calendar.DATE, 10);
		Date returnDeadline = cal.getTime();
		if (new Date().after(returnDeadline)) {
			throw new IllegalStateException("Return request is past the allowed time limit.");
		}
		if (order.getStatus().equalsIgnoreCase(SHIPPED) || order.getStatus().equalsIgnoreCase(PENDING) || !order.getStatus().equalsIgnoreCase("Delivered")) {
			throw new StatusNotFoundException("Return requests can only be made for  delivered orders.");
		}
		ReturnDetails returnDetails = returnDetailsRepository.findByOrderId(orderId);
		if (returnDetails == null) {
			returnDetails = new ReturnDetails();
			returnDetails.setOrderId(orderId);
		}
		returnDetails.setReturnReason(reason);
		returnDetails.setReturnRequestedDate(new Date());
		returnDetailsRepository.save(returnDetails);
		order.setStatus("Return Requested");
		orderRepository.save(order);
		return convertToOrderDTO(order);
	}

	/**
	 * Processes a return request for an order (approve or reject).
	 *
	 * @param orderId     The ID of the order.
	 * @param adminUserId The ID of the admin user processing the return.
	 * @param action      The action to take ("approve" or "reject").
	 * @return The updated OrderDTO.
	 * @throws ResourceNotFoundException If the order or admin user is not found.
	 * @throws SecurityException         If the user does not have permission to process returns.
	 * @throws IllegalStateException     If the order is not in the "Return Requested" state or if the return request is not found.
	 * @throws IllegalArgumentException  If the action is invalid.
	 */
	@Transactional
	public OrderDTO adminProcessReturn(Long orderId, Long adminUserId, String action) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND));

		ResponseEntity<UserDTO> adminResponse = userClient.getUserById(adminUserId);
		if (adminResponse == null || adminResponse.getStatusCode() != HttpStatus.OK || adminResponse.getBody() == null) {
			throw new ResourceNotFoundException("Admin user not found for ID: " + adminUserId);
		}

		UserDTO adminUser = adminResponse.getBody();
		if (adminUser.getRole() != Role.ADMIN) {
			throw new SecurityException("User does not have permission to process returns.");
		}

		if (!order.getStatus().equalsIgnoreCase("Return Requested")) {
			throw new IllegalStateException("Order is not in 'Return Requested' state.");
		}

		ReturnDetails returnDetails = returnDetailsRepository.findByOrderId(orderId);
		if (returnDetails == null) {
			throw new IllegalStateException("Return request not found for this order.");
		}
		if (action.equalsIgnoreCase("approve")) {
			approveReturn(order,returnDetails);
		} else if (action.equalsIgnoreCase("reject")) {
			rejectReturn(order,returnDetails);
		} else {
			throw new IllegalArgumentException("Invalid action: " + action + ". Must be 'approve' or 'reject'.");
		}

		returnDetailsRepository.save(returnDetails);
		orderRepository.save(order);

		return convertToOrderDTO(order);
	}


	private void approveReturn(Order order, ReturnDetails returnDetails) {
		if (order.getPaymentStatus() == PaymentStatus.PAID) {
			try {
				double refundAmount = order.getTotalAmount();
				userClient.addCredits(order.getUserId(), refundAmount);
				List<String> bookIds = order.getBookIds();
				if (bookIds != null) {
					List<String> bookIDsToUpdate = new ArrayList<>();
					List<Integer> quantitiesToUpdate = new ArrayList<>();
					for (String bookIdQuantity : bookIds) {
						String[] parts = bookIdQuantity.split(":");
						if (parts.length == 2) {
							String bookId = parts[0];
							int quantity = Integer.parseInt(parts[1]);
							bookIDsToUpdate.add(bookId);
							quantitiesToUpdate.add(-quantity);
						}
					}
					bookClient.updateInventoryAfterOrder(bookIDsToUpdate, quantitiesToUpdate);
				}
				order.setTotalAmount(0.0);
				order.setPaymentStatus(PaymentStatus.REFUNDED);
			} catch (FeignException e) {
				throw new RuntimeException("Feign client error during return approval.");
			} catch (NumberFormatException e) {
				throw new NumberFormatException("Error parsing book quantity.");
			} catch (Exception e) {
				throw new RuntimeException("Error during return approval.");
			}
			order.setTotalAmount(0.0);
			order.setPaymentStatus(PaymentStatus.REFUNDED);
		}
		returnDetails.setReturnApprovedDate(new Date());
		order.setStatus("Return Approved");
		returnDetailsRepository.save(returnDetails);
		orderRepository.save(order);
	}

	private void rejectReturn(Order order, ReturnDetails returnDetails) {
		returnDetails.setReturnRejectedDate(new Date());
		order.setStatus("Return Rejected");
		returnDetailsRepository.save(returnDetails);
		orderRepository.save(order);
	}


	/**
	 * Clears a specific item from the user's cart.
	 *
	 * @param userId The ID of the user.
	 * @param bookId The ID of the book to clear.
	 * @throws ResourceNotFoundException If the user is not found.
	 */
	public void clearCartItem(Long userId,String bookId){
		ResponseEntity<UserDTO> response = userClient.getUserById(userId);
		if(response != null && response.getStatusCode() == HttpStatus.OK && response.getBody() != null && response.getBody().getUserId() != null){
			Cart userCart = cartStorage.get(userId);
			if(userCart != null && !userCart.removeItem(bookId)){
				throw new ResourceNotFoundException(BOOK_NOT_FOUND + " in cart: " + bookId);
			}
		}else{
			throw new ResourceNotFoundException(USER_NOT_FOUND + userId);
		}
	}

	/**
	 * Retrieves the items in the user's cart.
	 *
	 * @param userId The ID of the user.
	 * @return The list of cart items.
	 */
	public List<CartItem> getCartItems(Long userId){
		Cart userCart = cartStorage.getOrDefault(userId, new Cart());
		return userCart.getItems();
	}

	/**
	 * Retrieves all books associated with an order.
	 *
	 * @param orderId The ID of the order.
	 * @return The list of books.
	 * @throws ResourceNotFoundException If the order is not found.
	 */
	public List<BookDTO> getBooksByOrderId(Long orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException(ORDER_NOT_FOUND + ": " + orderId));

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

	/**
	 * Retrieves all orders for a user.
	 *
	 * @param userId The ID of the user.
	 * @return The list of user orders.
	 */
	public List<OrderDTO> getUserOrders(Long userId){
		List<Order> orders = orderRepository.findByUserId(userId);
		List<OrderDTO> orderDTOs = new ArrayList<>();
		for(Order order : orders) {
			OrderDTO orderDTO = convertToOrderDTO(order);
			orderDTOs.add(orderDTO);
		}
		return orderDTOs;
	}

	/**
	 * Retrieves details of a specific order.
	 *
	 * @param orderId The ID of the order.
	 * @return The order details.
	 * @throws ResourceNotFoundException If the order is not found.
	 */
	public OrderDTO getOrderDetails(Long orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(()->new ResourceNotFoundException(ORDER_NOT_FOUND + ": "  + orderId));
		return convertToOrderDTO(order);
	}


	/**
	 * Clears the user's cart.
	 *
	 * @param userId The ID of the user.
	 */
	public void clearCart(Long userId) {
		Cart userCart = cartStorage.get(userId);
		if(userCart != null) {
			userCart.clear();
		}
	}

	/**
	 * Converts an Order entity to an OrderDTO.
	 *
	 * @param order The Order entity to convert.
	 * @return The corresponding OrderDTO.
	 */

	private OrderDTO convertToOrderDTO(Order order) {
		OrderDTO orderDTO = new OrderDTO();
		orderDTO.setOrderId(order.getOrderId());
		orderDTO.setOrderDate(order.getOrderDate());
		orderDTO.setTotalAmount(order.getTotalAmount());
		orderDTO.setStatus(order.getStatus());
		orderDTO.setUserId(order.getUserId());
		orderDTO.setBookIds(order.getBookIds());
		orderDTO.setPaymentStatus(order.getPaymentStatus());
		if (order.getStatus().equalsIgnoreCase(PENDING) || order.getStatus().equalsIgnoreCase(SHIPPED)) {
			TrackingDetails trackingDetails = trackingDetailsRepository.findByOrderId(order.getOrderId());
			if (trackingDetails != null) {
				TrackingDetailsDTO trackingDetailsDTO = new TrackingDetailsDTO();
				trackingDetailsDTO.setTrackingId(trackingDetails.getTrackingId());
				trackingDetailsDTO.setOrderId(trackingDetails.getOrderId());
				trackingDetailsDTO.setTrackingNumber(trackingDetails.getTrackingNumber());
				trackingDetailsDTO.setShippingCarrier(trackingDetails.getShippingCarrier());
				trackingDetailsDTO.setEstimatedDeliveryDate(trackingDetails.getEstimatedDeliveryDate());
				orderDTO.setTrackingDetails(trackingDetailsDTO);
			}
		}
		ReturnDetails returnDetails = returnDetailsRepository.findByOrderId(order.getOrderId());
		if (returnDetails != null) {
			ReturnDetailsDTO returnDetailsDTO = new ReturnDetailsDTO();
			returnDetailsDTO.setReturnId(returnDetails.getReturnId());
			returnDetailsDTO.setOrderId(returnDetails.getOrderId());
			returnDetailsDTO.setReturnReason(returnDetails.getReturnReason());
			returnDetailsDTO.setReturnRequestedDate(returnDetails.getReturnRequestedDate());
			returnDetailsDTO.setReturnApprovedDate(returnDetails.getReturnApprovedDate());
			returnDetailsDTO.setReturnRejectedDate(returnDetails.getReturnRejectedDate());
			orderDTO.setReturnDetails(returnDetailsDTO);
		}

		return orderDTO;
	}

	/**
	 * Generates a unique tracking number.
	 *
	 * @return A unique tracking number.
	 */
	private String generateTrackingNumber() {
		return UUID.randomUUID().toString();
	}
}


