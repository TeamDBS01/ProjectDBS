package com.project.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name="INVENTORY-SERVICE")
public interface InventoryInterface {

	@PostMapping("dbs/inventory/add")
	ResponseEntity<String> addBookToInventory(@RequestParam String bookID, @RequestParam int quantity);

	@DeleteMapping("dbs/inventory/{bookID}")
	ResponseEntity<String> deleteBookFromInventory(@PathVariable String bookID);

	}
