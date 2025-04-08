package com.project.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.config.JwtUtil;
import com.project.model.Recipes;
import com.project.model.User;
import com.project.model.krhMainVO;
import com.project.service.UserService;
import com.project.service.krhMainService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpSession;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/main")
public class krhMainController {
	
	@Autowired
	private final krhMainService krhmainService;
	
	@Autowired
	private final JwtUtil jwtUtil;
	
	@Autowired
	private final UserService userService;
	
	public krhMainController(krhMainService krhmainService, UserService userService) {
		super();
		this.krhmainService = krhmainService;
		this.jwtUtil = new JwtUtil();
		this.userService = userService;
	}

	//인기 레시피 조회
	@GetMapping("/popular")
	public List<Recipes> popularRecipe() { //리턴타입을 json형태로 변환
		return krhmainService.popularRecipe();
	}
	
	//최신 레시피 조회
	@GetMapping("/recent")
	public List<Recipes> recentRecipe(){
		return krhmainService.recentRecipe();
	}

	//관심 목록에 있는 레시피들이 가장 많이 속한 카테고리 속 레시피 추천
	@GetMapping("/recommend")
	public List<Recipes> getRecommendedRecipes(@RequestHeader("Authorization") String token) {
		String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
	    Claims  claims;
        try {
            claims = jwtUtil.extractAllClaims(jwtToken); 
        } catch (Exception e) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        String email = claims.getSubject(); 

        if (email == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

	    // 이메일로 사용자 찾기
	    User user = userService.getUserByEmail(email);
	    if (user == null) {
	        throw new RuntimeException("해당 이메일로 등록된 사용자가 없습니다.");
	    }

	    // userId 추출
	    long userId = user.getId();
	    System.out.println("JWT Token: " + jwtToken);
	    System.out.println("Claims: " + claims);
	    System.out.println("Extracted Email: " + email);
	    System.out.println("찾은 사용자: " + user);
	    // 사용자 추천 레시피 가져오기
	    return krhmainService.getRecommendedRecipes(userId);
	}

}