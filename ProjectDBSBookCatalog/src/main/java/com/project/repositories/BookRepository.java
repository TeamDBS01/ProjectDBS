package com.project.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.project.models.Book;

import jakarta.transaction.Transactional;

@Repository
@Transactional
public interface BookRepository extends JpaRepository<Book, String> {
	@Query("SELECT b FROM Book b WHERE b.categoryID = (SELECT c.categoryID FROM Category c WHERE c.categoryName = :categoryName)")
    //List<Book> getByCategory(String category);
	List<Book> getByCategory(@Param("categoryName") String categoryName);
	
	@Query("SELECT b FROM Book b WHERE b.authorID = (SELECT a.authorID FROM Author a WHERE a.authorName = :authorName)")
	List<Book> getByAuthor(@Param("authorName") String author);
	
//	 @Query("SELECT b FROM Book b JOIN Author a ON b.authorID = a.authorID JOIN Category c ON b.categoryID = c.categoryID WHERE (a.authorName IS NULL OR a.authorName = :authorName) AND (c.categoryName IS NULL OR c.categoryName = :categoryName)")
//	 List<Book> filter(@Param("authorName") String author, @Param("categoryName") String category);
	
	
	@Query(value="delete FROM Book b WHERE b.title= :title")
	@Modifying
	void deleteByTitle(String title);
}
