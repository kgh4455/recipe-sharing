package com.project.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;

import com.project.config.JwtUtil;
import com.project.model.Inquiry;
import com.project.model.Notification;
import com.project.model.User;
import com.project.model.krhBoardVO;
import com.project.service.EmailService;
import com.project.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;

    

    // ✅ 이메일 인증번호 전송
    @PostMapping("/verify-email")
    public ResponseEntity<String> sendVerificationEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        userService.sendVerificationEmail(email);
        return ResponseEntity.ok("이메일 인증 코드가 전송되었습니다.");
    }

    // ✅ 이메일 인증번호 확인
    @PostMapping("/confirm-email")
    public ResponseEntity<String> confirmEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code"); // ✅ 여기서 code로 변경

        userService.verifyEmail(email, code); // ✅ 서비스에서도 code로 변경

        return ResponseEntity.ok("Email verified successfully.");
    }


    // ✅ 1. 🔹 아이디 찾기 (이름 + 휴대폰 번호)
    @PostMapping("/find-id")
    public ResponseEntity<?> findUserId(@RequestBody Map<String, String> request) {
        try {
            String name = request.get("name");
            String phoneNumber = request.get("phoneNumber");
            String userId = userService.findUserIdByNameAndPhone(name, phoneNumber);
            return ResponseEntity.ok(Map.of("userId", userId));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ 2. 🔹 비밀번호 찾기 (이메일 + 휴대폰 번호 인증 후 코드 전송)
    @PostMapping("/send-verification-code")
    public ResponseEntity<String> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String phoneNumber = request.get("phoneNumber");
        userService.sendVerificationCode(email, phoneNumber);
        return ResponseEntity.ok("인증 코드가 이메일로 전송되었습니다.");
    }

    // ✅ 3. 🔹 비밀번호 재설정 (이메일 + 휴대폰 번호 + 인증 코드 확인)
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String newPassword = request.get("newPassword");

        userService.resetPassword(email, newPassword);
        return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
    }

    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerUser(
            @RequestPart("user") User user,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage) {

        System.out.println("✅ 회원가입 요청 데이터: " + user);
        userService.registerUser(user, profileImage);
        return ResponseEntity.ok("회원가입 성공!");
    }






    // ✅ 이메일 중복 확인 API

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@RequestParam String email) {
        boolean exists = userService.checkEmailExists(email);
        Map<String, Boolean> response = new HashMap<>();
        response.put("exists", exists);
        return ResponseEntity.ok(response);
    }


    // ✅ 휴대폰 번호 중복 확인 API
    @GetMapping("/check-phone")
    public ResponseEntity<Boolean> checkPhoneExists(@RequestParam String phoneNumber) {
        boolean exists = userService.checkPhoneExists(phoneNumber);
        return ResponseEntity.ok(exists);
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
        System.out.println("🔐 [컨트롤러] 로그인 요청됨 - 이메일: " + user.getEmail());

        // 🔥 login() 메서드로 로그인 처리 (여기서 로그인 기록 등 전부 처리됨)
        Map<String, Object> result = userService.login(user.getEmail(), user.getPassword());

        System.out.println("✅ [컨트롤러] 로그인 완료 후 응답 반환");

        return ResponseEntity.ok(result);
    }

    // ✅ 로그인 시 암호화된 비밀번호 조회
    @GetMapping("/get-hashed-password")
    public ResponseEntity<?> getHashedPassword(@RequestParam String email) {
        System.out.println("📡 [백엔드] 로그인 시 비밀번호 조회 요청 - 이메일: " + email);
        
        String hashedPassword = userService.getHashedPasswordByEmail(email);
        if (hashedPassword == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("이메일이 존재하지 않습니다.");
        }

        System.out.println("🔹 [백엔드] 조회된 해시된 비밀번호: " + hashedPassword);
        return ResponseEntity.ok(Map.of("password", hashedPassword));
    }
