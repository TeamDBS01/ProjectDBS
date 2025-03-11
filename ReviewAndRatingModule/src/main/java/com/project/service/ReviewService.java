package com.project.service;

import com.project.dto.ReviewDTO;
import com.project.exception.ReviewNotFoundException;
import com.project.exception.UserNotAuthorizedException;
import com.project.exception.UserNotFoundException;

import java.util.List;

public interface ReviewService {
	
	List<ReviewDTO> retrieveAllReviews() throws ReviewNotFoundException;
	List<ReviewDTO> retrieveAllReviewsByUserId(long userId) throws ReviewNotFoundException;
	ReviewDTO retrieveReviewById(long reviewId) throws ReviewNotFoundException;
	ReviewDTO addReview(float rating, String comment, long userId, String bookId);
	ReviewDTO updateReview(long userId, ReviewDTO reviewDTO) throws UserNotAuthorizedException, UserNotFoundException;
	boolean deleteReview(long userId, long reviewId) throws ReviewNotFoundException, UserNotFoundException, UserNotAuthorizedException;
}
