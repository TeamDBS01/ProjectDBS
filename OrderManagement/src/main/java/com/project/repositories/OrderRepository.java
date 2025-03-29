package com.project.repositories;

import com.project.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long>{
	@Query("Select o from Order o where o.userId = :userId")
	List<Order> findByUserId(Long userId);
}
