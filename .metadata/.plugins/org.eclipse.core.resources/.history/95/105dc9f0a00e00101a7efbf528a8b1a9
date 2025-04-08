package com.project.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String email;
    private String password;
    private String name;
    private String phoneNumber;
    private String profileImage;
    private String role = "USER";
    private Boolean isVerified;  // ✅ Boolean으로 선언해야 함
    private LocalDateTime createdAt;
    
    // ✅ 생성자 추가
    public User(String email, String password, String name, String phoneNumber, String profileImage, Boolean isVerified) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.profileImage = profileImage;
        this.role = "USER";
        this.isVerified = isVerified;
    }
}
