package com.project.service;

import java.util.List;

import com.project.dto.ReviewDTO;
import com.project.exception.ReviewNotFoundException;

public interface ReviewService {
	
	List<ReviewDTO> getAllReviews() throws ReviewNotFoundException;
	ReviewDTO getReview(long reviewId) throws ReviewNotFoundException;
	boolean addReview(long userId, String bookId, float rating, String comment);
	ReviewDTO updateReview(long userId, ReviewDTO reviewDTO);
	boolean deleteReview(long userId, long reviewId) throws ReviewNotFoundException;
}
