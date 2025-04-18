package com.project.service;

import com.project.dto.BookDTO;
import com.project.exception.BookAlreadyExistsException;
import com.project.exception.BookResourceNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


public interface BookService {
	 BookDTO getBookById(String bookId) throws BookResourceNotFoundException;
	 BookDTO getBookByTitle(String title) throws BookResourceNotFoundException;
	List<BookDTO> getBooksByTitle(String title) throws BookResourceNotFoundException;
	 List<BookDTO> getBooksByCategory(String categoryName) throws BookResourceNotFoundException;
	 List<BookDTO> getBooksByAuthor(String authorName) throws BookResourceNotFoundException;
	 List<String> getAllAuthors();
	List<String> getAllCategories();
	 List<BookDTO> filter(String author, String category) throws BookResourceNotFoundException;
	 List<BookDTO> getAllBooks(int page, int size) throws BookResourceNotFoundException;
	int getNoOfPages();

	boolean addBook(BookDTO bookDTO) throws BookResourceNotFoundException, BookAlreadyExistsException;
	boolean deleteBookById(String bookID) throws BookResourceNotFoundException;
	boolean deleteBookByTitle(String bookTitle) throws BookResourceNotFoundException;
	boolean updateBookById(String bookID, BookDTO bookDTO) throws BookResourceNotFoundException;

	void saveBookSampleChapter(String bookID, MultipartFile sampleChapterFile) throws IOException;
	byte[] getSampleChapter(String bookID) throws BookResourceNotFoundException;

}
