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

	@Autowired
	private ReviewRepository reviewRepository;
	
//	@Autowired
//	private BookService bookService;

	@Override
	public boolean addReview(long userId, String bookId, float rating, String comment) {
		boolean added = false;
		Review review = new Review();
		review.setRating(rating);
		review.setComment(comment);

//		BookDTO bookDTO = bookService.getBook();
//		review.setBook(bookDTO.getBookID());
		try {
			reviewRepository.save(review);
			added = true;
		} catch (Exception e) {
//			e.printStackTrace();
		}
		return added;
	}
//
//	@Override
//	public ReviewDTO updateReview(ReviewDTO reviewDTO) {
//	}
//


	@Override
	public ReviewDTO updateReview(ReviewDTO reviewDTO) {
		// TODO Auto-generated method stub
		Review review = new Review(reviewDTO.getRating(), reviewDTO.getComment(), reviewDTO.getUserId(),
				reviewDTO.getBookId());
		review = reviewRepository.save(review);
		reviewDTO = new ReviewDTO(review.getReviewId(), review.getRating(), review.getComment(), review.getUserId(),
				review.getBookId());
		return reviewDTO;
	}

	@Override
	public boolean deleteReview(long userId, int reviewId) {
		// TODO Auto-generated method stub
		return false;
	}
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
