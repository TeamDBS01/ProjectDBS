package com.project.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.project.dto.ReviewDTO;
import com.project.models.Review;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
	
    ReviewDTO mapReview(Review review);
    Review mapReview(ReviewDTO reviewDTO);
    

    List<ReviewDTO> mapReviewList(List<Review> reviewList);
    List<Review> mapReviewDtoList(List<ReviewDTO> reviewDtoList);
}