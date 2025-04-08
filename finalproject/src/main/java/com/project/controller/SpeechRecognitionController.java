package com.project.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.HttpEntity;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class SpeechRecognitionController {

	 @Value("${naver.client.secret}")
	    private String secretKey;  // Secret Key를 가져옵니다.

	    private static final String API_URL = "https://clovaspeech-gw.ncloud.com/recog/v1/stt?lang=Kor";  // 실제 API URL

	    @PostMapping("/api/recognize-speech")
	    public ResponseEntity<String> recognizeSpeech(@RequestParam("file") MultipartFile file) throws IOException {
	        HttpHeaders headers = new HttpHeaders();
	        headers.set("X-CLOVASPEECH-API-KEY", secretKey);  // Secret Key를 헤더에 추가
	        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);  // Content-Type을 multipart로 설정
	        
	        byte[] fileBytes = file.getBytes();
	        
	        HttpEntity<byte[]> entity = new HttpEntity<>(fileBytes, headers);

	        try {
	            RestTemplate restTemplate = new RestTemplate();
	            ResponseEntity<String> response = restTemplate.exchange(API_URL, HttpMethod.POST, entity, String.class);
	            return ResponseEntity.ok(response.getBody());
	        } catch (Exception e) {
	            // 에러 처리 및 로그 출력
	            e.printStackTrace();
	            return ResponseEntity.status(500).body("음성 인식 처리 중 오류가 발생했습니다.");
	        }
	    }

}