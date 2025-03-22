package com.project.repositories;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

	/**
	 * Finds all inventory records with pagination.
	 * @param pageable the pagination information.
	 * @return a Page containing the inventory records.
	 */
	 Page<Inventory> findAll(Pageable pageable);
}