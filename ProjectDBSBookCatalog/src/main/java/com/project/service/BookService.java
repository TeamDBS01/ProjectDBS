package com.project.service;

import java.util.List;

import com.project.dto.BookDTO;
import com.project.exception.BookAlreadyExistsException;
import com.project.exception.BookResourceNotFoundException;


public interface BookService {
	 BookDTO getBookById(String bookId) throws BookResourceNotFoundException;
	 List<BookDTO> getBooksByCategory(String categoryName) throws BookResourceNotFoundException;
	 List<BookDTO> getBooksByAuthor(String authorName) throws BookResourceNotFoundException;
	 List<BookDTO> filter(String...criteria) throws BookResourceNotFoundException;
	 List<BookDTO> getAllBooks(int page, int size) throws BookResourceNotFoundException;

	boolean addBook(BookDTO bookDTO) throws BookResourceNotFoundException, BookAlreadyExistsException;
	boolean deleteBookById(String bookID) throws BookResourceNotFoundException;
	boolean deleteBookByTitle(String bookTitle) throws BookResourceNotFoundException;
	boolean updateBookById(String bookID, BookDTO bookDTO) throws BookResourceNotFoundException;


}
