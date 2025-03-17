package com.project.controller;

import java.util.List;

import com.project.dto.InventoryDTO;
import com.project.exception.BookAlreadyExistsException;
import com.project.exception.BookNotFoundException;
import com.project.exception.OutOfStockException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dbs/inventory")
public interface InventoryController {

    @GetMapping("")
    ResponseEntity<?> displayInventory();

    @GetMapping("/{bookID}")
    ResponseEntity<?> getInventoryByBookID(@PathVariable @NotBlank(message = "Book ID cannot be blank") String bookID) throws BookNotFoundException;

    @GetMapping("/quantity/{bookID}")
    ResponseEntity<?> getNoOfBooks(@PathVariable @NotBlank(message = "Book ID cannot be blank") String bookID) throws BookNotFoundException;

    @PutMapping("/update/add")
    ResponseEntity<?> updateAddInventory(@RequestParam @NotBlank(message = "Book ID cannot be blank") String bookID,
                                         @RequestParam @Min(value = 1, message = "Quantity must be at least 1") int quantity) throws BookNotFoundException;

    @PutMapping("/update/remove")
    ResponseEntity<?> updateRemoveInventory(@RequestParam @NotBlank(message = "Book ID cannot be blank") String bookID,
                                            @RequestParam @Min(value = 1, message = "Quantity must be at least 1") int quantity) throws BookNotFoundException;

    @PostMapping("/order")
    ResponseEntity<?> placeOrder(@RequestParam @NotBlank(message = "Book ID cannot be blank") String bookID,
                                 @RequestParam @Min(value = 1, message = "Quantity must be at least 1") int quantity) throws BookNotFoundException, OutOfStockException;

    @PutMapping("/updateAfterOrder")
    ResponseEntity<?> updateInventoryAfterOrder(@RequestParam @NotNull @Size(min = 1, message = "Book IDs list cannot be empty") List<@NotBlank String> bookIDs,
                                                @RequestParam @NotNull @Size(min = 1, message = "Quantities list cannot be empty") List<@Min(value = 0, message = "Quantity cannot be negative") Integer> quantities);

    @PostMapping("/add")
    ResponseEntity<?> addBookToInventory(@RequestParam @NotBlank(message = "Book ID cannot be blank") String bookID,
                                         @RequestParam @Min(value = 1, message = "Quantity must be at least 1") int quantity) throws BookAlreadyExistsException;

    @DeleteMapping("/{bookID}")
    ResponseEntity<?> deleteBookFromInventory(@PathVariable @NotBlank(message = "Book ID cannot be blank") String bookID) throws BookNotFoundException;
}