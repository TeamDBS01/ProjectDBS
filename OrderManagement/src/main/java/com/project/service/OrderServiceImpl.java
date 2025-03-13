package com.project.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.project.dto.BookDTO;
import com.project.dto.Cart;
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

import jakarta.transaction.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Service
public class OrderServiceImpl { 
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private UserClient userClient;
	
	@Autowired
	private BookClient bookClient;

	public int getQuantity(String bookId) {
		return bookClient.getBookStockQuantity(bookId);
	}


//	@Autowired
//	private InventoryClient inventoryClient;
	
//	public static class CartItem{
//		private String bookId;
//		private int quantity;
//		public CartItem(String bookId,int quantity) {
//			this.bookId = bookId;
//			this.quantity = quantity;
//		}
//		public String getBookId() {
//			return bookId;
//		}
//		public int getQuantity() {
//			return quantity;
//		}
//	}
	
	private Map<Long,Cart> cartStorage = new HashMap<>();
	
	public List<CartItem> addToCart(Long userId,String bookId, int quantity) {
		ResponseEntity<UserDTO> response = userClient.getUserById(userId);
		if(response.getBody() == null) {
			throw new ResourceNotFoundException("User not found for ID: " + userId);
		}
		
//		
//		BookDTO bookDTO = new BookDTO();
//		bookDTO.setBookID(bookId);
//		bookDTO.setStockQuantity(100);
//		
		
		
		BookDTO bookDTO = bookClient.getBookById(bookId);
		if(bookDTO == null) {
			throw new ResourceNotFoundException("Book not found for ID: " + bookId);
		}
		int stockQuantity = bookClient.getBookStockQuantity(bookId);
//		if(bookDTO.getStockQuantity()<quantity) {
//			throw new InsufficientStockException("Requested quantity exceeds available stock.");
//		}
		if(stockQuantity<quantity) {
			throw new InsufficientStockException("Requested quantity exceeds available stock.");
		}
		
		CartItem cartItem = new CartItem(bookId,quantity);
		Cart userCart =  cartStorage.get(userId);
		if(userCart == null) {
			userCart = new Cart();
			cartStorage.put(userId,  userCart);
		}
		userCart.addItem(cartItem);
		return userCart.getItems();
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
	
	@Transactional
	public OrderDTO placeOrder(Long userId) {  //,Map<String,Integer> bookQuantities
		Order order = new Order();
		order.setOrderDate(new Date());
		order.setStatus("Pending");
		order.setUserId(userId);

		ResponseEntity<UserDTO> response = userClient.getUserById(userId);
		if (response.getBody()==null) {
			throw new ResourceNotFoundException("User not found for ID: " + userId);
		}
		UserDTO userDTO = response.getBody();
		
//		List<CartItem> cartItems = getCartItems(userId);
		Cart userCart = cartStorage.getOrDefault(userId, new Cart());
		List<CartItem> cartItems = userCart.getItems();
		List<String> bookIds = new ArrayList<>();
		double totalAmount = 0.0;
		
		for(CartItem item : cartItems) {
			String bookId = item.getBookId();
			Integer quantity = item.getQuantity();
			
//			
//			BookDTO bookDTO = new BookDTO();
//			bookDTO.setBookID(bookId);
//			bookDTO.setPrice(20.0);
//			bookDTO.setStockQuantity(100);
			
			BookDTO bookDTO = bookClient.getBookById(bookId);
			if(bookDTO == null) {
				throw new ResourceNotFoundException("Book not found for ID: " + bookId);
			}
			int stockQuantity = bookClient.getBookStockQuantity(bookId);
			
//			if(bookDTO.getStockQuantity()<quantity) {
//				throw new InsufficientStockException("Requested quantity exceeds available stock.");
//			}
			
			if(stockQuantity < quantity) {
				throw new InsufficientStockException("Insufficient stock for book ID: " + bookId);
			}
			totalAmount += bookDTO.getPrice() * quantity;
			bookClient.updateBookStock(bookId, quantity);
			bookIds.add(bookId);
		}
		order.setBookIds(bookIds);
		order.setTotalAmount(totalAmount);
		orderRepository.save(order);
		clearCart(userId);
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
		return orderDTO;
	}
	
	public OrderDTO updateOrderStatus(Long orderId,String status) {
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
	
	public List<BookDTO> getBooksByOrderId(Long orderId){
		Order order = orderRepository.findById(orderId)
				.orElseThrow(()->new ResourceNotFoundException("Order not found: " + orderId));
		List<BookDTO> bookDTOs = new ArrayList<>();
		for(String bookId : order.getBookIds()) {
			BookDTO bookDTO = bookClient.getBookById(bookId);
			bookDTOs.add(bookDTO);
		}
		return bookDTOs;
	}
	
	
//	public List<BookDTO> getBooksByOrderId(Long orderId) {
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new ResourceNotFoundException("Order not found: " + orderId));
//        List<BookDTO> bookDTOs = new ArrayList<>();
//        for (String bookId : order.getBookIds()) {
//            // Dummy BookDTO
//            BookDTO bookDTO = new BookDTO();
//            bookDTO.setBookID(bookId);
//            bookDTOs.add(bookDTO);
//        }
//        return bookDTOs;
//    }
//	
	
}



//@Transactional
//public OrderDTO placeOrder(Long userId) {
//    Order order = new Order();
//    order.setOrderDate(new Date());
//    order.setStatus("Pending");
//    order.setUserId(userId);
//
//    // Replace Feign calls with dummy data for testing
//    UserDTO userDTO = getDummyUserDTO(userId);
//    if (userDTO == null) {
//        throw new ResourceNotFoundException("User not found for ID: " + userId);
//    }
//
//    List<CartItem> cartItems = getCartItems(userId);
//    List<String> bookIds = new ArrayList<>();
//    double totalAmount = 0.0;
//
//    for (CartItem item : cartItems) {
//        String bookId = item.getBookId();
//        Integer quantity = item.getQuantity();
//
//        // Replace Feign call with dummy data
//        BookDTO bookDTO = getDummyBookDTO(bookId);
//        if (bookDTO == null) {
//            throw new ResourceNotFoundException("Book not found for ID: " + bookId);
//        }
//
//        if (bookDTO.getStockQuantity() < quantity) {
//            throw new InsufficientStockException("Insufficient stock for book ID: " + bookId);
//        }
//        totalAmount += bookDTO.getPrice() * quantity;
//        //bookClient.updateBookStock(bookId, quantity); // Remove or comment out
//        bookIds.add(bookId);
//
//    }
//    order.setBookIds(bookIds);
//    order.setTotalAmount(totalAmount);
//    orderRepository.save(order);
//    clearCart(userId);
//    return convertToOrderDTO(order);
//}


////List<String> bookIds = new ArrayList<>(bookQuantities.keySet());
////order.setBookIds(bookIds);
////
////double totalAmount = 0.0;
////for (Map.Entry<String, Integer> entry : bookQuantities.entrySet()) {
////String bookId = entry.getKey();
////Integer quantity = entry.getValue();
////
////BookDTO bookDTO = bookClient.getBookById(bookId);
////if (bookDTO == null) {
////	throw new ResourceNotFoundException("Book not found for ID: " + bookId);
////}
////
////if (bookDTO.getStockQuantity() < quantity) {
////	throw new InsufficientStockException("Insufficient stock for book ID: " + bookId);
////}
////totalAmount += bookDTO.getPrice() * quantity;
////bookClient.updateBookStock(bookId, quantity);
//////inventoryClient.updateRemoveInventory(bookId,quantity);
////}
//
//	
//private UserDTO getDummyUserDTO(Long userId) {
//UserDTO userDTO = new UserDTO();
//userDTO.setUserId(userId);
//userDTO.setName("Test User");
//return userDTO;
//}
//
//private BookDTO getDummyBookDTO(String bookId) {
//BookDTO bookDTO = new BookDTO();
//bookDTO.setBookId(bookId);
//bookDTO.setTitle("Test Book");
//bookDTO.setPrice(20.0);
//bookDTO.setStockQuantity(10);
//return bookDTO;
//}
//
//