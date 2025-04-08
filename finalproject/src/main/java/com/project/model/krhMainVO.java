package com.project.model;

import lombok.Data;

@Data
public class krhMainVO {
	private int recipeId;
	private String recipeName;
	private String recipeImgage;
	 private int view;
	private String weatherRecipe; //날씨 기반 추천 레시피
}
