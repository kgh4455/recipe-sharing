package com.project.service;

import java.util.List;

import com.project.model.Recipes;
import com.project.model.krhMainVO;

public interface krhMainService{
	//인기 레시피 조회
	List<Recipes> popularRecipe();
	
	//최신 레시피 조회
	List<Recipes> recentRecipe();
	
	//관심 목록에 있는 레시피들이 가장 많이 속한 카테고리 속 레시피 추천
	List<Recipes> getRecommendedRecipes(long userId);
}
