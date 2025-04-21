package com.project.service;


import com.project.dto.BookDTO;
import com.project.dto.ReviewDTO;
import com.project.dto.UserDTO;
import com.project.enums.Role;
import com.project.exception.*;
import com.project.feign.BookClient;
import com.project.feign.UserClient;
import com.project.models.Review;
import com.project.models.ReviewDelete;
import com.project.repositories.ReviewDeleteRepository;
import com.project.repositories.ReviewRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service implementation for managing reviews.
 * Provides methods to add, update, delete, and retrieve reviews.
 *
 * @author Sabarish Iyer
 * @see Review
 * @see ReviewRepository
 * @see ReviewDTO
 * @see UserClient
 * @see BookClient
 * @see ModelMapper
 * @see UserNotFoundException
 * @see BookNotFoundException
 * @see UserNotAuthorizedException
 * @see IDMismatchException
 * @see ReviewNotFoundException
 * @see ReviewService
 */

@SuppressWarnings("preview")
@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewDeleteRepository reviewDeleteRepository;
    private final UserClient userClient;
    private final BookClient bookClient;
    private final ModelMapper mapper;

    /**
     * Constructs a new ReviewServiceImpl with the specified dependencies.
     *
     * @param reviewRepository the review repository
     * @param userClient       the user client
     * @param bookClient       the book client
     * @param modelMapper      the model mapper
     */
    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository, ReviewDeleteRepository reviewDeleteRepository, UserClient userClient, BookClient bookClient, ModelMapper modelMapper) {
        this.reviewRepository = reviewRepository;
        this.reviewDeleteRepository = reviewDeleteRepository;
        this.userClient = userClient;
        this.bookClient = bookClient;
        this.mapper = modelMapper;
    }

    /**
     * Adds a new review.
     *
     * @param rating  the rating of the review
     * @param comment the comment of the review
     * @param userId  the ID of the user who wrote the review
     * @param bookId  the ID of the book being reviewed
     * @return the added review as a ReviewDTO
     * @throws UserNotFoundException if the user is not found
     * @throws BookNotFoundException if the book is not found
     */
    @Override
    public ReviewDTO addReview(float rating, String comment, long userId, String bookId) throws UserNotFoundException, BookNotFoundException, ServiceUnavailableException {
        Review review = new Review(rating, comment, userId, bookId);
        ResponseEntity<UserDTO> responseUser = userClient.getUserById(userId);
        if (responseUser.getBody() == null || responseUser.getBody().getUserId() == null) {
            throw new UserNotFoundException(STR."User with ID: \{userId} Not Found");
        }
        if (bookClient.getBookById(bookId).getStatusCode() != HttpStatus.OK)
            throw new BookNotFoundException(STR."Book with ID: \{bookId} Not Found");
        review = reviewRepository.save(review);
        ReviewDTO reviewDTO = mapper.map(review, ReviewDTO.class);
        reviewDTO.setUserName(responseUser.getBody().getName());
        return reviewDTO;
    }

    /**
     * Updates an existing review.
     *
     * @param userId    the ID of the user requesting the update
     * @param reviewDTO the review data to update
     * @return the updated review as a ReviewDTO
     * @throws UserNotAuthorizedException if the user is not authorized to update the review
     * @throws UserNotFoundException      if the user is not found
     * @throws IDMismatchException        if the review ID, user ID, or book ID do not match
     * @throws BookNotFoundException      if the book is not found
     */
    @Override
    public ReviewDTO updateReview(long userId, ReviewDTO reviewDTO) throws UserNotAuthorizedException, UserNotFoundException, IDMismatchException, BookNotFoundException, ServiceUnavailableException {
        ResponseEntity<UserDTO> responseUser = userClient.getUserById(userId);
        if (responseUser.getBody() == null || responseUser.getBody().getUserId() == null) {
            throw new UserNotFoundException(STR."User with ID: \{userId} Not Found");
        }

        int status = bookClient.getBookById(reviewDTO.getBookId()).getStatusCode().value();
        if (status != 200) {
            throw new BookNotFoundException(STR."Book with ID: \{reviewDTO.getBookId()} Not Found");
        }

        Optional<Review> optionalReview = reviewRepository.findById(reviewDTO.getReviewId());
        UserDTO userDto = responseUser.getBody();
        if (optionalReview.isEmpty() || !optionalReview.get().getBookId().equals(reviewDTO.getBookId()) || optionalReview.get().getUserId() != reviewDTO.getUserId()) {
            throw new IDMismatchException("ReviewID / UserID / BookID should not be changed");
        }
        if (userDto.getRole() == Role.ADMIN || reviewDTO.getUserId() == userId) {
            Review review = mapper.map(reviewDTO, Review.class);
            review = reviewRepository.save(review);
            reviewDTO = mapper.map(review, ReviewDTO.class);
        } else {
            throw new UserNotAuthorizedException(STR."User \{userDto.getName()} with ID: \{userId} is nor an Admin neither the review creator");
        }
        return reviewDTO;
    }

    /**
     * Deletes a review.
     *
     * @param userId   the ID of the user requesting the deletion
     * @param reviewId the ID of the review to delete
     * @return true if the review was deleted successfully
     * @throws ReviewNotFoundException    if the review is not found
     * @throws UserNotFoundException      if the user is not found
     * @throws UserNotAuthorizedException if the user is not authorized to delete the review
     */
    @Override
    public boolean deleteReview(long userId, long reviewId) throws ReviewNotFoundException, UserNotFoundException, UserNotAuthorizedException, ServiceUnavailableException {
        Optional<Review> optionalReview = reviewRepository.findById(reviewId);
        if (optionalReview.isEmpty()) {
            throw new ReviewNotFoundException(STR."Review with ID: \{reviewId} Not found!");
        }
        ResponseEntity<UserDTO> responseUser = userClient.getUserById(userId);
        if (responseUser.getBody() == null || responseUser.getBody().getUserId() == null) {
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
    public boolean addToReviewDelete(long reviewId, String reason) {
        reviewDeleteRepository.save(new ReviewDelete(reviewId, reason));
        return true;
    }

    /**
     * Deletes a reviewDelete.
     *
     * @param userId   the ID of the user requesting the deletion
     * @param reviewId the ID of the reviewDelete to delete
     * @return true if the review was deleted successfully
     * @throws ReviewNotFoundException     if the review is not found
     * @throws UserNotFoundException       if the user is not found
     * @throws UserNotAuthorizedException  if the user is not authorized to delete the review
     * @throws ServiceUnavailableException if user service is unavailable
     */
    @Override
    public boolean deleteReviewDelete(long userId, long reviewId) throws ReviewNotFoundException, ServiceUnavailableException, UserNotFoundException, UserNotAuthorizedException {
        Optional<ReviewDelete> optionalReviewDelete = reviewDeleteRepository.findByReviewId(reviewId);
        if (optionalReviewDelete.isEmpty()) {
            throw new ReviewNotFoundException(STR."Review with ID: \{reviewId} Not found in Review Delete!");
        }
        ResponseEntity<UserDTO> responseUser = userClient.getUserById(userId);
        if (responseUser.getBody() == null || responseUser.getBody().getUserId() == null) {
            throw new UserNotFoundException(STR."User with ID: \{userId} Not Found");
        }
        UserDTO userDTO = responseUser.getBody();
        if (userDTO.getRole() == Role.ADMIN) {
            reviewDeleteRepository.deleteByReviewId(reviewId);
        } else {
            throw new UserNotAuthorizedException(STR."User \{userDTO.getName()} with ID: \{userDTO.getUserId()} is not an Admin to delete this ReviewDelete");
        }
        return true;
    }

    @Override
    public List<Float> retrieveAverageRating(String bookId) {
        List<Review> reviewList = reviewRepository.findByBookId(bookId);
        List<Float> avgTotal = new ArrayList<>();
        List<Long> reviewDeleteList = reviewDeleteRepository.findAllReviewIds();
        avgTotal.add((float) reviewList.stream()
                .filter((review -> !reviewDeleteList.contains(review.getReviewId())))
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0));
        avgTotal.add((float) reviewList.size());
        return avgTotal;
    }

    /**
     * Retrieves all reviews.
     *
     * @return a list of all reviews as ReviewDTOs
     * @throws ReviewNotFoundException if no reviews are found
     */
    @Override
    public List<ReviewDTO> retrieveAllReviews() throws ReviewNotFoundException, ServiceUnavailableException {
        List<Review> reviewList = reviewRepository.findAll();
        if (reviewList.isEmpty()) {
            throw new ReviewNotFoundException("No Reviews Found!");
        }
        List<ReviewDTO> reviewDTOList = new ArrayList<>();
        List<Long> reviewDeleteList = reviewDeleteRepository.findAllReviewIds();
        for (Review review1 : reviewList) {
            if (reviewDeleteList.contains(review1.getReviewId())) {
                continue;
            }
            ReviewDTO reviewDTO = mapper.map(review1, ReviewDTO.class);
            reviewDTO.setUserName(userClient.getUserById(reviewDTO.getUserId()).getBody().getName());
            reviewDTO.setBookTitle(bookClient.getBookById(reviewDTO.getBookId()).getBody().getTitle());
            reviewDTOList.add(reviewDTO);
        }
        return reviewDTOList;
    }

    /**
     * Retrieves all reviews by user ID.
     *
     * @param userId the ID of the user
     * @return a list of reviews by the specified user as ReviewDTOs
     * @throws ReviewNotFoundException if no reviews are found for the user
     */
    @Override
    public List<ReviewDTO> retrieveAllReviewsByUserId(long userId) throws ReviewNotFoundException, ServiceUnavailableException {
        List<Review> reviewList = reviewRepository.findByUserId(userId);
        if (reviewList.isEmpty()) {
            throw new ReviewNotFoundException(STR."No Reviews with User ID: \{userId} Found!");
        }
        List<ReviewDTO> reviewDTOList = new ArrayList<>();
        List<Long> reviewDeleteList = reviewDeleteRepository.findAllReviewIds();
        for (Review review1 : reviewList) {
            if (reviewDeleteList.contains(review1.getReviewId())) {
                continue;
            }
            ReviewDTO reviewDTO = mapper.map(review1, ReviewDTO.class);
            reviewDTO.setUserName(userClient.getUserById(reviewDTO.getUserId()).getBody().getName());
            BookDTO bookDTO = bookClient.getBookById(reviewDTO.getBookId()).getBody();
            reviewDTO.setBookTitle(bookDTO.getTitle());
            reviewDTOList.add(reviewDTO);
        }
        return reviewDTOList;
    }

    /**
     * Retrieves all reviews by book ID.
     *
     * @param bookId the ID of the book
     * @return a list of reviews for the specified book as ReviewDTOs
     * @throws ReviewNotFoundException if no reviews are found for the book
     */
    @Override
    public List<ReviewDTO> retrieveAllReviewsByBookId(String bookId) throws ReviewNotFoundException, ServiceUnavailableException {
        List<Review> reviewList = reviewRepository.findByBookId(bookId);
        if (reviewList.isEmpty()) {
            throw new ReviewNotFoundException(STR."No Reviews with Book ID: \{bookId} Found!");
        }
        List<ReviewDTO> reviewDTOList = new ArrayList<>();
        List<Long> reviewDeleteList = reviewDeleteRepository.findAllReviewIds();
        for (Review review1 : reviewList) {
            if (reviewDeleteList.contains(review1.getReviewId())) {
                continue;
            }
            ReviewDTO reviewDTO = mapper.map(review1, ReviewDTO.class);
            reviewDTO.setUserName(userClient.getUserById(reviewDTO.getUserId()).getBody().getName());
            BookDTO bookDTO = bookClient.getBookById(reviewDTO.getBookId()).getBody();
            reviewDTO.setBookTitle(bookDTO.getTitle());
            reviewDTOList.add(reviewDTO);
        }
        return reviewDTOList;
    }

    /**
     * Retrieves a list of reviews, which are in review delete repository.
     *
     * @return the list of reviews as ReviewDTOs
     */
    @Override
    public List<ReviewDTO> retrieveAllReviewDeletes() throws ServiceUnavailableException {
        List<ReviewDTO> reviewDTOList = new ArrayList<>();
        List<ReviewDelete> reviewDeleteList = reviewDeleteRepository.findAll();
        for (ReviewDelete reviewDelete: reviewDeleteList) {
            try {
                ReviewDTO reviewDTO = retrieveReviewById(reviewDelete.getReviewId());
                reviewDTO.setReason(reviewDelete.getReason());
                reviewDTOList.add(reviewDTO);
            } catch (ReviewNotFoundException _) {}
        }
        return reviewDTOList;
    }

    /**
     * Retrieves a review by its ID.
     *
     * @param reviewId the ID of the review
     * @return the review as a ReviewDTO
     * @throws ReviewNotFoundException if the review is not found
     */
    @Override
    public ReviewDTO retrieveReviewById(long reviewId) throws ReviewNotFoundException, ServiceUnavailableException {
        Optional<Review> review = reviewRepository.findById(reviewId);
        if (review.isEmpty()) {
            throw new ReviewNotFoundException(STR."Review with ID: \{reviewId} Not Found");
        }
        ReviewDTO reviewDTO = mapper.map(review.get(), ReviewDTO.class);
        reviewDTO.setUserName(userClient.getUserById(review.get().getUserId()).getBody().getName());
        reviewDTO.setBookTitle(bookClient.getBookById(review.get().getBookId()).getBody().getTitle());
        return reviewDTO;
    }
}