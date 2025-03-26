package com.project.services;

import java.util.List;

import com.project.dto.InventoryDTO;

/**
 * Service interface for managing inventory operations.
 */
public interface InventoryService {
    /**
     * Displays the entire inventory with pagination.
     *
     * @param page the page number to retrieve (starting from 0).
     * @param size the number of items per page.
     * @return a list of InventoryDTO containing the inventory details.
     */
    List<InventoryDTO> displayInventory(int page, int size);

    /**
     * Retrieves inventory details for a specific book by its ID.
     * @param bookID the ID of the book.
     * @return an InventoryDTO containing the book's inventory details.
     */
    InventoryDTO getInventoryByBookID(String bookID);

    /**
     * Adds a specified quantity of a book to the inventory.
     * @param bookID the ID of the book.
     * @param quantity the quantity to add.
     */
    void updateAddInventory(String bookID, int quantity);

    /**
     * Removes a specified quantity of a book from the inventory.
     * @param bookID the ID of the book.
     * @param quantity the quantity to remove.
     */
    void updateRemoveInventory(String bookID, int quantity);

    /**
     * Updates the inventory after an order is placed.
     * @param bookIDs the list of book IDs.
     * @param quantities the list of quantities.
     */
    void updateInventoryAfterOrder(List<String> bookIDs, List<Integer> quantities);

    /**
     * Checks the stock level of a book and sends a notification if the stock is low.
     * @param bookID the ID of the book.
     */
    void checkAndNotifyLowStock(String bookID);

    /**
     * Checks if an order can be placed for a specified quantity of a book.
     * @param bookID the ID of the book.
     * @param quantity the quantity to order.
     */
    void placeOrder(String bookID, int quantity);

    /**
     * Retrieves the quantity of a specific book by its ID.
     * @param bookID the ID of the book.
     * @return the quantity of the book.
     */
	int getNoOfBooks(String bookID);

    /**
     * Adds a new book to the inventory.
     * @param bookID the ID of the book.
     * @param quantity the quantity to add.
     */
    void addBookToInventory(String bookID, int quantity);

    /**
     * Deletes a book from the inventory.
     * @param bookID the ID of the book.
     */
    void deleteBookFromInventory(String bookID);
}
