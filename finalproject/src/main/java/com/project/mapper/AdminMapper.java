package com.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.project.config.StringToListTypeHandler;
import com.project.model.Ingredient;
import com.project.model.Inquiry;
import com.project.model.Recipe;
import com.project.model.User;
import com.project.model.UserDeletionRequest;
import com.project.model.UserRecipe;
import com.project.model.krhBoardVO;
import com.project.model.krhReportVO;

@Mapper
public interface AdminMapper {

    // 🔹 전체 회원 조회
    @Select("SELECT * FROM users WHERE role = 'USER'")
    List<User> getAllUsers();

    // 🔹 회원 삭제
    @Delete("DELETE FROM users WHERE email = #{email}")
    void deleteUser(@Param("email") String email);

 // 🔹 회원탈퇴 요청 목록 조회 (users 테이블에서 profileImage 가져오기)
    @Select("""
    	    SELECT d.id, d.email, d.reason, d.created_at, 
    	           COALESCE(u.profile_image, '') AS profile_image  -- ✅ null 값을 빈 문자열로 대체
    	    FROM user_deletion_requests d
    	    LEFT JOIN users u ON d.email = u.email
    	""")
    	@Results({
    	    @Result(property = "id", column = "id"),
    	    @Result(property = "email", column = "email"),
    	    @Result(property = "reason", column = "reason"),
    	    @Result(property = "createdAt", column = "created_at"),
    	    @Result(property = "profileImage", column = "profile_image") // ✅ 추가된 필드
    	})
    	List<UserDeletionRequest> getAllDeletionRequests();
    // 🔹 회원탈퇴 요청 승인 (회원 삭제 후 요청 삭제)
    @Delete("DELETE FROM users WHERE email = #{email}")
    void approveDeletionRequest(@Param("email") String email);
 // 🔹 회원탈퇴 요청 승인 (회원 삭제 후 요청 목록에서도 제거)
    @Delete("DELETE FROM user_deletion_requests WHERE email = #{email}")
    void deleteUserDeletionRequest(@Param("email") String email);
    // 🔹 1:1 문의 전체 목록 조회
    @Select("SELECT i.*, u.name as user_name FROM inquiries i JOIN users u ON i.user_email = u.email")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "title", column = "title"),
        @Result(property = "content", column = "content"),
        @Result(property = "userEmail", column = "user_email"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "reply", column = "reply"),
        @Result(property = "userName", column = "user_name") // ✅ 추가된 필드
    })
    List<Inquiry> getAllInquiries();
    // 🔹 1:1 문의 답변 등록
    @Update("""
    	    UPDATE inquiries 
    	    SET reply = #{reply} 
    	    WHERE id = #{inquiryId}
    	""")
    	void updateInquiryReply(@Param("inquiryId") Long inquiryId, @Param("reply") String reply);

    // 🔹 1:1 문의 답변 삭제
        @Select("""
            SELECT i.*, u.name AS userName 
            FROM inquiries i
            JOIN users u ON i.user_email = u.email
            WHERE i.id = #{id}
        """)
        Inquiry getInquiryDetail(@Param("id") Long id);
        // ✅ 1:1 문의 삭제 (문의 자체를 삭제)
        @Delete("DELETE FROM inquiries WHERE id = #{id}")
        void deleteInquiry(@Param("id") Long id);

        // ✅ 1:1 문의 답변만 삭제 (문의는 남기고 답변만 삭제)
        @Update("UPDATE inquiries SET reply = NULL WHERE id = #{id}")
        void deleteInquiryReply(@Param("id") Long id);

   



    /** ✅ 일반 레시피 (Recipes) 관리 **/

    /** ✅ 1. 모든 레시피 가져오기 */
    @Select("SELECT * FROM Recipes")
    List<Recipe> getAllRecipes();

    @Select("SELECT r.*, " +
            "GROUP_CONCAT(i.name ORDER BY i.ingredient_id SEPARATOR ', ') AS ingredients " +
            "FROM Recipes r " +
            "LEFT JOIN Recipe_Ingredients ri ON r.recipes_id = ri.recipes_id " +
            "LEFT JOIN Ingredients i ON ri.ingredient_id = i.ingredient_id " +
            "WHERE r.recipes_id = #{recipeId} " +
            "GROUP BY r.recipes_id")
    @Results({
        @Result(property = "recipesId", column = "recipes_id"),
        @Result(property = "foodName", column = "foodName"),
        @Result(property = "foodImg", column = "foodImg"),
        @Result(property = "foodTime", column = "foodTime"),
        @Result(property = "categoryId", column = "category_id"),
        @Result(property = "weatherId", column = "weatherId"),
        @Result(property = "step1", column = "step1"),
        @Result(property = "step2", column = "step2"),
        @Result(property = "step3", column = "step3"),
        @Result(property = "step4", column = "step4"),
        @Result(property = "step5", column = "step5"),
        @Result(property = "step6", column = "step6"),
        @Result(property = "ingredients", column = "ingredients", typeHandler = StringToListTypeHandler.class)
    })
    Recipe getRecipeById(@Param("recipeId") Long recipeId);

   
    /** ✅ 3. 특정 레시피의 재료 목록 조회 */
    @Select("SELECT i.ingredient_id, i.name FROM Ingredients i " +
            "JOIN Recipe_Ingredients ri ON ri.ingredient_id = i.ingredient_id " +
            "WHERE ri.recipes_id = #{recipeId}")
    List<Ingredient> getIngredientsByRecipeId(@Param("recipeId") Long recipeId);
    
    /** ✅ 4. 레시피 추가 */
    @Insert("INSERT INTO Recipes (foodName, foodImg, step1, step2, step3, step4, step5, step6, " +
            "stepImg1, stepImg2, stepImg3, stepImg4, stepImg5, stepImg6, foodTime, category_id, weatherId) " +
            "VALUES (#{foodName}, #{foodImg}, #{step1}, #{step2}, #{step3}, #{step4}, #{step5}, #{step6}, " +
            "#{stepImg1}, #{stepImg2}, #{stepImg3}, #{stepImg4}, #{stepImg5}, #{stepImg6}, #{foodTime}, #{categoryId}, #{weatherId})")
    @Options(useGeneratedKeys = true, keyProperty = "recipesId")
    void insertRecipe(Recipe recipe);

    /** ✅ 기존 레시피의 모든 재료 삭제 */
    @Delete("DELETE FROM Recipe_Ingredients WHERE recipes_id = #{recipeId}")
    void deleteIngredientsByRecipeId(@Param("recipeId") Long recipeId);

    /** ✅ 재료 추가 (Recipe_Ingredients 테이블) */
    @Insert("INSERT INTO Recipe_Ingredients (recipes_id, ingredient_id) VALUES (#{recipeId}, #{ingredientId})")
    void insertRecipeIngredient(@Param("recipeId") Long recipeId, @Param("ingredientId") Integer ingredientId);

    /** ✅ 특정 재료 ID 조회 */
    @Select("SELECT ingredient_id FROM Ingredients WHERE name = #{name}")
    Integer getIngredientIdByName(@Param("name") String name);

    /** ✅ 새 재료 추가 */
    @Insert("INSERT INTO Ingredients (name) VALUES (#{name})")
    @Options(useGeneratedKeys = true, keyProperty = "ingredientId", keyColumn = "ingredient_id") // 🔥 Integer 타입으로 변경
    void insertNewIngredient(Ingredient ingredient);

    /** ✅ 9. 레시피 업데이트 */
    @Update("UPDATE Recipes SET foodName = #{foodName}, foodImg = #{foodImg}, " +
            "foodTime = #{foodTime}, category_id = #{categoryId}, " +
            "step1 = #{step1}, step2 = #{step2}, step3 = #{step3}, step4 = #{step4}, " +
            "step5 = #{step5}, step6 = #{step6}, " +
            "stepImg1 = #{stepImg1}, stepImg2 = #{stepImg2}, stepImg3 = #{stepImg3}, " +
            "stepImg4 = #{stepImg4}, stepImg5 = #{stepImg5}, stepImg6 = #{stepImg6}, " +
            "weatherId = #{weatherId} " +
            "WHERE recipes_id = #{recipesId}")
    int updateRecipe(Recipe recipe);
    // ✅ 특정 레시피 단계별 이미지 경로 조회
    @Select("SELECT ${column} FROM recipes WHERE recipes_id = #{recipeId}")
    String getStepImagePath(@Param("recipeId") Long recipeId, @Param("column") String column);

    // ✅ 특정 단계 이미지 삭제
    @Update("UPDATE recipes SET ${column} = NULL WHERE recipes_id = #{recipeId}")
    void deleteStepImage(@Param("recipeId") Long recipeId, @Param("column") String column);


    /** ✅ 10. 레시피 삭제 */
    @Delete("DELETE FROM Recipes WHERE recipes_id = #{recipeId}")
    void deleteRecipe(@Param("recipeId") Long recipeId);
    /** ✅ 레시피 검색 (이름, 카테고리, 날씨 필터링 포함) */

        /** ✅ 검색 기능 포함된 레시피 조회 */
        @Select("<script>" +
                "SELECT * FROM Recipes " +
                "WHERE 1=1 " +
                "<if test='keyword != null and keyword != \"\"'> AND foodName LIKE CONCAT('%', #{keyword}, '%') </if>" +
                "<if test='categoryId != null'> AND category_Id = #{categoryId} </if>" +
                "<if test='weatherId != null'> AND weatherId = #{weatherId} </if>" +
                "ORDER BY recipes_Id DESC" +
                "</script>")
        List<Recipe> findRecipes(@Param("keyword") String keyword,
                                 @Param("categoryId") Integer categoryId,
                                 @Param("weatherId") Integer weatherId);

    /** ✅ 유저 레시피 (User_Recipes) 관리 **/
    
     // ✅ 전체 유저 레시피 조회 (작성자 정보 포함)
        @Select("""
            SELECT ur.*, u.name AS writerName, u.email AS writerEmail
            FROM user_recipes ur
            JOIN users u ON ur.user_id = u.id
        """)
        List<UserRecipe> getAllUserRecipes();

        // ✅ 특정 유저 레시피 조회 (user_id 기준, 작성자 정보 포함)
        @Select("""
        	    SELECT ur.*, u.name AS writerName, u.email AS writerEmail,
        	           GROUP_CONCAT(ing.name SEPARATOR ', ') AS ingredientsss
        	    FROM user_recipes ur
        	    JOIN users u ON ur.user_id = u.id
        	    LEFT JOIN recipe_ingredients ri ON ri.user_recipes_id = ur.user_recipes_id
        	    LEFT JOIN ingredients ing ON ri.ingredient_id = ing.ingredient_id
        	    WHERE ur.user_recipes_id = #{id}
        	    GROUP BY ur.user_recipes_id
        	""")
        	@Results({
        	    @Result(property = "ingredientsss", column = "ingredientsss"),
        	    @Result(property = "writerName", column = "writerName"),
        	    @Result(property = "writerEmail", column = "writerEmail"),
        	    // 필요한 필드 매핑 더 추가 가능
        	})
        	UserRecipe getUserRecipeById(@Param("id") Long id);


        // ✅ 승인 대기 중인 유저 레시피 조회 (STATUS = 'OFF'만 조회)
        @Select("""
            SELECT ur.*, u.name AS writerName, u.email AS writerEmail
            FROM user_recipes ur
            JOIN users u ON ur.user_id = u.id
            WHERE ur.status = 'OFF'
        """)
        List<UserRecipe> getPendingUserRecipes();
        //승인된 레시피 조회 
        @Select("SELECT * FROM user_recipes WHERE status = #{status}")
        List<UserRecipe> findByStatus(@Param("status") String status);
        // ✅ 유저 레시피 승인 (STATUS = 'ON'으로 변경)
        @Update("UPDATE user_recipes SET status = 'ON' WHERE user_recipes_id = #{id}")
        int approveUserRecipe(@Param("id") Integer id);

        // ✅ 유저 레시피 거절 (STATUS를 'OFF'로 유지)
        @Update("UPDATE user_recipes SET status = 'OFF' WHERE user_recipes_id = #{id}")
        int rejectUserRecipe(@Param("id") Integer id);

        // ✅ 유저 레시피 삭제
        @Delete("DELETE FROM user_recipes WHERE user_recipes_id = #{id}")
        int deleteUserRecipe(@Param("id") Integer id);
    // 🔹 특정 유저에게 알림 전송
    @Insert("INSERT INTO notifications (receiver_email, message, is_read, created_at) VALUES (#{email}, #{message}, FALSE, NOW())")
    void sendUserNotification(@Param("email") String email, @Param("message") String message);
  
    // ✅ 전체 게시글 조회
    @Select("SELECT * FROM board ORDER BY createdAt DESC")
    List<krhBoardVO> getAllBoards();
    
    //특정게시물 조회
    @Select("SELECT * FROM board WHERE boardId = #{boardId}")
    krhBoardVO getBoardById(int boardId);
    //게시물 삭제 
    @Delete("DELETE FROM board WHERE boardId = #{boardId}")
    void deleteBoardById(int boardId);

    // 전체 신고 조회
    @Select("SELECT * FROM reports ORDER BY reportedAt DESC")
    List<krhReportVO> getAllReports();

    // 상세 신고 조회
    @Select("SELECT * FROM reports WHERE reportId = #{reportId}")
    krhReportVO getReportById(int reportId);

    // 신고 삭제
    @Delete("DELETE FROM reports WHERE reportId = #{reportId}")
    void deleteReport(int reportId);
    @Select("SELECT COUNT(*) FROM categories WHERE category_id = #{categoryId}")
    int checkCategoryExists(int categoryId);


    @Select("SELECT COUNT(*) FROM weather_data WHERE weatherId = #{weatherId}")
    int checkWeatherExists(int weatherId);
    
    
}