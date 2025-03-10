package com.project.service;

import org.springframework.web.bind.annotation.GetMapping;

import com.project.dto.BookDTO;
import org.springframework.web.bind.annotation.RequestMapping;

//@FeignClient("BOOK-MODULE")
@RequestMapping("dbs/book")
public interface BookService {

	@GetMapping("/book")
	BookDTO getBook();
}
