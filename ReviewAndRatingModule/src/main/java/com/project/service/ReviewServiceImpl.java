package com.project.service;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.dto.ReviewDTO;
import com.project.dto.UserDTO;
import com.project.enums.Role;
import com.project.exception.ReviewNotFoundException;
import com.project.mapper.ReviewMapper;
import com.project.models.Review;
import com.project.repositories.ReviewRepository;

@Service
public class ReviewServiceImpl implements ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;
	
//	@Autowired
//	private BookService bookService;

//	@Autowired
//	private UserService userService;
	
	@Autowired
	private ReviewMapper reviewMapper;

	@Override
	public boolean addReview(long userId, String bookId, float rating, String comment) {
		boolean added = false;
		Review review = new Review(rating, comment, userId, bookId);
		review.setUserId(userId);
		review.setBookId(bookId);
		review.setRating(rating);
		review.setComment(comment);
		try {
			reviewRepository.save(review);
			added = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return added;
	}


	@Override
	public ReviewDTO updateReview(long userId, ReviewDTO reviewDTO) {
//		UserDTO userDto = userService.getUserById(userId);
//		if (userDto.getRole() == Role.ADMIN || reviewDTO.getUserId() == userId) {
			Review review = reviewMapper.mapReview(reviewDTO);
			review = reviewRepository.save(review);
			reviewDTO = reviewMapper.mapReview(review);
//		} esle NOT AUTHORISED
		return reviewDTO;
	}

	@Override
	public boolean deleteReview(long userId, long reviewId) throws ReviewNotFoundException {
		Optional<Review> optionalReview = reviewRepository.findById(reviewId);
		if (optionalReview.isEmpty()) {
			throw new ReviewNotFoundException("Review with Id: " + reviewId + " Not found!");
		}
		ReviewDTO reviewDTO = reviewMapper.mapReview(optionalReview.get());
//		UserDTO userDto = userService.getUserById(userId);
		boolean deleted = false;
//		if (userDto.getRole() == Role.ADMIN || reviewDTO.getUserId() == userId) {
			try {
				reviewRepository.deleteById(reviewId);
				deleted = true;
			} catch (Exception e) {
			}
//		} else {
//			throw new Exception("NOT AUTHORISED");
//		}
		return deleted;
	}


	@Override
	public List<ReviewDTO> getAllReviews() throws ReviewNotFoundException {
		List<Review> reviewList = reviewRepository.findAll();
		if (reviewList.isEmpty()) {
			throw new ReviewNotFoundException("No Reviews Found!");
		}
		List<ReviewDTO> list = reviewMapper.mapReviewList(reviewList);
		List<Review> ls = reviewMapper.mapReviewDtoList(list);
		list = reviewMapper.mapReviewList(ls);
		return list;
	}


	@Override
	public ReviewDTO getReview(long reviewId) throws ReviewNotFoundException {
		Optional<Review> review = reviewRepository.findById(reviewId);
		if (review.isEmpty()) {
			throw new ReviewNotFoundException("Review with Id: " + reviewId + " Not Found");
		}
		ReviewDTO reviewDTO = reviewMapper.mapReview(review.get());
		return reviewDTO;
	}

}
