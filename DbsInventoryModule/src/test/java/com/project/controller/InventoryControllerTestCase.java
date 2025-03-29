package com.project.controller;

import com.project.dto.InventoryDTO;
import com.project.exception.*;
import com.project.services.InventoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InventoryControllerImpl.class)
class InventoryControllerTestCase {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private InventoryServiceImpl inventoryServiceimpl;


    private InventoryDTO inventoryDTO;

    @BeforeEach
    void setUp() {
        inventoryDTO = new InventoryDTO();
        inventoryDTO.setBook_Id("B001");
        inventoryDTO.setQuantity(10);
    }

    @Test
    @DisplayName("Display Inventory - Positive Case")
    public void testDisplayInventory_Positive() throws Exception {
        List<InventoryDTO> inventoryDTOS = Arrays.asList(inventoryDTO);
        when(inventoryServiceimpl.displayInventory(0, 5)).thenReturn(inventoryDTOS);

        mockMvc.perform(get("/dbs/inventory")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    @DisplayName("Display Inventory - Negative Case")
    public void testDisplayInventory_Negative() throws Exception {
        when(inventoryServiceimpl.displayInventory(0, 5)).thenThrow(new BookNotFoundException("Inventory is Empty"));

        mockMvc.perform(get("/dbs/inventory")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Inventory is Empty"));
    }

    @Test
    @DisplayName("Display Inventory - Page Out of Bounds Case")
    void testDisplayInventory_PageOutOfBounds() throws Exception {
        when(inventoryServiceimpl.displayInventory(10, 5)).thenThrow(new PageOutOfBoundsException("Page number exceeds total pages available"));

        mockMvc.perform(get("/dbs/inventory")
                        .param("page", "10")
                        .param("size", "5"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Page number exceeds total pages available"));
    }

    @Test
    @DisplayName("Get Inventory by Book ID - Positive Case")
    public void testGetInventoryByBookID_Positive() throws Exception {
        when(inventoryServiceimpl.getInventoryByBookID(anyString())).thenReturn(inventoryDTO);

        mockMvc.perform(get("/dbs/inventory/{bookID}", "B001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.book_Id").value("B001"))
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    @DisplayName("Get Inventory by Book ID - Negative Case")
    public void testGetInventoryByBookID_Negative() throws Exception {
        when(inventoryServiceimpl.getInventoryByBookID(anyString())).thenThrow(new BookNotFoundException("Book not found"));

        mockMvc.perform(get("/dbs/inventory/{bookID}", "B001"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No Such books found in the inventory"));
    }

    @Test
    @DisplayName("Get Number of Books - Positive Case")
    public void testGetNoOfBooks_Positive() throws Exception {
        when(inventoryServiceimpl.getNoOfBooks(anyString())).thenReturn(10);

        mockMvc.perform(get("/dbs/inventory/quantity/{bookID}", "B002"))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }

    @Test
    @DisplayName("Get Number of Books - Negative Case")
    public void testGetNoOfBooks_Negative() throws Exception {
        when(inventoryServiceimpl.getNoOfBooks(anyString())).thenThrow(new BookNotFoundException("Book not found for ID: B002"));

        mockMvc.perform(get("/dbs/inventory/quantity/{bookID}", "B002"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Update Add Inventory - Positive Case")
    public void testUpdateAddInventory_Positive() throws Exception {
        mockMvc.perform(put("/dbs/inventory/update/add")
                        .param("bookID", "B001")
                        .param("quantity", "10"))
                .andExpect(status().isOk())
                .andExpect(content().string("Addition successful"));
    }

    @Test
    @DisplayName("Update Add Inventory - Negative Case")
    public void testUpdateAddInventory_Negative() throws Exception {
        doThrow(new BookNotFoundException("Book not found for ID: B001")).when(inventoryServiceimpl).updateAddInventory("B001", 10);

        mockMvc.perform(put("/dbs/inventory/update/add")
                        .param("bookID", "B001")
                        .param("quantity", "10"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Updation failed - No book found with given bookID"));
    }

    @Test
    @DisplayName("Update Remove Inventory - Positive Case")
    public void testUpdateRemoveInventory_Positive() throws Exception {
        mockMvc.perform(put("/dbs/inventory/update/remove")
                        .param("bookID", "B001")
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(content().string("Reduction successful"));
    }

    @Test
    @DisplayName("Update Remove Inventory - Book Not Found Case")
    public void testUpdateRemoveInventory_BookNotFound() throws Exception {
        doThrow(new BookNotFoundException("Book not found")).when(inventoryServiceimpl).updateRemoveInventory("B001", 5);

        mockMvc.perform(put("/dbs/inventory/update/remove")
                        .param("bookID", "B001")
                        .param("quantity", "5"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book not found"));
    }

    @Test
    @DisplayName("Remove Inventory - Insufficient Inventory Case")
    public void testRemoveInventoryInsufficientInventory() throws Exception {
        doThrow(new InsufficientInventoryException("Not enough books in inventory")).when(inventoryServiceimpl).updateRemoveInventory("B001", 50);

        mockMvc.perform(put("/dbs/inventory/update/remove")
                        .param("bookID", "B001")
                        .param("quantity", "50"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Not enough books in Inventory"));
    }

    @Test
    @DisplayName("Place Order - Positive Case")
    public void testPlaceOrder_Positive() throws Exception {
        mockMvc.perform(post("/dbs/inventory/order")
                        .param("bookID", "B001")
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(content().string("Order can be placed"));
    }

    @Test
    @DisplayName("Place Order - Out of Stock Case")
    public void testPlaceOrder_OutOfStock() throws Exception {
        doThrow(new OutOfStockException("Out of stock")).when(inventoryServiceimpl).placeOrder("B001", 5);
        when(inventoryServiceimpl.getNoOfBooks("B001")).thenReturn(2);

        mockMvc.perform(post("/dbs/inventory/order")
                        .param("bookID", "B001")
                        .param("quantity", "5"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Stock unavailable.\nAvailable number of books: 2"));
    }

    @Test
    @DisplayName("Place Order - Book Not Found Case")
    public void testPlaceOrder_BookNotFound() throws Exception {
        doThrow(new BookNotFoundException("Book not found")).when(inventoryServiceimpl).placeOrder("B001", 5);

        mockMvc.perform(post("/dbs/inventory/order")
                        .param("bookID", "B001")
                        .param("quantity", "5"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book not found"));
    }

    @Test
    @DisplayName("Update Inventory After Order - Positive Case")
    public void testUpdateInventoryAfterOrder_Positive() throws Exception {
        List<String> bookIDs = Arrays.asList("B001", "B002");
        List<Integer> quantities = Arrays.asList(5, 3);

        mockMvc.perform(put("/dbs/inventory/updateAfterOrder")
                        .param("bookIDs", "B001,B002")
                        .param("quantities", "5,3"))
                .andExpect(status().isOk())
                .andExpect(content().string("Inventory updated after order"));
    }

    @Test
    @DisplayName("Add Book to Inventory - Positive Case")
    public void testAddBookToInventory_Positive() throws Exception {
        mockMvc.perform(post("/dbs/inventory/add")
                        .param("bookID", "B003")
                        .param("quantity", "10"))
                .andExpect(status().isOk())
                .andExpect(content().string("Book added to inventory successfully"));
    }

    @Test
    @DisplayName("Add Book to Inventory - Negative Case")
    public void testAddBookToInventory_Negative() throws Exception {
        doThrow(new BookAlreadyExistsException("Book already exists in the inventory")).when(inventoryServiceimpl).addBookToInventory("B003", 10);

        mockMvc.perform(post("/dbs/inventory/add")
                        .param("bookID", "B003")
                        .param("quantity", "10"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book with the given bookID already exists"));
    }

    @Test
    @DisplayName("Delete Book from Inventory - Positive Case")
    public void testDeleteBookFromInventory_Positive() throws Exception {
        mockMvc.perform(delete("/dbs/inventory/{bookID}","B003"))
                .andExpect(status().isOk())
                .andExpect(content().string("Book deleted from inventory successfully"));
    }

    @Test
    @DisplayName("Delete Book from Inventory - Negative Case")
    public void testDeleteBookFromInventory_Negative() throws Exception {
        doThrow(new BookNotFoundException("There are no existing books with the given BookId")).when(inventoryServiceimpl).deleteBookFromInventory("B003");

        mockMvc.perform(delete("/dbs/inventory/{bookID}","B003"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book with the given book ID does not exist"));
    }
}