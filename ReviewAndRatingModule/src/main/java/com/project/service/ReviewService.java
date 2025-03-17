package com.project.service;

import com.project.dto.ReviewDTO;
import com.project.exception.*;

import java.util.List;

public interface ReviewService {
	
	List<ReviewDTO> retrieveAllReviews() throws ReviewNotFoundException;
	List<ReviewDTO> retrieveAllReviewsByUserId(long userId) throws ReviewNotFoundException;
	List<ReviewDTO> retrieveAllReviewsByBookId(String bookId) throws ReviewNotFoundException;
	ReviewDTO retrieveReviewById(long reviewId) throws ReviewNotFoundException;
	ReviewDTO addReview(float rating, String comment, long userId, String bookId) throws UserNotFoundException, BookNotFoundException;
	ReviewDTO updateReview(long userId, ReviewDTO reviewDTO) throws UserNotAuthorizedException, UserNotFoundException, IDMismatchException, BookNotFoundException;
	boolean deleteReview(long userId, long reviewId) throws ReviewNotFoundException, UserNotFoundException, UserNotAuthorizedException;
}
