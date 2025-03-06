package com.project.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.models.Order;


public interface OrderRepository extends JpaRepository<Order,Long>{
	@Query("Select o from Order o where o.userId = :userId")
	List<Order> findOrdersByUserId(@Param("userId")Long userId);
}
