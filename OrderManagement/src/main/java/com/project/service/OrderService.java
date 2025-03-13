package com.project.service;

import java.util.List;
import java.util.Map;

import com.project.dto.BookDTO;
import com.project.dto.CartItem;
import com.project.dto.OrderDTO;
import com.project.exception.InsufficientStockException;
import com.project.exception.ResourceNotFoundException;

public interface OrderService {

	List<CartItem> addToCart(Long userId,String bookId,int quantity) throws ResourceNotFoundException,InsufficientStockException;
	List<CartItem> getCartItems(Long userId);
	void clearCart(Long userId);
	OrderDTO placeOrder(Long userId) throws ResourceNotFoundException;
	OrderDTO updateOrderStatus(Long orderId,String status) throws ResourceNotFoundException;
	List<OrderDTO> getUserOrders(Long userId) throws ResourceNotFoundException;
	OrderDTO getOrderDetails(Long orderId) throws ResourceNotFoundException;
	List<BookDTO> getBooksByOrderId(Long orderId) throws ResourceNotFoundException;
}
