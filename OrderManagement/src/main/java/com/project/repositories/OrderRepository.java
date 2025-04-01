package com.project.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.project.models.Order;

/**
 * Repository interface for managing {@link Order} entities.
 * Extends {@link JpaRepository} to provide standard JPA repository operations.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order,Long>{
	/**
	 * Retrieves a list of orders associated with a specific user ID.
	 *
	 * @param userId The ID of the user.
	 * @return A list of {@link Order} entities associated with the given user ID.
	 */
	@Query("Select o from Order o where o.userId = :userId")
	List<Order> findByUserId(Long userId);
}
