package com.project.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.config.JwtUtil;
import com.project.model.Ingredients;
import com.project.model.User;
import com.project.model.UserRecipe;
import com.project.service.UserService;
import com.project.service.ljmUserRecipeService;

import io.jsonwebtoken.Claims;


@CrossOrigin(origins = "http://localhost:5173")
@RestController
public class ljmUserRecipeController {
	
	@Autowired
    private ljmUserRecipeService ljmuserRecipeService;
	@Autowired
	private UserService userService;
	@Autowired
	private JwtUtil jwtUtil;

    // 모든 사용자 레시피 가져오기
    @GetMapping("/api/userrecipes")
    public List<UserRecipe> getUserRecipes() {
        List<UserRecipe> userRecipes = ljmuserRecipeService.getAllUserRecipes();
        for(UserRecipe userRecipe : userRecipes){
        	List<Ingredients> ingredients = ljmuserRecipeService.getIngredientsByUserRecipeId(userRecipe.getUserRecipesId());
        	userRecipe.setIngredientss(ingredients);
        }
        return userRecipes;
    }
	
    @GetMapping("/api/userrecipes/{id}")
    public UserRecipe getUserRecipe(@PathVariable long id) {
    	UserRecipe userRecipes = ljmuserRecipeService.userRecipeById(id);
    	List<Ingredients> ingredients = ljmuserRecipeService.getIngredientsByUserRecipeId(id);
    	userRecipes.setIngredientss(ingredients);
    	return userRecipes;
    }
    
    @PutMapping("/api/userrecipes/{id}/increase-view")
    @Transactional
    public UserRecipe userIncreaseViewCount(@PathVariable Long id) {
    	UserRecipe userRecipe = ljmuserRecipeService.findUserById(id);
    	if(userRecipe !=null) {
    		userRecipe.setView(userRecipe.getView() +1);
    		ljmuserRecipeService.update(userRecipe);
    		return userRecipe;
    	}else {
    	throw new RuntimeException("레시피를 찾을 수 없습니다.");
    	}
    }
    
    @PostMapping("/api/userrecipes/{id}/favorites")
    public UserRecipe addUserFavorite(@PathVariable Long id, @RequestHeader("Authorization") String token) {
    	String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
    	Claims claims;
    	try {
    		claims = jwtUtil.extractAllClaims(jwtToken);
    	}catch(Exception e) {
    		throw new RuntimeException("유효하지 않은 토큰입니다.");
    	}
    	
    	String email = claims.getSubject();
    	
    	if(email == null) {
    		throw new RuntimeException("로그인이 필요합니다.");
    	}
    	
    	User user = userService.getUserByEmail(email);
    	UserRecipe userRecipe = ljmuserRecipeService.findUserById(id);
    	if(userRecipe != null) {
    		ljmuserRecipeService.addUserFavorite(userRecipe, email);
    		return userRecipe;
    	}else {
    		throw new RuntimeException("추가 실패");
    	}
    }
    
    @DeleteMapping("/api/userrecipes/{id}/favorites")
    public UserRecipe deleteUserFavorite(@PathVariable Long id, @RequestHeader("Authorization") String token) {
    	String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
    	Claims claims;
    	try {
    		claims = jwtUtil.extractAllClaims(jwtToken);
    	}catch(Exception e) {
    		throw new RuntimeException("유효하지 않은 토큰입니다.");
    	}
    	
    	String email = claims.getSubject();
    	if(email == null) {
    		throw new RuntimeException("로그인이 필요합니다.");
    	}
    	
    	User user = userService.getUserByEmail(email);
    	UserRecipe userRecipe = ljmuserRecipeService.findUserById(id);
    	
    	if(userRecipe != null) {
    		ljmuserRecipeService.deleteUserFavorite(userRecipe, email);
    		return userRecipe;
    	}else {
    		throw new RuntimeException("삭제 실패");
    	}
    }
    	
    	@GetMapping("/api/userrecipes/search")
    	public List<UserRecipe> searchUserRecipes(@RequestParam(value = "query", required = false) String query,
    											 @RequestParam(value = "category", required = false) String category){
    		if("음식명".equals(category)) {
    			return ljmuserRecipeService.findByUserFoodName(query);
    		}else if("재료".equals(category)) {
    			return ljmuserRecipeService.findByUserIngredients(query);
    		}
    		return ljmuserRecipeService.getAllUserRecipes();
    	}
    	
    	
    	@GetMapping("/api/userrecipes/favorites")
    	public ResponseEntity<Map<String,Object>> getUserFavorite(@RequestHeader("Authorization") String token){
    		String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
    		Claims claims;
    		try {
    			claims = jwtUtil.extractAllClaims(jwtToken);
    		}catch(Exception e) {
    			throw new RuntimeException("유효하지 않은 토큰입니다.");
    		}
    		String email = claims.getSubject();
    		
    		if(email == null) {
    			throw new RuntimeException("로그인이 필요합니다.");
    		}
    		User user = userService.getUserByEmail(email);
    		if(user == null) {
    			throw new RuntimeException("사용자를 찾을 수 없습니다.");
    		}
    		
    		List<UserRecipe> favoriteRecipes = ljmuserRecipeService.getUserFavoriteByUserId(user.getId());
    		
    		Map<String, Object> response = new HashMap<>();
    		response.put("favorites", favoriteRecipes);
    		return ResponseEntity.ok(response); 
    	}
}

