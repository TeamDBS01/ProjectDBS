package com.project.controller;

import com.project.dto.BookDTO;
import com.project.exception.BookResourceNotFoundException;
import com.project.service.BookServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookController.class)
class BookControllerTestCase {

    @Autowired
    private MockMvc mockMvc;

    @Mock
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
    void testDeleteBookById_Positive() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/dbs/books/delete/{bookID}", "B001"))
                .andExpect(status().isOk())
                .andExpect(content().string("DELETED"))
                .andReturn();
    }

    @Test
    void testDeleteBookById_Negative() throws Exception {
        when(bookServiceImpl.deleteBookById("B001")).thenThrow(new BookResourceNotFoundException("Book not found"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/dbs/books/delete/{bookID}", "B001"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book not found"))
                .andReturn();
    }

    @Test
    void testDeleteBookByTitle_Positive() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/dbs/books/deleteByTitle/{bookTitle}", "Some Title"))
                .andExpect(status().isOk())
                .andExpect(content().string("DELETED"))
                .andReturn();
    }

    @Test
    void testDeleteBookByTitle_Negative() throws Exception {
        when(bookServiceImpl.deleteBookByTitle("Some Title")).thenThrow(new BookResourceNotFoundException("Book not found"));

        mockMvc.perform(MockMvcRequestBuilders.delete("/dbs/books/deleteByTitle/{bookTitle}", "Some Title"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book not found"))
                .andReturn();
    }

    @Test
    void testUpdateBookById_Positive() throws Exception {
        BookDTO updatedBookDTO = new BookDTO();
        updatedBookDTO.setBookID("B001");
        updatedBookDTO.setTitle("Updated Title");

        mockMvc.perform(MockMvcRequestBuilders.put("/dbs/books/update/{bookID}", "B001")
                        .contentType("application/json")
                        .content("{\"bookID\":\"B001\",\"title\":\"Updated Title\"}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Book updated successfully"))
                .andReturn();
    }

    @Test
    void testUpdateBookById_Negative() throws Exception {
        BookDTO updatedBookDTO = new BookDTO();
        updatedBookDTO.setBookID("B001");
        updatedBookDTO.setTitle("Updated Title");

        when(bookServiceImpl.updateBookById("B001", updatedBookDTO)).thenThrow(new BookResourceNotFoundException("Book not found"));

        mockMvc.perform(MockMvcRequestBuilders.put("/dbs/books/update/{bookID}", "B001")
                        .contentType("application/json")
                        .content("{\"bookID\":\"B001\",\"title\":\"Updated Title\"}"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book not found"))
                .andReturn();
    }

    @Test
    void testUploadImage_Positive() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("imageFile", "image.jpg", "image/jpeg", "image content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/dbs/books/{bookID}/upload-image", "B001")
                        .file(imageFile))
                .andExpect(status().isOk())
                .andExpect(content().string("Image uploaded successfully"))
                .andReturn();
    }



    @Test
    void testUploadSampleChapter_Positive() throws Exception {
        MockMultipartFile sampleFile = new MockMultipartFile("file", "sample.pdf", "application/pdf", "sample content".getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart("/dbs/books/{bookID}/sample", "B001")
                        .file(sampleFile))
                .andExpect(status().isOk())
                .andExpect(content().string("Sample chapter uploaded successfully"))
                .andReturn();
    }



    @Test
    void testGetSampleChapter_Positive() throws Exception {
        byte[] pdfData = "sample pdf content".getBytes();
        when(bookServiceImpl.getSampleChapter("B001")).thenReturn(pdfData);

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/{bookID}/sample", "B001"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sample.pdf"))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
                .andExpect(content().bytes(pdfData))
                .andReturn();
    }

    @Test
    void testGetSampleChapter_Negative() throws Exception {
        when(bookServiceImpl.getSampleChapter("B001")).thenThrow(new BookResourceNotFoundException("Book not found"));

        mockMvc.perform(MockMvcRequestBuilders.get("/dbs/books/{bookID}/sample", "B001"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book not found"))
                .andReturn();
    }
}
