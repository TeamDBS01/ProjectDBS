package com.project.controller;

import com.project.dto.BookDTO;
import com.project.exception.BookResourceNotFoundException;
import com.project.exception.PageOutOfBoundsException;
import com.project.service.BookServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * RestFul Controller exposing endpoints for resource of type Book.
 * @author Preethi
 */
@RestController
@RequestMapping("/dbs/books")
@Validated
public class BookController {

    @Autowired
    private BookServiceImpl bookServiceImpl;
    public static final String DELETED = "Book deleted successfully";


    /**
     * Retrieves a list of all books.
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
                                              @RequestParam(defaultValue = "10") int size) {
        try {
            List<BookDTO> bookList = bookServiceImpl.getAllBooks(page, size);
            return ResponseEntity.ok(bookList);
        } catch (PageOutOfBoundsException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
        catch (BookResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("ERROR: " + e.getMessage());
        }
    }
    /**
     * @author Preethi
     * @param bookId ID of the book
     * @return ResponseEntity - Entity of type Book
     * @see BookDTO
     */
    @Operation(summary = "Get book by ID", description = "Retrieves a book by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved book by ID"),
            @ApiResponse(responseCode = "404", description = "Book not found with ID")
    })
    @GetMapping("/{bookId}")
    public ResponseEntity<Object> getBookById(@PathVariable("bookId")  String bookId){
    	try {
    		BookDTO bookDTO=bookServiceImpl.getBookById(bookId);
    		return new ResponseEntity<>(bookDTO, HttpStatus.OK);
    	}catch(BookResourceNotFoundException e) {
            return new ResponseEntity<>(new BookDTO(),HttpStatus.NO_CONTENT);
    		//return ResponseEntity.status(HttpStatus.OK).body("ERROR: "+e.getMessage());
    	}
    }

    /**
     * @author Preethi
     * @param categoryName Name of the category
     * @return ResponseEntity - List of BookDTO
     * @see BookDTO
     */
    @Operation(summary = "Get books by category", description = "Retrieves books by category.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved books of category"),
            @ApiResponse(responseCode = "404", description = "Books not found with specified category")
    })
    @GetMapping("/category/{categoryName}")
    public ResponseEntity<Object> getBooksByCategory(@PathVariable("categoryName") @Valid String categoryName){
        try {
            List<BookDTO> bookList=bookServiceImpl.getBooksByCategory(categoryName);
            return new ResponseEntity<>(bookList, HttpStatus.OK);
        }catch(BookResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    /**
     * @author Preethi
     * @param authorName Name of the author
     * @return ResponseEntity- List of BookDTO
     */
    @Operation(summary = "Get books by author", description = "Retrieves books by author.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved books of Authors"),
            @ApiResponse(responseCode = "404", description = "Books not found with specified category")
    })
    @GetMapping("/author/{authorName}")
    public ResponseEntity<Object> getBooksByAuthor(@PathVariable("authorName") @Valid String authorName){
        try {
            List<BookDTO> bookList=bookServiceImpl.getBooksByAuthor(authorName);
            return new ResponseEntity<>(bookList, HttpStatus.OK);
        }catch(BookResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }


    /**
     * Filters books by author and/or category.
     *
     * @param author Name of the author
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
    @GetMapping("/filter")
public ResponseEntity<List<BookDTO>> filterBooks(@RequestParam(required = false) @Valid String author,
                                                 @RequestParam(required = false) @Valid String category) {
    try {
        List<BookDTO> books;
        if (author != null && category != null) {
            books = bookServiceImpl.filter(author, category);
        } else if (author != null) {
            books = bookServiceImpl.filter(author);
        } else if (category != null) {
            books = bookServiceImpl.filter(category);
        } else {
            throw new IllegalArgumentException("At least one criterion must be provided");
        }
        return new ResponseEntity<>(books, HttpStatus.OK);
    } catch (BookResourceNotFoundException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
    } catch (IllegalArgumentException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
    }
}


    /**
     * @author Suryanarayanan
     * @param bookDTO Details of the book to be added
     * @return ResponseEntity- Status message
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
                return new ResponseEntity<>("Failed to add book", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            return new ResponseEntity<>("An unexpected error occurred", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * @author Suryanarayanan
     * @param bookID ID of the book to be deleted
     * @return ResponseEntity- Status message or error message
     */
    @Operation(summary = "Delete a book by ID", description = "Deletes a book from the database using its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted book"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })

    @DeleteMapping("/delete/{bookID}")
    public ResponseEntity<String> deleteBookById(@PathVariable String bookID){
        try{
            bookServiceImpl.deleteBookById(bookID);
            return new ResponseEntity<>(DELETED, HttpStatus.OK);
        }catch(BookResourceNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * @author Suryanarayanan
     * @param bookTitle Title of the book to be deleted
     * @return ResponseEntity- Status message or error message
     */
    @Operation(summary = "Delete a book by title", description = "Deletes a book from the database using its title.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted book"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @DeleteMapping("/deleteByTitle/{bookTitle}")
    public ResponseEntity<String> deleteBookByTitle(@PathVariable String bookTitle){
        try{
            bookServiceImpl.deleteBookByTitle(bookTitle);
            return new ResponseEntity<>(DELETED, HttpStatus.OK);
        }catch(BookResourceNotFoundException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    /**
     * @author Suryanarayanan
     * @param bookID ID of the book to be updated
     * @param bookDTO Updated details of the book
     * @return ResponseEntity- Status message or error message
     */
    @Operation(summary = "Update a book by ID", description = "Updates the details of a book using its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated book"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @PutMapping("/update/{bookID}")
    public ResponseEntity<String> updateBookById(@PathVariable String bookID, @Valid @RequestBody BookDTO bookDTO){
        try{
            bookServiceImpl.updateBookById(bookID, bookDTO);
            return new ResponseEntity<>("Book updated successfully",HttpStatus.OK);
        }catch(BookResourceNotFoundException e){
            return new ResponseEntity<>(e.getMessage(),HttpStatus.NOT_FOUND);
        }
    }
}
