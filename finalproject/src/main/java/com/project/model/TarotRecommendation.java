package com.project.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TarotRecommendation {
    private Long id;
    private Long tarotCardId;
    private Long recipeId;
    private String createdAt;
}