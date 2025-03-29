package com.project.repositories;

import com.project.models.Inventory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class InventoryRepositoryTestCase {

    private static Inventory inventory;

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private TestEntityManager testEntityManager;

    @BeforeEach
    void setUp() throws Exception {
        inventory = Inventory.builder().quantity(500).book_Id("B1001").build();
    }

    @AfterEach
    void tearDown() throws Exception {
        inventory = null;
    }

    @Test
    @DisplayName("Display Inventory - Positive Case")
    void testDisplayInventory_Positive() {
        testEntityManager.persist(inventory);
        Page<Inventory> inventoryPage = inventoryRepository.findAll(Pageable.unpaged());
        assertTrue(inventoryPage.hasContent());
        assertEquals(1, inventoryPage.getTotalElements());
    }

    @Test
    @DisplayName("Display Inventory - Negative Case")
    void testDisplayInventory_Negative() {
        Page<Inventory> inventoryPage = inventoryRepository.findAll(Pageable.unpaged());
        assertFalse(inventoryPage.hasContent());
    }

    @Test
    @DisplayName("Add Inventory - Positive Case")
    void testAddInventory_Positive() {
        Inventory expected = inventoryRepository.save(inventory);
        Inventory actual = testEntityManager.find(Inventory.class, expected.getInventoryId());
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Add Inventory - Negative Case")
    void testAddInventory_Negative() {
        try {
            inventoryRepository.save(null);
            fail("Expected an exception to be thrown");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    @DisplayName("Delete Inventory by ID - Positive Case")
    void testDeleteInventoryById_Positive() {
        Inventory savedInventory = testEntityManager.persist(inventory);
        inventoryRepository.deleteById(savedInventory.getInventoryId());
        Inventory foundInventory = testEntityManager.find(Inventory.class, inventory.getInventoryId());
        assertNull(foundInventory);
    }

    @Test
    @DisplayName("Delete Inventory by ID - Negative Case")
    void testDeleteInventoryById_Negative() {
        try {
            inventoryRepository.deleteById(null);
            fail("Expected an exception to be thrown");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    @DisplayName("Find All Inventory - Positive Case")
    void testFindAll_Positive() {
        testEntityManager.persist(inventory);
        Iterable<Inventory> inventoryList = inventoryRepository.findAll();
        assertTrue(inventoryList.iterator().hasNext());
    }

    @Test
    @DisplayName("Find All Inventory - Negative Case")
    void testFindAll_Negative() {
        Iterable<Inventory> inventoryList = inventoryRepository.findAll();
        assertFalse(inventoryList.iterator().hasNext());
    }

    @Test
    @DisplayName("Find Inventory by ID - Positive Case")
    void testFindById_Positive() {
        Inventory savedInventory = testEntityManager.persist(inventory);
        Optional<Inventory> optionalOfInventory = inventoryRepository.findById(savedInventory.getInventoryId());
        assertEquals(optionalOfInventory.get(), savedInventory);
    }

    @Test
    @DisplayName("Find Inventory by ID - Negative Case")
    void testFindById_Negative() {
        Optional<Inventory> optionalOfInventory = inventoryRepository.findById(1L);
        assertFalse(optionalOfInventory.isPresent());
    }

    @Test
    @DisplayName("Update Inventory - Positive Case")
    void testUpdateInventory_Positive() {
        Inventory savedInventory = testEntityManager.persist(inventory);
        Inventory foundInventory = testEntityManager.find(Inventory.class, savedInventory.getInventoryId());
        assertNotNull(foundInventory);
        savedInventory.setQuantity(100);
        testEntityManager.persist(savedInventory);
        assertEquals(100, savedInventory.getQuantity());
    }

    @Test
    @DisplayName("Find Inventory by Book ID - Positive Case")
    void testFindByBookId_Positive() {
        Inventory savedInventory = testEntityManager.persist(inventory);
        Optional<Inventory> optionalOfInventory = inventoryRepository.findByBookId("B1001");
        assertEquals(optionalOfInventory.get(), savedInventory);
    }

    @Test
    @DisplayName("Find Inventory by Book ID - Negative Case")
    void testFindByBookId_Negative() {
        Optional<Inventory> optionalOfInventory = inventoryRepository.findByBookId("B1001");
        assertFalse(optionalOfInventory.isPresent());
    }
}