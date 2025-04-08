package com.project.mapper;

import com.project.model.Recipe;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RecipeMapper {

    // 🔹 레시피 전체 조회
    @Select("SELECT * FROM recipes")
    List<Recipe> getAllRecipes();

    // 🔹 레시피 상세 조회
    @Select("SELECT * FROM recipes WHERE id = #{id}")
    Recipe getRecipeById(Long id);

    // 🔹 레시피 등록
    @Insert("INSERT INTO recipes(name, description, image_url, recipe_url, category) VALUES(#{name}, #{description}, #{imageUrl}, #{recipeUrl}, #{category})")
    void insertRecipe(Recipe recipe);

    // 🔹 레시피 수정
    @Update("UPDATE recipes SET name=#{name}, description=#{description}, category=#{category}, image_url=#{imageUrl}, recipe_url=#{recipeUrl} WHERE id=#{id}")
    void updateRecipe(Recipe recipe);

    // 🔹 레시피 삭제
    @Delete("DELETE FROM recipes WHERE id=#{id}")
    void deleteRecipe(Long id);
}