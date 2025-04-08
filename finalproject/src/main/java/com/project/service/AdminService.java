package com.project.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.mapper.AdminMapper;
import com.project.model.Ingredient;
import com.project.model.Inquiry;
import com.project.model.Recipe;
import com.project.model.User;
import com.project.model.UserDeletionRequest;
import com.project.model.UserRecipe;
import com.project.model.krhBoardVO;
import com.project.model.krhReportVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final AdminMapper adminMapper;
    private final JdbcTemplate jdbcTemplate;
    private final FileStorageService fileStorageService; // ✅ 파일 저장을 위한 서비스 (필요 시 구현)
    private static final String IMAGE_UPLOAD_DIR = "/path/to/upload/directory/";
    
    
    // ✅ 로그인할 때마다 login_history 테이블에 기록 추가
    public void saveLoginHistory(Long userId) {
        String sql = "INSERT INTO login_history (user_id) VALUES (?)";
        jdbcTemplate.update(sql, userId);
    }

    // ✅ 최근 30일 동안 가장 많이 로그인한 유저 조회
    public List<Map<String, Object>> getMostActiveUsers() {
        String sql = "SELECT u.email, COUNT(lh.id) AS login_count " +
                     "FROM users u " +
                     "JOIN login_history lh ON u.id = lh.user_id " +
                     "WHERE lh.login_time >= NOW() - INTERVAL 30 DAY " +
                     "GROUP BY u.id " +
                     "ORDER BY login_count DESC " +
                     "LIMIT 5";
        return jdbcTemplate.queryForList(sql);
    }
    // 🔹 전체 회원 조회
    public List<User> getAllUsers() {
        return adminMapper.getAllUsers();
    }

    // 🔹 회원 삭제
    public void deleteUser(String email) {
        adminMapper.deleteUser(email);
    }

    // 🔹 회원탈퇴 요청 목록 조회
    public List<UserDeletionRequest> getAllDeletionRequests() {
        List<UserDeletionRequest> requests = adminMapper.getAllDeletionRequests();

        // ✅ 날짜 포맷을 문자열로 변환하여 응답
        return requests.stream().map(request -> {
            request.setCreatedAt(request.getCreatedAt()); // 그대로 유지
            return request;
        }).collect(Collectors.toList());
    }

    // 🔹 회원탈퇴 요청 승인
    public void approveDeletionRequest(String email) {
        adminMapper.approveDeletionRequest(email);
        adminMapper.deleteUserDeletionRequest(email);
    }

    // 🔹 1:1 문의 전체 목록 조회
    public List<Inquiry> getAllInquiries() {
        return adminMapper.getAllInquiries();
    }

    // 🔹 1:1 문의 답변 등록
    public void updateInquiryReply(Long id, String reply) {
        adminMapper.updateInquiryReply(id, reply);
    }
    // ✅ 1:1 문의 삭제 (문의 자체 삭제)
    public void deleteInquiry(Long id) {
        adminMapper.deleteInquiry(id);
    }

    // ✅ 1:1 문의 답변 삭제 (문의는 남기고 답변만 삭제)
    public void deleteInquiryReply(Long id) {
        adminMapper.deleteInquiryReply(id);
    }
    // ✅ 1:1 문의 상세보기 서비스
    public Inquiry getInquiryDetail(Long id) {
        return adminMapper.getInquiryDetail(id);
    }
    // 🔹 특정 유저에게 알림 전송
    public void sendUserNotification(String receiverEmail, String message) {
        adminMapper.sendUserNotification(receiverEmail, message);
    }

    /** ✅ 일반 레시피 (Recipes) 관리 **/
    /** ✅ 1. 모든 레시피 가져오기 */
    public List<Recipe> getAllRecipes() {
        return adminMapper.getAllRecipes();
    }

    /** ✅ 2. 특정 레시피 조회 */
    public Recipe getRecipeById(Long recipeId) {
        Recipe recipe = adminMapper.getRecipeById(recipeId);
        if (recipe == null) {
            throw new RuntimeException("❌ 해당 레시피가 존재하지 않습니다.");
        }

        // ✅ 해당 레시피의 재료도 함께 조회
        List<Ingredient> ingredients = adminMapper.getIngredientsByRecipeId(recipeId);
        recipe.setIngredients(ingredients);
        System.out.println("✅ 가져온 재료: " + recipe.getIngredients());
        return recipe;
    }

    /** ✅ 3. 특정 레시피의 재료 목록 조회 */
    public List<Ingredient> getIngredientsByRecipeId(Long recipeId) {
        return adminMapper.getIngredientsByRecipeId(recipeId);
    }

    /** ✅ 4. 레시피 추가 */
    @Transactional
    public void addRecipe(Recipe recipe, MultipartFile foodImg, 
                          String step1, String step2, String step3, String step4, String step5, String step6,
                          MultipartFile stepImg1, MultipartFile stepImg2, MultipartFile stepImg3, 
                          MultipartFile stepImg4, MultipartFile stepImg5, MultipartFile stepImg6,
                          int categoryId, int foodTime, Integer weatherId, List<String> ingredients) {
        try {
            // ✅ 1. 대표 이미지 저장
            recipe.setFoodImg(saveImageIfExists(foodImg));

            // ✅ 2. 기본 정보 저장
            recipe.setCategoryId(categoryId);
            recipe.setFoodTime(foodTime);
            recipe.setWeatherId(weatherId);

            // ✅ 3. 단계별 설명 및 이미지 저장
            setRecipeSteps(recipe, step1, step2, step3, step4, step5, step6);
            setRecipeStepImages(recipe, stepImg1, stepImg2, stepImg3, stepImg4, stepImg5, stepImg6, null);

            // ✅ 4. 레시피 저장
            adminMapper.insertRecipe(recipe);
            System.out.println("🟢 새로운 레시피 추가 완료! 레시피 ID: " + recipe.getRecipesId());

            // ✅ 5. 재료 추가 (이 부분이 제대로 실행되지 않는 문제 해결)
            // ✅ 재료 추가
            if (ingredients != null && !ingredients.isEmpty()) {
            	for (String ingredientName : ingredients) {
            	    Integer ingredientId = adminMapper.getIngredientIdByName(ingredientName);

            	    if (ingredientId == null) {
            	        Ingredient newIngredient = new Ingredient();
            	        newIngredient.setName(ingredientName);

            	        adminMapper.insertNewIngredient(newIngredient); // ✅ 객체 전달
            	        ingredientId = newIngredient.getIngredientId(); // ✅ 생성된 키 받아오기
            	    }

            	    adminMapper.insertRecipeIngredient(recipe.getRecipesId(), ingredientId);
            	}
            }
        } catch (Exception e) {
            throw new RuntimeException("❌ 레시피 추가 중 오류 발생: " + e.getMessage());
        }
    }

    /** ✅ 5. 기존 재료 삭제 후 새로운 재료 추가 */
    /** ✅ 재료를 추가하는 메서드 */
    private void insertIngredients(Long recipeId, List<String> ingredients) {
        if (ingredients == null || ingredients.isEmpty()) {
            System.out.println("⚠️ 추가할 재료 없음.");
            return;
        }

        for (String ingredientName : ingredients) {
            // ✅ 1. 해당 재료가 이미 존재하는지 확인
            Integer ingredientId = adminMapper.getIngredientIdByName(ingredientName);

            // ✅ 2. 존재하지 않으면 새로운 재료 추가
            if (ingredientId == null) {
                Ingredient newIngredient = new Ingredient(); // 객체 생성
                newIngredient.setName(ingredientName);

                adminMapper.insertNewIngredient(newIngredient); // ✅ 객체 전달
                ingredientId = newIngredient.getIngredientId(); // ✅ 새로 생성된 ID 가져오기
            }

            // ✅ 3. Recipe_Ingredients 테이블에 추가
            adminMapper.insertRecipeIngredient(recipeId, ingredientId);
        }
        System.out.println("🟢 재료 추가 완료! 레시피 ID: " + recipeId);
    }


    /** ✅ 6. 레시피 수정 */
    public void updateRecipe(
            Recipe recipe, MultipartFile foodImg,
            String step1, String step2, String step3, String step4, String step5, String step6,
            MultipartFile stepImg1, MultipartFile stepImg2, MultipartFile stepImg3,
            MultipartFile stepImg4, MultipartFile stepImg5, MultipartFile stepImg6,
            boolean deleteFoodImg, boolean deleteStepImg1, boolean deleteStepImg2,
            boolean deleteStepImg3, boolean deleteStepImg4, boolean deleteStepImg5, boolean deleteStepImg6,
            List<String> ingredients) {

        Recipe existing = adminMapper.getRecipeById(recipe.getRecipesId());
        if (existing == null) throw new RuntimeException("❌ 레시피 없음");

        // ✅ 대표 이미지 처리
        if (deleteFoodImg) {
            deleteFile(existing.getFoodImg());
            recipe.setFoodImg(null);
        } else {
            recipe.setFoodImg(updateImageIfExists(foodImg, existing.getFoodImg()));
        }

        // ✅ 단계 이미지 처리 (삭제 요청이 들어오면 `null`로 설정)
        recipe.setStepImg1(handleStepImage(stepImg1, existing.getStepImg1(), deleteStepImg1));
        recipe.setStepImg2(handleStepImage(stepImg2, existing.getStepImg2(), deleteStepImg2));
        recipe.setStepImg3(handleStepImage(stepImg3, existing.getStepImg3(), deleteStepImg3));
        recipe.setStepImg4(handleStepImage(stepImg4, existing.getStepImg4(), deleteStepImg4));
        recipe.setStepImg5(handleStepImage(stepImg5, existing.getStepImg5(), deleteStepImg5));
        recipe.setStepImg6(handleStepImage(stepImg6, existing.getStepImg6(), deleteStepImg6));

        // ✅ 단계별 설명 업데이트
        setRecipeSteps(recipe, step1, step2, step3, step4, step5, step6);

        // ✅ MyBatis에서 `null`이 업데이트되는지 확인
        System.out.println("🔍 업데이트할 데이터: " + recipe.toString());

        // ✅ DB 업데이트 실행
        adminMapper.updateRecipe(recipe);

        // ✅ 재료 업데이트
        updateRecipeIngredients(recipe.getRecipesId(), ingredients);
    }

    private String handleStepImage(MultipartFile newFile, String existingFile, boolean deleteFlag) {
        if (deleteFlag) {
            deleteFile(existingFile);
            return null;
        } else if (newFile != null && !newFile.isEmpty()) {
            deleteFile(existingFile);
            return fileStorageService.storeFile(newFile);
        } else {
            return existingFile;
        }
    }
    public void deleteStepImage(Long recipeId, int stepNumber) {
        if (stepNumber < 1) {  
            throw new IllegalArgumentException("❌ 단계 번호는 1 이상이어야 합니다. stepNumber: " + stepNumber);
        }

        try {
            // ✅ DB에서 기존 이미지 경로 조회
            String column = "stepImg" + stepNumber;
            String imagePath = adminMapper.getStepImagePath(recipeId, column);

            // ✅ 이미지 파일 삭제 (deleteFile() 사용)
            if (imagePath != null && !imagePath.isEmpty()) {
                fileStorageService.deleteFile(imagePath);
                System.out.println("✅ 이미지 파일 삭제 완료: " + imagePath);
            }

            // ✅ DB에서 해당 컬럼을 NULL로 업데이트
            adminMapper.deleteStepImage(recipeId, column);
            System.out.println("✅ 단계 " + stepNumber + " 이미지 삭제 완료 (DB)");
        } catch (Exception e) {
            throw new RuntimeException("❌ 이미지 삭제 실패: " + e.getMessage());
        }
    }

    public void deleteFile(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            fileStorageService.deleteFile(fileName);
        }
    }
    /** ✅ 기존 재료 삭제 후 새로운 재료 추가 */
    private void updateRecipeIngredients(Long recipeId, List<String> ingredients) {
        adminMapper.deleteIngredientsByRecipeId(recipeId); // 기존 재료 삭제
        insertIngredients(recipeId, ingredients); // 새로운 재료 추가
    }
    /** ✅ 레시피 단계별 설명 업데이트 */
    private void setRecipeSteps(Recipe recipe, String step1, String step2, String step3, String step4, String step5, String step6) {
        recipe.setStep1(step1);
        recipe.setStep2(step2);
        recipe.setStep3(step3);
        recipe.setStep4(step4);
        recipe.setStep5(step5);
        recipe.setStep6(step6);
    }

    /** ✅ 레시피 단계별 이미지 업데이트 */
    private void setRecipeStepImages(Recipe recipe, MultipartFile stepImg1, MultipartFile stepImg2, MultipartFile stepImg3,
                                     MultipartFile stepImg4, MultipartFile stepImg5, MultipartFile stepImg6, Recipe existingRecipe) {
        recipe.setStepImg1(updateImageIfExists(stepImg1, existingRecipe != null ? existingRecipe.getStepImg1() : null));
        recipe.setStepImg2(updateImageIfExists(stepImg2, existingRecipe != null ? existingRecipe.getStepImg2() : null));
        recipe.setStepImg3(updateImageIfExists(stepImg3, existingRecipe != null ? existingRecipe.getStepImg3() : null));
        recipe.setStepImg4(updateImageIfExists(stepImg4, existingRecipe != null ? existingRecipe.getStepImg4() : null));
        recipe.setStepImg5(updateImageIfExists(stepImg5, existingRecipe != null ? existingRecipe.getStepImg5() : null));
        recipe.setStepImg6(updateImageIfExists(stepImg6, existingRecipe != null ? existingRecipe.getStepImg6() : null));
    }

    /** ✅ 이미지 저장 (수정 없으면 기존 이미지 유지, 삭제 요청이 오면 삭제) */
    private String updateImageIfExists(MultipartFile file, String existingFileName) {
        try {
            if (file != null && "DELETE".equals(file.getOriginalFilename())) {
                if (existingFileName != null) {
                    fileStorageService.deleteFile(existingFileName); // ✅ 파일 삭제
                }
                return null; // ✅ DB 반영 시 null 저장
            }

            if (file != null && !file.isEmpty()) {
                if (existingFileName != null) {
                    fileStorageService.deleteFile(existingFileName);
                }
                return fileStorageService.storeFile(file);
            }

            return existingFileName;
        } catch (Exception e) {
            throw new RuntimeException("❌ 이미지 업데이트 실패: " + e.getMessage());
        }
    }

    /** ✅ 이미지 저장 (없으면 null 반환) */
    private String saveImageIfExists(MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            return fileStorageService.storeFile(file);
        }
        return null;
    }


    /** ✅ 레시피 목록 가져오기 (검색 및 필터 포함) */
    public List<Recipe> getRecipes(String keyword, Integer categoryId, Integer weatherId) {
        return adminMapper.findRecipes(keyword, categoryId, weatherId);
    }

    /** ✅ 5. 레시피 삭제 */
    public void deleteRecipe(Long id) {
        adminMapper.deleteRecipe(id);
    }
    /** ✅ 유저 레시피 (User_Recipes) 관리 **/
    public List<UserRecipe> getUserRecipesByStatus(String status) {
        return adminMapper.findByStatus(status);
    }
    // ✅ 전체 유저 레시피 조회
    public List<UserRecipe> getAllUserRecipes() {
        System.out.println("📥 [Service] 전체 유저 레시피 조회 요청");
        return adminMapper.getAllUserRecipes();
    }

    // ✅ 특정 유저 레시피 조회
    public UserRecipe getUserRecipeById(Long id) {
        UserRecipe recipe = adminMapper.getUserRecipeById(id);
        System.out.println("✅ 쿼리 결과: " + recipe);
        System.out.println("✅ 재료 필드 ingredientsss: " + recipe.getIngredientsss());
        return recipe;
    }
    // ✅ 승인 대기 중인 유저 레시피 조회 (STATUS = 'OFF'만 가져옴)
    public List<UserRecipe> getPendingUserRecipes() {
        System.out.println("📥 [Service] 승인 대기 레시피 목록 조회 요청");
        return adminMapper.getPendingUserRecipes();
    }

    // ✅ 유저 레시피 승인 (STATUS = 'ON'으로 변경)
    public void approveUserRecipe(Integer id) {
        System.out.println("🔍 [Service] 승인 요청된 레시피 ID: " + id);

        int updatedRows = adminMapper.approveUserRecipe(id);

        if (updatedRows == 0) {
            System.out.println("❌ [Service] 승인 실패 - 존재하지 않는 ID일 가능성");
            throw new RuntimeException("레시피 승인 실패: 존재하지 않는 ID일 가능성이 있음.");
        }

        System.out.println("✅ [Service] 승인 완료! 업데이트된 행 수: " + updatedRows);
    }

    // ✅ 유저 레시피 삭제
    public void deleteUserRecipe(Integer id) {
        System.out.println("🗑️ [Service] 유저 레시피 삭제 요청 - ID: " + id);
        adminMapper.deleteUserRecipe(id);
    }

    // 📌 전체 신고 목록 조회
    public List<krhReportVO> getReports() {
        return adminMapper.getAllReports();
    }

    // 📌 신고 상세 조회
    public krhReportVO getReportById(int reportId) {
        return adminMapper.getReportById(reportId);
    }

    // 📌 신고 삭제
    public void deleteReport(int reportId) {
        adminMapper.deleteReport(reportId);
    }
 // 게시물 전체 조회
    public List<krhBoardVO> getAllBoards() {
        return adminMapper.getAllBoards();
    }

    //특정 게시물 조회
    public krhBoardVO getBoardById(int boardId) {
        return adminMapper.getBoardById(boardId);
    }
    //특정 게시물 삭제
    public void deleteBoard(int boardId) {
        adminMapper.deleteBoardById(boardId);
    }

}