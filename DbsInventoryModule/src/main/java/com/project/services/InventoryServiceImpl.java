package com.project.services;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.project.exception.BookAlreadyExistsException;
import com.project.exception.InsufficientInventoryException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import com.project.dto.InventoryDTO;
import com.project.exception.BookNotFoundException;
import com.project.exception.OutOfStockException;
import com.project.models.Inventory;
import com.project.repositories.InventoryRepository;

import jakarta.transaction.Transactional;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ModelMapper mapper;
    @Autowired
    private EmailService emailService;

    @Override
    public List<InventoryDTO> displayInventory() {
        List<Inventory> inventoryList = inventoryRepository.findAll();
        if (inventoryList.isEmpty()) {
            throw new BookNotFoundException("Inventory Empty");
        }
        List<InventoryDTO> inventoryDTOList = inventoryList.stream()
                .map(inventory -> mapper.map(inventory, InventoryDTO.class))
                .collect(Collectors.toList());
        return inventoryDTOList;
    }

    @Override
    public InventoryDTO getInventoryByBookID(String bookID) throws BookNotFoundException {
        Optional<Inventory> optionalInventory = inventoryRepository.findByBookId(bookID);
        if (optionalInventory.isPresent()) {
            return mapper.map(optionalInventory.get(),InventoryDTO.class);
        } else {
            throw new BookNotFoundException("Book not found for ID: " + bookID);
        }
    }

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
                    inventory.setQuantity(inventory.getQuantity() - quantity);
                    inventoryRepository.save(inventory);
                    checkAndNotifyLowStock(bookID);
                }
            }
        } catch (Exception e) {
            throw e;
        }
    }

    @Override
    @EventListener(ApplicationContext.class)
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

    @Override
    @Transactional
    public void placeOrder(String bookID, int quantity) throws BookNotFoundException {
        try {
            Optional<Inventory> optionalInventory = inventoryRepository.findByBookId(bookID);
            if (optionalInventory.isPresent()) {
                Inventory inventory = optionalInventory.get();
                if (inventory.getQuantity() < quantity) {
                    throw new OutOfStockException("Not enough stock available");
                }
                inventory.setQuantity(inventory.getQuantity() - quantity);
                inventoryRepository.save(inventory);
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

    @Override
    public void deleteBookFromInventory(String bookID) throws BookNotFoundException {

        Inventory existingInventory = inventoryRepository.findByBookId(bookID)
                .orElseThrow(()-> new BookNotFoundException("There are no existing books with the given BookId"));
        inventoryRepository.deleteById(existingInventory.getInventoryId());
    }

}