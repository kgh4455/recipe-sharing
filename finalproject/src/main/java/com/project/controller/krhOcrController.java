package com.project.controller;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.project.service.krhOcrService;

import lombok.AllArgsConstructor;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/ocr")
public class krhOcrController {

	@Autowired
    private krhOcrService ocrService;
	
    private static final String UPLOAD_DIR = System.getProperty("user.dir") + "/uploads/";

    @PostMapping("/extract-text")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();

        if (file.isEmpty()) {
            response.put("message", "파일을 선택해주세요.");
            return ResponseEntity.badRequest().body(response);
        }

        try {
            // 업로드 폴더 생성 (존재하지 않으면)
            File uploadFolder = new File(UPLOAD_DIR);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdirs();
            }

            // 파일 저장
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            File destFile = new File(UPLOAD_DIR + fileName);
            file.transferTo(destFile);

            // OCR 처리
            JSONObject result = ocrService.processOCR(UPLOAD_DIR, fileName);

            // OCR 데이터 추출
            Map<String, String> extractedData = ocrService.extractData(result);

            // 결과를 JSON 형태로 반환
            response.put("imagePath", "/uploads/" + fileName);
            response.put("ocrData", extractedData);
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            response.put("message", "파일 업로드 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}