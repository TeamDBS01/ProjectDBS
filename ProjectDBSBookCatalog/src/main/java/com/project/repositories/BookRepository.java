package com.project.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.models.Book;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository interface for managing Book entities.
 */
@Repository
@Transactional
public interface BookRepository extends JpaRepository<Book, String> {
	/**
	 * Retrieves all books with pagination.
	 *
	 * @param pageable the pagination information
	 * @return a page of books
	 */
	Page<Book> findAll(Pageable pageable);
	/**
	 * Retrieves books by category name.
	 *
	 * @param categoryName the name of the category
	 * @return a list of books in the specified category
	 */
	@Query("SELECT b FROM Book b WHERE b.categoryID = (SELECT c.categoryID FROM Category c WHERE c.categoryName = :categoryName)")
	List<Book> getByCategory(@Param("categoryName") String categoryName);

	/**
	 * Retrieves books by author name.
	 *
	 * @return a list of books by the specified author
	 */
	@Query("SELECT b FROM Book b WHERE b.authorID = (SELECT a.authorID FROM Author a WHERE a.authorName = :authorName)")
	List<Book> getByAuthor(@Param("authorName") String author);

	/**
	 * Deletes a book by its title.
	 *
	 * @param title the title of the book to be deleted
	 */

	@Query(value="DELETE FROM Book b WHERE b.title= :title")
	@Modifying
	void deleteByTitle(String title);

	/**
	 * Finds a book by its title.
	 *
	 * @param title the title of the book
	 * @return an Optional containing the book if found, or empty if not found
	 */

	Optional<Book> findByTitle(String title);

	/**
	 * Updates a book by its ID.
	 *
	 * @param bookID     the ID of the book to be updated
	 * @param title      the new title of the book
	 * @param price      the new price of the book
	 * @param authorID   the new author ID of the book
	 * @param categoryID the new category ID of the book
	 */
	@Modifying
	@Query(value="UPDATE Book b SET b.title = :title, b.price = :price, b.authorID = :authorID, b.categoryID = :categoryID WHERE b.bookID = :bookID")
	void updateBookById(@Param("bookID") String bookID,
					   @Param("title") String title,
					   @Param("price") double price,
					   @Param("authorID") int authorID,
					   @Param("categoryID") int categoryID);
}
