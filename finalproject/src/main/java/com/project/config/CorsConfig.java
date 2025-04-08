package com.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/**")
                        .allowedOrigins("http://localhost:5173") // ✅ 프론트엔드 주소
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS") // ✅ PUT 요청 허용
                        .allowedHeaders("*")
                        .allowCredentials(true); // ✅ 쿠키 포함 허용
            }
        };
    }
}


