package com.project.service;

import com.project.dto.ReviewDTO;

public interface ReviewService {
	
	boolean addReview(long userId, String bookId, float rating, String comment);
	ReviewDTO updateReview(ReviewDTO reviewDTO);
	boolean deleteReview(long userId, int reviewId);
}
