package com.project.service;

import com.project.mapper.RecipeMapper;
import com.project.model.Recipe;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RecipeService {

    private final RecipeMapper recipeMapper;
    private final Random random = new Random();

    // ✅ 랜덤 레시피 반환
    public Recipe getRandomRecipe() {
        List<Recipe> recipes = recipeMapper.getAllRecipes();
        if (recipes.isEmpty()) {
            return null;
        }
        return recipes.get(random.nextInt(recipes.size()));
    }
}