package com.project.repositories;

import com.project.models.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Repository interface for Review entities.
 * Provides CRUD operations and custom query methods.
 *
 * @see Review
 * @see JpaRepository
 * @see Repository
 *
 * @author Sabarish Iyer
 */

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * Finds all reviews by the specified user ID.
     *
     * @param userId the ID of the user
     * @return a list of reviews by the specified user
     */
    List<Review> findByUserId(long userId);

    /**
     * Finds all reviews by the specified book ID.
     *
     * @param bookId the ID of the book
     * @return a list of reviews for the specified book
     */
    List<Review> findByBookId(String bookId);
}
