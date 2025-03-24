package com.project.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

import com.project.dto.BookDTO;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient(name="BOOK-SERVICE")
public interface BookClient {

	@GetMapping("dbs/books/{bookId}")
	BookDTO getBookById(@PathVariable String bookId);

	@GetMapping("dbs/books/quantity/{bookId}")
	int getBookStockQuantity(@PathVariable String bookId);

	@PutMapping("dbs/inventory/updateAfterOrder")
	ResponseEntity<String>  updateInventoryAfterOrder(@RequestParam("bookIDs") List<String> bookIDs, @RequestParam("quantities") List<Integer> quantities);

}




