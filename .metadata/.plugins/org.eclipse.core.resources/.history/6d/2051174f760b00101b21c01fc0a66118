package com.project.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.project.mapper.TarotMapper;
import com.project.model.Recipe;
import com.project.model.TarotCard;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TarotService {

    private final TarotMapper tarotMapper;
    private final RecipeService recipeService;

    /**
     * ✅ 랜덤 타로 카드 4장 가져오기
     */
    public List<Map<String, Object>> getRandomTarotCards(int count) {
        return tarotMapper.getRandomTarotCards(count);
    }

    /**
     * ✅ 선택한 타로 카드 저장 및 추천 레시피 반환
     * - 하루 2번 제한은 주석 처리 (테스트 중)
     */
    public Map<String, Object> saveSelectedTarot(String email, int tarotCardId) {
        // 하루 2번 제한 해제 (테스트용)
        // int selectionCount = tarotMapper.countTodaySelections(email);
        // if (selectionCount >= 2) {
        //     throw new IllegalStateException("오늘은 최대 두 번까지 타로 카드를 선택할 수 있습니다.");
        // }

        // ✅ 타로 카드 선택 저장
        tarotMapper.insertTarotSelection(email, tarotCardId);

        // ✅ 타로 카드 상세 정보 가져오기
        TarotCard selectedCard = tarotMapper.getTarotCardById(tarotCardId);

        // ✅ 랜덤 추천 레시피 가져오기 (id, 이름, 이미지 포함)
        Recipe recipe = recipeService.getRandomRecipe();
        Map<String, Object> recipeInfo = new HashMap<>();
        recipeInfo.put("id", recipe.getRecipesId());
        recipeInfo.put("name", recipe.getFoodName());
        recipeInfo.put("imageUrl", recipe.getFoodImg()); // ✅ 이미지 포함

        // ✅ 최종 응답 구성
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Tarot card selection saved.");
        response.put("selectedCard", selectedCard);
        response.put("randomRecipe", recipeInfo);

        return response;
    }
}