package com.project.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name="category")
public class Category {
	@Id
	@Column(name="category_id")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int categoryID;
	@Column(name="category_name")
	private String categoryName;
}
