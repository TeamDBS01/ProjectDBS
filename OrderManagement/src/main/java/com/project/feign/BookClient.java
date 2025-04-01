package com.project.feign;

import com.project.dto.BookDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * Feign client interface for interacting with the BOOK-SERVICE.
 * This interface defines methods to retrieve book details and update inventory.
 */
@FeignClient(name = "BOOK-SERVICE")
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
     * @param bookID The ID of the book.
     * @return The current stock quantity of the book.
     */
    @GetMapping("dbs/books/inventory/quantity/{bookID}")
    int getBookStockQuantity(@PathVariable String bookID);

    @PutMapping("dbs/books/updateAfterOrder")
    ResponseEntity<String> updateInventoryAfterOrder(@RequestParam("bookIDs") List<String> bookIDs, @RequestParam("quantities") List<Integer> quantities);
}




