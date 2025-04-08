package com.project.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.project.model.Ingredients;
import com.project.model.Recipes;
import com.project.service.RecipesService;
import com.project.service.WeatherService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
public class WeatherController {
	private final WeatherService weatherService;
	@Autowired
	private RecipesService recipesService;
	
	@Value("${weather.api.key}")  
    private String authKey;
	
	 public WeatherController(WeatherService weatherService) {
	        this.weatherService = weatherService;
	    }

    @GetMapping("/api/weather")
    public ResponseEntity<String> getWeather(@RequestParam String baseDate,
                                             @RequestParam String baseTime,
                                             @RequestParam int nx,
                                             @RequestParam int ny) {
    	String response = weatherService.getWeatherData(baseDate, baseTime, nx, ny, authKey);
    	return ResponseEntity.ok(response);
    }
    
    @GetMapping("/api/weather/recipe")
    public List<Recipes> getWeatherRecipes(@RequestParam String precipitation){
    	List<Recipes> recipes = recipesService.getWeatherRecipes(precipitation);
    	System.out.println("요청받은 강수 형태: " + precipitation);
    	for(Recipes recipe : recipes) {
    		List<Ingredients> ingredients = recipesService.getIngredientsByRecipeId(recipe.getRecipesId());
    		recipe.setIngredients(ingredients);
    	}
    	return recipes;
    }
}
