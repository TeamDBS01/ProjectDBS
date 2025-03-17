package com.project.controller;


import com.project.dto.BookDTO;
import com.project.exception.BookResourceNotFoundException;
import com.project.service.BookServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class BookControllerTestCase {
    @Mock
    private BookServiceImpl bookServiceImpl;

    @InjectMocks
    private BookController bookController;

    private BookDTO bookDataTO;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        bookDataTO = new BookDTO();
    }

    @AfterEach
    public void tearDown(){
        bookDataTO = null;
    }

    @Test
    void testGetAllBooks_Positive() throws BookResourceNotFoundException {
        List<BookDTO> booksDTOList = Collections.singletonList(bookDataTO);
        when(bookServiceImpl.getAllBooks()).thenReturn(booksDTOList);

        ResponseEntity<?> response = bookController.getAllBooks();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Book added successfully", response.getBody());
    }

    @Test
    void testGetAllBooks_negative() throws BookResourceNotFoundException{
        when(bookServiceImpl.getAllBooks()).thenThrow(new BookResourceNotFoundException("Book Resource not found"));

        ResponseEntity<?> response=bookController.getAllBooks();

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Book Resource not found", response.getBody());
    }

    @Test
    void testGetBookById_positive() throws BookResourceNotFoundException{

        when(bookServiceImpl.getBookById("B001")).thenReturn(bookDataTO);

        ResponseEntity<?> result=bookController.getBookById("B001");

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("result:"+ bookDataTO, result.getBody());
    }

    @Test
    void testGetBookById_negative() throws BookResourceNotFoundException{
        when(bookServiceImpl.getBookById("1")).thenThrow(new BookResourceNotFoundException("Book not found"));

        ResponseEntity<?> response = bookController.getBookById("1");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Book not found", response.getBody());
    }


    @Test
   void testGetBooksByCategory_Positive() throws BookResourceNotFoundException {
        List<BookDTO> books = Collections.singletonList(bookDataTO);
        when(bookServiceImpl.getBooksByCategory("Fiction")).thenReturn(books);

        ResponseEntity<?> response = bookController.getBooksByCategory("Fiction");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("bookList:"+books, response.getBody());
    }

    @Test
    void testGetBooksByCategory_NotFound() throws BookResourceNotFoundException {
        when(bookServiceImpl.getBooksByCategory("Fiction")).thenThrow(new BookResourceNotFoundException("Books not found"));

        ResponseEntity<?> response = bookController.getBooksByCategory("Fiction");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Books not found", response.getBody());
    }
    @Test
    void testGetBooksByAuthor_Success() throws BookResourceNotFoundException {
        List<BookDTO> books = Collections.singletonList(bookDataTO);
        when(bookServiceImpl.getBooksByAuthor("Author")).thenReturn(books);

        ResponseEntity<?> response = bookController.getBooksByAuthor("Author");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("bookList:"+books, response.getBody());
    }

    @Test
    void testGetBooksByAuthor_NotFound() throws BookResourceNotFoundException {
        when(bookServiceImpl.getBooksByAuthor("Author")).thenThrow(new BookResourceNotFoundException("Books not found"));

        ResponseEntity<?> response = bookController.getBooksByAuthor("Author");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Books not found", response.getBody());
    }

    @Test
    void testFilterBooks_ByAuthorAndCategory_Success() throws BookResourceNotFoundException {
        List<BookDTO> books = Collections.singletonList(bookDataTO);
        when(bookServiceImpl.filter("Author", "Fiction")).thenReturn(books);

        ResponseEntity<List<BookDTO>> response = bookController.filterBooks("Author", "Fiction");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(books, response.getBody());
    }

    @Test
    void testFilterBooks_ByAuthor_Success() throws BookResourceNotFoundException {
        List<BookDTO> books = Collections.singletonList(bookDataTO);
        when(bookServiceImpl.filter("Author")).thenReturn(books);

        ResponseEntity<List<BookDTO>> response = bookController.filterBooks("Author", null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(books, response.getBody());
    }

    @Test
    void testFilterBooks_ByCategory_Success() throws BookResourceNotFoundException {
        List<BookDTO> books = Collections.singletonList(bookDataTO);
        when(bookServiceImpl.filter("Fiction")).thenReturn(books);

        ResponseEntity<List<BookDTO>> response = bookController.filterBooks(null, "Fiction");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(books, response.getBody());
    }

    @Test
    void testFilterBooks_NoCriteria() {
        ResponseEntity<List<BookDTO>> response = bookController.filterBooks(null, null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testAddBook_Success() throws BookResourceNotFoundException {
        when(bookServiceImpl.addBook(bookDataTO)).thenReturn(true);

        ResponseEntity<String> response = bookController.addBook(bookDataTO);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Book added successfully", response.getBody());
    }

    @Test
    void testAddBook_NullBookDTO() throws BookResourceNotFoundException {
        when(bookServiceImpl.addBook(null)).thenThrow(new BookResourceNotFoundException("Book Resource cannot be null"));

        ResponseEntity<String> response = bookController.addBook(null);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("An unexpected error occurred", response.getBody());
    }

    @Test
    void testAddBook_Exception() throws BookResourceNotFoundException {
        when(bookServiceImpl.addBook(bookDataTO)).thenThrow(new RuntimeException("Unexpected error"));

        ResponseEntity<String> response = bookController.addBook(bookDataTO);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("An unexpected error occurred", response.getBody());
    }

    @Test
    void testDeleteBookById_Success() throws BookResourceNotFoundException {
        String bookID = "1";
        when(bookServiceImpl.deleteBookById(bookID)).thenReturn(true);

        ResponseEntity<?> response = bookController.deleteBookById(bookID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("result:"+true, response.getBody());
    }

    @Test
    void testDeleteBookById_NotFound() throws BookResourceNotFoundException {
        String bookID = "1";
        when(bookServiceImpl.deleteBookById(bookID)).thenThrow(new BookResourceNotFoundException("Book not found"));

        ResponseEntity<?> response = bookController.deleteBookById(bookID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Book not found", response.getBody());
    }

    @Test
    void testDeleteBookByTitle_Success() throws BookResourceNotFoundException {
        String bookTitle = "Effective Java";
        when(bookServiceImpl.deleteBookByTitle(bookTitle)).thenReturn(true);

        ResponseEntity<?> response = bookController.deleteBookByTitle(bookTitle);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("result:"+true, response.getBody());
    }

    @Test
    void testDeleteBookByTitle_NotFound() throws BookResourceNotFoundException {
        String bookTitle = "Effective Java";
        when(bookServiceImpl.deleteBookByTitle(bookTitle)).thenThrow(new BookResourceNotFoundException("Book not found"));

        ResponseEntity<?> response = bookController.deleteBookByTitle(bookTitle);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Book not found", response.getBody());
    }

    @Test
    void testUpdateBookById_Success() throws BookResourceNotFoundException {
        String bookID = "1";
        BookDTO bookObj= new BookDTO(); // Initialize with appropriate values
        when(bookServiceImpl.updateBookById(bookID, bookObj)).thenReturn(true);

        ResponseEntity<?> response = bookController.updateBookById(bookID, bookObj);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("result:"+true, response.getBody());
    }

    @Test
    void testUpdateBookById_NotFound() throws BookResourceNotFoundException {
        String bookID = "1";
        BookDTO bookDTO = new BookDTO(); // Initialize with appropriate values
        when(bookServiceImpl.updateBookById(bookID, bookDTO)).thenThrow(new BookResourceNotFoundException("Book not found"));

        ResponseEntity<?> response = bookController.updateBookById(bookID, bookDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Book not found", response.getBody());
    }

}

