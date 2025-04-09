package com.project.service;

import java.util.Date;
import java.util.List;

import com.project.dto.BookDTO;
import com.project.dto.ShippingDetailsDTO;
import com.project.models.CartItem;
import com.project.dto.OrderDTO;
import com.project.dto.PaymentDetailsDTO;
import com.project.exception.InsufficientStockException;
import com.project.exception.ResourceNotFoundException;

public interface OrderService {

	List<CartItem> addToCart(Long userId,String bookId,int quantity) throws ResourceNotFoundException,InsufficientStockException;
	List<CartItem> getCartItems(Long userId);
	void clearCart(Long userId);
	void clearCartItem(Long userId,String bookId) throws ResourceNotFoundException;
	OrderDTO placeOrder(Long userId, ShippingDetailsDTO shippingDetails) throws ResourceNotFoundException;
	OrderDTO updateOrderStatus(Long orderId,String status,Long adminUserId) throws ResourceNotFoundException;
	List<OrderDTO> getUserOrders(Long userId) throws ResourceNotFoundException;
	OrderDTO getOrderDetails(Long orderId) throws ResourceNotFoundException;
	List<BookDTO> getBooksByOrderId(Long orderId) throws ResourceNotFoundException;
	PaymentDetailsDTO processPayment(Long orderId,Long userId) throws ResourceNotFoundException;
	OrderDTO cancelOrder(Long orderId,Long userId) throws ResourceNotFoundException;
	OrderDTO updateTracking(Long orderId, String shippingCarrier, Date estimatedDeliveryDate, Long adminUserId) throws ResourceNotFoundException;
	OrderDTO requestReturn(Long orderId, Long userId, String reason) throws ResourceNotFoundException;
	OrderDTO adminProcessReturn(Long orderId, Long adminUserId, String action) throws ResourceNotFoundException;
}

