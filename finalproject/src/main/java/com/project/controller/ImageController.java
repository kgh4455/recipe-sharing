package com.project.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/uploads") // ✅ API 경로 유지
@CrossOrigin(origins = "http://localhost:5173") // ✅ 프론트엔드 CORS 설정
public class ImageController {

    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    // ✅ 서버 실행 시 업로드 폴더 자동 생성
    public ImageController() {
        File uploadFolder = new File(UPLOAD_DIR);
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs(); // ✅ 업로드 폴더 자동 생성
        }
    }

    // 🔹 1️⃣ 이미지 업로드 API
    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("파일이 없습니다.");
            }

            // ✅ 파일명 UUID로 변환 (한글 문제 해결)
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf(".")); // 확장자 추출
            String fileName = UUID.randomUUID() + extension; // 랜덤 UUID 파일명 생성

            Path filePath = Paths.get(UPLOAD_DIR + fileName);
            file.transferTo(filePath.toFile());

            System.out.println("✅ 저장된 이미지: " + filePath.toAbsolutePath());

            return ResponseEntity.ok(fileName); // ✅ 파일명 반환 (경로 X)
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패: " + e.getMessage());
        }
    }

    // 🔹 2️⃣ 이미지 조회 API
    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            // ✅ MIME 타입 설정
            String contentType = Files.probeContentType(filePath);
            MediaType mediaType = (contentType != null) ? MediaType.parseMediaType(contentType) : MediaType.APPLICATION_OCTET_STREAM;

            return ResponseEntity.ok()
                    .contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + filename + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 🔹 3️⃣ 이미지 삭제 API
    @DeleteMapping("/{filename}")
    public ResponseEntity<?> deleteFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(filename).normalize();
            Files.deleteIfExists(filePath);
            return ResponseEntity.ok().body("파일 삭제 완료");
        } catch (IOException e) {
            return ResponseEntity.status(500).body("파일 삭제 실패");
        }
    }
}