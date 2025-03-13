package com.project.controller;

import com.project.dto.InventoryDTO;
import com.project.exception.BookAlreadyExistsException;
import com.project.exception.BookNotFoundException;
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

        mockMvc.perform(get("/inventory"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").exists());
    }

    @Test
    public void testDisplayService_Negative() throws Exception {
        when(inventoryServiceimpl.displayInventory()).thenThrow(new BookNotFoundException("Inventory Empty"));

        mockMvc.perform(get("/inventory"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("No such book in inventory"));
    }

    @Test
    public void testGetInventoryByBookID_Positive() throws Exception {
        when(inventoryServiceimpl.getInventoryByBookID(anyString())).thenReturn(inventoryDTO);

        mockMvc.perform(get("/inventory/{bookID}", "B001"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.book_Id").value("B001"))
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    public void testGetInventoryByBookID_Negative() throws Exception {
        when(inventoryServiceimpl.getInventoryByBookID(anyString())).thenThrow(new BookNotFoundException("Book not found"));

        mockMvc.perform(get("/inventory/{bookID}", "B001"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testGetNoOfBooks_Positive() throws Exception {
        when(inventoryServiceimpl.getNoOfBooks(anyString())).thenReturn(10);

        mockMvc.perform(get("/inventory/quantity/{bookID}", "B002"))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));
    }

    @Test
    public void testGetNoOfBooks_Negative() throws Exception {
        when(inventoryServiceimpl.getNoOfBooks(anyString())).thenThrow(new BookNotFoundException("Book not found for ID: B002"));

        mockMvc.perform(get("/inventory/books/{bookID}", "B002"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testUpdateAddInventory_Positive() throws Exception {
        mockMvc.perform(post("/inventory/add")
                        .param("bookID", "B001")
                        .param("quantity", "10"))
                .andExpect(status().isOk())
                .andExpect(content().string("Addition successful"));
    }

    @Test
    public void testUpdateAddInventory_Negative() throws Exception {
        // Simulate the scenario where the book is not found
        doThrow(new BookNotFoundException("Book not found for ID: B001")).when(inventoryServiceimpl).updateAddInventory("B001", 10);

        mockMvc.perform(post("/inventory/add")
                        .param("bookID", "B001")
                        .param("quantity", "10"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book not found"));
    }

    @Test
    public void testUpdateRemoveInventory_Positive() throws Exception {
        mockMvc.perform(post("/inventory/remove")
                        .param("bookID", "B001")
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(content().string("Reduction successful"));
    }

    @Test
    public void testUpdateRemoveInventory_Negative() throws Exception {
        doThrow(new BookNotFoundException("Book not found for ID: B001")).when(inventoryServiceimpl).updateRemoveInventory("B001", 5);

        mockMvc.perform(post("/inventory/remove")
                        .param("bookID", "B001")
                        .param("quantity", "5"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book not found"));
    }

    @Test
    public void testPlaceOrder_Positive() throws Exception {
        mockMvc.perform(post("/inventory/order")
                        .param("bookID", "B001")
                        .param("quantity", "5"))
                .andExpect(status().isOk())
                .andExpect(content().string("Order can be placed"));
    }

    @Test
    public void testPlaceOrder_OutOfStock() throws Exception {
        doThrow(new OutOfStockException("Out of stock")).when(inventoryServiceimpl).placeOrder("B001", 5);
        when(inventoryServiceimpl.getNoOfBooks("B001")).thenReturn(2);

        mockMvc.perform(post("/inventory/order")
                        .param("bookID", "B001")
                        .param("quantity", "5"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Stock unavailable.\nAvailable number of books: 2"));
    }

    @Test
    public void testPlaceOrder_BookNotFound() throws Exception {
        doThrow(new BookNotFoundException("Book not found")).when(inventoryServiceimpl).placeOrder("B001", 5);

        mockMvc.perform(post("/inventory/order")
                        .param("bookID", "B001")
                        .param("quantity", "5"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Book not found"));
    }

    @Test
    public void testUpdateInventoryAfterOrder_Positive() throws Exception {
        List<String> bookIDs = Arrays.asList("B001", "B002");
        List<Integer> quantities = Arrays.asList(5, 3);

        mockMvc.perform(post("/inventory/updateAfterOrder")
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
        //when(inventoryServiceimpl.addBookToInventory(anyString(), anyInt())).thenThrow(new BookAlreadyExistsException("Book already exists in the inventory"));
        mockMvc.perform(post("/dbs/inventory/add")
                        .param("bookID", "B003")
                        .param("quantity", "10"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void testDeleteBookFromInventory_Positive() throws Exception {
        mockMvc.perform(delete("/dbs/inventory/delete")
                        .param("bookID", "B003"))
                .andExpect(status().isOk())
                .andExpect(content().string("Book deleted from inventory successfully"));
    }

    @Test
    public void testDeleteBookFromInventory_Negative() throws Exception {
        doThrow(new BookNotFoundException("Book not found")).when(inventoryServiceimpl).deleteBookFromInventory("B003");

        mockMvc.perform(delete("/dbs/inventory/delete")
                        .param("bookID", "B003"))
                .andExpect(status().isNotFound());
    }
}