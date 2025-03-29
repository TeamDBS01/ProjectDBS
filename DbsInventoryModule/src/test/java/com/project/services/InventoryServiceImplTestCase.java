package com.project.services;

import com.project.dto.InventoryDTO;
import com.project.exception.BookAlreadyExistsException;
import com.project.exception.BookNotFoundException;
import com.project.exception.InsufficientInventoryException;
import com.project.exception.OutOfStockException;
import com.project.models.Inventory;
import com.project.repositories.InventoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class InventoryServiceImplTestCase {

    @Mock
    private ModelMapper mapper;

    @InjectMocks
    private InventoryServiceImpl inventoryServiceImpl;

    @Mock
    private InventoryRepository inventoryRepository;

    @MockitoBean
    private EmailServiceImpl emailServiceImpl;

    private Inventory inventory;
    private InventoryDTO inventoryDTO;

    @BeforeEach
    void setUp() throws Exception {
        inventory = new Inventory();
        inventoryDTO = new InventoryDTO();
        MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        inventoryRepository = null;
        inventoryServiceImpl = null;
        inventory = null;
    }

    @Test
    @DisplayName("Get Inventory by Book ID - Positive Case")
    void testGetInventoryByBookID_Positive() throws BookNotFoundException {

        String bookID = "B1001";
        inventoryDTO.setBook_Id(bookID);
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.of(inventory));
        when(mapper.map(inventory, InventoryDTO.class)).thenReturn(inventoryDTO);

        inventoryDTO = inventoryServiceImpl.getInventoryByBookID(bookID);

        assertNotNull(inventoryDTO);
        assertEquals("B1001", inventoryDTO.getBook_Id());
    }

    @Test
    @DisplayName("Get Inventory by Book ID - Negative Case")
    void testGetInventoryByBookID_Negative() throws BookNotFoundException {

        when(inventoryRepository.findByBookId("B1000")).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> {
            inventoryServiceImpl.getInventoryByBookID("B1000");
        });
    }

    @Test
    @DisplayName("Update Add Inventory - Positive Case")
    void testUpdateAddInventory_Positive() {
        String bookID = "B1000";
        int quantityToAdd = 10;
        inventory.setBook_Id(bookID);
        inventory.setQuantity(10);
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.of(inventory));

        inventoryServiceImpl.updateAddInventory(bookID, quantityToAdd);

        verify(inventoryRepository).save(inventory);
        assertEquals(20, inventory.getQuantity());
    }

    @Test
    @DisplayName("Update Add Inventory - Negative Case")
    void testUpdateAddInventory_Negative() {
        String bookID = "B1001";
        int quantityToAdd = 5;
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> {
            inventoryServiceImpl.updateAddInventory(bookID, quantityToAdd);
        });

        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    @DisplayName("Update Remove Inventory - Positive Case")
    public void testUpdateRemoveInventory_Positive() throws BookNotFoundException, InsufficientInventoryException {
        String bookID = "B1001";
        int quantityToDeduct = 5;
        inventory.setBook_Id(bookID);
        inventory.setQuantity(10);
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.of(inventory));

        inventoryServiceImpl.updateRemoveInventory(bookID, quantityToDeduct);

        assertEquals(5, inventory.getQuantity());
        verify(inventoryRepository, times(1)).save(inventory);
    }

    @Test
    @DisplayName("Update Remove Inventory - Book Not Found Case")
    public void testUpdateRemoveInventory_BookNotFound() {
        String bookID = "B1001";
        int quantityToDeduct = 5;
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> inventoryServiceImpl.updateRemoveInventory(bookID, quantityToDeduct));
    }

    @Test
    @DisplayName("Update Remove Inventory - Insufficient Inventory Case")
    public void testUpdateRemoveInventory_InsufficientInventory() {
        String bookID = "B1001";
        int quantityToDeduct = 15;
        inventory.setBook_Id(bookID);
        inventory.setQuantity(10);
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.of(inventory));

        assertThrows(InsufficientInventoryException.class, () -> inventoryServiceImpl.updateRemoveInventory(bookID, quantityToDeduct));
    }

    @Test
    @DisplayName("Update Inventory After Order - Positive Case")
    void testUpdateInventoryAfterOrder_Positive() {
        List<String> bookIDs = List.of("B1001", "B1002");
        List<Integer> quantities = List.of(5, 3);
        Inventory inventory1 = new Inventory();
        inventory1.setBook_Id("B1001");
        inventory1.setQuantity(15);
        Inventory inventory2 = new Inventory();
        inventory2.setBook_Id("B1002");
        inventory2.setQuantity(28);

        when(inventoryRepository.findByBookId("B1001")).thenReturn(Optional.of(inventory1));
        when(inventoryRepository.findByBookId("B1002")).thenReturn(Optional.of(inventory2));

        inventoryServiceImpl.updateInventoryAfterOrder(bookIDs, quantities);

        assertEquals(10, inventory1.getQuantity());
        assertEquals(25, inventory2.getQuantity());
    }

    @Test
    @DisplayName("Update Inventory After Order - Negative Case")
    void testUpdateInventoryAfterOrder_Negative() {
        List<String> bookIDs = List.of("B1001", "B1002");
        List<Integer> quantities = List.of(4, 3);
        Inventory inventory1 = new Inventory();
        inventory1.setBook_Id("B1001");
        inventory1.setQuantity(14);
        Inventory inventory2 = new Inventory();
        inventory2.setBook_Id("B1002");
        inventory2.setQuantity(28);

        when(inventoryRepository.findByBookId("B1001")).thenReturn(Optional.of(inventory1));
        when(inventoryRepository.findByBookId("B1002")).thenReturn(Optional.empty());

        inventoryServiceImpl.updateInventoryAfterOrder(bookIDs, quantities);
        assertEquals(10, inventory1.getQuantity());
        assertEquals(28, inventory2.getQuantity());
    }

    @Test
    @DisplayName("Check and Notify Low Stock - Positive Case")
    void testCheckAndNotifyLowStock_Positive() {
        String bookID = "B1001";
        inventory.setBook_Id(bookID);
        inventory.setQuantity(5);
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.of(inventory));

        inventoryServiceImpl.checkAndNotifyLowStock(bookID);

        verify(emailServiceImpl).sendLowStockAlert(bookID, inventory.getQuantity());
    }

    @Test
    @DisplayName("Check and Notify Low Stock - Not Low Stock Case")
    void testCheckAndNotifyLowStock_NotLowStock() {
        String bookID = "B1001";
        inventory.setBook_Id(bookID);
        inventory.setQuantity(15);
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.of(inventory));

        inventoryServiceImpl.checkAndNotifyLowStock(bookID);

        verify(emailServiceImpl, never()).sendLowStockAlert(bookID, inventory.getQuantity());
    }

    @Test
    @DisplayName("Check and Notify Low Stock - Not Found Case")
    void testCheckAndNotifyLowStock_NotFound() {
        String bookID = "B1001";
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> {
            inventoryServiceImpl.checkAndNotifyLowStock(bookID);
        });

        verify(emailServiceImpl, never()).sendLowStockAlert(bookID, 0);
    }

    @Test
    @DisplayName("Check and Notify Low Stock - Email Sending Failure Case")
    void testCheckAndNotifyLowStock_EmailSendingFailure() {
        String bookID = "B1001";
        inventory.setBook_Id(bookID);
        inventory.setQuantity(5);
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.of(inventory));
        doThrow(new MailException("Email sending failed") {}).when(emailServiceImpl).sendLowStockAlert(bookID, inventory.getQuantity());

        assertThrows(MailException.class, () -> {
            inventoryServiceImpl.checkAndNotifyLowStock(bookID);
        });
    }

    @Test
    @DisplayName("Place Order - Positive Case")
    void testPlaceOrder_Positive() throws BookNotFoundException {
        String bookID = "B1001";
        int quantityToOrder = 5;
        inventory.setBook_Id(bookID);
        inventory.setQuantity(100);
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.of(inventory));

        inventoryServiceImpl.placeOrder(bookID, quantityToOrder);

        assertDoesNotThrow(() -> inventoryServiceImpl.placeOrder("B1001", 10));
    }

    @Test
    @DisplayName("Place Order - Out of Stock Case")
    void testPlaceOrder_OutOfStock() {
        String bookID = "B1001";
        int quantityToOrder = 15;
        inventory.setBook_Id(bookID);
        inventory.setQuantity(10);
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.of(inventory));

        assertThrows(OutOfStockException.class, () -> {
            inventoryServiceImpl.placeOrder(bookID, quantityToOrder);
        });
    }

    @Test
    @DisplayName("Place Order - Book Not Found Case")
    void testPlaceOrder_BookNotFound() {
        String bookID = "B1001";
        int quantityToOrder = 5;
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> {
            inventoryServiceImpl.placeOrder(bookID, quantityToOrder);
        });
    }

    @Test
    @DisplayName("Get Number of Books - Positive Case")
    void testGetNoOfBooks_Positive() {
        String bookID = "B1001";
        inventory.setBook_Id(bookID);
        inventory.setQuantity(10);
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.of(inventory));
        assertEquals(10, inventoryServiceImpl.getNoOfBooks(bookID));
    }

    @Test
    @DisplayName("Get Number of Books - Negative Case")
    void testGetNoOfBooks_Negative() {
        String bookID = "B1000";
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, () -> {
            inventoryServiceImpl.getInventoryByBookID(bookID);
        });
    }

    @Test
    @DisplayName("Display Inventory - Positive Case")
    void testDisplayInventory_Positive() {
        Page<Inventory> inventoryPage = new PageImpl<>(Arrays.asList(inventory));
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        when(inventoryRepository.findAll(pageable)).thenReturn(inventoryPage);
        when(mapper.map(inventory, InventoryDTO.class)).thenReturn(inventoryDTO);

        List<InventoryDTO> result = inventoryServiceImpl.displayInventory(page, size);

        assertEquals(1, result.size());
        assertEquals(inventoryDTO, result.get(0));
    }

    @Test
    @DisplayName("Display Inventory - Negative Case")
    void testDisplayInventory_Negative() {
        Page<Inventory> emptyPage = new PageImpl<>(Collections.emptyList());
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        when(inventoryRepository.findAll(pageable)).thenReturn(emptyPage);

        assertThrows(BookNotFoundException.class, () -> {
            inventoryServiceImpl.displayInventory(page, size);
        });
    }

    @Test
    @DisplayName("Add Book to Inventory - Positive Case")
    void testAddBookToInventory_Positive() throws BookAlreadyExistsException {
        String bookID = "B1002";
        int quantity = 10;
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.empty());

        inventoryServiceImpl.addBookToInventory(bookID, quantity);

        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    @DisplayName("Add Book to Inventory - Negative Case")
    void testAddBookToInventory_Negative() {
        String bookID = "B1002";
        int quantity = 10;

        Inventory existingInventory = new Inventory();
        existingInventory.setBook_Id(bookID);
        existingInventory.setQuantity(5);
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.of(existingInventory));

        assertThrows(BookAlreadyExistsException.class, () -> {
            inventoryServiceImpl.addBookToInventory(bookID, quantity);
        });
    }

    @Test
    @DisplayName("Delete Book from Inventory - Positive Case")
    void testDeleteBookFromInventory_Positive() throws BookNotFoundException {
        String bookID = "B1002";

        Inventory existingInventory = new Inventory();
        existingInventory.setBook_Id(bookID);
        existingInventory.setInventoryId(1L);
        existingInventory.setQuantity(10);
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.of(existingInventory));

        inventoryServiceImpl.deleteBookFromInventory(bookID);

        verify(inventoryRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Delete Book from Inventory - Not Found Case")
    void testDeleteBookFromInventory_NotFound() {
        String bookID = "B1002";

        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> {
            inventoryServiceImpl.deleteBookFromInventory(bookID);
        });
    }
}