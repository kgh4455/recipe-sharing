package com.project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.mapper.krhMainMapper;
import com.project.model.Recipes;
import com.project.model.krhMainVO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class krhMainServiceImpl implements krhMainService{
	private final krhMainMapper krhmainMapper;
	
	//실제 비즈니스 로직
	
	//인기 레시피 조회
	@Override
	public List<Recipes> popularRecipe() {
		// TODO Auto-generated method stub
		return krhmainMapper.popularRecipe();
	}

	//최신 레시피 조회
	@Override
	public List<Recipes> recentRecipe() {
		// TODO Auto-generated method stub
		return krhmainMapper.recentRecipe();
	}

	//관심 목록에 있는 레시피들이 가장 많이 속한 카테고리 속 레시피 추천
	@Override
	public List<Recipes> getRecommendedRecipes(long userId) {
		// TODO Auto-generated method stub
		return krhmainMapper.getRecommendedRecipes(userId);
	}

	

}