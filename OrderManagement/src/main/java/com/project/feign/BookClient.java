package com.project.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.project.dto.BookDTO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign client interface for interacting with the BOOK-SERVICE.
 * This interface defines methods to retrieve book details and update inventory.
 */
@FeignClient(name="BOOK-SERVICE")
public interface BookClient {

	/**
	 * Retrieves a book by its ID.
	 *
	 * @param bookId The ID of the book to retrieve.
	 * @return The BookDTO representing the book, or null if not found.
	 */
	@GetMapping("dbs/books/{bookId}")
	BookDTO getBookById(@PathVariable String bookId);

	/**
	 * Retrieves the stock quantity of a book by its ID.
	 *
	 * @param bookId The ID of the book.
	 * @return The current stock quantity of the book.
	 */
	@GetMapping("dbs/books/quantity/{bookId}")
	int getBookStockQuantity(@PathVariable String bookId);

	/**
	 * Nested Feign client interface for interacting with the INVENTORY-SERVICE.
	 * This interface defines methods to update inventory after an order is placed.
	 */
	@FeignClient(name = "INVENTORY-SERVICE")
	interface InventoryClient {
		/**
		 * Updates the inventory after an order has been placed.
		 *
		 * @param bookIDs     A list of book IDs that were part of the order.
		 * @param quantities  A list of quantities corresponding to each book ID, representing the change in stock.
		 * Negative quantities indicate a decrease in stock (books sold), while positive quantities
		 * indicate an increase (books returned or added).
		 * @return            A {@link ResponseEntity} containing a String message indicating the result of the inventory update.
		 * Typically, this will be a success or failure message.
		 */
		@PutMapping("dbs/inventory/updateAfterOrder")
		ResponseEntity<String> updateInventoryAfterOrder(
				@RequestParam("bookIDs") List<String> bookIDs,
				@RequestParam("quantities") List<Integer> quantities);
	}
}




