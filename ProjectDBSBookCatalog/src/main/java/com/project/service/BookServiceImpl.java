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
//import com.project.mapper.BookMapper;
import com.project.models.Book;
import com.project.models.Category;
import com.project.repositories.BookRepository;

@Service
public class BookServiceImpl{

	//@Autowired
	private BookRepository bookRepository;
	
	//@Autowired
	private ModelMapper modelMapper;
	
	//@Autowired
	private InventoryInterface inventoryInterface;
	@Autowired
// public ReviewServiceImpl(ReviewRepository reviewRepository, ModelMapper modelMapper) {
	public BookServiceImpl(BookRepository bookRepository, ModelMapper modelMapper) {
		this.bookRepository = bookRepository;
		this.modelMapper = modelMapper;
	}
	
//	public List<BookDTO> getAllBooks() throws BookResourceNotFoundException {
//		List<Book> bookList=bookRepository.findAll();
//		if (bookList.isEmpty()) {
//            throw new BookResourceNotFoundException("No books found");
//        }
//		System.out.println(bookList);
//        return modelMapper.bookListToBookDTOList(bookList);
//    }
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

	 public void updateInventoryOnOrder(List<Long> inventoryIDs, List<Integer> quantities) {

		    for (int i = 0; i < inventoryIDs.size(); i++) {
		        Long inventoryID = inventoryIDs.get(i);
		        int quantity = quantities.get(i);

		        inventoryInterface.updateInventoryAfterOrder(inventoryID, quantity);

		    }
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





//	public boolean addBook(BookDTO bookDTO) {
//		Book book = modelMapper.map(bookDTO, Book.class);
//		System.out.println("" + book);
//		Book addedBook = bookRepository.save(book);
//		return bookDTO.getBookID().equals(addedBook.getBookID());
//	}
//public boolean addBook(BookDTO bookDTO) throws BookResourceNotFoundException {
//	Book book = modelMapper.map(bookDTO, Book.class);
//	Book addedBook = bookRepository.save(book);
//	if (addedBook == null) {
//		return false;
//	}
//	return true;
//}
public boolean addBook(BookDTO bookDTO) throws BookResourceNotFoundException {
		if(bookDTO==null){
			throw new BookResourceNotFoundException("Book resource cannot be null");
		}
	Book book = modelMapper.map(bookDTO, Book.class);
	Book save = bookRepository.save(book);
    return true;
}
//public boolean addBook(BookDTO bookDTO) {
//	try {
//		Book book = modelMapper.map(bookDTO, Book.class);
//		Book addedBook = bookRepository.save(book);
//		return addedBook != null;
//	} catch (Exception e) {
////		logger.error("Error adding book: ", e);
//		throw e;
//	}
//}
	public boolean deleteBookById(String bookID) throws BookResourceNotFoundException {
		Optional<Book> optionalOfBook = bookRepository.findById(bookID);
		if (optionalOfBook.isPresent()) {
			bookRepository.deleteById(bookID);
			return true;
		} else {
			throw new BookResourceNotFoundException("Book Resource not found");
		}
	}

	public boolean deleteBookByTitle(String bookTitle) throws BookResourceNotFoundException {
		Optional<Book> optionalOfBook = bookRepository.findByTitle(bookTitle);
		if (optionalOfBook.isPresent()) {
			bookRepository.deleteByTitle(bookTitle);
			return true;
		} else {
			throw new BookResourceNotFoundException("Book Resource not found");
		}
	}

	public boolean updateBookById(String bookID, BookDTO bookDTO) throws BookResourceNotFoundException {
		Optional<Book> optionalOfBook = bookRepository.findById(bookID);
		if (optionalOfBook.isPresent()) {
			Book book = optionalOfBook.get();
			book.setTitle(bookDTO.getTitle());
			book.setPrice(bookDTO.getPrice());
			book.setInventoryID(bookDTO.getInventoryID());
			book.setAuthorID(bookDTO.getAuthorID());
			book.setCategoryID(bookDTO.getCategoryID());
			//modelMapper.map(bookDTO, book);
			bookRepository.save(book);
			return true;
		} else {
			throw new BookResourceNotFoundException("Book Resource not found");
		}
	}

}
