package com.project.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name="INVENTORY-SERVICE")
public interface InventoryInterface {

	@PostMapping("dbs/inventory/add")
	ResponseEntity<String> addBookToInventory(@RequestParam String bookID, @RequestParam int quantity);

	@DeleteMapping("dbs/inventory/{bookID}")
	ResponseEntity<String> deleteBookFromInventory(@PathVariable String bookID);

	}
