package com.project.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.project.dto.BookDTO;


@FeignClient(name="BOOK-SERVICE")
public interface BookClient {

	@GetMapping("dbs/books/{bookId}")
	BookDTO getBookById(@PathVariable String bookId);

	@PutMapping("dbs/books/{bookId}/stock/{quantity}")
	ResponseEntity<String> updateBookStock(@PathVariable String bookId, @PathVariable int quantity);

	@GetMapping("dbs/books/quantity/{bookId}")
	int getBookStockQuantity(@PathVariable String bookId);
}



