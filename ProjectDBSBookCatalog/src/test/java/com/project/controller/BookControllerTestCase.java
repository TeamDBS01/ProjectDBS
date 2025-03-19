package com.project.controller;

import com.project.dto.BookDTO;
import com.project.exception.BookResourceNotFoundException;
import com.project.service.BookServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTestCase {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BookServiceImpl bookServiceImpl;


    private BookDTO bookDataTO;

    @BeforeEach
    public void setUp() {
        bookDataTO = new BookDTO();
    }

    @AfterEach
    public void tearDown() {
        bookDataTO = null;
    }

    @Test
    void testGetAllBooks_Positive() throws Exception {
        List<BookDTO> booksDTOList = Collections.singletonList(bookDataTO);
        when(bookServiceImpl.getAllBooks()).thenReturn(booksDTOList);

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].bookID").value(bookDataTO.getBookID()))
                .andReturn();
    }

    @Test
    void testGetAllBooks_Negative() throws Exception {
        when(bookServiceImpl.getAllBooks()).thenThrow(new BookResourceNotFoundException("Book Resource not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("ERROR: Book Resource not found"))
                .andReturn();
    }

    @Test
    void testGetBookById_Positive() throws Exception {
        when(bookServiceImpl.getBookById("B001")).thenReturn(bookDataTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/{bookId}", "B001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.bookID").value(bookDataTO.getBookID()))
                .andReturn();
    }

    @Test
    void testGetBookById_Negative() throws Exception {
        when(bookServiceImpl.getBookById("1")).thenThrow(new BookResourceNotFoundException("Book not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/{bookId}", "1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("ERROR: Book not found"))
                .andReturn();
    }

    @Test
    void testGetBooksByCategory_Positive() throws Exception {
        List<BookDTO> books = Collections.singletonList(bookDataTO);
        when(bookServiceImpl.getBooksByCategory("Fiction")).thenReturn(books);

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/category/{categoryName}", "Fiction"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].bookID").value(bookDataTO.getBookID()))
                .andReturn();
    }

    @Test
    void testGetBooksByCategory_Negative() throws Exception {
        when(bookServiceImpl.getBooksByCategory("Fiction")).thenThrow(new BookResourceNotFoundException("Books not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/category/{categoryName}", "Fiction"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Books not found"))
                .andReturn();
    }

    @Test
    void testGetBooksByAuthor_Success() throws Exception {
        List<BookDTO> books = Collections.singletonList(bookDataTO);
        when(bookServiceImpl.getBooksByAuthor("Author")).thenReturn(books);

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/author/{authorName}", "Author"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].bookID").value(bookDataTO.getBookID()))
                .andReturn();
    }

    @Test
    void testGetBooksByAuthor_NotFound() throws Exception {
        when(bookServiceImpl.getBooksByAuthor("Author")).thenThrow(new BookResourceNotFoundException("Books not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/author/{authorName}", "Author"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Books not found"))
                .andReturn();
    }

    @Test
    void testFilterBooks_ByAuthor_Success() throws Exception {
        List<BookDTO> books = Collections.singletonList(bookDataTO);
        when(bookServiceImpl.filter("Author")).thenReturn(books);

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/filter")
                        .param("author", "Author"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"bookID\":null,\"title\":null,\"price\":0.0,\"authorID\":0,\"categoryID\":0}]"))
                .andReturn();
    }

    @Test
    void testFilterBooks_ByAuthorAndCategory_Success() throws Exception {
        List<BookDTO> books = Collections.singletonList(bookDataTO);
        when(bookServiceImpl.filter("Author", "Fiction")).thenReturn(books);

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/filter")
                        .param("author", "Author")
                        .param("category", "Fiction"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"bookID\":null,\"title\":null,\"price\":0.0,\"authorID\":0,\"categoryID\":0}]"))
                .andReturn();
    }

    @Test
    void testFilterBooks_ByCategory_Success() throws Exception {
        List<BookDTO> books = Collections.singletonList(bookDataTO);
        when(bookServiceImpl.filter("Fiction")).thenReturn(books);

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/filter")
                        .param("category", "Fiction"))
                .andExpect(status().isOk())
                .andExpect(content().json("[{\"bookID\":null,\"title\":null,\"price\":0.0,\"authorID\":0,\"categoryID\":0}]"))
                .andReturn();
    }

    @Test
    void testFilterBooks_NoCriteria() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/filter"))
                .andExpect(status().isBadRequest())
                .andReturn();
    }

    @Test
    void testAddBook_Success() throws Exception {
        BookDTO validBookDTO = new BookDTO();
        validBookDTO.setBookID("B001");
        validBookDTO.setTitle("Valid Title");
        validBookDTO.setPrice(100.0);
        validBookDTO.setAuthorID(1);
        validBookDTO.setCategoryID(1);

        when(bookServiceImpl.addBook(validBookDTO)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/dbs/books/addBooks")
                        .contentType("application/json")
                        .content("{\"bookID\":\"B001\",\"title\":\"Valid Title\",\"price\":100.0,\"authorID\":1,\"categoryID\":1}"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Book added successfully"))
                .andReturn();
    }

    @Test
    void testAddBook_Exception() throws Exception {
        BookDTO validBookDTO = new BookDTO();
        validBookDTO.setBookID("B001");
        validBookDTO.setTitle("Valid title");
        validBookDTO.setPrice(10.0);
        validBookDTO.setAuthorID(1);
        validBookDTO.setCategoryID(1);

        when(bookServiceImpl.addBook(validBookDTO)).thenThrow(new RuntimeException("Unexpected error"));

        mockMvc.perform(MockMvcRequestBuilders.post("/dbs/books/addBooks")
                        .contentType("application/json")
                        .content("{\"bookID\":\"B001\",\"title\":\"Valid title\",\"price\":10.0,\"authorID\":1,\"categoryID\":1}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("An unexpected error occurred"))
                .andReturn();
    }

    @Test
    void testDeleteBookById_Success() throws Exception {
        String bookID = "B001";
        when(bookServiceImpl.deleteBookById(bookID)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/dbs/books/delete/{bookId}", bookID))
                .andExpect(status().isOk())
                .andExpect(content().string("Book deleted successfully"))
                .andReturn();
    }

    @Test
    void testDeleteBookById_NotFound() throws Exception {
        String bookID = "B001";
        when(bookServiceImpl.deleteBookById(bookID)).thenThrow(new BookResourceNotFoundException("Book not found"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/dbs/books/delete/{bookID}", bookID))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book not found"))
                .andReturn();
    }

    @Test
    void testDeleteBookByTitle_Success() throws Exception {
        String bookTitle = "Valid Title";
        when(bookServiceImpl.deleteBookByTitle(bookTitle)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/dbs/books/deleteByTitle/{bookTitle}", bookTitle))
                .andExpect(status().isOk())
                .andExpect(content().string("Book deleted successfully"))
                .andReturn();
    }

    @Test
    void testDeleteBookByTitle_NotFound() throws Exception {
        String bookTitle = "Valid Title";
        when(bookServiceImpl.deleteBookByTitle(bookTitle)).thenThrow(new BookResourceNotFoundException("Book not found"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/dbs/books/deleteByTitle/{bookTitle}", bookTitle))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book not found"))
                .andReturn();
    }

    @Test
    void testUpdateBookById_Success() throws Exception {
        BookDTO validBookDTO = new BookDTO();
        validBookDTO.setBookID("B001");
        validBookDTO.setTitle("Updated Title");
        validBookDTO.setPrice(20.0);
        validBookDTO.setAuthorID(1);
        validBookDTO.setCategoryID(1);

        when(bookServiceImpl.updateBookById("B001", validBookDTO)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.put("/dbs/books/update/{bookID}", "B001")
                        .contentType("application/json")
                        .content("{\"bookID\":\"B001\",\"title\":\"Updated Title\",\"price\":20.0,\"authorID\":1,\"categoryID\":1}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Book updated successfully"))
                .andReturn();
    }

    @Test
    void testUpdateBookById_NotFound() throws Exception {
        BookDTO validBookDTO = new BookDTO();
        validBookDTO.setBookID("B001");
        validBookDTO.setTitle("Updated Title");
        validBookDTO.setPrice(20.0);
        validBookDTO.setAuthorID(1);
        validBookDTO.setCategoryID(1);

        when(bookServiceImpl.updateBookById("B001", validBookDTO)).thenThrow(new BookResourceNotFoundException("Book not found"));

        mockMvc.perform(MockMvcRequestBuilders.put("/dbs/books/update/{bookID}", "B001")
                        .contentType("application/json")
                        .content("{\"bookID\":\"B001\",\"title\":\"Updated Title\",\"price\":20.0,\"authorID\":1,\"categoryID\":1}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book not found"))
                .andReturn();
    }
}
