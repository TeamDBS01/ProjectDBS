package com.project.controller;

import java.util.List;

import com.project.exception.BookAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.dto.InventoryDTO;
import com.project.exception.BookNotFoundException;
import com.project.exception.OutOfStockException;
import com.project.services.InventoryService;

@RestController
@RequestMapping("/dbs/inventory")
public class InventoryControllerImpl implements InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @Override
    public ResponseEntity<?>displayInventory() {
        try {
            List<InventoryDTO> inventoryDTOS = inventoryService.displayInventory();
            return new ResponseEntity<>(inventoryDTOS, HttpStatus.OK);
        } catch (BookNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Inventory is Empty");
        }
    }

    @Override
    public ResponseEntity<?> getInventoryByBookID(@PathVariable String bookID) {
      try {
          InventoryDTO inventoryDTO = inventoryService.getInventoryByBookID(bookID);
          return ResponseEntity.ok(inventoryDTO);
      }catch(BookNotFoundException e){
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Such books found in the inventory");
      }
    }

    @Override
    public ResponseEntity<?> getNoOfBooks(@PathVariable String bookID) {
        try {
            int noOfBooks = inventoryService.getNoOfBooks(bookID);
            return ResponseEntity.ok(noOfBooks);
        } catch (BookNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No such book in inventory");
        }
    }

    @Override
    public ResponseEntity<?> updateAddInventory(@RequestParam String bookID, @RequestParam int quantity) {
        try {
            inventoryService.updateAddInventory(bookID, quantity);
            return ResponseEntity.ok("Addition successful");
        } catch (BookNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Updation failed - No book failed with given bookID");
        }
    }

    @Override
    public ResponseEntity<String> updateRemoveInventory(@RequestParam String bookID, @RequestParam int quantity) {
        try {
            inventoryService.updateRemoveInventory(bookID, quantity);
            return ResponseEntity.ok("Reduction successful");
        } catch (BookNotFoundException e) {
            return ResponseEntity.status(404).body("Book not found");
        }
    }

    @Override
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
    public ResponseEntity<String> updateInventoryAfterOrder(@RequestParam List<String> bookIDs, @RequestParam List<Integer> quantities) {
        inventoryService.updateInventoryAfterOrder(bookIDs, quantities);
        return ResponseEntity.ok("Inventory updated after order");
    }

    @Override
    public ResponseEntity<String> addBookToInventory(@RequestParam String bookID, @RequestParam int quantity) throws BookAlreadyExistsException {
      try {
          inventoryService.addBookToInventory(bookID, quantity);
          return ResponseEntity.ok("Book added to inventory successfully");
      }catch(BookAlreadyExistsException e){
          return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book with the given bookid Already exist");
      }
    }

    @Override
    public ResponseEntity<?> deleteBookFromInventory(@PathVariable  String bookID) throws BookNotFoundException {
        try {
            inventoryService.deleteBookFromInventory(bookID);
            return ResponseEntity.ok("Book deleted from inventory successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book with the given bookid Doesnot exist");
        }
    }
}