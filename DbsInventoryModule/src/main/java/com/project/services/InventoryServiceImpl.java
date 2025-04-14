package com.project.services;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.project.exception.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.project.dto.InventoryDTO;
import com.project.models.Inventory;
import com.project.repositories.InventoryRepository;

import jakarta.transaction.Transactional;
/**
 * Implementation of InventoryService for managing inventory operations.
 */
@Service
public class InventoryServiceImpl implements InventoryService {


    private InventoryRepository inventoryRepository;

    private ModelMapper mapper;

    private EmailService emailService;

    /**
     * Constructs an InventoryServiceImpl with the specified dependencies.
     * @param inventoryRepository the InventoryRepository to use.
     * @param mapper the ModelMapper to use.
     * @param emailService the EmailService to use.
     */
    @Autowired
    public InventoryServiceImpl(InventoryRepository inventoryRepository,ModelMapper mapper, EmailService emailService){
        this.inventoryRepository = inventoryRepository;
        this.mapper = mapper;
        this.emailService = emailService;
    }
    public InventoryServiceImpl(){
    }

    @Override
    public int getNoOfPages() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Inventory> inventoryPage = inventoryRepository.findAll(pageable);
        return inventoryPage.getTotalPages();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<InventoryDTO> displayInventory(int page, int size) throws PageOutOfBoundsException, BookNotFoundException {
        Pageable pageable = PageRequest.of(page, size);
        Page<Inventory> inventoryPage = inventoryRepository.findAll(pageable);
        if (page >= inventoryPage.getTotalPages()) {
            throw new PageOutOfBoundsException("Page number exceeds total pages available. Total page number is " +inventoryPage.getTotalPages());
        }
        if (inventoryPage.isEmpty()) {
            throw new BookNotFoundException("Inventory Empty");
        }
        return inventoryPage.getContent().stream()
                .map(inventory -> mapper.map(inventory, InventoryDTO.class))
                .collect(Collectors.toList());
    }

    /**
         * {@inheritDoc}
         */
    @Override
    public InventoryDTO getInventoryByBookID(String bookID) throws BookNotFoundException {
        Optional<Inventory> optionalInventory = inventoryRepository.findByBookId(bookID);
        if (optionalInventory.isPresent()) {
            return mapper.map(optionalInventory.get(),InventoryDTO.class);
        } else {
            throw new BookNotFoundException("Book not found for ID: " + bookID);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void updateAddInventory(String bookID, int quantity) throws BookNotFoundException {
        Optional<Inventory> optionalInventory = inventoryRepository.findByBookId(bookID);
        if (optionalInventory.isPresent()) {
            Inventory inventory = optionalInventory.get();
            inventory.setQuantity(inventory.getQuantity() + quantity);
            inventoryRepository.save(inventory);
        } else {
            throw new BookNotFoundException("Book not found");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void updateRemoveInventory(String bookID, int quantity) throws BookNotFoundException, InsufficientInventoryException {
        Optional<Inventory> optionalInventory = inventoryRepository.findByBookId(bookID);
        if (optionalInventory.isPresent()) {
            Inventory inventory = optionalInventory.get();
            if (inventory.getQuantity() >= quantity) {
                inventory.setQuantity(inventory.getQuantity() - quantity);
                inventoryRepository.save(inventory);
                checkAndNotifyLowStock(bookID);
            } else {
                throw new InsufficientInventoryException("Not enough books in inventory");
            }
        } else {
            throw new BookNotFoundException("Book not found");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void updateInventoryAfterOrder(List<String> bookIDs, List<Integer> quantities) {
        try {
            for (int i = 0; i < bookIDs.size(); i++) {
                String bookID = bookIDs.get(i);
                int quantity = quantities.get(i);
                Optional<Inventory> optionalInventory = inventoryRepository.findByBookId(bookID);
                if (optionalInventory.isPresent()) {
                    Inventory inventory = optionalInventory.get();
                    inventory.setQuantity(Math.max((inventory.getQuantity() - quantity),0));
                    inventoryRepository.save(inventory);
                    checkAndNotifyLowStock(bookID);
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void checkAndNotifyLowStock(String bookID) throws BookNotFoundException {
        Optional<Inventory> optionalInventory = inventoryRepository.findByBookId(bookID);
        if (optionalInventory.isPresent()) {
            Inventory inventory = optionalInventory.get();
            int quantity = inventory.getQuantity();
            if (quantity< 10) {
                emailService.sendLowStockAlert(bookID, quantity);
            }
        } else {
            throw new BookNotFoundException("Book not found");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void canPlaceOrder(String bookID, int quantity) throws BookNotFoundException, OutOfStockException {
        try {
            Optional<Inventory> optionalInventory = inventoryRepository.findByBookId(bookID);
            if (optionalInventory.isPresent()) {
                Inventory inventory = optionalInventory.get();
                if (inventory.getQuantity() < quantity) {
                    throw new OutOfStockException("Not enough stock available");
                }
                checkAndNotifyLowStock(bookID);
            } else {
                throw new BookNotFoundException("Book not found for ID: " + bookID);
            }
        } catch (OutOfStockException e) {
            throw e;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getNoOfBooks(String bookID) throws BookNotFoundException {
        Optional<Inventory> optionalInventory = inventoryRepository.findByBookId(bookID);
        if (optionalInventory.isPresent()) {
            Inventory inventory = optionalInventory.get();
            return inventory.getQuantity();
        } else {
            throw new BookNotFoundException("Book not found");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void addBookToInventory(String bookID, int quantity) throws BookAlreadyExistsException {
        Optional<Inventory> optionalInventory = inventoryRepository.findByBookId(bookID);
        if (optionalInventory.isEmpty()) {
            Inventory newInventory = Inventory.builder().quantity(quantity).book_Id(bookID).build();
            inventoryRepository.save(newInventory);
        } else {
            throw new BookAlreadyExistsException("Book already exists in the inventory");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional
    public void deleteBookFromInventory(String bookID) throws BookNotFoundException {

        Inventory existingInventory = inventoryRepository.findByBookId(bookID)
                .orElseThrow(()-> new BookNotFoundException("There are no existing books with the given BookId"));
        inventoryRepository.deleteById(existingInventory.getInventoryId());
    }

}