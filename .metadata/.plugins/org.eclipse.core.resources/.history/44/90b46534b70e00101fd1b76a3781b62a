package com.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.mapper.krhUserRecipeMapper;
import com.project.model.UserRecipe;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class krhUserRecipeServiceImpl implements krhUserRecipeService {
    
    @Autowired
    private krhUserRecipeMapper userRecipeMapper;

    @Transactional
    @Override
    public void addUserRecipe(UserRecipe userRecipe, List<String> ingredients) {
        System.out.println("재료들: " + ingredients);

        // 1. 레시피 추가
        userRecipeMapper.insertUserRecipe(userRecipe);

        // 2. 레시피 ID를 가져와서 재료 추가
        long recipeId = userRecipe.getUserRecipesId(); // 레시피의 ID는 DB에 저장된 후에 생성됩니다.
        System.out.println("레시피 아이디: " + recipeId);

        // 3. 재료가 존재하지 않으면 추가 (반복문을 통해 하나씩 처리)
        for (String ingredient : ingredients) {
            userRecipeMapper.insertIngredientsIfNotExist(ingredient);
        }

        // 4. 재료와 레시피의 관계 추가
        for (String ingredient : ingredients) {
            long ingredientId = getIngredientId(ingredient);
            if (ingredientId > 0) {
                userRecipeMapper.linkUserRecipeIngredient(recipeId, ingredientId);
            } else {
                // 재료 ID가 유효하지 않은 경우 예외 처리 또는 로그 출력
                System.err.println("재료 ID가 유효하지 않습니다: " + ingredient);
            }
        }
    }

    // 재료 이름을 이용하여 ID를 조회하는 메서드
    private long getIngredientId(String ingredient) {
        Long ingredientId = userRecipeMapper.getIngredientIdByName(ingredient);
        if (ingredientId == null) {
            // 재료가 없는 경우 예외를 던지거나 기본값을 반환
            return 0;
        }
        return ingredientId;
    }
}
