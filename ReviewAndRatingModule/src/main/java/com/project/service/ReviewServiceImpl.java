package com.project.service;


import com.project.dto.ReviewDTO;
import com.project.dto.UserDTO;
import com.project.enums.Role;
import com.project.exception.ReviewNotFoundException;
import com.project.models.Review;
import com.project.repositories.ReviewRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReviewServiceImpl implements ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;

//	@Autowired
	private UserService userService;
	
	@Autowired
	private ModelMapper mapper;

	@Override
	public boolean addReview(float rating, String comment, long userId, String bookId) {
		boolean added = false;
		Review review = new Review(rating, comment, userId, bookId);
		try {
			reviewRepository.save(review);
			added = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return added;
	}

	@Override
	public ReviewDTO updateReview(long userId, ReviewDTO reviewDTO) throws Exception {
		UserDTO userDto = userService.getUserById(userId).getBody();
		if (userDto.getRole() == Role.ADMIN || reviewDTO.getUserId() == userId) {
			Review review = mapper.map(reviewDTO, Review.class);
			review = reviewRepository.save(review);
			reviewDTO = mapper.map(review, ReviewDTO.class);
		} else {
			throw new Exception("NOT AUTHORISED");
		}
		return reviewDTO;
	}

	@Override
	public boolean deleteReview(long userId, long reviewId) throws ReviewNotFoundException, Exception {
		Optional<Review> optionalReview = reviewRepository.findById(reviewId);
		if (optionalReview.isEmpty()) {
			throw new ReviewNotFoundException("Review with Id: " + reviewId + " Not found!");
		}
		ReviewDTO reviewDTO = mapper.map(optionalReview.get(), ReviewDTO.class);
		UserDTO userDTO = userService.getUserById(userId).getBody();
		boolean deleted = false;
		if (userDTO.getRole() == Role.ADMIN || reviewDTO.getUserId() == userId) {
//			TODO: Chk if review Exists!
            reviewRepository.deleteById(reviewId);
            deleted = true;
        } else {
			throw new Exception("NOT AUTHORISED");
		}
		return deleted;
	}

	@Override
	public List<ReviewDTO> getAllReviews() throws ReviewNotFoundException {
		List<Review> reviewList = reviewRepository.findAll();
		if (reviewList.isEmpty()) {
			throw new ReviewNotFoundException("No Reviews Found!");
		}
		return reviewList.stream()
				.map(review -> mapper.map(review, ReviewDTO.class))
				.toList(); // use collect?
	}

	@Override
	public ReviewDTO getReviewById(long reviewId) throws ReviewNotFoundException {
		Optional<Review> review = reviewRepository.findById(reviewId);
		if (review.isEmpty()) {
			throw new ReviewNotFoundException("Review with Id: " + reviewId + " Not Found");
		}
		return mapper.map(review.get(), ReviewDTO.class);
	}
}
