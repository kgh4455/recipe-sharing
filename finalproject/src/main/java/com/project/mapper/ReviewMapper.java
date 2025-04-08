package com.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.mybatis.spring.annotation.MapperScan;

import com.project.model.Review;

@Mapper
@MapperScan("com.project.mapper")
public interface ReviewMapper {
	
	@Select("SELECT r.*, u.* FROM Reviews r JOIN Users u ON r.user_id = u.id WHERE r.recipes_id = #{id} ORDER BY r.created_at DESC")
    List<Review> getAllReview(@Param("id") long id);
	
	@Select("SELECT r.*, u.* FROM Reviews r JOIN Users u ON r.user_id = u.id WHERE r.UserRecipesId = #{id} ORDER BY r.created_at DESC")
	List<Review> getAllUserReview(@Param("id") long id);
	
	@Insert("INSERT INTO Reviews (recipes_id, user_id, review_text, rating, created_at, updated_at) " +
	        "VALUES (#{recipesId}, #{usersId}, #{reviewText}, #{rating}, NOW(), NOW())")
	void insertReview(Review review);
	
	@Insert("INSERT INTO Reviews (UserRecipesId, user_id, review_text, rating, created_at, updated_at)" +
			"VALUES (#{userRecipesId}, #{usersId}, #{reviewText}, #{rating}, NOW(), NOW())")
	void insertUserReview(Review review);
	
	@Update("UPDATE Reviews SET review_text = #{reviewText}, rating = #{rating}, updated_at = NOW() WHERE review_id = #{reviewId}")
    void putReview(Review review);
	
    
	@Delete("DELETE FROM Reviews WHERE review_id = #{id}")
    void deleteReview(@Param("id") long id);
    
	@Select("SELECT r.*, u.* FROM Reviews r LEFT JOIN Users u ON r.user_id = u.id WHERE r.review_id = #{reviewId}")
	Review getReviewById(@Param("reviewId") long reviewId);

	@Select("SELECT COUNT(*) FROM Reviews WHERE recipes_id = #{recipesId}")
	int countRecipeReview(@Param("recipesId") long recipesId);

	@Select("SELECT COUNT(*) FROM Reviews WHERE UserRecipesId = #{userRecipesId}")
	int countUserRecipeReview(@Param("userRecipesId") long userRecipesId);
}