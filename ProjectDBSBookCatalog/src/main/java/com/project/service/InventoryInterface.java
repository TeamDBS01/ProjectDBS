package com.project.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@FeignClient(name="INVENTORY-SERVICE")
public interface InventoryInterface {
	//InventoryDTO getInventoryByBookID(long inventoryID);

	void updateInventoryAfterOrder(long inventoryID, int quantity);
}
