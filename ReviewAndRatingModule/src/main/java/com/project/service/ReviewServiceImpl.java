package com.project.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.dto.BookDTO;
import com.project.dto.ReviewDTO;
import com.project.dto.UserDTO;
import com.project.models.Review;
import com.project.repositories.ReviewRepository;

@Service
public class ReviewServiceImpl implements ReviewService {

//	@Autowired
//	private ReviewRepository reviewRepository;
//
//	@Override
//	public boolean addReview(UserDTO userDto, float rating, String comment, BookDTO bookDto) {
//		boolean added = false;
//		Review review = new Review();
//		review.setRating(rating);
//		review.setComment(comment);
//		review.setBook(bookDto);
//		try {
//			reviewRepository.save(review);
//			added = true;
//		} catch (Exception e) {
//		}
//		return added;
//	}
//
//	@Override
//	public ReviewDTO updateReview(ReviewDTO reviewDTO) {
//		Review review = new Review(reviewDTO.getRating(), reviewDTO.getComment(), reviewDTO.getUser(),
//				reviewDTO.getBook());
//		review = reviewRepository.save(review);
//		reviewDTO = new ReviewDTO(review.getReviewId(), review.getRating(), review.getComment(), review.getBook(),
//				review.getUser());
//		return reviewDTO;
//	}
//
//	@Override
//	public boolean deleteReview(UserDTO userDto, int reviewId) {
//		boolean deleted = false;
//		if (userDto.isAdmin()) {
//			try {
//				reviewRepository.deleteById(reviewId);
//				deleted = true;
//			} catch (Exception e) {
//			}
//		}
//		return deleted;
//	}

}
