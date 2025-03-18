package com.project.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import com.project.enums.PaymentStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
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
		List<String> bookIds1 = new ArrayList<>();
		bookIds1.add("book1");
		List<String> bookIds2 = new ArrayList<>();
		bookIds2.add("book2");
		List<String> bookIds3 = new ArrayList<>();
		bookIds3.add("book3");
		Order order1 = new Order(new Date(),10.00, PaymentStatus.PENDING,"Pending",1L,bookIds1);
		Order order2 = new Order(new Date(),20.00,PaymentStatus.PAID,"Shipped",1L,bookIds2);
		Order order3 = new Order(new Date(),30.00,PaymentStatus.FAILED,"Delivered",2L,bookIds3);
		orderRepository.save(order1);
		orderRepository.save(order2);
		orderRepository.save(order3);
		List<Order> list = orderRepository.findAll();
		assertFalse(list.isEmpty());
		assertEquals(3,list.size());
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
		List<String> bookIds1 = new ArrayList<>();
		bookIds1.add("book1");
		List<String> bookIds2 = new ArrayList<>();
		bookIds2.add("book2");
		Order order1 = new Order(new Date(),10.00, PaymentStatus.PENDING,"Pending",1L,bookIds1);
		Order order2 = new Order(new Date(),20.00,PaymentStatus.PAID,"Shipped",1L,bookIds2);orderRepository.save(order1);
		orderRepository.save(order2);
		Optional<Order> optionalOrder = orderRepository.findById(order1.getOrderId());
		assertTrue(optionalOrder.isPresent());
		assertEquals(order1,optionalOrder.get());
	}

	@Test
	@DisplayName("Find by id negative test case")
	void test_findById_negative() {
		List<String> bookIds1 = new ArrayList<>();
		bookIds1.add("book1");
		Order order1 = new Order(new Date(),10.00, PaymentStatus.PENDING,"Pending",1L,bookIds1);
		Optional<Order> optionalOrder = orderRepository.findById(9L);
		assertTrue(optionalOrder.isEmpty());
	}

	@Test
	@DisplayName("Update Positive")
	void test_update_positive() {
		List<String> bookIds1 = new ArrayList<>();
		bookIds1.add("book1");
		Order order1 = new Order(new Date(),10.00, PaymentStatus.PENDING,"Pending",1L,bookIds1);
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
		List<String> bookIds1 = new ArrayList<>();
		bookIds1.add("book1");
		Order order = new Order(new Date(),10.00, PaymentStatus.PENDING,"Pending",1L,bookIds1);
		orderRepository.save(order);
		orderRepository.deleteById(order.getOrderId());
		Optional<Order> optionalOrderFind = orderRepository.findById(order.getOrderId());
		assertFalse(optionalOrderFind.isPresent());

	}

	@Test
	@DisplayName("Delete order")
	void test_delete() {
		List<String> bookIds1 = new ArrayList<>();
		bookIds1.add("book1");
		Order order1 = new Order(new Date(),10.00, PaymentStatus.PENDING,"Pending",1L,bookIds1);
		orderRepository.save(order1);
		orderRepository.delete(order1);
		Optional<Order> optionalOrder = orderRepository.findById(order1.getOrderId());
		assertFalse(optionalOrder.isPresent());
	}

	@Test
	@DisplayName("Find by user id-positive test case")
	void test_findByUserId_positive() {
		List<String> bookIds1 = new ArrayList<>();
		bookIds1.add("book1");
		List<String> bookIds2 = new ArrayList<>();
		bookIds2.add("book2");
		List<String> bookIds3 = new ArrayList<>();
		bookIds3.add("book3");
		Order order1 = new Order(new Date(),10.00, PaymentStatus.PENDING,"Pending",1L,bookIds1);
		Order order2 = new Order(new Date(),20.00,PaymentStatus.PAID,"Shipped",1L,bookIds2);
		Order order3 = new Order(new Date(),30.00,PaymentStatus.FAILED,"Delivered",2L,bookIds3);
		orderRepository.save(order1);
		orderRepository.save(order2);
		orderRepository.save(order3);

		List<Order> orders = orderRepository.findByUserId(1L);
		List<Order> savedOrders = new ArrayList<>();
		savedOrders.add(order1);
		savedOrders.add(order2);
		assertEquals(2,orders.size());
		assertTrue(orders.contains(order1));
		assertTrue(orders.contains(order2));
		assertFalse(orders.contains(order3));
		assertEquals(savedOrders, orders);
	}

	@Test
	void test_findByUserId_negative() {
		List<Order> orders = orderRepository.findByUserId(7L);
		assertTrue(orders.isEmpty());
	}

}
