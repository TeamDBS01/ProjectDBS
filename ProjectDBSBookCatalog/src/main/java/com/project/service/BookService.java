package com.project.service;

import java.util.List;

import com.project.dto.BookDTO;
import com.project.exception.BookResourceNotFoundException;

public interface BookService {
	 List<BookDTO> getAllBooks() throws BookResourceNotFoundException;
	 BookDTO getBookById() throws BookResourceNotFoundException;
	 List<BookDTO> getBooksByCategory() throws BookResourceNotFoundException;
	 List<BookDTO> getBooksByAuthor() throws BookResourceNotFoundException;
	 List<BookDTO> filter() throws BookResourceNotFoundException;
	 
}
