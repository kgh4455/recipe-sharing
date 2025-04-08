package com.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.project.model.UserRecipe;

@Mapper
public interface krhUserRecipeMapper {
	
	//레시피 추가
	void insertUserRecipe(UserRecipe userRecipe);
	
	//재료 추가
	void insertIngredientsIfNotExist(String ingredient);

	//레시피-재료 추가
	void linkUserRecipeIngredient(Long userRecipesId, long ingredientId);

	long getIngredientIdByName(String ingredient);
}