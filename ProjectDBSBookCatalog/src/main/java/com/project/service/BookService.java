package com.project.service;

import com.project.dto.BookDTO;
import com.project.exception.BookAlreadyExistsException;
import com.project.exception.BookResourceNotFoundException;

import java.util.List;


public interface BookService {
	BookDTO getBookById(String bookId) throws BookResourceNotFoundException;
	BookDTO getBookByTitle(String title) throws BookResourceNotFoundException;
	List<BookDTO> getBooksByTitle(String title) throws BookResourceNotFoundException;
	List<BookDTO> getBooksByCategory(String categoryName) throws BookResourceNotFoundException;
	List<BookDTO> getBooksByAuthor(String authorName) throws BookResourceNotFoundException;
	List<BookDTO> filter(String author, String category) throws BookResourceNotFoundException;
	List<BookDTO> getAllBooks(int page, int size) throws BookResourceNotFoundException;

	boolean addBook(BookDTO bookDTO) throws BookResourceNotFoundException, BookAlreadyExistsException;
	boolean deleteBookById(String bookID) throws BookResourceNotFoundException;
	boolean deleteBookByTitle(String bookTitle) throws BookResourceNotFoundException;
	boolean updateBookById(String bookID, BookDTO bookDTO) throws BookResourceNotFoundException;

}
