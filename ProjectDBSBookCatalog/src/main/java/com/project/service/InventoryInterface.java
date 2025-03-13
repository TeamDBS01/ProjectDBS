package com.project.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="INVENTORY-SERVICE")
public interface InventoryInterface {
	//InventoryDTO getInventoryByBookID(long inventoryID);

	@GetMapping("/dbs/inventory/quantity/{bookID}")
	ResponseEntity<?> getNoOfBooks(@PathVariable String bookID);

//	void updateInventoryAfterOrder(long inventoryID, int quantity);
}