//    @PostMapping("/login")
//    public ResponseEntity<Map<String, Object>> login(@RequestBody User user) {
//        System.out.println("🔹 UserController: login() 실행됨!"); // 컨트롤러 로그
//
//        Map<String, Object> response = userService.login(user.getEmail(), user.getPassword());
//
//        System.out.println("✅ UserController: userService.login() 호출 성공!"); // 로그인 성공 로그
//
//        return ResponseEntity.ok(response);
//    }

 // ✅ 유저 정보 조회
 // ✅ 유저 정보 조회 엔드포인트
    @GetMapping("/get-user")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        User user = userService.getUserByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("사용자를 찾을 수 없습니다.");
        }

        // ✅ 디버깅 로그 추가 (DB에서 가져온 프로필 이미지 확인)
        System.out.println("📢 [DB에서 가져온 프로필 이미지]: " + user.getProfileImage());

        // ✅ 프로필 이미지 변환 (중복 변환 방지)
        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
            String profileImage = user.getProfileImage().trim();

            // 🚀 이미 전체 URL이면 변환하지 않음
            if (!profileImage.startsWith("http://") && !profileImage.startsWith("https://")) {
                // ✅ 파일명만 저장된 경우에만 URL 변환
                String encodedFileName = encodeURIComponent(profileImage);
                String fullImageUrl = "http://localhost:8080/uploads/" + encodedFileName;

                System.out.println("🖼 [백엔드 응답 프로필 이미지 URL]: " + fullImageUrl);
                user.setProfileImage(fullImageUrl);
            } else {
                System.out.println("🖼 기존 URL 유지: " + profileImage);
            }
        }

        return ResponseEntity.ok(user);
    }

    // ✅ 파일 이름 URL 인코딩 함수
    private String encodeURIComponent(String fileName) {
        try {
            return URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20"); // ✅ 공백을 "%20"으로 변환 (브라우저 호환성)
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("파일명 인코딩 실패", e);
        }
    }

    // ✅ 7. 🔹 본인 정보 수정
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(
            @RequestParam("email") String email,
            @RequestParam(value = "password", required = false) String password,
            @RequestParam("name") String name,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam(value = "file", required = false) MultipartFile file) {

        System.out.println("📡 [Backend] 회원 정보 수정 요청:");
        System.out.println("Email: " + email);
        System.out.println("Password: " + password);
        System.out.println("Name: " + name);
        System.out.println("PhoneNumber: " + phoneNumber);
        if (file != null) {
            System.out.println("File Name: " + file.getOriginalFilename());
        } else {
            System.out.println("File: 없음");
        }

        try {
            // ✅ 업데이트된 유저 정보 받아오기
            User updatedUser = userService.updateUser(email, password, name, phoneNumber, file);

            // ✅ 새로운 프로필 이미지 URL 생성
            String profileImageUrl = updatedUser.getProfileImage() != null 
                ? "http://localhost:8080/uploads/" + updatedUser.getProfileImage()
                : null;

            // ✅ 유저 정보 + 프로필 이미지 URL 반환
            return ResponseEntity.ok(Map.of(
                "email", updatedUser.getEmail(),
                "name", updatedUser.getName(),
                "phoneNumber", updatedUser.getPhoneNumber(),
                "profileImage", profileImageUrl
            ));
        } catch (RuntimeException e) {
            System.err.println("❌ [Error] 회원 정보 수정 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 처리 중 오류 발생: " + e.getMessage());
        }
    }




 // ✅ 8. 🔹 회원 탈퇴 요청
    @PostMapping("/request-deletion")
    public ResponseEntity<String> requestDeletion(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String reason = request.get("reason");

        // 🔍 요청이 들어오는지 확인하는 로그 추가
        System.out.println("🟢 [회원 탈퇴 요청] email: " + email + ", reason: " + reason);

        userService.requestAccountDeletion(email, reason);
        
        return ResponseEntity.ok("Account deletion request submitted.");
    }


    // ✅ 9. 🔹 유저 알림 목록 조회
    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getUserNotifications(@RequestParam String email) {
        return ResponseEntity.ok(userService.getUserNotifications(email));
    }

    // ✅ 10. 🔹 유저 알림 읽음 처리
    @PatchMapping("/notifications/read")
    public ResponseEntity<String> readUserNotification(@RequestBody Map<String, String> request) {
        userService.markUserNotificationAsRead(Long.parseLong(request.get("notificationId")), request.get("email"));
        return ResponseEntity.ok("Notification read");
    }
    // ✅ 특정 알림 삭제 API
    @DeleteMapping("/notifications/{id}")
    public String deleteUserNotification(@PathVariable Long id) {
        userService.deleteUserNotification(id);
        return "알림이 삭제되었습니다.";
    }
    /** ✅ 유저 즐겨찾기 관련 기능 **/

    // 유저의 즐겨찾기 목록 조회 (마이페이지에서)
    @GetMapping("/{userId}/favorites")
    public ResponseEntity<List<Map<String, Object>>> getFavoritesByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getFavoriteRecipeList(userId));
    }

    
    // 유저의 즐겨찾기 삭제 (본인의 것만)
    @DeleteMapping("/{userId}/favorites/{recipeId}")
    public ResponseEntity<String> removeFavorite(@PathVariable Long userId, @PathVariable Long recipeId) {
        userService.removeFavorite(userId, recipeId);
        return ResponseEntity.ok("즐겨찾기에서 삭제되었습니다.");
    }

    // ✅ 13. 🔹 1:1 문의 등록
 // ✅ 1:1 문의 등록
    @PostMapping("/inquiries")
    public ResponseEntity<String> createInquiry(@RequestBody Inquiry inquiry) {
        if (inquiry.getUserEmail() == null || inquiry.getTitle() == null || inquiry.getContent() == null) {
            return ResponseEntity.badRequest().body("모든 필드를 입력해주세요.");
        }

        userService.insertInquiry(inquiry);
        return ResponseEntity.ok("Inquiry successfully created.");
    }
    // ✅ 14. 🔹 1:1 문의 조회
    @GetMapping("/inquiries/list")
    public ResponseEntity<?> getUserInquiries(@RequestParam String email) {
        if (email == null || email.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("이메일을 입력해야 합니다.");
        }
        List<Inquiry> inquiries = userService.getUserInquiries(email);
        return ResponseEntity.ok(inquiries);
    }
    // ✅ 특정 문의 조회 API
    @GetMapping("/inquiries/{id}")
    public ResponseEntity<?> getInquiryById(@PathVariable Long id) {
        Inquiry inquiry = userService.getInquiryById(id);

        if (inquiry == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("❌ 해당 ID의 문의가 존재하지 않습니다.");
        }

        return ResponseEntity.ok(inquiry);
    }
    // ✅ 15. 🔹 특정 1:1 문의 삭제
    @DeleteMapping("/inquiries/delete")
    public ResponseEntity<String> deleteInquiry(@RequestBody Map<String, String> request) {
        Long inquiryId = Long.parseLong(request.get("id"));
        String email = request.get("email");

        // ✅ 사용자가 해당 문의의 작성자인지 확인
        if (!userService.isInquiryOwner(inquiryId, email)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("삭제 권한이 없습니다.");
        }

        userService.deleteInquiry(inquiryId, email);
        return ResponseEntity.ok("문의가 삭제되었습니다.");
    }

    // ✅ 16. 🔹 게시물 조회
 // ✅ 게시물 조회 (email 기반)
    @GetMapping("/my-board-titles")
    public ResponseEntity<List<krhBoardVO>> getUserBoardTitles(@RequestParam String email) {
        return ResponseEntity.ok(userService.getUserBoardTitles(email));
    }

}