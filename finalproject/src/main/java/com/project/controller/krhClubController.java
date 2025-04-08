package com.project.controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.io.File;
import java.io.IOException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.config.JwtUtil;
import com.project.model.Notification;
import com.project.model.User;
import com.project.model.krhApplicationRequestVO;
import com.project.model.krhClubVO;
import com.project.model.krhTagVO;
import com.project.service.UserService;
import com.project.service.krhClubService;
import com.project.service.krhNotificationService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
@CrossOrigin(origins = "http://localhost:5173")
@AllArgsConstructor
@RestController
@RequestMapping("/api/club")
public class krhClubController {
	
	@Autowired
	private final krhClubService krhclubService;
	
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private final JwtUtil jwtUtil;
	
	@Autowired
	private final UserService userService;
	
	@Autowired
	private final krhNotificationService krhnotificationService;
	
	//전체 목록 조회
	@GetMapping
	public ResponseEntity<List<krhClubVO>>clubList(){
		return ResponseEntity.ok(krhclubService.clubList());
	}
	
	//태그 10개 조회
	@GetMapping("/tags")
	public ResponseEntity<List<krhTagVO>>tagList(){
		return ResponseEntity.ok(krhclubService.tagList());
	}
	
	//태그에 맞는 클럽 조회
	@GetMapping("/tags/{tagId}")
	public ResponseEntity<List<krhClubVO>>getClubsByTag(@PathVariable int tagId){
		System.out.println("Received tagId: " + tagId);  // 서버에서 tagId 확인
		return ResponseEntity.ok(krhclubService.clubtagList(tagId));
	}
	
	//검색어에 따른 클럽 검색
	@GetMapping("/search")
	public ResponseEntity<List<krhClubVO>>searchClub(@RequestParam String keyword){
		return ResponseEntity.ok(krhclubService.searchClub(keyword));
	}
	
	//클럽 추가
	 @PostMapping("/add")
	    public ResponseEntity<String> createClub(
	            @RequestParam("title") String title,  // 제목
	            @RequestParam("location") String location,  // 모임 장소
	            @RequestParam String hashtags, 
	            @RequestParam("date") LocalDate date,  // 종료일
	            @RequestParam("clubFeatures") String clubFeatures,  // 내용
	            @RequestParam("clubUrl") String clubUrl,
	            @RequestParam(value = "clubImage", required = false) MultipartFile clubImage,  // 이미지 파일
	            @RequestHeader("Authorization") String token  // JWT 토큰
	    ) throws IOException {
		   // 유효성 검사: 해시태그가 null이면 빈 리스트로 처리
		 // 해시태그를 JSON 배열로 파싱
         ObjectMapper objectMapper = new ObjectMapper();
         List<String> tagList = objectMapper.readValue(hashtags, List.class);

	        // 로그로 데이터 출력
	        System.out.println("클럽 제목: " + title);
	        System.out.println("모임 장소: " + location);
	        System.out.println("Hashtags: " + tagList);
	        System.out.println("종료일: " + date);
	        System.out.println("내용: " + clubFeatures);
	        System.out.println("url: " + clubUrl);
	        if (clubImage != null) {
	            System.out.println("파일 정보: " + clubImage.getOriginalFilename());  // 파일 이름 출력
	        }

	        // JWT 토큰에서 이메일 추출
	        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
	        String email;

	        try {
	            // JWT에서 이메일 추출
	            email = jwtUtil.extractEmail(jwtToken);
	        } catch (Exception e) {
	            // 토큰이 잘못된 경우
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
	        }

	        // 이메일이 정상적으로 추출되지 않은 경우
	        if (email == null || email.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
	        }

	        // 이메일로 사용자 정보 조회
	        User user = userService.getUserByEmail(email);

	        // 사용자가 존재하지 않을 경우
	        if (user == null) {
	            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("사용자 정보를 찾을 수 없습니다.");
	        }

	        // 클럽 이름이 없으면 요청 처리 실패
	        if (title == null || title.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("동호회 이름을 입력해주세요.");
	        }

	        // 모임 장소가 없으면 요청 처리 실패
	        if (location == null || location.isEmpty()) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("모임 장소를 입력해주세요.");
	        }

	        // 클럽 객체 생성
	        krhClubVO krhclubVO = new krhClubVO();
	        krhclubVO.setClubName(title);
	        krhclubVO.setLocation(location);
	        krhclubVO.setHashtags(tagList);  // 해시태그 설정
	        krhclubVO.setDate(date);
	        krhclubVO.setClubFeatures(clubFeatures);
	        krhclubVO.setRecruiterEmail(email);  // 사용자가 작성한 클럽이므로 이메일을 저장
	        
	        krhclubVO.setClubUrl(clubUrl);
	        
