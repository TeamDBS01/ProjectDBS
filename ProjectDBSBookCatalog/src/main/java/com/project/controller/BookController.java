package com.project.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import com.project.exception.PageOutOfBoundsException;
import com.project.service.InventoryInterface;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.project.dto.BookDTO;
import com.project.exception.BookResourceNotFoundException;
import com.project.service.BookServiceImpl;
import org.springframework.web.multipart.MultipartFile;

/**
 * RestFul Controller exposing endpoints for resource of type Book.
 *
 * @author Preethi
 */
@RestController
@RequestMapping("/dbs/books")
@Validated
public class BookController {

    @Autowired
    private BookServiceImpl bookServiceImpl;

    @Autowired
    private InventoryInterface inventoryInterface;

    public static final String DELETED = "Book deleted successfully";


    /**
     * Retrieves a list of all books.
     *
     * @return ResponseEntity - a list of booksDTO
     * @see BookDTO
     */
    @Operation(summary = "Get all books", description = "Retrieves a list of all books.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list of books"),
            @ApiResponse(responseCode = "404", description = "Books not found")
    })

    @GetMapping
    public ResponseEntity<Object> getAllBooks(@RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "6") int size) {
        try {
            List<BookDTO> bookList = bookServiceImpl.getAllBooks(page, size);
            return ResponseEntity.ok(bookList);
        } catch (PageOutOfBoundsException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (BookResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ERROR: " + e.getMessage());
        }
    }

    /**
     * Retrieves the total number of pages of books.
     *
     * @return ResponseEntity containing the number of pages.
     */
    @Operation(summary = "Get number of pages", description = "Retrieves the total number of pages of books.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the number of pages")
    @GetMapping("pages")
    public int pages(){
        return bookServiceImpl.getNoOfPages();
    }

    /**
     * @param bookId ID of the book
     * @return ResponseEntity - Entity of type Book
     * @author Preethi
     * @see BookDTO
     */
    @Operation(summary = "Get book by ID", description = "Retrieves a book by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved book by ID"),
            @ApiResponse(responseCode = "404", description = "Book not found with ID")
    })
    @GetMapping("/{bookId}")
    public ResponseEntity<Object> getBookById(@PathVariable("bookId") String bookId) {
        try {
            BookDTO bookDTO = bookServiceImpl.getBookById(bookId);
            return new ResponseEntity<>(bookDTO, HttpStatus.OK);
        } catch (BookResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ERROR: " + e.getMessage());
        }
    }

    /**
     * Retrieves a book by its title.
     *
     * @param title The title of the book to retrieve.
     * @return ResponseEntity containing the BookDTO.
     */
    @Operation(summary = "Get book by title", description = "Retrieves a book by its title.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the book"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @GetMapping("/title/{title}")
    public ResponseEntity<BookDTO> getBookByTitle(@PathVariable String title) {
        try {
            BookDTO bookDTO = bookServiceImpl.getBookByTitle(title);
            return ResponseEntity.ok(bookDTO);
        } catch (BookResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves a book by its title.
     *
     * @param title The title of the book to retrieve.
     * @return ResponseEntity containing the BookDTO.
     */
    @Operation(summary = "Get book by title", description = "Retrieves a book by its title.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the book"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @GetMapping("/search/{title}")
    public ResponseEntity<List<BookDTO>> searchBooksByTitle(@PathVariable String title) {
        try {
            List<BookDTO> bookDTOs = bookServiceImpl.getBooksByTitle(title);
            return ResponseEntity.ok(bookDTOs);
        } catch (BookResourceNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Retrieves a list of all distinct authors.
     *
     * @return ResponseEntity containing the list of authors.
     */
    @Operation(summary = "Get all authors", description = "Retrieves a list of all distinct authors.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of authors")
    @GetMapping("/authors")
    public ResponseEntity<List<String>> getAllAuthors() {
        List<String> authors = bookServiceImpl.getAllAuthors();
        return new ResponseEntity<>(authors, HttpStatus.OK);
    }

    /**
     * Retrieves a list of all distinct categories.
     *
     * @return ResponseEntity containing the list of categories.
     */
    @Operation(summary = "Get all categories", description = "Retrieves a list of all distinct categories.")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of categories")
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = bookServiceImpl.getAllCategories();
        return new ResponseEntity<>(categories, HttpStatus.OK);
    }


    /**
     * @param categoryName Name of the category
     * @return ResponseEntity containing List of BookDTO
     * @author Preethi
     * @see BookDTO
     */
    @Operation(summary = "Get books by category", description = "Retrieves books by category.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved books of category"),
            @ApiResponse(responseCode = "404", description = "Books not found with specified category")
    })
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<Object> getBooksByCategory(@PathVariable("categoryName") @Valid String categoryName) {
        try {
            List<BookDTO> bookList = bookServiceImpl.getBooksByCategory(categoryName);
            return new ResponseEntity<>(bookList, HttpStatus.OK);
        } catch (BookResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * @param authorName Name of the author
     * @return ResponseEntity- List of BookDTO
     * @author Preethi
     */
    @Operation(summary = "Get books by author", description = "Retrieves books by author.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved books of Authors"),
            @ApiResponse(responseCode = "404", description = "Books not found with specified category")
    })
    @GetMapping("/author/{authorName}")
    public ResponseEntity<Object> getBooksByAuthor(@PathVariable("authorName") @Valid String authorName) {
        try {
            List<BookDTO> bookList = bookServiceImpl.getBooksByAuthor(authorName);
            return new ResponseEntity<>(bookList, HttpStatus.OK);
        } catch (BookResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    /**
     * Filters books by author and/or category.
     *
     * @param author   Name of the author
     * @param category Name of the category
     * @return ResponseEntity- a list of books matching the criteria
     * @throws IllegalArgumentException if no criteria are provided
     * @see BookDTO
     */
    @Operation(summary = "Filter books", description = "Filters books by author and/or category.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved books"),
            @ApiResponse(responseCode = "404", description = "Books not found"),
            @ApiResponse(responseCode = "400", description = "Invalid criteria provided")
    })
//
    @GetMapping("/filter")
    public ResponseEntity<List<BookDTO>> filterBooks(@RequestParam(required = false) String author,
                                                     @RequestParam(required = false) String category) {
        try {
            List<BookDTO> books = bookServiceImpl.filter(author, category);
            return new ResponseEntity<>(books, HttpStatus.OK);
        } catch (BookResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    /**
     * @param bookDTO Details of the book to be added
     * @return ResponseEntity- Status message
     * @author Suryanarayanan
     */
    @Operation(summary = "Add a new book", description = "Adds a new book to the database.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book added successfully"),
            @ApiResponse(responseCode = "400", description = "Failed to add book"),
            @ApiResponse(responseCode = "400", description = "An unexpected error occurred")
    })

    @PostMapping("/addBooks")
    public ResponseEntity<String> addBook(@Valid @RequestBody BookDTO bookDTO) {
        try {
            boolean isAdded = bookServiceImpl.addBook(bookDTO);
            if (isAdded) {
                return new ResponseEntity<>("Book added successfully", HttpStatus.CREATED);
            } else {
                return new ResponseEntity<>("Failed to add book", HttpStatus.CONFLICT);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.EXPECTATION_FAILED);
        }
    }

    /**
     * @param bookID ID of the book to be deleted
     * @return ResponseEntity- Status message or error message
     * @author Suryanarayanan
     */
    @Operation(summary = "Delete a book by ID", description = "Deletes a book from the database using its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted book"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })

    @DeleteMapping("/delete/{bookID}")
    public ResponseEntity<String> deleteBookById(@PathVariable String bookID) {
        try {
            bookServiceImpl.deleteBookById(bookID);
            return new ResponseEntity<>("Book Deleted", HttpStatus.OK);
        } catch (BookResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * @param bookTitle Title of the book to be deleted
     * @return ResponseEntity- Status message or error message
     * @author Suryanarayanan
     */
    @Operation(summary = "Delete a book by title", description = "Deletes a book from the database using its title.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted book"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @DeleteMapping("/deleteByTitle/{bookTitle}")
    public ResponseEntity<String> deleteBookByTitle(@PathVariable String bookTitle) {
        try {
            bookServiceImpl.deleteBookByTitle(bookTitle);
            return new ResponseEntity<>(DELETED, HttpStatus.OK);
        } catch (BookResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * @param bookID  ID of the book to be updated
     * @param bookDTO Updated details of the book
     * @return ResponseEntity- Status message or error message
     * @author Suryanarayanan
     */
    @Operation(summary = "Update a book by ID", description = "Updates the details of a book using its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated book"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @PutMapping("/update/{bookID}")
    public ResponseEntity<String> updateBookById(@PathVariable String bookID, @Valid @RequestBody BookDTO bookDTO) {
        try {
            bookServiceImpl.updateBookById(bookID, bookDTO);
            return new ResponseEntity<>("Book updated successfully", HttpStatus.OK);
        } catch (BookResourceNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Retrieves the quantity of a specific book in the inventory.
     *
     * @param bookID The ID of the book.
     * @return ResponseEntity containing the quantity of the book.
     */
    @Operation(summary = "Get number of books in inventory", description = "Retrieves the quantity of a specific book in the inventory.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved quantity"),
            @ApiResponse(responseCode = "404", description = "Book not found in inventory")
    })
    @GetMapping("/inventory/quantity/{bookID}")
    public ResponseEntity<?> getNoOfBooks(@PathVariable String bookID) {
        return inventoryInterface.getNoOfBooks(bookID);
    }

    /**
     * Updates the quantity of books in the inventory after an order is placed.
     *
     * @param bookIDs A list of book IDs.
     * @param quantities A list of quantities corresponding to the book IDs.
     * @return ResponseEntity indicating the update status.
     */
    @Operation(summary = "Update inventory after order", description = "Updates the quantity of books in the inventory after an order is placed.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PutMapping("/updateAfterOrder")
    public ResponseEntity<?> updateInventoryAfterOrder(@RequestParam List<String> bookIDs,
                                                       @RequestParam List<Integer> quantities) {
        return inventoryInterface.updateInventoryAfterOrder(bookIDs, quantities);
    }

    /**
     * Uploads an image for a book's cover.
     *
     * @param bookID The ID of the book.
     * @param imageFile The image file to upload.
     * @return ResponseEntity containing the upload status.
     */
    @Operation(summary = "Upload book image", description = "Uploads an image for a book's cover.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
            @ApiResponse(responseCode = "400", description = "Upload failed")
    })
    @PostMapping("/{bookID}/upload-image")
    public ResponseEntity<String> uploadImage(@PathVariable String bookID, @RequestParam("imageFile") MultipartFile imageFile) {
        try {
            bookServiceImpl.saveBookImage(bookID, imageFile);
            return ResponseEntity.ok("Image uploaded successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("upload failed");
        }
    }


    /**
     * Uploads a sample chapter (PDF) for a book.
     *
     * @param bookID The ID of the book.
     * @param file The PDF file to upload.
     * @return ResponseEntity containing the upload status.
     */
    @Operation(summary = "Upload sample chapter", description = "Uploads a sample chapter (PDF) for a book.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sample chapter uploaded successfully"),
            @ApiResponse(responseCode = "417", description = "Error uploading sample chapter")
    })
        @PostMapping("/{bookID}/sample")
        public ResponseEntity<String> uploadSampleChapter(@PathVariable String bookID, @RequestParam("file") MultipartFile file) {
            try {
                bookServiceImpl.saveBookSampleChapter(bookID, file);
                return ResponseEntity.ok("Sample chapter uploaded successfully");
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body("Error uploading sample chapter");
            }
        }

    /**
     * Retrieves the sample chapter (PDF) of a book.
     *
     * @param bookID The ID of the book.
     * @return ResponseEntity containing the sample chapter as a PDF.
     * @throws BookResourceNotFoundException if the book is not found.
     */
    @Operation(summary = "Get sample chapter", description = "Retrieves the sample chapter (PDF) of a book.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the sample chapter as a PDF"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @GetMapping("/{bookID}/sample")
    public ResponseEntity<ByteArrayResource> getSampleChapter(@PathVariable String bookID) throws BookResourceNotFoundException {
        byte[] pdfData = bookServiceImpl.getSampleChapter(bookID);
        ByteArrayResource resource = new ByteArrayResource(pdfData);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=sample.pdf");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

        return new ResponseEntity<>(resource, headers, HttpStatus.OK);
    }

    /**
     * Retrieves a sample PDF file from a hardcoded path.
     * <p>
     * Note: This method uses a hardcoded file path and is likely for testing purposes.
     *
     * @return ResponseEntity containing the sample PDF.
     */
    @Operation(summary = "Get sample PDF", description = "Retrieves a sample PDF file from a hardcoded path.") //Added Description
    @ApiResponse(responseCode = "200", description = "Returns a sample PDF")
    @GetMapping("pdf")
    public ResponseEntity<?> getPdf(){
        File pdfFile = new File("C:\\Users\\2387997\\Desktop\\Book Sample\\Harry Potter.pdf");
        byte[] pdfBytes = null;

        try (FileInputStream fis = new FileInputStream(pdfFile)) {
            pdfBytes = new byte[(int) pdfFile.length()];
            fis.read(pdfBytes);
        } catch (IOException e) {
            e.getMessage();
        }
        ByteArrayResource resource = new ByteArrayResource(pdfBytes);

        // Set the response headers
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data.bin");
        headers.add(HttpHeaders.CONTENT_TYPE, "application/octet-stream");

        // Return the binary data as a ResponseEntity
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);

    }



}
