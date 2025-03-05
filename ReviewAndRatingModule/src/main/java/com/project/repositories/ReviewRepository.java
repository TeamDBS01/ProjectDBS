package com.project.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.entities.Review;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {

}
