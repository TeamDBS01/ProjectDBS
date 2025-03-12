package com.project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.dto.BookDTO;
import com.project.exception.BookResourceNotFoundException;
import com.project.models.Book;


public interface BookService {
	 List<BookDTO> getAllBooks() throws BookResourceNotFoundException;
	 BookDTO getBookById(String bookId) throws BookResourceNotFoundException;
	 List<BookDTO> getBooksByCategory(String categoryName) throws BookResourceNotFoundException;
	 List<BookDTO> getBooksByAuthor(String authorName) throws BookResourceNotFoundException;
	 List<BookDTO> filter(String...criteria) throws BookResourceNotFoundException;


	boolean addBook(BookDTO bookDTO);
	boolean deleteBookById(String bookID) throws BookResourceNotFoundException;
	boolean deleteBookByTitle(String bookTitle) throws BookResourceNotFoundException;
	boolean updateBookById(String bookID, BookDTO bookDTO) throws BookResourceNotFoundException;


}
