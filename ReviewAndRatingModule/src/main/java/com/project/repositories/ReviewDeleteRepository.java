package com.project.repositories;

import com.project.models.ReviewDelete;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ReviewDeleteRepository extends JpaRepository<ReviewDelete,Long> {
    @Query("SELECT r.reviewId FROM ReviewDelete r")
    List<Long> findAllReviewIds();

    @Transactional
    void deleteByReviewId(long reviewId);

    Optional<ReviewDelete> findByReviewId(long reviewId);
}
