package com.project.service;

import java.util.List;
import java.util.stream.Collectors;

import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;

import com.project.dto.BookDTO;
import com.project.exception.BookResourceNotFoundException;
import com.project.mapper.BookMapper;
import com.project.models.Book;
import com.project.repositories.BookRepository;

@Service
public class BookServiceImpl {
	
	@Autowired
	private BookRepository bookRepository;
	
	@Autowired
	private final BookMapper bookMapper = BookMapper.INSTANCE;
	
	
	public List<BookDTO> getAllBooks() throws BookResourceNotFoundException {
		List<Book> bookList=bookRepository.findAll();
		if (bookList.isEmpty()) {
            throw new BookResourceNotFoundException("No books found");
        }
        return bookList.stream()
                       .map(bookMapper::bookToBookDTO)
                       .collect(Collectors.toList());
    }
		
	
	//public Book getBookById() throws BookResourceNotFoundException;
}
