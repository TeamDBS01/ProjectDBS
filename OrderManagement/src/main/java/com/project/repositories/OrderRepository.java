package com.project.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.models.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long>{
//	@Query("Select o from Order o where o.userId = :userId")
	List<Order> findByUserId(Long userId);
	@Query("Select o from Order o")
	Order findOne(Order order);
}
