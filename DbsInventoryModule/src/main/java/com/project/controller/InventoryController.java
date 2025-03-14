package com.project.controller;

import java.util.List;

import com.project.dto.InventoryDTO;
import com.project.exception.BookAlreadyExistsException;
import com.project.exception.BookNotFoundException;
import com.project.exception.OutOfStockException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dbs/inventory")
public interface InventoryController {

    @GetMapping("")
    ResponseEntity<?> displayInventory();

    @GetMapping("/{bookID}")
    ResponseEntity<?> getInventoryByBookID(@PathVariable String bookID) throws BookNotFoundException;

    @GetMapping("/quantity/{bookID}")
    ResponseEntity<?> getNoOfBooks(@PathVariable String bookID) throws BookNotFoundException;

    @PutMapping("/update/add")
    ResponseEntity<?> updateAddInventory(@RequestParam String bookID, @RequestParam int quantity) throws BookNotFoundException;

    @PutMapping("/update/remove")
    ResponseEntity<?> updateRemoveInventory(@RequestParam String bookID, @RequestParam int quantity) throws BookNotFoundException;

    @PostMapping("/order")
    ResponseEntity<?> placeOrder(@RequestParam String bookID, @RequestParam int quantity) throws BookNotFoundException, OutOfStockException;

    @PutMapping("/updateAfterOrder")
    ResponseEntity<?> updateInventoryAfterOrder(@RequestParam List<String> bookIDs, @RequestParam List<Integer> quantities);

    @PostMapping("/add")
    ResponseEntity<?> addBookToInventory(@RequestParam String bookID, @RequestParam int quantity) throws BookAlreadyExistsException;

    @DeleteMapping("/{bookID}")
    ResponseEntity<?> deleteBookFromInventory(@PathVariable String bookID) throws BookNotFoundException;
}