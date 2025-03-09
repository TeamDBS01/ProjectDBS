package com.project.models;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Table(name="Inventory_table")
@Entity
public class Inventory {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="inventory_id")
    private Long inventoryID;
	
	@Column(name="quantity")
    private int quantity;

    @OneToOne
    @JoinColumn(name = "bookID", nullable = false)
    @Column(name="book_id")
    private Book book;
}
