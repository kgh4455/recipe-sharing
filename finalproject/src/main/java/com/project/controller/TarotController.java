package com.project.controller;

import com.project.service.TarotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tarot")
@RequiredArgsConstructor
public class TarotController {

    private final TarotService tarotService;

    // ✅ 4장의 랜덤 타로 카드 가져오기
    @GetMapping("/cards")
    public ResponseEntity<List<Map<String, Object>>> getRandomTarotCards() {
        return ResponseEntity.ok(tarotService.getRandomTarotCards(4));
    }

    // ✅ 타로 카드 선택 (하루 2번 제한)
    @PostMapping("/select")
    public ResponseEntity<?> selectTarotCard(@AuthenticationPrincipal UserDetails userDetails, 
                                             @RequestParam int tarotCardId) {
        if (userDetails == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        String email = userDetails.getUsername();

        try {
            // ✅ 선택한 타로 카드 + 랜덤 레시피 ID & 제목 반환
            Map<String, Object> response = tarotService.saveSelectedTarot(email, tarotCardId);
            return ResponseEntity.ok(response);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(400).body(e.getMessage());
        }
    }
}