package com.project.service;

import java.util.List;  

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.mapper.RecipesMapper;
import com.project.model.Favorite;
import com.project.model.Ingredients;
import com.project.model.Recipes;
import com.project.model.User;
import com.project.model.UserRecipe;

@Service
public class RecipesService {
	
	@Autowired
	private RecipesMapper recipesMapper;
	@Autowired
	private UserService userService;
	
	public List<Recipes> getAllRecipes(){
		List<Recipes> recipes = recipesMapper.getAllRecipes();
		System.out.println("기본");
		return recipes;
	}
	
	public List<Recipes> getPopularRecipes(){
		List<Recipes> recipes = recipesMapper.getPopularRecipes();
		return recipes;
	}
	
	public Recipes recipeById(long recipesId) {
		Recipes recipe = recipesMapper.findById(recipesId);
		if (recipe == null) {
			throw new RuntimeException("레시피를 찾을 수 없습니다: " + recipesId);
		}
		return recipe;
	}
	
	public Recipes findById(long recipesId) {
		Recipes recipe = recipesMapper.findById(recipesId);
		if(recipe == null) {
			throw new RuntimeException("레시피를 찾을 수 없음");
		}
		return recipe;
	}
	
	 public Recipes findByRecipeId(long recipeId) {
	        return recipesMapper.findByRecipeId(recipeId);
	    }

	
	public List<Ingredients> getIngredientsByRecipeId(long recipesId) {
		 List<Ingredients> ingredients = recipesMapper.findIngredientsByRecipeId(recipesId);
		    
		    return ingredients;
	}
	
	 public void update(Recipes recipe) {
	        
		 recipesMapper.incrementViewCount(recipe.getRecipesId());
	    }
	
	 public void addFavorite(Recipes recipe, String email) {
		    User user = userService.getUserByEmail(email); // 사용자 정보 가져오기
		    if (user != null) {
		        recipesMapper.addFavoriteList(recipe.getRecipesId(), user.getId());
		    } else {
		        throw new RuntimeException("사용자를 찾을 수 없습니다.");
		    }
		}
	
	 @Transactional
	public void deleteFavorite(Recipes recipe, String email) {
		 User user = userService.getUserByEmail(email);
		 if(user != null) {
			 long userId = user.getId();  
		     long recipeId = recipe.getRecipesId();  

		     recipesMapper.deleteFavoriteList(userId, recipeId);
		 }
	}
	
	public List<Recipes> findByFoodName(String query){
		return recipesMapper.findByFoodName(query);
	}
	
	public List<Recipes> findByIngredient(String query){
		return recipesMapper.findByIngredient(query);
	}
	
	public List<Recipes> getFavoritesByUserId(Long userId) {
	    return recipesMapper.getFavoritesByUserId(userId);
	}
	
	public List<Recipes> getWeatherRecipes(String precipitation){
    	List<Recipes> recipes = recipesMapper.getWeatherRecipes(precipitation);
    	return recipes;
    }
	

	
	
}



