package com.project.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.dto.BookDTO;
import com.project.exception.BookResourceNotFoundException;
import com.project.exception.PageOutOfBoundsException;
import com.project.service.BookServiceImpl;
import com.project.service.InventoryInterface;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
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
    @Mock
    private InventoryInterface inventoryInterface;

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
        when(bookServiceImpl.getAllBooks(0, 6)).thenReturn(bookList);

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(3));

        verify(bookServiceImpl, times(1)).getAllBooks(0, 6);
    }

    @Test
    void getAllBooks_positive_withSpecificPage() throws Exception {
        List<BookDTO> bookList = Arrays.asList(new BookDTO(), new BookDTO());
        when(bookServiceImpl.getAllBooks(1, 6)).thenReturn(bookList);

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books?page=1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2));

        verify(bookServiceImpl, times(1)).getAllBooks(1, 6);
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

        verify(bookServiceImpl, times(1)).getAllBooks(100, 6);
    }

    @Test
    void getAllBooks_negative_resourceNotFound() throws Exception {
        when(bookServiceImpl.getAllBooks(anyInt(), anyInt())).thenThrow(new BookResourceNotFoundException("Books not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books?page=0"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("ERROR: Books not found"));

        verify(bookServiceImpl, times(1)).getAllBooks(0, 6);
    }

    @Test
    void pages_positive_returnsNumberOfPages() throws Exception {
        when(bookServiceImpl.getNoOfPages()).thenReturn(6);

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
    void testGetBooksByCategory_NotFound() throws Exception {
        String categoryName = "NonExistentCategory";
        String e = "Category not found";
when(bookServiceImpl.getBooksByCategory(categoryName)).thenThrow(new BookResourceNotFoundException(e));

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/category/{categoryName}", categoryName)).andExpect(status().isNotFound());
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

    @Test
    void addBook_positive_successfulCreation() throws Exception {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setBookID("B015");
        bookDTO.setTitle("New Book Title");
        bookDTO.setPrice(19.99);
        bookDTO.setAuthorName("Test Author");
        bookDTO.setCategoryName("Test Category");
        bookDTO.setDescription("A new test book.");
        bookDTO.setBase64img("base64encodedimage");

        when(bookServiceImpl.addBook(bookDTO)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.post("/dbs/books/addBooks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bookDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Book added successfully"));

        verify(bookServiceImpl, times(1)).addBook(bookDTO);
    }

    @Test
    void addBook_negative_conflict() throws Exception {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setBookID("B015");
        bookDTO.setTitle("Conflicting Book");
        bookDTO.setPrice(29.99);
        bookDTO.setAuthorName("Another Author");
        bookDTO.setCategoryName("Another Category");
        bookDTO.setDescription("A book that will conflict.");
        bookDTO.setBase64img("anotherbase64image");

        when(bookServiceImpl.addBook(bookDTO)).thenReturn(false);

        mockMvc.perform(MockMvcRequestBuilders.post("/dbs/books/addBooks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(bookDTO)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Failed to add book"));

        verify(bookServiceImpl, times(1)).addBook(bookDTO);
    }

    @Test
    void deleteBookById_positive_bookDeleted() throws Exception {
        String bookIDToDelete = "B020";
        when(bookServiceImpl.deleteBookById(bookIDToDelete)).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.delete("/dbs/books/delete/{bookID}", bookIDToDelete))
                .andExpect(status().isOk())
                .andExpect(content().string("Book Deleted"));

        verify(bookServiceImpl, times(1)).deleteBookById(bookIDToDelete);
    }
    @Test
    void deleteBookById_negative_bookNotFound() throws Exception {
        String nonExistingBookID = "B021";
        doThrow(new BookResourceNotFoundException("Book with ID " + nonExistingBookID + " not found"))
                .when(bookServiceImpl).deleteBookById(nonExistingBookID);

        mockMvc.perform(MockMvcRequestBuilders.delete("/dbs/books/delete/{bookID}", nonExistingBookID))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book with ID " + nonExistingBookID + " not found"));

        verify(bookServiceImpl, times(1)).deleteBookById(nonExistingBookID);
    }
    @Test
    void deleteBookByTitle_positive_bookDeleted() throws Exception {
        String titleToDelete = "The Vanishing";
        when(bookServiceImpl.deleteBookByTitle(titleToDelete)).thenReturn(true); // Or false, or whatever it returns

        mockMvc.perform(MockMvcRequestBuilders.delete("/dbs/books/deleteByTitle/{bookTitle}", titleToDelete))
                .andExpect(status().isOk())
                .andExpect(content().string("Book deleted successfully")); // Assuming DELETED constant is "Deleted"

        verify(bookServiceImpl, times(1)).deleteBookByTitle(titleToDelete);
    }

    @Test
    void deleteBookByTitle_negative_bookNotFound() throws Exception {
        String nonExistingTitle = "The Nonexistent Book";
        doThrow(new BookResourceNotFoundException("Book with title '" + nonExistingTitle + "' not found"))
                .when(bookServiceImpl).deleteBookByTitle(nonExistingTitle);

        mockMvc.perform(MockMvcRequestBuilders.delete("/dbs/books/deleteByTitle/{bookTitle}", nonExistingTitle))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book with title '" + nonExistingTitle + "' not found"));

        verify(bookServiceImpl, times(1)).deleteBookByTitle(nonExistingTitle);
    }
    @Test
    void updateBookById_positive_bookUpdated() throws Exception {
        String bookIDToUpdate = "B030";
        BookDTO updatedBookDTO = new BookDTO();
        updatedBookDTO.setTitle("Updated Title");
        updatedBookDTO.setPrice(29.99);

        when(bookServiceImpl.updateBookById(eq(bookIDToUpdate), any(BookDTO.class))).thenReturn(true);

        mockMvc.perform(MockMvcRequestBuilders.put("/dbs/books/update/{bookID}", bookIDToUpdate)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedBookDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string("Book updated successfully"));

        verify(bookServiceImpl, times(1)).updateBookById(eq(bookIDToUpdate), any(BookDTO.class));
    }

    @Test
    void updateBookById_negative_bookNotFound() throws Exception {
        String nonExistingBookID = "B031";
        BookDTO updatedBookDTO = new BookDTO();
        updatedBookDTO.setTitle("Updated Title");
        updatedBookDTO.setPrice(29.99);

        doThrow(new BookResourceNotFoundException("Book with ID " + nonExistingBookID + " not found"))
                .when(bookServiceImpl).updateBookById(eq(nonExistingBookID), any(BookDTO.class));

        mockMvc.perform(MockMvcRequestBuilders.put("/dbs/books/update/{bookID}", nonExistingBookID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(updatedBookDTO)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book with ID " + nonExistingBookID + " not found"));

        verify(bookServiceImpl, times(1)).updateBookById(eq(nonExistingBookID), any(BookDTO.class));
    }
//    @Test
//    void getNoOfBooks_positive_quantityRetrieved() throws Exception {
//        String bookIDToCheck = "B040";
//        when(inventoryInterface.getNoOfBooks(bookIDToCheck))
//                .thenReturn(new ResponseEntity<>(5, HttpStatus.OK)); // Assuming quantity is 5
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/inventory/quantity/{bookID}", bookIDToCheck))
//                .andExpect(status().isOk())
//                .andExpect(content().string("5")); // Assuming the quantity is directly returned as string
//    }

    @Test
    void getNoOfBooks_negative_bookNotFound() throws Exception {
        String nonExistingBookID = "B041";
        when(inventoryInterface.getNoOfBooks(nonExistingBookID))
                .thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/inventory/quantity/{bookID}", nonExistingBookID))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateInventoryAfterOrder_positive_inventoryUpdated() throws Exception {
        List<String> bookIDsToUpdate = Arrays.asList("B050", "B051");
        List<Integer> quantitiesToUpdate = Arrays.asList(2, 1);
        when(inventoryInterface.updateInventoryAfterOrder(bookIDsToUpdate, quantitiesToUpdate))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(MockMvcRequestBuilders.put("/dbs/books/updateAfterOrder")
                        .param("bookIDs", "B050", "B051")
                        .param("quantities", "2", "1"))
                .andExpect(status().isOk());

        verify(inventoryInterface, times(1)).updateInventoryAfterOrder(bookIDsToUpdate, quantitiesToUpdate);
    }
    @Test
    void updateInventoryAfterOrder_negative_invalidInput() throws Exception {
        List<String> bookIDsToUpdate = Arrays.asList("B060");
        List<Integer> quantitiesToUpdate = Arrays.asList(); // Mismatched quantities
        when(inventoryInterface.updateInventoryAfterOrder(bookIDsToUpdate, quantitiesToUpdate))
                .thenReturn(new ResponseEntity<>(HttpStatus.BAD_REQUEST));

        mockMvc.perform(MockMvcRequestBuilders.put("/dbs/books/updateAfterOrder")
                        .param("bookIDs", "B060")
                        .param("quantities", "")) // Simulate invalid quantities
                .andExpect(status().isBadRequest());

        verify(inventoryInterface, times(1)).updateInventoryAfterOrder(bookIDsToUpdate, quantitiesToUpdate);
    }
    @Test
    void uploadImage_positive_imageUploaded() throws Exception {
        String bookIDToUpload = "B070";
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "test.jpg", "image/jpeg", "some image data".getBytes());
        doNothing().when(bookServiceImpl).saveBookImage(eq(bookIDToUpload), any(MultipartFile.class));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/dbs/books/{bookID}/upload-image", bookIDToUpload)
                        .file(imageFile))
                .andExpect(status().isOk())
                .andExpect(content().string("Image uploaded successfully"));

        verify(bookServiceImpl, times(1)).saveBookImage(eq(bookIDToUpload), any(MultipartFile.class));
    }

    @Test
    void uploadSampleChapter_positive_chapterUploaded() throws Exception {
        String bookIDToUpload = "B080";
        MockMultipartFile sampleFile = new MockMultipartFile("file", "sample.pdf", "application/pdf", "some pdf data".getBytes());
        doNothing().when(bookServiceImpl).saveBookSampleChapter(eq(bookIDToUpload), any(MultipartFile.class));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/dbs/books/{bookID}/sample", bookIDToUpload)
                        .file(sampleFile))
                .andExpect(status().isOk())
                .andExpect(content().string("Sample chapter uploaded successfully"));

        verify(bookServiceImpl, times(1)).saveBookSampleChapter(eq(bookIDToUpload), any(MultipartFile.class));
    }
    @Test
    void uploadSampleChapter_negative_uploadFailed() throws Exception {
        String bookIDToUpload = "B081";
        MockMultipartFile sampleFile = new MockMultipartFile("file", "sample.pdf", "application/pdf", "some pdf data".getBytes());
        doThrow(new IOException("Error saving sample chapter")).when(bookServiceImpl).saveBookSampleChapter(eq(bookIDToUpload), any(MultipartFile.class));

        mockMvc.perform(MockMvcRequestBuilders.multipart("/dbs/books/{bookID}/sample", bookIDToUpload)
                        .file(sampleFile))
                .andExpect(status().isExpectationFailed())
                .andExpect(content().string("Error uploading sample chapter"));

        verify(bookServiceImpl, times(1)).saveBookSampleChapter(eq(bookIDToUpload), any(MultipartFile.class));
    }
    @Test
    void getSampleChapter_positive_chapterRetrieved() throws Exception {
        String bookIDToGet = "B090";
        byte[] pdfData = "some pdf data".getBytes();
        when(bookServiceImpl.getSampleChapter(bookIDToGet)).thenReturn(pdfData);

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/{bookID}/sample", bookIDToGet))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sample.pdf"))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/octet-stream"))
                .andExpect(content().bytes(pdfData));

        verify(bookServiceImpl, times(1)).getSampleChapter(bookIDToGet);
    }
