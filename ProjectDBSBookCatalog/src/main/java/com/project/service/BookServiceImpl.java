package com.project.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.dto.BookDTO;
import com.project.exception.BookResourceNotFoundException;
import com.project.models.Book;
import com.project.repositories.BookRepository;

@Service
public class BookServiceImpl implements BookService{

	private BookRepository bookRepository;
	
	private ModelMapper modelMapper;
	
	private InventoryInterface inventoryInterface;

	private static final String BOOK_NOT_FOUND_MESSAGE = "Book Resource not found";


	@Autowired
	public BookServiceImpl(BookRepository bookRepository, ModelMapper modelMapper, InventoryInterface inventoryInterface) {
		this.bookRepository = bookRepository;
		this.modelMapper = modelMapper;
		this.inventoryInterface=inventoryInterface;
	}

	public List<BookDTO> getAllBooks() throws BookResourceNotFoundException {
		List<Book> bookList = bookRepository.findAll();
		if (bookList.isEmpty()) {
			throw new BookResourceNotFoundException("No books found");
		}
		return bookList.stream()
				.map(book -> modelMapper.map(book, BookDTO.class))
				.collect(Collectors.toList());
	}
	

	 public BookDTO getBookById(String bookId) throws BookResourceNotFoundException{
		 Book book = bookRepository.findById(bookId)
		            .orElseThrow(() -> new BookResourceNotFoundException("No book with ID found: " + bookId));
		    return modelMapper.map(book, BookDTO.class);
	 }

	 public List<BookDTO> getBooksByCategory(String categoryName) throws BookResourceNotFoundException{
		 List<Book> bookList=bookRepository.getByCategory(categoryName);

		 if (bookList.isEmpty()) {
	            throw new BookResourceNotFoundException("No books found");
	        }
		 return bookList.stream()
				 .map(book -> modelMapper.map(book, BookDTO.class))
				 .collect(Collectors.toList());
	 }

	 public List<BookDTO> getBooksByAuthor(String authorName) throws BookResourceNotFoundException{
		 List<Book> bookList=bookRepository.getByAuthor(authorName);
		 if (bookList.isEmpty()) {
	            throw new BookResourceNotFoundException("No books found");
	        }
	        return bookList.stream()
					.map(book -> modelMapper.map(book, BookDTO.class))
					.collect(Collectors.toList());
	 }

	 public List<BookDTO> filter(String... criteria) throws BookResourceNotFoundException {
		 if (criteria.length == 0) {
			 throw new IllegalArgumentException("At least one criterion must be provided");
		 }

		 List<Book> filteredBooks = new ArrayList<>();

		 if (criteria.length == 1) {
			 String criterion = criteria[0];
			 filteredBooks.addAll(bookRepository.getByAuthor(criterion));
			 filteredBooks.addAll(bookRepository.getByCategory(criterion));
		 } else if (criteria.length == 2) {
			 String author = criteria[0];
			 String category = criteria[1];
			 List<Book> booksByAuthor = bookRepository.getByAuthor(author);
			 for (Book book : booksByAuthor) {
				 if (bookRepository.getByCategory(category).contains(book)) {
					 filteredBooks.add(book);
				 }
			 }
		 }

		 if (filteredBooks.isEmpty()) {
			 throw new BookResourceNotFoundException("No books found for the given criteria");
		 }

		 return filteredBooks.stream()
				 .map(book -> modelMapper.map(book, BookDTO.class))
				 .collect(Collectors.toList());
	 }

public boolean addBook(BookDTO bookDTO) throws BookResourceNotFoundException {
		if(bookDTO==null){
			throw new BookResourceNotFoundException("Book resource cannot be null");
		}
	Book book = modelMapper.map(bookDTO, Book.class);
	Book save = bookRepository.save(book);
	inventoryInterface.addBookToInventory(save.getBookID(), 1); // Assuming quantity is 1 for simplicity

	return true;
}
	public boolean deleteBookById(String bookID) throws BookResourceNotFoundException {
		Optional<Book> optionalOfBook = bookRepository.findById(bookID);
		if (optionalOfBook.isPresent()) {
			bookRepository.deleteById(bookID);
			inventoryInterface.deleteBookFromInventory(bookID);
			return true;
		} else {
			throw new BookResourceNotFoundException(BOOK_NOT_FOUND_MESSAGE);
		}
	}

	public boolean deleteBookByTitle(String bookTitle) throws BookResourceNotFoundException {
		Optional<Book> optionalOfBook = bookRepository.findByTitle(bookTitle);
		if (optionalOfBook.isPresent()) {
			bookRepository.deleteByTitle(bookTitle);
			return true;
		} else {
			throw new BookResourceNotFoundException(BOOK_NOT_FOUND_MESSAGE);
		}
	}

	public boolean updateBookById(String bookID, BookDTO bookDTO) throws BookResourceNotFoundException {
		Optional<Book> optionalOfBook = bookRepository.findById(bookID);
		if (optionalOfBook.isPresent()) {
			Book book = optionalOfBook.get();
			modelMapper.map(bookDTO, book);
			bookRepository.save(book);
			return true;
		} else {
			throw new BookResourceNotFoundException(BOOK_NOT_FOUND_MESSAGE);
		}
	}

}
