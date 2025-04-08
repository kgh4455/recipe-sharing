package com.project.service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.project.mapper.RecipesMapper;
import com.project.model.Recipes;

import jakarta.annotation.PostConstruct;

@Service
public class WeatherService {

	@Value("${weather.api.key}")
    private String authKey;
	
	@Autowired
	private RecipesMapper recipesMapper;
	 

    public String getWeatherData(String baseDate, String baseTime, int nx, int ny, String authKey) {
    	String url = UriComponentsBuilder.fromUriString("https://apihub.kma.go.kr/api/typ02/openApi/VilageFcstInfoService_2.0/getUltraSrtNcst")
    	        .queryParam("pageNo", 1)
    	        .queryParam("numOfRows", 1000)
    	        .queryParam("dataType", "XML")
    	        .queryParam("base_date", baseDate)
    	        .queryParam("base_time", baseTime)
    	        .queryParam("nx", nx)
    	        .queryParam("ny", ny)
    	        .queryParam("authKey", authKey) 
    	        .toUriString(); 

        // RestTemplate 사용
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // 응답 로그 출력
        System.out.println("API 응답 데이터: " + response.getBody());

        return response.getBody();
    }
    
    
}