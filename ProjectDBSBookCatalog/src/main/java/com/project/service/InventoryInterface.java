package com.project.service;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name="INVENTORY-SERVICE")
public interface InventoryInterface {

	@PostMapping("dbs/inventory/add")
	ResponseEntity<String> addBookToInventory(@RequestParam String bookID, @RequestParam int quantity);

	@DeleteMapping("dbs/inventory/{bookID}")
	ResponseEntity<String> deleteBookFromInventory(@PathVariable String bookID);

	@GetMapping("dbs/inventory/quantity/{bookID}")
	ResponseEntity<?> getNoOfBooks(@PathVariable String bookID);

	@PutMapping("/updateAfterOrder")
	ResponseEntity<?> updateInventoryAfterOrder(@RequestParam  List<String> bookIDs,
												@RequestParam  List<Integer> quantities);



}
