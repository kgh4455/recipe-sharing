package com.project.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Inquiry {
    private Long id;
    private String userEmail;
    private String title;
    private String content;
    private String reply;
    private String createdAt;
    private String userName; // ✅ 추가된 필드 (작성자 이름)
}