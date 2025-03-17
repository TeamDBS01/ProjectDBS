package com.project.controller;

import com.project.controller.InventoryControllerImpl;
import com.project.dto.InventoryDTO;
import com.project.exception.BookAlreadyExistsException;
import com.project.exception.BookNotFoundException;
import com.project.exception.InsufficientInventoryException;
import com.project.exception.OutOfStockException;
import com.project.services.InventoryServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
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
    public void testDisplayInventory_Positive() throws Exception {
        List<InventoryDTO> inventoryDTOS = Arrays.asList(inventoryDTO);
        when(inventoryServiceimpl.displayInventory()).thenReturn(inventoryDTOS);

        mockMvc.perform(get("/dbs/inventory"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    public void testDisplayService_Negative() throws Exception {
        when(inventoryServiceimpl.displayInventory()).thenThrow(new BookNotFoundException("Inventory is Empty"));

        mockMvc.perform(get("/dbs/inventory"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Inventory is Empty"));
    }

    @Test
    public void testGetInventoryByBookID_Positive() throws Exception {
        when(inventoryServiceimpl.getInventoryByBookID(anyString())).thenReturn(inventoryDTO);

        mockMvc.perform(get("/dbs/inventory/{bookID}", "B001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.book_Id").value("B001"))
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    public void testGetInventoryByBookID_Negative() throws Exception {
        when(inventoryServiceimpl.getInventoryByBookID(anyString())).thenThrow(new BookNotFoundException("Book not found"));

        mockMvc.perform(get("/dbs/inventory/{bookID}", "B001"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No Such books found in the inventory"));
    }

    @Test
    public void testGetNoOfBooks_Positive() throws Exception {
        when(inventoryServiceimpl.getNoOfBooks(anyString())).thenReturn(10);

        mockMvc.perform(get("/dbs/inventory/quantity/{bookID}", "B002"))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }

    @Test
    public void testGetNoOfBooks_Negative() throws Exception {
        when(inventoryServiceimpl.getNoOfBooks(anyString())).thenThrow(new BookNotFoundException("Book not found for ID: B002"));

        mockMvc.perform(get("/dbs/inventory/quantity/{bookID}", "B002"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateAddInventory_Positive() throws Exception {
        mockMvc.perform(put("/dbs/inventory/update/add")
                        .param("bookID", "B001")
                        .param("quantity", "10"))
                .andExpect(status().isOk())
                .andExpect(content().string("Addition successful"));
    }

    @Test
    public void testUpdateAddInventory_Negative() throws Exception {
        doThrow(new BookNotFoundException("Book not found for ID: B001")).when(inventoryServiceimpl).updateAddInventory("B001", 10);

        mockMvc.perform(put("/dbs/inventory/update/add")
                        .param("bookID", "B001")
                        .param("quantity", "10"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Updation failed - No book found with given bookID"));
    }

    @Test
    public void testUpdateRemoveInventory_Positive() throws Exception {
        mockMvc.perform(put("/dbs/inventory/update/remove")
                        .param("bookID", "B001")
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(content().string("Reduction successful"));
    }

    @Test
    public void testUpdateRemoveInventory_BookNotFound() throws Exception {
        doThrow(new BookNotFoundException("Book not found")).when(inventoryServiceimpl).updateRemoveInventory("B001", 5);

        mockMvc.perform(put("/dbs/inventory/update/remove")
                        .param("bookID", "B001")
                        .param("quantity", "5"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book not found"));
    }

    @Test
    public void testRemoveInventoryInsufficientInventory() throws Exception {
        doThrow(new InsufficientInventoryException("Not enough books in inventory")).when(inventoryServiceimpl).updateRemoveInventory("B001", 50);

        mockMvc.perform(put("/dbs/inventory/update/remove")
                        .param("bookID", "B001")
                        .param("quantity", "50"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Not enough books in Inventory"));
    }

    @Test
    public void testPlaceOrder_Positive() throws Exception {
        mockMvc.perform(post("/dbs/inventory/order")
                        .param("bookID", "B001")
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(content().string("Order can be placed"));
    }

    @Test
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
    public void testPlaceOrder_BookNotFound() throws Exception {
        doThrow(new BookNotFoundException("Book not found")).when(inventoryServiceimpl).placeOrder("B001", 5);

        mockMvc.perform(post("/dbs/inventory/order")
                        .param("bookID", "B001")
                        .param("quantity", "5"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book not found"));
    }

    @Test
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
    public void testAddBookToInventory_Positive() throws Exception {
        mockMvc.perform(post("/dbs/inventory/add")
                        .param("bookID", "B003")
                        .param("quantity", "10"))
                .andExpect(status().isOk())
                .andExpect(content().string("Book added to inventory successfully"));
    }

    @Test
    public void testAddBookToInventory_Negative() throws Exception {
        doThrow(new BookAlreadyExistsException("Book already exists in the inventory")).when(inventoryServiceimpl).addBookToInventory("B003", 10);

        mockMvc.perform(post("/dbs/inventory/add")
                        .param("bookID", "B003")
                        .param("quantity", "10"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book with the given bookID already exists"));
    }

    @Test
    public void testDeleteBookFromInventory_Positive() throws Exception {
        mockMvc.perform(delete("/dbs/inventory/{bookID}","B003"))
                .andExpect(status().isOk())
                .andExpect(content().string("Book deleted from inventory successfully"));
    }

    @Test
    public void testDeleteBookFromInventory_Negative() throws Exception {
        doThrow(new BookNotFoundException("There are no existing books with the given BookId")).when(inventoryServiceimpl).deleteBookFromInventory("B003");

        mockMvc.perform(delete("/dbs/inventory/{bookID}","B003"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book with the given book ID does not exist"));
    }
}