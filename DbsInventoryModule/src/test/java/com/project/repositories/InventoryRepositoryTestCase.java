package com.project.repositories;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;


import com.project.models.Inventory;


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
    void testDisplayInventory_Positive() {
        Inventory savedInventory = testEntityManager.persist(inventory);
        Iterable<Inventory> listOfInventory = inventoryRepository.findAll();
        assertTrue(listOfInventory.iterator().hasNext());
    }

    @Test
    void testDisplayInventory_Negative() {
        Iterable<Inventory> listOfInventory = inventoryRepository.findAll();
        assertFalse(listOfInventory.iterator().hasNext());
    }

    @Test
    void testAddInventory_Positive() {
        Inventory expected = inventoryRepository.save(inventory);
        Inventory actual = testEntityManager.find(Inventory.class,expected.getInventoryId());
        assertEquals(expected,actual);
    }

    @Test
    void testAddInventory_Negative() {
        try {
            inventoryRepository.save(null);
            fail("Expected an exception to be thrown");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void testDeleteInventoryById_Positive() {
        Inventory savedInventory = testEntityManager.persist(inventory);
        inventoryRepository.deleteById(savedInventory.getInventoryId());
        Inventory foundInventory = testEntityManager.find(Inventory.class,inventory.getInventoryId());
        assertNull(foundInventory);
    }

    @Test
    void testDeleteInventoryById_Negative() {
        try {
            inventoryRepository.deleteById(null);
            fail("Expected an exception to be thrown");
        } catch (Exception e) {
            assertTrue(true);
        }
    }

    @Test
    void testFindAll_Positive() {
        testEntityManager.persist(inventory);
        Iterable<Inventory> inventoryList = inventoryRepository.findAll();
        assertTrue(inventoryList.iterator().hasNext());
    }

    @Test
    void testFindAll_Negative() {
        Iterable<Inventory> inventoryList = inventoryRepository.findAll();
        assertFalse(inventoryList.iterator().hasNext());
    }

    @Test
    void testFindById_Positive() {
        Inventory savedInventory = testEntityManager.persist(inventory);
        Optional<Inventory> optionalOfInventory = inventoryRepository.findById(savedInventory.getInventoryId());
        assertEquals(optionalOfInventory.get(), savedInventory);
    }

    @Test
    void testFindById_Negative() {
        Optional<Inventory> optionalOfInventory = inventoryRepository.findById(1L);
        assertFalse(optionalOfInventory.isPresent());
    }

    @Test
    void testUpdateInventory_Positive() {
        Inventory savedInventory = testEntityManager.persist(inventory);
        Inventory foundInventory= testEntityManager.find(Inventory.class, savedInventory.getInventoryId());
        assertNotNull(foundInventory);
        savedInventory.setQuantity(100);
        testEntityManager.persist(savedInventory);
        assertEquals(100, savedInventory.getQuantity());
    }

    @Test
    void testFindByBookId_Positive() {
        Inventory savedInventory = testEntityManager.persist(inventory);
        Optional<Inventory> optionalOfInventory = inventoryRepository.findByBookId("B1001");
        assertEquals(optionalOfInventory.get(), savedInventory);
    }

    @Test
    void testFindByBookId_Negative() {
        Optional<Inventory> optionalOfInventory = inventoryRepository.findByBookId("B1001");
        assertFalse(optionalOfInventory.isPresent());
    }
}