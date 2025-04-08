package com.project.mapper;

import java.util.List; 

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.mybatis.spring.annotation.MapperScan;

import com.project.model.Ingredients;
import com.project.model.Recipes;

@Mapper
@MapperScan("com.project.mapper")
public interface RecipesMapper {

    @Select("SELECT r.*, c.category_name AS categoryName FROM Recipes r LEFT JOIN Categories c ON r.category_id = c.category_id ORDER BY RAND()")
    List<Recipes> getAllRecipes();

    @Select("SELECT r.*, c.category_name AS categoryName FROM Recipes r LEFT JOIN Categories c ON r.category_id = c.category_id ORDER BY r.view DESC")
    List<Recipes> getPopularRecipes();

    @Select("SELECT r.*, c.category_name AS categoryName FROM Recipes r JOIN Categories c ON r.category_id = c.category_id WHERE r.recipes_id = #{id}")
    Recipes findById(@Param("id") Long id);

    @Select("SELECT DISTINCT r.* FROM Recipes r JOIN Recipe_Ingredients ri ON r.recipes_id = ri.recipes_id JOIN Ingredients i ON ri.ingredient_id = i.ingredient_id WHERE i.name LIKE CONCAT('%', #{query}, '%')")
    List<Recipes> findByIngredient(@Param("query") String query);

    @Select("SELECT * FROM Recipes WHERE foodName LIKE CONCAT('%', #{query}, '%')")
    List<Recipes> findByFoodName(@Param("query") String query);

    @Select("SELECT i.* FROM Ingredients i JOIN Recipe_Ingredients ri ON i.ingredient_id = ri.ingredient_id WHERE ri.recipes_id = #{recipesId}")
    List<Ingredients> findIngredientsByRecipeId(@Param("recipesId") Long recipesId);

    @Update("UPDATE Recipes SET view = view + 1 WHERE recipes_id = #{recipesId}")
    void incrementViewCount(@Param("recipesId") Long recipesId);

    @Insert("INSERT INTO favorite (recipe_id,user_id) VALUES (#{recipesId},#{userId})")
    void addFavoriteList(@Param("recipesId") Long recipesId, @Param("userId") Long userId);

    @Delete("DELETE FROM favorite WHERE user_id = #{userId} AND recipe_id = #{recipeId}")
    void deleteFavoriteList(@Param("userId") long userId, @Param("recipeId") long recipeId);


    @Select("SELECT r.* FROM Recipes r JOIN favorite f ON r.recipes_id = f.recipe_id WHERE f.user_id = #{userId}")
    List<Recipes> getFavoritesByUserId(@Param("userId") Long userId);
    
    @Select("SELECT r.*, w.weatherType FROM Recipes r LEFT JOIN weather_data w ON r.weatherId = w.weatherId WHERE w.weatherType = #{precipitation} ORDER BY RAND() LIMIT 4")
    List<Recipes> getWeatherRecipes(@Param("precipitation") String precipitation);


    @Select("SELECT * FROM Recipes WHERE recipes_id = #{recipeId}")
    Recipes findByRecipeId(long recipeId);
   

}