	        if (clubImage != null && !clubImage.isEmpty()) {
	            // 프로젝트 루트 경로를 기준으로 파일 경로 설정
	            String uploadDir = System.getProperty("user.dir") + "/uploads/";
	            File dir = new File(uploadDir);
	            if (!dir.exists()) {
	                dir.mkdirs();  // 디렉토리 없으면 생성
	            }

	            // 파일명 유니크하게 변경 (UUID 사용)
	            String fileName = UUID.randomUUID().toString() + "_" + clubImage.getOriginalFilename();
	            File saveFile = new File(uploadDir + "/" + fileName);
	            
	            try {
	                // 파일을 저장
	                clubImage.transferTo(saveFile);
	                krhclubVO.setClubImage(fileName);  // 클럽 객체에 파일 경로 설정
	            } catch (IOException ioException) {
	                // 파일 업로드 실패 시 예외 처리
	                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 업로드 실패: " + ioException.getMessage());
	            }
	        }

	        try {
	            // 클럽 생성 서비스 호출
	            krhclubService.createClub(krhclubVO);
	            return ResponseEntity.ok("게시글이 등록되었습니다!");
	        } catch (Exception e) {
	            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 등록 실패: " + e.getMessage());
	        }
	    }
	
	//클럽 단건조회
	@GetMapping("/{clubId}")
	public ResponseEntity<krhClubVO> getClubById(@PathVariable int clubId){
		krhclubService.getClubById(clubId);
		return ResponseEntity.ok(krhclubService.getClubById(clubId));
	}
	
	//클럽 신청을 위한 주최자의 이메일 갖고오기
	@PostMapping("/{clubId}/send-application")
	public ResponseEntity<String> sendApplication(@PathVariable int clubId, @RequestBody krhApplicationRequestVO apvo) {
	    // 요청 객체에 clubId 설정
	    apvo.setClubId(clubId);
	    apvo.setApplyDate(LocalDateTime.now());
	    System.out.println(apvo);

	    try {
	        // 게시글(동호회) 작성자의 이메일 가져오기
	        String ownerEmail = krhclubService.getEmailbyId(clubId);

	        if (ownerEmail == null) {
	            return ResponseEntity.badRequest().body("해당 동호회 정보를 찾을 수 없습니다.");
	        }

	        // 신청 데이터 저장
	        krhclubService.insertApplication(apvo);
	        
	        Notification notification = new Notification();
	        notification.setReceiverEmail(ownerEmail);  // 주최자 이메일
	        String message = apvo.getApplicantName() + "님이 회원님의 모임에 참가 신청하셨습니다. 이메일 "+ownerEmail+" 을 통해 확인 부탁드립니다.";
	        notification.setMessage(message);  // 알림 메시지 내용
	        krhnotificationService.insertNotification(notification);  // 알림 DB에 저장
	        // 이메일 전송
	        sendEmail(ownerEmail, apvo);

	        // 성공적인 신청 처리
	        return ResponseEntity.ok("신청이 완료되었습니다!");

	    } catch (Exception e) {
	        // 예외 발생 시 로그 출력
	        System.out.println("오류 발생: " + e.getMessage());
	        e.printStackTrace();  // 예외의 자세한 정보도 출력

	        // 클라이언트에게 오류 메시지 반환
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("신청 처리 중 오류가 발생했습니다.");
	    }
	}

	// 이메일 보내기
	private void sendEmail(String ownerEmail, krhApplicationRequestVO apvo) {
	    try {
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setTo(ownerEmail);
	        message.setSubject("[냠냠] 회원님의 모임에 새로운 신청이 도착했습니다!");
	        message.setText(
	        		"안녕하세요 모임 담당자님,\r\n"
	        		+ "\r\n"
	        		+ apvo.getApplicantName() + "님이 모임에 신청하셨습니다. 아래는 신청자 정보입니다:\n\n"
	                + "신청자 정보\n\n"
	                + "이름: " + apvo.getApplicantName() + "\n"
	                + "이메일: " + apvo.getApplicantEmail() + "\n"
	                + "나이: " + apvo.getApplicantAge() + "세\n"
	                + "성별: " + apvo.getApplicantGender() + "\n"
	                + "신청 일자: " + (apvo.getApplyDate() != null ? apvo.getApplyDate().toString() : "정보 없음") + "\n"
	                + "개인 정보 동의 여부: " + (apvo.isPrivacyAgreement() ? "동의함" : "동의하지 않음") + "\n\n"
	                + "신청자 정보 확인 후, 모임 진행에 필요한 조치를 취해 주세요.\n\n"
	                + "감사합니다."
	        );
	        mailSender.send(message);
	        System.out.println("이메일이 성공적으로 발송되었습니다.");

	    } catch (Exception e) {
	        // 이메일 발송 중 예외 발생 시 로그 출력
	        System.out.println("이메일 발송 오류: " + e.getMessage());
	        e.printStackTrace();  // 예외의 자세한 정보도 출력
	    }
	}

}