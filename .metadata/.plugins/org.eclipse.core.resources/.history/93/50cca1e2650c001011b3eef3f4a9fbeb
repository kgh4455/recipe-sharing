package com.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.mapper.ReviewMapper;
import com.project.model.Review;
import com.project.model.User;

@Service
public class ReviewService {
	
	@Autowired
	private ReviewMapper reviewMapper;
	
	
	public List<Review> getAllReview(long recipesId){
		List<Review> reviews = reviewMapper.getAllReview(recipesId);
		return reviews;
	}
	
	public List<Review> getAllUserReview(long userRecipesId){
		List<Review> reviews = reviewMapper.getAllUserReview(userRecipesId);
		return reviews;
	}
	
	public void addReview(Review review, Long userId) {
	    if (userId != null) {
	        review.setUsersId(userId);  
	        reviewMapper.insertReview(review);  
	    } else {
	        throw new RuntimeException("사용자를 찾을 수 없습니다.");
	    }
	}
	
	public void addUserReview(Review review, Long userId) {
		if(userId != null) {
			review.setUsersId(userId);
			reviewMapper.insertUserReview(review);
		}else {
			throw new RuntimeException("사용자를 찾을 수 없습니다.");
		}
	}
	
	 public void putReview(Review review, String email) {
	        review.setEmail(email);
	        reviewMapper.putReview(review);
	    }
	 
	 public void putUserReview(Review review, String email) {
		 review.setEmail(email);
		 reviewMapper.putReview(review);
	 }
	
	 public void deleteReview(Long reviewId) {
	        reviewMapper.deleteReview(reviewId);
	    }

	 public Review getReviewById(Long reviewId) {
	        return reviewMapper.getReviewById(reviewId);
	    }

	 public Review getUserReviewById(Long reviewId) {
		 	return reviewMapper.getReviewById(reviewId);
	 }
}