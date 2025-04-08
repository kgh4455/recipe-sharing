package com.project.model;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {
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
    private int categoryId;
    private Integer weatherId;

    // ✅ 해당 레시피의 재료 리스트 추가
    private List<Ingredient> ingredients;

    // ✅ 올바른 생성자 추가 (컨트롤러와 일치하도록)
    public Recipe(Long recipesId, String foodName, int foodTime, int categoryId, Integer weatherId) {
        this.recipesId = recipesId;
        this.foodName = foodName;
        this.foodTime = foodTime;
        this.categoryId = categoryId;
        this.weatherId = weatherId;
    }

    public Recipe(String foodName, int foodTime, int categoryId, Integer weatherId) {
        this.foodName = foodName;
        this.foodTime = foodTime;
        this.categoryId = categoryId;
        this.weatherId = weatherId;
    }

    public Recipe(Long recipesId, String foodName, int foodTime, int categoryId) {
        this.recipesId = recipesId;
        this.foodName = foodName;
        this.foodTime = foodTime;
        this.categoryId = categoryId;
    }
}