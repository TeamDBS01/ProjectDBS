package com.project.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import com.project.dto.BookDTO;

//@FeignClient("PROJECTDBSBOOKCATALOG")
public interface BookService {

//	@GetMapping("/bok")
//	public BookDTO getBook();
}