//    @Test
//    void getSampleChapter_negative_bookNotFound() throws Exception {
//        String nonExistingBookID = "B091";
//        when(bookServiceImpl.getSampleChapter(nonExistingBookID))
//                .thenThrow(new BookResourceNotFoundException("Sample chapter not found for book ID " + nonExistingBookID));
//
//        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/{bookID}/sample", nonExistingBookID))
//                .andExpect(status().isNotFound())
//                .andExpect(content().string("Sample chapter not found for book ID " + nonExistingBookID));
//
//        verify(bookServiceImpl, times(1)).getSampleChapter(nonExistingBookID);
//    }
    @Test
    void getPdf_positive_pdfRetrieved() throws Exception {
        // Assuming the file "C:\\Users\\2387997\\Desktop\\Book Sample\\Harry Potter.pdf" exists
        File pdfFile = new File("C:\\Users\\2387997\\Desktop\\Book Sample\\Harry Potter.pdf");
        byte[] pdfBytes = new byte[(int) pdfFile.length()];
        try (FileInputStream fis = new FileInputStream(pdfFile)) {
            fis.read(pdfBytes);
        }

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.bin"))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "application/octet-stream"))
                .andExpect(content().bytes(pdfBytes));
    }
    @Test
    void getPdf_negative_ioException() throws Exception {
        // Mocking FileInputStream to throw an IOException
        File pdfFile = new File("nonexistent_path.pdf"); // Use a path that will likely cause an error

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/pdf"))
                .andExpect(status().isOk());
    }
}
