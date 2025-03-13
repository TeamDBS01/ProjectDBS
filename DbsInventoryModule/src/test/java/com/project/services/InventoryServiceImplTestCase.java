package com.project.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.project.exception.BookAlreadyExistsException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.project.DbsInventoryModuleApplication;
import com.project.dto.InventoryDTO;
import com.project.exception.BookNotFoundException;
import com.project.exception.OutOfStockException;
import com.project.models.Inventory;
import com.project.repositories.InventoryRepository;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.MailException;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = DbsInventoryModuleApplication.class)
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
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetInventoryByBookID_Positive() throws BookNotFoundException {
        // Arrange
        String bookID = "B1001";
        inventoryDTO.setBook_Id(bookID);
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.of(inventory));
        when(mapper.map(inventory, InventoryDTO.class)).thenReturn(inventoryDTO);

        inventoryDTO = inventoryServiceImpl.getInventoryByBookID(bookID);

        assertNotNull(inventoryDTO);
        assertEquals("B1001", inventoryDTO.getBook_Id());
    }

    @Test
    void testGetInventoryByBookID_Negative() throws BookNotFoundException {
        // Arrange
        when(inventoryRepository.findByBookId("B1000")).thenReturn(Optional.empty());
        
        assertThrows(BookNotFoundException.class, () -> {
            inventoryServiceImpl.getInventoryByBookID("B1000");
        });
    }
    
    @Test
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
    void testUpdateRemoveInventory_Positive() {
        String bookID = "B1001";
        int quantityToDeduct = 5;
        inventory.setBook_Id(bookID);
        inventory.setQuantity(20);
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.of(inventory));

        inventoryServiceImpl.updateRemoveInventory(bookID, quantityToDeduct);

        verify(inventoryRepository).save(inventory);
        assertEquals(15, inventory.getQuantity());
    }
    
    @Test
    void testUpdateRemoveInventory_Negative() {
        String bookID = "B1001";
        int quantityToDeduct = 5;
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> {
            inventoryServiceImpl.updateRemoveInventory(bookID, quantityToDeduct);
        });

        verify(inventoryRepository, never()).save(any(Inventory.class));    
    }
    
    @Test
    void testUpdateInventoryAfterOrder_Positive() {
    	List<String> bookIDs = List.of("B1001", "B1002");
    	List<Integer> quantities = List.of(5, 3);
    	Inventory inventory1=new Inventory();
    	inventory1.setBook_Id("B1001");
    	inventory1.setQuantity(15);
    	Inventory inventory2 = new Inventory();
    	inventory2.setBook_Id("B1002");
    	inventory2.setQuantity(28);
    	
    	when(inventoryRepository.findByBookId("B1001")).thenReturn(Optional.of(inventory1));
    	when(inventoryRepository.findByBookId("B1002")).thenReturn(Optional.of(inventory2));
    	
    	inventoryServiceImpl.updateInventoryAfterOrder(bookIDs, quantities);
    	
    	assertEquals(10, inventory1.getQuantity());
    	assertEquals(25,inventory2.getQuantity());
    }
    
    @Test
    void testUpdateInventoryAfterOrder_Negative() {
    	List<String> bookIDs = List.of("B1001", "B1002");
    	List<Integer> quantities = List.of(4, 3);
    	Inventory inventory1=new Inventory();
    	inventory1.setBook_Id("B1001");
    	inventory1.setQuantity(14);
    	Inventory inventory2 = new Inventory();
    	inventory2.setBook_Id("B1002");
    	inventory2.setQuantity(28);
    	
    	when(inventoryRepository.findByBookId("B1001")).thenReturn(Optional.of(inventory1));
    	when(inventoryRepository.findByBookId("B1002")).thenReturn(Optional.empty());
    	
    	inventoryServiceImpl.updateInventoryAfterOrder(bookIDs, quantities);
    	assertEquals(10, inventory1.getQuantity());
    	assertEquals(28,inventory2.getQuantity());
    }

    @Test
    void testCheckAndNotifyLowStock_Positive() {
        String bookID = "B1001";
        inventory.setBook_Id(bookID);
        inventory.setQuantity(5);
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.of(inventory));

        inventoryServiceImpl.checkAndNotifyLowStock(bookID);

        verify(emailServiceImpl).sendLowStockAlert(bookID, inventory.getQuantity());
    }

    @Test
    void testCheckAndNotifyLowStock_NotLowStock() {
        String bookID = "B1001";
        inventory.setBook_Id(bookID);
        inventory.setQuantity(15);
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.of(inventory));

        inventoryServiceImpl.checkAndNotifyLowStock(bookID);

        verify(emailServiceImpl, never()).sendLowStockAlert(bookID, inventory.getQuantity());
    }

    @Test
    void testCheckAndNotifyLowStock_NotFound() {
        String bookID = "B1001";
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> {
            inventoryServiceImpl.checkAndNotifyLowStock(bookID);
        });

        verify(emailServiceImpl, never()).sendLowStockAlert(bookID, 0);
    }

    @Test
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
    void testPlaceOrder_Positive() throws BookNotFoundException {
        String bookID = "B1001";
        int quantityToOrder = 5;
        inventory.setBook_Id(bookID);
        inventory.setQuantity(100);
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.of(inventory));

        inventoryServiceImpl.placeOrder(bookID, quantityToOrder);

        verify(inventoryRepository).save(inventory);
        
        assertEquals(95, inventory.getQuantity());
    }

    @Test
    void testPlaceOrder_OutOfStock() {
    	
        String bookID = "B1001";
        int quantityToOrder = 15;
        inventory.setBook_Id(bookID);
        inventory.setQuantity(10);
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.of(inventory));
        
        assertThrows(OutOfStockException.class, () -> {
            inventoryServiceImpl.placeOrder(bookID, quantityToOrder);
        });
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void testPlaceOrder_BookNotFound() {
        String bookID = "B1001";
        int quantityToOrder = 5;
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> {
            inventoryServiceImpl.placeOrder(bookID, quantityToOrder);
        });
        verify(inventoryRepository, never()).save(any(Inventory.class));
    }

    @Test
    void testGetNoOfBooks_Positive() {
        String bookID ="B1001";
        inventory.setBook_Id(bookID);
        inventory.setQuantity(10);
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.of(inventory));
        assertEquals(10, inventoryServiceImpl.getNoOfBooks(bookID));
    }

    @Test
    void testGetNoOfBooks_Negative() {
        String bookID="B1000";
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.empty());
        assertThrows(BookNotFoundException.class, ()->{
            inventoryServiceImpl.getInventoryByBookID(bookID);
        });
    }

    @Test
    void testDisplayInventory_Positive() {
        // Mock the repository call to return a list with one inventory item
        when(inventoryRepository.findAll()).thenReturn(Arrays.asList(inventory));
        when(mapper.map(inventory, InventoryDTO.class)).thenReturn(inventoryDTO);

        // Call the method and assert the result
        List<InventoryDTO> result = inventoryServiceImpl.displayInventory();
        assertEquals(1, result.size());
        assertEquals(inventoryDTO, result.get(0));
    }

    @Test
    void testDisplayInventory_Negative() {
        // Mock the repository call to return an empty list
        when(inventoryRepository.findAll()).thenReturn(Collections.emptyList());

        // Call the method and assert that BookNotFoundException is thrown
        assertThrows(BookNotFoundException.class, () -> {
            inventoryServiceImpl.displayInventory();
        });
    }

    @Test
    void testAddBookToInventory_Positive() throws BookAlreadyExistsException {
        // Arrange
        String bookID = "12345";
        int quantity = 10;
        InventoryRepository inventoryRepository = mock(InventoryRepository.class);
        InventoryServiceImpl inventoryService = new InventoryServiceImpl();
        ReflectionTestUtils.setField(inventoryService, "inventoryRepository", inventoryRepository);
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.empty());

        // Act
        inventoryService.addBookToInventory(bookID, quantity);

        // Assert
        verify(inventoryRepository, times(1)).save(any(Inventory.class));
    }

    @Test
    public void testAddBookToInventory_Negative() {
        // Arrange
        String bookID = "12345";
        int quantity = 10;
        InventoryRepository inventoryRepository = mock(InventoryRepository.class);
        InventoryServiceImpl inventoryService = new InventoryServiceImpl();
        ReflectionTestUtils.setField(inventoryService, "inventoryRepository", inventoryRepository);
        Inventory existingInventory = new Inventory();
        existingInventory.setBook_Id(bookID);
        existingInventory.setQuantity(5);
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.of(existingInventory));

        // Act & Assert
        assertThrows(BookAlreadyExistsException.class, () -> {
            inventoryService.addBookToInventory(bookID, quantity);
        });
    }

    @Test
    public void testDeleteBookFromInventorySuccess() throws BookNotFoundException {
        // Arrange
        String bookID = "12345";
        InventoryRepository inventoryRepository = mock(InventoryRepository.class);
        InventoryServiceImpl inventoryService = new InventoryServiceImpl();
        ReflectionTestUtils.setField(inventoryService, "inventoryRepository", inventoryRepository);
        Inventory existingInventory = new Inventory();
        existingInventory.setBook_Id(bookID);
        existingInventory.setInventoryId(1L);
        existingInventory.setQuantity(10);
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.of(existingInventory));

        // Act
        inventoryService.deleteBookFromInventory(bookID);

        // Assert
        verify(inventoryRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testDeleteBookFromInventory_NotFound() {
        // Arrange
        String bookID = "99999";
        InventoryRepository inventoryRepository = mock(InventoryRepository.class);
        InventoryServiceImpl inventoryService = new InventoryServiceImpl();
        ReflectionTestUtils.setField(inventoryService, "inventoryRepository", inventoryRepository);
        when(inventoryRepository.findByBookId(bookID)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(BookNotFoundException.class, () -> {
            inventoryService.deleteBookFromInventory(bookID);
        });
    }
}