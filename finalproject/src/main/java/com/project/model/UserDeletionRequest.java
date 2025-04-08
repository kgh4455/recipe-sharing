package com.project.model;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDeletionRequest {
    private Long id;         // 요청 ID (자동 증가)
    private String email;    // 회원 이메일 (유저 식별자)
    private String reason;   // 탈퇴 요청 사유
    private LocalDateTime createdAt; // ✅ 요청 날짜를 LocalDateTime으로 변경
    private String profileImage; // ✅ 추가 (users 테이블에서 가져옴)
}