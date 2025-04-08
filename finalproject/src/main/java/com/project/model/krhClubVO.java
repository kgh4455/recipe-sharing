package com.project.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class krhClubVO {
    private int clubId; //자동생성
    private String clubName;
    private String clubFeatures;
    private String location;
    private LocalDate date;
    private String recruiterEmail;
    private String clubImage;
    private String status;
    private List<String> hashtags;
    private LocalDateTime createdAt;  // 작성 시간
    private String clubUrl;
    
}
