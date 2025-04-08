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
import com.project.model.Recipes;
import com.project.model.User;
import com.project.service.RecipesService;
import com.project.service.UserService;

import io.jsonwebtoken.Claims;

@CrossOrigin(origins= "http://localhost:5173")
@RestController
public class RecipesController {
	@Autowired
	private RecipesService recipesService;
	@Autowired
	private UserService userService;
	@Autowired
	private JwtUtil jwtUtil;
	

	
	@GetMapping("/api/recipes")
	public List<Recipes> getRecipes(){
		List<Recipes> recipes = recipesService.getAllRecipes(); 
	    for (Recipes recipe : recipes) {
	        List<Ingredients> ingredients = recipesService.getIngredientsByRecipeId(recipe.getRecipesId()); // 각 레시피의 재료를 가져옴
	        recipe.setIngredients(ingredients);
	    }
	    return recipes; 
	}
	
	@GetMapping("/api/recipes/popular")
	public List<Recipes> getPopularRecipes(){
		List<Recipes> recipes = recipesService.getPopularRecipes();
		for (Recipes recipe : recipes) {
	        List<Ingredients> ingredients = recipesService.getIngredientsByRecipeId(recipe.getRecipesId()); // 각 레시피의 재료를 가져옴
	        recipe.setIngredients(ingredients);
	    }
		return recipes;
	}
	
	@GetMapping("/api/recipes/{id}")
	public Recipes getRecipe(@PathVariable long id) {
		Recipes recipe = recipesService.recipeById(id);
		List<Ingredients> ingredients = recipesService.getIngredientsByRecipeId(id);
		recipe.setIngredients(ingredients); 
		return recipe;
	}
	@PutMapping("/api/recipes/{id}/increase-view")
	@Transactional
	public Recipes increaseViewCount(@PathVariable Long id) {
	    Recipes recipe = recipesService.findById(id);
	    if (recipe != null) {
	        recipe.setView(recipe.getView() + 1); 
	        recipesService.update(recipe); 
	        return recipe; 
	    }else {
	    	throw new RuntimeException("레시피를 찾을 수 없습니다.");
	    }
	}
	
	@PostMapping("/api/recipes/{id}/favorite")
	public Recipes addFavorite(@PathVariable Long id, @RequestHeader("Authorization") String token) {
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

        User user = userService.getUserByEmail(email);  
        Recipes recipe = recipesService.findById(id);
        if (recipe != null) {
            recipesService.addFavorite(recipe, email); 
            return recipe;
        } else {
            throw new RuntimeException("추가 실패");
        }
    }

    @DeleteMapping("/api/recipes/{id}/favorite")
    public Recipes deleteFavorite(@PathVariable Long id, @RequestHeader("Authorization") String token) {
    	String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        Claims claims;
        try {
            claims = jwtUtil.extractAllClaims(jwtToken); // JWT에서 클레임 추출하는 메소드
        } catch (Exception e) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }

        String email = claims.getSubject();

        if (email == null) {
            throw new RuntimeException("로그인이 필요합니다.");
        }

        User user = userService.getUserByEmail(email);
        Recipes recipe = recipesService.findById(id);

        if (recipe != null) {
            recipesService.deleteFavorite(recipe, email);
            return recipe;
        } else {
            throw new RuntimeException("삭제 실패");
        }
    }

	
	@GetMapping("/api/recipes/search")
	public List<Recipes> searchRecipes(@RequestParam(value = "query",required = false) String query,
									   @RequestParam(value = "category", required = false) String category){
		if("음식명".equals(category)) {
			return recipesService.findByFoodName(query);
		}else if("재료".equals(category)) {
			return recipesService.findByIngredient(query);
		}
		return recipesService.getAllRecipes();
	}
	
	 @GetMapping("/api/recipes/favorites")
	    public ResponseEntity<Map<String, Object>> getFavorites(@RequestHeader("Authorization") String token) {
	        // JWT 토큰에서 사용자 정보를 추출
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
	        User user = userService.getUserByEmail(email);
	        if (user == null) {
	            throw new RuntimeException("사용자를 찾을 수 없습니다.");
	        }
	        // 사용자의 즐겨찾기 목록을 가져옴
	        List<Recipes> favoriteRecipes = recipesService.getFavoritesByUserId(user.getId());

	        Map<String, Object> response = new HashMap<>();
	        response.put("favorites", favoriteRecipes);

	        return ResponseEntity.ok(response);
	    }

}

	
	