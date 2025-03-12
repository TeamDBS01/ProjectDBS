package com.project.controller;


import com.project.dto.BookDTO;
import com.project.exception.BookResourceNotFoundException;
import com.project.service.BookServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class BookControllerTestCase {
    @Mock
    private BookServiceImpl bookServiceImpl;

    @InjectMocks
    private BookController bookController;

    private BookDTO bookDTO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        bookDTO = new BookDTO();
    }

    @AfterEach
    public void tearDown(){

    }

    @Test
    public void testGetAllBooks_Positive() throws BookResourceNotFoundException {
        List<BookDTO> booksDTO = Arrays.asList(bookDTO);
        when(bookServiceImpl.getAllBooks()).thenReturn(booksDTO);

        ResponseEntity<?> response = bookController.getAllBooks();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(booksDTO, response.getBody());
    }

    @Test
    public void testGetAllBooks_negative() throws BookResourceNotFoundException{
        when(bookServiceImpl.getAllBooks()).thenThrow(new BookResourceNotFoundException("Book Resource not found"));

        ResponseEntity<?> response=bookController.getAllBooks();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Book Resource not found", response.getBody());
    }

    @Test
    public void testGetBookById_positive() throws BookResourceNotFoundException{

        when(bookServiceImpl.getBookById("B001")).thenReturn(bookDTO);

        ResponseEntity<?> result=bookController.getBookById("B001");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals(bookDTO, result.getBody());
    }

    @Test
    public void testGetBookById_negative() throws BookResourceNotFoundException{
        when(bookServiceImpl.getBookById("1")).thenThrow(new BookResourceNotFoundException("Book not found"));

        ResponseEntity<?> response = bookController.getBookById("1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Book not found", response.getBody());
    }


    @Test
    public void testGetBooksByCategory_Positive() throws BookResourceNotFoundException {
        List<BookDTO> books = Arrays.asList(bookDTO);
        when(bookServiceImpl.getBooksByCategory("Fiction")).thenReturn(books);

        ResponseEntity<?> response = bookController.getBooksByCategory("Fiction");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(books, response.getBody());
    }

    @Test
    public void testGetBooksByCategory_NotFound() throws BookResourceNotFoundException {
        when(bookServiceImpl.getBooksByCategory("Fiction")).thenThrow(new BookResourceNotFoundException("Books not found"));

        ResponseEntity<?> response = bookController.getBooksByCategory("Fiction");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Books not found", response.getBody());
    }
    @Test
    public void testGetBooksByAuthor_Success() throws BookResourceNotFoundException {
        List<BookDTO> books = Arrays.asList(bookDTO);
        when(bookServiceImpl.getBooksByAuthor("Author")).thenReturn(books);

        ResponseEntity<?> response = bookController.getBooksByAuthor("Author");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(books, response.getBody());
    }

    @Test
    public void testGetBooksByAuthor_NotFound() throws BookResourceNotFoundException {
        when(bookServiceImpl.getBooksByAuthor("Author")).thenThrow(new BookResourceNotFoundException("Books not found"));

        ResponseEntity<?> response = bookController.getBooksByAuthor("Author");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Books not found", response.getBody());
    }

    @Test
    public void testFilterBooks_ByAuthorAndCategory_Success() throws BookResourceNotFoundException {
        List<BookDTO> books = Arrays.asList(bookDTO);
        when(bookServiceImpl.filter("Author", "Fiction")).thenReturn(books);

        ResponseEntity<List<BookDTO>> response = bookController.filterBooks("Author", "Fiction");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(books, response.getBody());
    }

    @Test
    public void testFilterBooks_ByAuthor_Success() throws BookResourceNotFoundException {
        List<BookDTO> books = Arrays.asList(bookDTO);
        when(bookServiceImpl.filter("Author")).thenReturn(books);

        ResponseEntity<List<BookDTO>> response = bookController.filterBooks("Author", null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(books, response.getBody());
    }

    @Test
    public void testFilterBooks_ByCategory_Success() throws BookResourceNotFoundException {
        List<BookDTO> books = Arrays.asList(bookDTO);
        when(bookServiceImpl.filter("Fiction")).thenReturn(books);

        ResponseEntity<List<BookDTO>> response = bookController.filterBooks(null, "Fiction");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(books, response.getBody());
    }

    @Test
    public void testFilterBooks_NoCriteria() {
        ResponseEntity<List<BookDTO>> response = bookController.filterBooks(null, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void testAddBook_Success() throws BookResourceNotFoundException {
        when(bookServiceImpl.addBook(bookDTO)).thenReturn(true);

        ResponseEntity<String> response = bookController.addBook(bookDTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Book added successfully", response.getBody());
    }

    @Test
    public void testAddBook_NullBookDTO() throws BookResourceNotFoundException {
        when(bookServiceImpl.addBook(null)).thenThrow(new BookResourceNotFoundException("Book Resource cannot be null"));

        ResponseEntity<String> response = bookController.addBook(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Book resource cannot be null", response.getBody());
    }

    @Test
    public void testAddBook_Exception() throws BookResourceNotFoundException {
        when(bookServiceImpl.addBook(bookDTO)).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<String> response = bookController.addBook(bookDTO);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred", response.getBody());
    }


}

