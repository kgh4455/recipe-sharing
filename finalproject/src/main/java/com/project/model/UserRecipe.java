package com.project.model;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRecipe {
    private Long userRecipesId;
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
    private Long userId;
    private int foodTime;
    private int categoryId;
    private String status; // 승인 여부 (on/off)
    private List<String> ingredients; //김륜하
    private List<Ingredients> ingredientss; //이정면
    private String name;
    private String writerName;
    private String writerEmail;
    private String ingredientsss; //김동하
    
    public String getIngredientsss() {
        return ingredientsss;
    }

    public void setIngredientsss(String ingredientsss) {
        this.ingredientsss = ingredientsss;
    }
}
