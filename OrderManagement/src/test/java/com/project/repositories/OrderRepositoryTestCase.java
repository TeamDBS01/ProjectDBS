package com.project.repositories;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ContextConfiguration;

import com.project.OrderManagementApplication;
import com.project.models.Order;

@DataJpaTest
@ContextConfiguration(classes= {OrderManagementApplication.class})
class OrderRepositoryTestCase {

	@Autowired
	private OrderRepository orderRepository;

	@Test
	@DisplayName("Find all orders positive test case")
	void test_findAll_positive() {
		Order order1 = new Order(1L,new Date(),10.00,"Pending",1L);
		Order order2 = new Order(2L,new Date(),20.00,"Shipped",1L);
		Order order3 = new Order(3L,new Date(),30.00,"Delivered",2L);
		orderRepository.save(order1);
		orderRepository.save(order2);
		orderRepository.save(order3);
		List<Order> list = orderRepository.findAll();
		assertFalse(list.isEmpty());
		assertEquals(order1,list.get(0));
	}
	
	@Test
	@DisplayName("Find all orders negative test case")
	void test_findAll_negative() {
		List<Order> list = orderRepository.findAll();
		assertTrue(list.isEmpty());
	}
	
	@Test
	@DisplayName("Find by id positive test case")
	void test_findById_positive() {
		Order order1 = new Order(1L,new Date(),10.00,"Pending",1L);
		Order order2 = new Order(2L,new Date(),20.00,"Shipped",1L);
		orderRepository.save(order1);
		orderRepository.save(order2);
		Optional<Order> optionalOrder = orderRepository.findById(order1.getOrderId());
		assertTrue(optionalOrder.isPresent());
		assertEquals(order1,optionalOrder.get());
	}
	
	@Test
	@DisplayName("Find by id negative test case")
	void test_findById_negative() {
		Order order1 = new Order(1L,new Date(),10.00,"Pending",1L);
		orderRepository.save(order1);
		Optional<Order> optionalOrder = orderRepository.findById(9L);
		assertTrue(optionalOrder.isEmpty());
	}
	
	@Test
	@DisplayName("Update Positive")
	void test_update_positive() {
		Order order1 = new Order(1L,new Date(),10.00,"Pending",1L);
		orderRepository.save(order1);
		order1.setStatus("Delivered");
		orderRepository.save(order1);
		Optional<Order> optionalOrder = orderRepository.findById(order1.getOrderId());
		assertTrue(optionalOrder.isPresent());
		assertEquals(order1.getStatus(),optionalOrder.get().getStatus());
	}
	
	@Test
	@DisplayName("Delete by Id positive")
	void test_deleteById_positive() {
		Order order1 = new Order(1L,new Date(),10.00,"Pending",1L);
		orderRepository.save(order1);
		orderRepository.deleteById(order1.getOrderId());
		Optional<Order> optionalOrder = orderRepository.findById(order1.getOrderId());
		assertFalse(optionalOrder.isPresent());
	}
	
	@Test
	@DisplayName("Delete order")
	void test_delete() {
		Order order1 = new Order(1L,new Date(),10.00,"Pending",1L);
		orderRepository.delete(order1);
		Optional<Order> optionalOrder = orderRepository.findById(order1.getOrderId());
		assertFalse(optionalOrder.isPresent());
	}
	
	@Test
	@DisplayName("Find by user id-positive test case")
	void test_findByUserId_positive() {
		Order order1 = new Order(1L,new Date(),10.00,"Pending",1L);
		Order order2 = new Order(2L,new Date(),20.00,"Shipped",1L);
		Order order3 = new Order(3L,new Date(),20.00,"Shipped",2L);
		orderRepository.save(order1);
		orderRepository.save(order2);
		orderRepository.save(order3);
		List<Order> orders = orderRepository.findOrdersByUserId(1L);
		assertEquals(2,orders.size());
		assertTrue(orders.contains(order1));
		assertTrue(orders.contains(order2));
		assertFalse(orders.contains(order3));
	}
	
	@Test
	void test_findByUserId_negative() {
		List<Order> orders = orderRepository.findOrdersByUserId(7L);
		assertTrue(orders.isEmpty());
	}
	
}
