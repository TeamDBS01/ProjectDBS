package com.project.service;

import org.springframework.stereotype.Component;

public interface InventoryInterface {
	//InventoryDTO getInventoryByBookID(long inventoryID);
	void updateInventoryAfterOrder(long inventoryID, int quantity);
}
