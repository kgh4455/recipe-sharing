package com.project.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class Review {
	private long reviewId;
	private long recipesId;
	private long usersId;
	private long userRecipesId;
	private String reviewText;
	private Integer rating;
	private String name;
	private String email;
	private LocalDateTime createdAt = LocalDateTime.now();
	private LocalDateTime updatedAt = LocalDateTime.now();

	
}