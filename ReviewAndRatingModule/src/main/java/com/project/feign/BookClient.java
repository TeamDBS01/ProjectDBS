package com.project.feign;

import com.project.exception.ServiceUnavailableException;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "BOOK-SERVICE", fallback = BookClientFallback.class)
public interface BookClient {

	@GetMapping("dbs/books/{bookId}")
	ResponseEntity<?> getBookById(@PathVariable String bookId) throws ServiceUnavailableException;
}
