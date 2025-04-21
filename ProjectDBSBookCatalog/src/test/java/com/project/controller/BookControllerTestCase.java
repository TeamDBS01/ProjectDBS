package com.project.controller;

import com.project.dto.BookDTO;
import com.project.exception.BookResourceNotFoundException;
import com.project.exception.PageOutOfBoundsException;
import com.project.service.BookServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@WebMvcTest(BookController.class)
@ExtendWith(MockitoExtension.class)
class BookControllerTestCase {
    private MockMvc mockMvc;

    @Mock
    private BookServiceImpl bookServiceImpl;
    private BookDTO bookDataTO;

    @InjectMocks
    private BookController bookController;

    @BeforeEach
    public void setUp() {
        bookDataTO = new BookDTO();
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @AfterEach
    public void tearDown() {
        bookDataTO = null;
    }
    @Test
    void getAllBooks_positive_withDefaultPagination() throws Exception {
        List<BookDTO> bookList = Arrays.asList(new BookDTO(), new BookDTO(), new BookDTO());
        when(bookServiceImpl.getAllBooks(0, 3)).thenReturn(bookList);

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));

        verify(bookServiceImpl, times(1)).getAllBooks(0, 3);
    }

    @Test
    void getAllBooks_positive_withSpecificPage() throws Exception {
        List<BookDTO> bookList = Arrays.asList(new BookDTO(), new BookDTO());
        when(bookServiceImpl.getAllBooks(1, 3)).thenReturn(bookList);

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books?page=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(bookServiceImpl, times(1)).getAllBooks(1, 3);
    }

    @Test
    void getAllBooks_positive_withSpecificSize() throws Exception {
        List<BookDTO> bookList = Arrays.asList(new BookDTO(), new BookDTO(), new BookDTO(), new BookDTO(), new BookDTO());
        when(bookServiceImpl.getAllBooks(0, 5)).thenReturn(bookList);

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books?size=5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(5));

        verify(bookServiceImpl, times(1)).getAllBooks(0, 5);
    }

    @Test
    void getAllBooks_negative_pageOutOfBounds() throws Exception {
        when(bookServiceImpl.getAllBooks(anyInt(), anyInt())).thenThrow(new PageOutOfBoundsException("Page out of bounds"));

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books?page=100"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Page out of bounds"));

        verify(bookServiceImpl, times(1)).getAllBooks(100, 3);
    }

    @Test
    void getAllBooks_negative_resourceNotFound() throws Exception {
        when(bookServiceImpl.getAllBooks(anyInt(), anyInt())).thenThrow(new BookResourceNotFoundException("Books not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books?page=0"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("ERROR: Books not found"));

        verify(bookServiceImpl, times(1)).getAllBooks(0, 3);
    }

    @Test
    void pages_positive_returnsNumberOfPages() throws Exception {
        when(bookServiceImpl.getNoOfPages()).thenReturn(5);

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/pages"))
                .andExpect(status().isOk());


        verify(bookServiceImpl, times(1)).getNoOfPages();
    }

    // No explicit negative test case for 'pages' as it directly returns an int.
    // Negative scenarios would likely involve exceptions in the service layer,
    // which without explicit handling in the controller, would result in a 500.

    @Test
    void getBookById_positive_validId() throws Exception {
        when(bookServiceImpl.getBookById("1")).thenReturn(bookDataTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(bookServiceImpl, times(1)).getBookById("1");
    }

    @Test
    void getBookById_negative_invalidId() throws Exception {
        when(bookServiceImpl.getBookById("2")).thenThrow(new BookResourceNotFoundException("Book not found with ID: 2"));

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/2"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("ERROR: Book not found with ID: 2"));

        verify(bookServiceImpl, times(1)).getBookById("2");
    }

    @Test
    void getBookByTitle_positive_existingTitle() throws Exception {
        when(bookServiceImpl.getBookByTitle("Test Book")).thenReturn(bookDataTO);

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/title/Test Book"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(bookServiceImpl, times(1)).getBookByTitle("Test Book");
    }

    @Test
    void getBookByTitle_negative_nonExistingTitle() throws Exception {
        when(bookServiceImpl.getBookByTitle("Non Existing Book")).thenThrow(new BookResourceNotFoundException("Book with title 'Non Existing Book' not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/title/Non Existing Book"))
                .andExpect(status().isNotFound());

        verify(bookServiceImpl, times(1)).getBookByTitle("Non Existing Book");
    }

    @Test
    void searchBooksByTitle_positive_matchingTitle() throws Exception {
        List<BookDTO> bookList = Collections.singletonList(bookDataTO);
        when(bookServiceImpl.getBooksByTitle("Test")).thenReturn(bookList);

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/search/Test"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        verify(bookServiceImpl, times(1)).getBooksByTitle("Test");
    }

    @Test
    void searchBooksByTitle_negative_noMatchingTitle() throws Exception {
        when(bookServiceImpl.getBooksByTitle("Non Matching Title")).thenThrow(new BookResourceNotFoundException("No books found with title 'Non Matching Title'"));

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/search/Non Matching Title"))
                .andExpect(status().isNotFound());

        verify(bookServiceImpl, times(1)).getBooksByTitle("Non Matching Title");
    }

    @Test
    void getAllAuthors_positive_returnsAuthorList() throws Exception {
        List<String> authors = Arrays.asList("Author 1", "Author 2");
        when(bookServiceImpl.getAllAuthors()).thenReturn(authors);

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/authors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0]").value("Author 1"))
                .andExpect(jsonPath("$[1]").value("Author 2"));

        verify(bookServiceImpl, times(1)).getAllAuthors();
    }

    @Test
    void getAllAuthors_positive_noAuthors() throws Exception {
        when(bookServiceImpl.getAllAuthors()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/authors"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(bookServiceImpl, times(1)).getAllAuthors();
    }

    @Test
    void getAllCategories_positive() throws Exception {
        List<String> categories = Arrays.asList("Fiction", "Science", "History");
        when(bookServiceImpl.getAllCategories()).thenReturn(categories);

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0]").value("Fiction"))
                .andExpect(jsonPath("$[1]").value("Science"))
                .andExpect(jsonPath("$[2]").value("History"));

        verify(bookServiceImpl, times(1)).getAllCategories();
    }

    @Test
    void getAllCategories_noCategories() throws Exception {
        when(bookServiceImpl.getAllCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));

        verify(bookServiceImpl, times(1)).getAllCategories();
    }
    @Test
    void getBooksByCategory_positive() throws Exception {
        List<BookDTO> bookList = Arrays.asList(new BookDTO(), new BookDTO());
        when(bookServiceImpl.getBooksByCategory("Science")).thenReturn(bookList);

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/category/Science"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(bookServiceImpl, times(1)).getBooksByCategory("Science");
    }


    @Test
    void getBooksByAuthor_positive() throws Exception {
        List<BookDTO> bookList = Arrays.asList(new BookDTO(), new BookDTO());
        when(bookServiceImpl.getBooksByAuthor("John Doe")).thenReturn(bookList);

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/author/John Doe"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(bookServiceImpl, times(1)).getBooksByAuthor("John Doe");
    }

    @Test
    void getBooksByAuthor_notFound() throws Exception {
        when(bookServiceImpl.getBooksByAuthor("UnknownAuthor")).thenThrow(new BookResourceNotFoundException("Books not found with specified author"));

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/author/UnknownAuthor"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Books not found with specified author"));

        verify(bookServiceImpl, times(1)).getBooksByAuthor("UnknownAuthor");
    }
    @Test
    void filterBooks_positive_withAuthorAndCategory() throws Exception {
        List<BookDTO> bookList = Arrays.asList(new BookDTO(), new BookDTO());
        when(bookServiceImpl.filter("John Doe", "Science")).thenReturn(bookList);

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/filter")
                    .param("author", "John Doe")
                        .param("category", "Science"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(bookServiceImpl, times(1)).filter("John Doe", "Science");
    }

    @Test
    void filterBooks_notFound() throws Exception {
        when(bookServiceImpl.filter("UnknownAuthor", "UnknownCategory")).thenThrow(new BookResourceNotFoundException("Books not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/filter")
                        .param("author", "UnknownAuthor")
                        .param("category", "UnknownCategory"))
                .andExpect(status().isNotFound());

        verify(bookServiceImpl, times(1)).filter("UnknownAuthor", "UnknownCategory");
    }


}
