package com.project.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.project.models.Inventory;

/**
 * Repository interface for Inventory entity.
 */
public interface InventoryRepository extends JpaRepository<Inventory, Long> {

	/**
	 * Finds an inventory record by the book ID.
	 * @param bookId the ID of the book.
	 * @return an Optional containing the inventory record if found, or empty if not found.
	 */
	 @Query("SELECT i FROM Inventory i WHERE i.book_Id = :bookId")
	 Optional<Inventory> findByBookId(@Param("bookId") String bookId);
}