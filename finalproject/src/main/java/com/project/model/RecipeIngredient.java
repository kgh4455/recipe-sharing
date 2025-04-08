package com.project.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredient {
    private Long id;
    private Long recipesId;      // ✅ 레시피 ID
    private Long ingredientId;   // ✅ 재료 ID
}