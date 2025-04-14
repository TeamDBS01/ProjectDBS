package com.project.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity class representing the inventory.
 */
@Entity
@Data
@Table(name = "inventory_table")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Inventory {

    /**
     * The ID of the inventory.
     */
    @Id
    @Column(name = "inventory_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inventoryId;

    /**
     * The quantity of books in the inventory.
     */
    @Column(name = "quantity")
    private int quantity;

    /**
     * The ID of the book.
     * Cannot be null.
     */
    @Column(name = "book_Id", nullable = false, unique = true)
    private String book_Id;
}