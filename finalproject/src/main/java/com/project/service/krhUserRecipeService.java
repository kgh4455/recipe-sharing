package com.project.service;

import java.util.List;

import com.project.model.UserRecipe;

public interface krhUserRecipeService {
	void addUserRecipe(UserRecipe userRecipe, List<String> ingredients);
}
