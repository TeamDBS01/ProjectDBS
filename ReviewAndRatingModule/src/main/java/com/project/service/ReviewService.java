package com.project.service;

import java.util.List;

import com.project.dto.ReviewDTO;
import com.project.exception.ReviewNotFoundException;

public interface ReviewService {
	
	List<ReviewDTO> getAllReviews() throws ReviewNotFoundException;
	ReviewDTO getReviewById(long reviewId) throws ReviewNotFoundException;
	boolean addReview(float rating, String comment, long userId, String bookId);
	ReviewDTO updateReview(long userId, ReviewDTO reviewDTO) throws Exception;
	boolean deleteReview(long userId, long reviewId) throws ReviewNotFoundException, Exception;
}
