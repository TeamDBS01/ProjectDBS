package com.project.service;

import java.util.List;

import com.project.dto.BookDTO;
import com.project.dto.CartItem;
import com.project.dto.OrderDTO;
import com.project.dto.PaymentDetailsDTO;
import com.project.exception.InsufficientStockException;
import com.project.exception.ResourceNotFoundException;

public interface OrderService {

	List<CartItem> addToCart(Long userId,String bookId,int quantity) throws ResourceNotFoundException,InsufficientStockException;
	List<CartItem> getCartItems(Long userId);
	void clearCart(Long userId);
	void clearCartItem(Long userId,String bookId) throws ResourceNotFoundException;
	OrderDTO placeOrder(Long userId) throws ResourceNotFoundException;
	OrderDTO updateOrderStatus(Long orderId,String status,Long adminUserId) throws ResourceNotFoundException;
	List<OrderDTO> getUserOrders(Long userId) throws ResourceNotFoundException;
	OrderDTO getOrderDetails(Long orderId) throws ResourceNotFoundException;
	List<BookDTO> getBooksByOrderId(Long orderId) throws ResourceNotFoundException;
	PaymentDetailsDTO processPayment(Long orderId,Long userId) throws ResourceNotFoundException;
	OrderDTO cancelOrder(Long orderId,Long userId) throws ResourceNotFoundException;
}
