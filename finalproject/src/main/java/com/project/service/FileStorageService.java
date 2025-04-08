package com.project.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

@Service
public class FileStorageService {

    private final String uploadDir =System.getProperty("user.dir") + "/uploads/"; // ✅ **저장 경로 변경**

    // 🔹 파일 저장
    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            System.out.println("❌ 파일이 비어 있음: " + file);
            return null;
        }

        try {
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs(); // ✅ 폴더 자동 생성
            }

            // ✅ 파일명을 UUID로 변환하여 저장 (한글 문제 해결)
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename().replaceAll(" ", "_");
            Path filePath = Paths.get(uploadDir, fileName);
            Files.write(filePath, file.getBytes());

            System.out.println("✅ 파일 저장 성공: " + filePath.toString());
            return fileName; // **저장된 파일명을 반환**
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("❌ 파일 저장 실패!", e);
        }
    }

    // 🔹 저장된 파일 로드
    public Resource loadFileAsResource(String fileName) {
        try {
            Path filePath = Paths.get(uploadDir).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("파일을 찾을 수 없습니다: " + fileName);
            }
        } catch (Exception e) {
            throw new RuntimeException("파일 로딩 실패: " + fileName, e);
        }
    }

    // 🔹 파일 삭제
    public void deleteFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) return;

        // ✅ `http://localhost:8080/uploads/` 제거 후 파일명만 남기기
        String cleanedFileName = fileName.replace("http://localhost:8080/uploads/", "");
        String filePath = uploadDir + cleanedFileName; // **실제 파일 경로**

        File file = new File(filePath);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("✅ 파일 삭제 성공: " + filePath);
            } else {
                System.out.println("❌ 파일 삭제 실패: " + filePath);
            }
        } else {
            System.out.println("⚠️ 삭제할 파일이 존재하지 않음: " + filePath);
        }
    }
}