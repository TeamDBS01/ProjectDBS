package com.project.service;


import com.project.dto.ReviewDTO;
import com.project.dto.UserDTO;
import com.project.enums.Role;
import com.project.exception.ReviewNotFoundException;
import com.project.exception.UserNotAuthorizedException;
import com.project.exception.UserNotFoundException;
import com.project.models.Review;
import com.project.repositories.ReviewRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@SuppressWarnings("preview")
@Service
public class ReviewServiceImpl implements ReviewService {

	@Autowired
	private ReviewRepository reviewRepository;
//	private UserService userService;
	@Autowired
	private UserService userService;
	@Autowired
	private ModelMapper mapper;

//	@Autowired
//	public ReviewServiceImpl(ReviewRepository reviewRepository, ModelMapper modelMapper) {
//	public ReviewServiceImpl(ReviewRepository reviewRepository, UserService userService, ModelMapper modelMapper) {
//		this.reviewRepository = reviewRepository;
//		this.userService = userService;
//		this.mapper = modelMapper;
//	}

	@Override
	public ReviewDTO addReview(float rating, String comment, long userId, String bookId) {
		Review review = new Review(rating, comment, userId, bookId);
		review = reviewRepository.save(review);
		return mapper.map(review, ReviewDTO.class);
	}

	@Override
	public ReviewDTO updateReview(long userId, ReviewDTO reviewDTO) throws UserNotAuthorizedException, UserNotFoundException {
		ResponseEntity<UserDTO> responseUser = userService.getUserById(userId);
		if (responseUser == null || !responseUser.hasBody() || responseUser.getBody() == null) {
			throw new UserNotFoundException(STR."User with ID: \{userId} Not Found");
		}
		UserDTO userDto = responseUser.getBody();
		if (userDto.getRole() == Role.ADMIN || reviewDTO.getUserId() == userId) {
			Review review = mapper.map(reviewDTO, Review.class);
			review = reviewRepository.save(review);
			reviewDTO = mapper.map(review, ReviewDTO.class);
		} else {
			throw new UserNotAuthorizedException(STR."User with ID: \{userId} is nor an Admin neither the review creator");
		}
		return reviewDTO;
	}

	@Override
	public boolean deleteReview(long userId, long reviewId) throws ReviewNotFoundException, UserNotFoundException, UserNotAuthorizedException {
		Optional<Review> optionalReview = reviewRepository.findById(reviewId);
		if (optionalReview.isEmpty()) {
			throw new ReviewNotFoundException(STR."Review with Id: \{reviewId} Not found!");
		}
		ResponseEntity<UserDTO> responseUser = userService.getUserById(userId);
		if (responseUser == null || !responseUser.hasBody() || responseUser.getBody() == null) {
			throw new UserNotFoundException(STR."User with ID: \{userId} Not Found");
		}
		UserDTO userDTO = responseUser.getBody();
		ReviewDTO reviewDTO = mapper.map(optionalReview.get(), ReviewDTO.class);
		if (userDTO.getRole() == Role.ADMIN || reviewDTO.getUserId() == userId) {
            reviewRepository.deleteById(reviewId);
        } else {
			throw new UserNotAuthorizedException(STR."User \{userDTO.getName()} with ID: \{userDTO.getUserId()} is nor an Admin, neither is the creator of this Review");
		}
		return true;
	}

	@Override
	public List<ReviewDTO> retrieveAllReviews() throws ReviewNotFoundException {
		List<Review> reviewList = reviewRepository.findAll();
		if (reviewList.isEmpty()) {
			throw new ReviewNotFoundException("No Reviews Found!");
		}
		return reviewList.stream()
				.map(review -> mapper.map(review, ReviewDTO.class))
				.toList();
	}

	@Override
	public List<ReviewDTO> retrieveAllReviewsByUserId(long userId) throws ReviewNotFoundException {
		List<Review> reviewList = reviewRepository.findByUserId(userId);
		if (reviewList.isEmpty()) {
			throw new ReviewNotFoundException(STR."No Reviews with User ID: \{userId} Found!");
		}
		return reviewList.stream()
				.map((review) ->mapper.map(review, ReviewDTO.class))
				.toList();
	}

	@Override
	public ReviewDTO retrieveReviewById(long reviewId) throws ReviewNotFoundException {
		Optional<Review> review = reviewRepository.findById(reviewId);
		if (review.isEmpty()) {
			throw new ReviewNotFoundException(STR."Review with Id: \{reviewId} Not Found");
		}
		return mapper.map(review.get(), ReviewDTO.class);
	}
}
