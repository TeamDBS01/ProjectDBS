package com.project.controller;

import java.util.List;

import com.project.exception.BookAlreadyExistsException;
import com.project.exception.InsufficientInventoryException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.dto.InventoryDTO;
import com.project.exception.BookNotFoundException;
import com.project.exception.OutOfStockException;
import com.project.services.InventoryService;

@RestController
@Validated
public class InventoryControllerImpl implements InventoryController {


    private InventoryService inventoryService;

    @Autowired
    public InventoryControllerImpl(InventoryService inventoryService){
        this.inventoryService = inventoryService;
    }
    @Override
    @Operation(summary = "Display Inventory", description = "Retrieves a list of all books and their quantities from the inventory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
            @ApiResponse(responseCode = "404", description = "Inventory Empty")
    })
    public ResponseEntity<?> displayInventory() {
        try {
            List<InventoryDTO> inventoryDTOS = inventoryService.displayInventory();
            return new ResponseEntity<>(inventoryDTOS, HttpStatus.OK);
        } catch (BookNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Inventory is Empty");
        }
    }

    @Override
    @Operation(summary="Get Inventory by Book ID", description = "Retrieves inventory details for a specific book ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved book details"),
            @ApiResponse(responseCode = "404", description = "No Such books found in the inventory")
    })
    public ResponseEntity<?> getInventoryByBookID(@PathVariable String bookID) {
        try {
            InventoryDTO inventoryDTO = inventoryService.getInventoryByBookID(bookID);
            return ResponseEntity.ok(inventoryDTO);
        } catch (BookNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Such books found in the inventory");
        }
    }

    @Override
    @Operation(summary = "Get Number of Books", description = "Retrieves the number of books available for a specific book ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved number of books"),
            @ApiResponse(responseCode = "404", description = "No such book in inventory")
    })
    public ResponseEntity<?> getNoOfBooks(@PathVariable String bookID) {
        try {
            int noOfBooks = inventoryService.getNoOfBooks(bookID);
            return ResponseEntity.ok(noOfBooks);
        } catch (BookNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such book in inventory");
        }
    }

    @Override
    @Operation(summary = "Update Add Inventory", description = "Adds a specified quantity of the book with given book ID to the inventory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Addition successful"),
            @ApiResponse(responseCode = "404", description = "Updation failed - No book found with given bookID")
    })
    public ResponseEntity<?> updateAddInventory(@RequestParam String bookID, @RequestParam int quantity) {
        try {
            inventoryService.updateAddInventory(bookID, quantity);
            return ResponseEntity.ok("Addition successful");
        } catch (BookNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Updation failed - No book found with given bookID");
        }
    }

    @Override
    @Operation(summary = "Update Remove Inventory", description = "Removes a specified quantity of the book with given book ID to the inventory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reduction successful"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "400", description = "Not enough books in Inventory")
    })
    public ResponseEntity<String> updateRemoveInventory(@RequestParam String bookID, @RequestParam int quantity) {
        try {
            inventoryService.updateRemoveInventory(bookID, quantity);
            return ResponseEntity.ok("Reduction successful");
        } catch (BookNotFoundException e) {
            return ResponseEntity.status(404).body("Book not found");
        } catch (InsufficientInventoryException e) {
            return ResponseEntity.status(400).body("Not enough books in Inventory");
        }
    }

    @Override
    @Operation(summary = "Place Order", description = "Verifies if an order can be placed by checking if there is enough stock of given book/books")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order can be placed"),
            @ApiResponse(responseCode = "404", description = "Book not found"),
            @ApiResponse(responseCode = "409", description = "Stock unavailable")
    })
    public ResponseEntity<String> placeOrder(@RequestParam String bookID, @RequestParam int quantity) {
        try {
            inventoryService.placeOrder(bookID, quantity);
            return ResponseEntity.ok("Order can be placed");
        } catch (BookNotFoundException e) {
            return ResponseEntity.status(404).body("Book not found");
        } catch (OutOfStockException e) {
            int noOfBooks = inventoryService.getNoOfBooks(bookID);
            return ResponseEntity.status(409).body("Stock unavailable.\nAvailable number of books: " + noOfBooks);
        }
    }

    @Override
    @Operation(summary = "Update Inventory After Order", description = "Updates the inventory after an order is placed")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Inventory updated after order"),
            @ApiResponse(responseCode = "400", description = "Book IDs list and Quantities list must be of the same size")
    })
    public ResponseEntity<String> updateInventoryAfterOrder(@RequestParam List<String> bookIDs, @RequestParam List<Integer> quantities) {
        if (bookIDs.size() != quantities.size()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Book IDs list and Quantities list must be of the same size");
        }
        inventoryService.updateInventoryAfterOrder(bookIDs, quantities);
        return ResponseEntity.ok("Inventory updated after order");
    }

    @Override
    @Operation(summary = "Add Book to Inventory", description = "Adds a new book to the inventory")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book added to inventory successfully"),
            @ApiResponse(responseCode = "404", description = "Book with the given bookID already exists")
    })
    public ResponseEntity<String> addBookToInventory(@RequestParam String bookID, @RequestParam int quantity) throws BookAlreadyExistsException {
        try {
            inventoryService.addBookToInventory(bookID, quantity);
            return ResponseEntity.ok("Book added to inventory successfully");
        } catch (BookAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book with the given bookID already exists");
        }
    }

    @Override
    @Operation(summary = "Delete Book from Inventory", description = "Deletes a book from the inventory by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book deleted from inventory successfully"),
            @ApiResponse(responseCode = "404", description = "Book with the given book ID does not exist")
    })
    public ResponseEntity<?> deleteBookFromInventory(@PathVariable String bookID) throws BookNotFoundException {
        try {
            inventoryService.deleteBookFromInventory(bookID);
            return ResponseEntity.ok("Book deleted from inventory successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book with the given book ID does not exist");
        }
    }
}