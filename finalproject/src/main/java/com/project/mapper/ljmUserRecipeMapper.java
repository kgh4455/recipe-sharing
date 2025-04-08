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
import com.project.model.UserRecipe;

@Mapper
@MapperScan("com.project.mapper")
public interface ljmUserRecipeMapper {

	

	@Select("SELECT * FROM User_Recipes WHERE status = 'on'")
    List<UserRecipe> getAllUserRecipes();

    // 특정 레시피에 대한 재료 가져오기
    @Select("SELECT i.* FROM Ingredients i " +
            "JOIN Recipe_Ingredients ri ON i.ingredient_id = ri.ingredient_id " +
            "WHERE ri.user_recipes_id = #{userRecipesId}")
    List<Ingredients> getIngredientsByUserRecipeId(Long userRecipesId);
    
    @Select("SELECT i.* FROM Ingredients i JOIN Recipe_Ingredients ri ON i.ingredient_id = ri.ingredient_id WHERE ri.user_recipes_id = #{userRecipesId}")
    List<Ingredients> findByIngredientsByUserRecipeId(@Param("userRecipesId") Long userRecipesid);
    
    @Select("SELECT ur.*, c.category_name AS categoryName, u.name AS Name " +
            "FROM User_Recipes ur " +
            "JOIN Categories c ON ur.category_id = c.category_id " +
            "JOIN Users u ON ur.user_id = u.id " +  // Users 테이블과 조인
            "WHERE ur.user_recipes_id = #{id}")
    UserRecipe userFindById(@Param("id") Long id);
//    @Select("SELECT ur.*, c.category_name AS categoryName FROM User_Recipes ur JOIN Categories c ON ur.category_id = c.category_id WHERE ur.user_recipes_id = #{id}")
//    UserRecipe findUserById(@Param("id") Long id);
    
    @Update("UPDATE User_Recipes SET view = view + 1 WHERE user_recipes_id = #{userRecipesId}")
    void incrementUserViewCount(@Param("userRecipesId") Long userrecipesId);
    
    @Insert("INSERT INTO favorite (userRecipesId,user_id) VALUES (#{userRecipesId},#{userId})")
    void addUserFavoriteList(@Param("userRecipesId") Long userRecipesId, @Param("userId") Long userId);
    
    @Delete("DELETE FROM favorite WHERE userRecipesId = #{userRecipesId} AND user_id = #{userId}")
    void deleteUserFavoriteList(@Param("userId") Long userId, @Param("userRecipesId") Long userRecipesId);
    
    @Select("SELECT * FROM User_Recipes WHERE foodName LIKE CONCAT('%', #{query}, '%')")
    List<UserRecipe> findByUserFoodName(@Param("query") String query);
    
    @Select("SELECT DISTINCT ur.* FROM User_Recipes ur JOIN Recipe_Ingredients ri ON ur.user_recipes_id = ri.user_recipes_id JOIN Ingredients i ON ri.ingredient_id = i.ingredient_id WHERE i.name LIKE CONCAT('%', #{query}, '%')")
    List<UserRecipe> findByUserIngredients(@Param("query") String query);

    @Select("SELECT ur.* FROM user_Recipes ur JOIN favorite f ON ur.user_recipes_id = f.userRecipesId WHERE f.user_id = #{userId}")
    List<UserRecipe> getUserFavoriteByUserId(@Param("userId") Long userId);
    
    @Select("SELECT * FROM User_Recipes WHERE user_recipes_id = #{userRecipesId}")
    UserRecipe findByRecipeId(long userRecipeId);

}