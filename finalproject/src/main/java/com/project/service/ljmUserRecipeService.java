package com.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.mapper.ljmUserRecipeMapper;
import com.project.model.Ingredients;
import com.project.model.Recipes;
import com.project.model.User;
import com.project.model.UserRecipe;

@Service
public class ljmUserRecipeService {

	 @Autowired
	    private ljmUserRecipeMapper ljmuserRecipeMapper;
	 @Autowired
	 	private UserService userService;

	    // 모든 사용자 레시피와 재료 가져오기
	    public List<UserRecipe> getAllUserRecipes() {
	        List<UserRecipe> userRecipes = ljmuserRecipeMapper.getAllUserRecipes();
	        
	        return userRecipes;
	    }
	    
	    public List<Ingredients> getIngredientsByUserRecipeId(long userRecipesId){
	    	List<Ingredients> ingredients = ljmuserRecipeMapper.findByIngredientsByUserRecipeId(userRecipesId);
	    	
	    	return ingredients;
	    }
	    
	    public UserRecipe userRecipeById(long userRecipesId) {
	    	UserRecipe userRecipe = ljmuserRecipeMapper.userFindById(userRecipesId);
	    	if(userRecipe == null) {
	    		throw new RuntimeException("레시피를 찾을 수 없습니다 : " + userRecipesId);
	    	}
	    	return userRecipe;
	    }
	    
	    public UserRecipe findUserById(long userRecipesId) {
	    	UserRecipe userRecipe = ljmuserRecipeMapper.userFindById(userRecipesId);
	    	if(userRecipe == null) {
	    		throw new RuntimeException("레시피를 찾을 수 없습니다.");
	    	}
	    	return userRecipe;
	    }
	    
	    public void update(UserRecipe userRecipe) {
	    	ljmuserRecipeMapper.incrementUserViewCount(userRecipe.getUserRecipesId());
	    }
	    
	    public void addUserFavorite(UserRecipe userRecipe, String email) {
	    	User user = userService.getUserByEmail(email);
	    	if(user != null) {
	    		ljmuserRecipeMapper.addUserFavoriteList(userRecipe.getUserRecipesId(), user.getId());
	    	}else {
	    		throw new RuntimeException("사용자를 찾을 수 없습니다.");
	    	}	
	    }
	    
	    
	    @Transactional
	    public void deleteUserFavorite(UserRecipe userRecipe, String email) {
	    	User user = userService.getUserByEmail(email);
	    	if(user  != null) {
	    		long userId = user.getId();
	    		long userRecipesId = userRecipe.getUserRecipesId();
	    		
	    		ljmuserRecipeMapper.deleteUserFavoriteList(userId, userRecipesId);
	    	}
	    }
	    
	    public List<UserRecipe> findByUserFoodName(String query){
	    	return ljmuserRecipeMapper.findByUserFoodName(query);
	    }
	    
	    public List<UserRecipe> findByUserIngredients(String query){
	    	return ljmuserRecipeMapper.findByUserIngredients(query);
	    }
	    
	    public List<UserRecipe> getUserFavoriteByUserId(Long userId){
	    	return ljmuserRecipeMapper.getUserFavoriteByUserId(userId);
	    }
	    
	    public UserRecipe findByUserRecipeId(long userRecipesId) {
	        return ljmuserRecipeMapper.findByRecipeId(userRecipesId);
	    }

}