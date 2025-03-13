package com.project.services;

import java.util.List;

import com.project.dto.InventoryDTO;

public interface InventoryService {
    List<InventoryDTO> displayInventory();
    InventoryDTO getInventoryByBookID(String bookID);
    void updateAddInventory(String bookID, int quantity);
    void updateRemoveInventory(String bookID, int quantity);
    void updateInventoryAfterOrder(List<String> bookIDs, List<Integer> quantities);
    void checkAndNotifyLowStock(String bookID);
    void placeOrder(String bookID, int quantity);
	int getNoOfBooks(String bookID);
    void addBookToInventory(String bookID, int quantity);
    void deleteBookFromInventory(String bookID);
}
