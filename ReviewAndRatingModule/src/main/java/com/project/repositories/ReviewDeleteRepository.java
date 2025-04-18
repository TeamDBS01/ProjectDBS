package com.project.repositories;

import com.project.models.ReviewDelete;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewDeleteRepository extends JpaRepository<ReviewDelete,Long> {
    @Query("SELECT r.reviewId FROM ReviewDelete r")
    List<Long> findAllReviewIds();
}
