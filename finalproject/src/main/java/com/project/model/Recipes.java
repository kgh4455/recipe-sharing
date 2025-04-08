package com.project.model;


import java.util.List;

import lombok.Data;

@Data
public class Recipes {
	 private Long recipesId;
	 private String foodName;
	 private String foodImg;
	 private String step1;
	 private String step2;
	 private String step3;
	 private String step4;
	 private String step5;
	 private String step6;
	 private String stepImg1;
	 private String stepImg2;
	 private String stepImg3;
	 private String stepImg4;
	 private String stepImg5;
	 private String stepImg6;
	 private int view;
	 private int foodTime;
	 private String categoryName;
	 private int categoryId;
	 private String ingredientNames;
	 private List<Ingredients> ingredients; 
	 
	 public List<Ingredients> getIngredients() {
	        return ingredients;
	    }

	    public void setIngredients(List<Ingredients> ingredients) {
	        this.ingredients = ingredients;
	    }

}