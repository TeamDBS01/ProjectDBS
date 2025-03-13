package com.project.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.project.models.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Long> {
	 @Query("SELECT i FROM Inventory i WHERE i.book_Id = :bookId")
	 Optional<Inventory> findByBookId(@Param("bookId") String bookId);
}