package com.project.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordEncoderUtil {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String encodedPassword = encoder.encode("asdasdasd");  // 원하는 비밀번호 입력
        System.out.println("Encoded Password: " + encodedPassword);
    }
}