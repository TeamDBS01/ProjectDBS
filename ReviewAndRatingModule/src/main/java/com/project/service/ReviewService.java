package com.project.service;

import com.project.dto.ReviewDTO;
import com.project.exception.*;

import java.util.List;

public interface ReviewService {
	
	List<ReviewDTO> retrieveAllReviews() throws ReviewNotFoundException, ServiceUnavailableException;
	List<ReviewDTO> retrieveAllReviewsByUserId(long userId) throws ReviewNotFoundException, ServiceUnavailableException;
	List<ReviewDTO> retrieveAllReviewsByBookId(String bookId) throws ReviewNotFoundException, ServiceUnavailableException;
	ReviewDTO retrieveReviewById(long reviewId) throws ReviewNotFoundException, ServiceUnavailableException;
	ReviewDTO addReview(float rating, String comment, long userId, String bookId) throws UserNotFoundException, BookNotFoundException, ServiceUnavailableException;
	ReviewDTO updateReview(long userId, ReviewDTO reviewDTO) throws UserNotAuthorizedException, UserNotFoundException, IDMismatchException, BookNotFoundException, ServiceUnavailableException;
	boolean deleteReview(long userId, long reviewId) throws ReviewNotFoundException, UserNotFoundException, UserNotAuthorizedException, ServiceUnavailableException;
	boolean addToReviewDelete(long reviewId, String comment);
	List<Float> retrieveAverageRating(String bookId);
}
