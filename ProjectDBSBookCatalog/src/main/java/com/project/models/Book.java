package com.project.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="book")
public class Book {
	@Id
	@Column(name="book_id")
	private String bookID;
	@Column(name="title")
	private String title;
	@Column(name="price")
	private double price;
	@Column(name="inventory_id")
	private long inventoryID;
	
	@Column(name="author_id")
	private int authorID;
	@Column(name="category_id")
	private int categoryID;
//
//	public String getBookID() {
//		return BookID;
//	}
//
//	public Book() {
//	}
//
//	public Book(String bookID, String title, double price, long inventoryID, int authorID, int categoryID) {
//		BookID = bookID;
//		this.title = title;
//		this.price = price;
//		this.inventoryID = inventoryID;
//		this.authorID = authorID;
//		this.categoryID = categoryID;
//	}
//
//	@Override
//	public boolean equals(Object o) {
//		if (o == null || getClass() != o.getClass()) return false;
//		Book book = (Book) o;
//		return Double.compare(price, book.price) == 0 && inventoryID == book.inventoryID && authorID == book.authorID && categoryID == book.categoryID && Objects.equals(BookID, book.BookID) && Objects.equals(title, book.title);
//	}
//
//	@Override
//	public int hashCode() {
//		return Objects.hash(BookID, title, price, inventoryID, authorID, categoryID);
//	}
//
//	@Override
//	public String toString() {
//		return "Book{" +
//				"BookID='" + BookID + '\'' +
//				", title='" + title + '\'' +
//				", price=" + price +
//				", inventoryID=" + inventoryID +
//				", authorID=" + authorID +
//				", categoryID=" + categoryID +
//				'}';
//	}
//
//	public void setBookID(String bookID) {
//		BookID = bookID;
//	}
//
//	public String getTitle() {
//		return title;
//	}
//
//	public void setTitle(String title) {
//		this.title = title;
//	}
//
//	public double getPrice() {
//		return price;
//	}
//
//	public void setPrice(double price) {
//		this.price = price;
//	}
//
//	public long getInventoryID() {
//		return inventoryID;
//	}
//
//	public void setInventoryID(long inventoryID) {
//		this.inventoryID = inventoryID;
//	}
//
//	public int getAuthorID() {
//		return authorID;
//	}
//
//	public void setAuthorID(int authorID) {
//		this.authorID = authorID;
//	}
//
//	public int getCategoryID() {
//		return categoryID;
//	}
//
//	public void setCategoryID(int categoryID) {
//		this.categoryID = categoryID;
//	}
}
