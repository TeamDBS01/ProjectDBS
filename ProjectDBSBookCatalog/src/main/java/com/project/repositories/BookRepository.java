package com.project.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.models.Book;
import org.springframework.transaction.annotation.Transactional;


@Repository
@Transactional
public interface BookRepository extends JpaRepository<Book, String> {
	@Query("SELECT b FROM Book b WHERE b.categoryID = (SELECT c.categoryID FROM Category c WHERE c.categoryName = :categoryName)")
    //List<Book> getByCategory(String category);
	List<Book> getByCategory(@Param("categoryName") String categoryName);
	
	@Query("SELECT b FROM Book b WHERE b.authorID = (SELECT a.authorID FROM Author a WHERE a.authorName = :authorName)")
	List<Book> getByAuthor(@Param("authorName") String author);
	
	@Query(value="delete FROM Book b WHERE b.title= :title")
	@Modifying
	void deleteByTitle(String title);
	Optional<Book> findByTitle(String title);
	@Modifying
	@Query(value="UPDATE Book b SET b.title = :title, b.price = :price, b.authorID = :authorID, b.categoryID = :categoryID WHERE b.bookID = :bookID")
	void updateBookById(@Param("bookID") String bookID,
					   @Param("title") String title,
					   @Param("price") double price,
					   @Param("authorID") int authorID,
					   @Param("categoryID") int categoryID);
}
