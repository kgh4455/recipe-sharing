package com.project.controller;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.project.config.JwtUtil;
import com.project.model.Notification;
import com.project.model.User;
import com.project.model.krhBoardVO;
import com.project.model.krhCommentVO;
import com.project.model.krhLikeVO;
import com.project.model.krhReportVO;
import com.project.service.UserService;
import com.project.service.krhBoardService;
import com.project.service.krhCommentService;
import com.project.service.krhNotificationService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpSession;
@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/board")
public class krhBoardController {
    
    @Autowired
    private krhBoardService krhboardService;
    @Autowired
	private UserService userService;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private krhCommentService krhcommentService;

	@Autowired
	private krhNotificationService krhnotificationService;
	
	
    //게시글 목록 조회
    @GetMapping
    public ResponseEntity<Map<String, Object>> getBoardList(
        @RequestParam(value = "page", defaultValue = "1") int page,
        @RequestParam(value = "size", defaultValue = "10") int size,
        @RequestParam(value = "findStr", defaultValue = "") String findStr) {

        Map<String, Object> result = krhboardService.getBoardList(page, size, findStr);
        return ResponseEntity.ok(result);
    }
    
    //게시글 단건 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<krhBoardVO> getBoardById(@PathVariable int boardId) {
        return ResponseEntity.ok(krhboardService.getBoardById(boardId));
    }
    
    @GetMapping("/{boardId}/incrementviews")
    public ResponseEntity<Void> incrementViews(@PathVariable int boardId) {
        krhboardService.incrementViews(boardId);
        return ResponseEntity.ok().build();
    }

    //게시글 삭제
    @DeleteMapping("/{boardId}/delete")
    public ResponseEntity<String> deleteBoard(@PathVariable int boardId, @RequestHeader("Authorization") String token) {
    	String jwtToken = token.startsWith("Bearer ") ? token.substring(7):token;
		Claims claims;
		try {
			claims = jwtUtil.extractAllClaims(jwtToken); 
		}catch(Exception e){
			throw new RuntimeException("유효하지 않은 토큰입니다.");
		}
		
		String email = claims.getSubject();
		if(email==null) {
			throw new RuntimeException("로그인이 필요합니다.");
		}

		 // 사용자 이메일을 기반으로 게시물 삭제 요청
	    try {
	        krhboardService.deleteBoard(boardId, email);
	        return ResponseEntity.ok("게시물이 성공적으로 삭제되었습니다.");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시물 삭제에 실패했습니다.");
	    }
    }
    
    //게시글 추가
    @PostMapping("/add")
    public ResponseEntity<String> insertBoard(@RequestBody krhBoardVO krhboardVo, @RequestHeader("Authorization") String token) {
    	String jwtToken = token.startsWith("Bearer ") ? token.substring(7):token;
		Claims claims;
		try {
			claims = jwtUtil.extractAllClaims(jwtToken); 
		}catch(Exception e){
			throw new RuntimeException("유효하지 않은 토큰입니다.");
		}
		
		String email = claims.getSubject();
		if(email==null) {
			throw new RuntimeException("로그인이 필요합니다.");
		}

		User user = userService.getUserByEmail(email);
		// 사용자가 존재하지 않을 경우 예외 처리
	    if (user == null) {
	        throw new RuntimeException("해당 이메일로 등록된 사용자가 없습니다.");
	    }

        long authorId=user.getId(); //id 갖고오기
        String author=user.getName(); //이름 갖고오기
        
        //게시글 정보에 사용자 정보 추가
        krhboardVo.setAuthor(author);
        krhboardVo.setAuthorId(authorId);
        krhboardVo.setAuthorEmail(email);
        
        // 게시글 등록
        try {
            krhboardService.insertBoard(krhboardVo);
            return ResponseEntity.status(HttpStatus.CREATED).body("게시글이 성공적으로 등록되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 등록에 실패했습니다.");
        }
    }
    
    //게시글 수정
    @PutMapping("/{boardId}/update")
    public ResponseEntity<String> updateBoard(@RequestBody krhBoardVO krhboardVo, @PathVariable int boardId, @RequestHeader("Authorization") String token) {
    	String jwtToken = token.startsWith("Bearer ") ? token.substring(7):token;
		Claims claims;
		try {
			claims = jwtUtil.extractAllClaims(jwtToken); 
		}catch(Exception e){
			throw new RuntimeException("유효하지 않은 토큰입니다.");
		}
		
		String email = claims.getSubject();
		
		if(email==null) {
			throw new RuntimeException("로그인이 필요합니다.");
		}
    	
		// 게시글의 authorEmail이 토큰에서 받은 이메일과 동일한지 확인
	    if (!email.equals(krhboardVo.getAuthorEmail())) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인 작성한 게시물만 수정할 수 있습니다.");
	    }
	    
	    // 게시글 수정
        try {
            krhboardService.updateBoard(krhboardVo);
            return ResponseEntity.status(HttpStatus.CREATED).body("게시글이 성공적으로 수정되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 수정에 실패했습니다.");
        }
    }
    
    //신고하기
    @PostMapping("/{boardId}/report")
    public ResponseEntity<String> reportBoard(@PathVariable int boardId, @RequestBody krhReportVO report, @RequestHeader("Authorization") String token) {
        try {
            String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
            Claims claims = jwtUtil.extractAllClaims(jwtToken); // JWT에서 클레임 추출
            String email = claims.getSubject();

            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            User user = userService.getUserByEmail(email);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 이메일로 등록된 사용자가 없습니다.");
            }

            long reporterId = user.getId();
            report.setReporter(email);
            report.setReporterId(reporterId);
            report.setBoardId(boardId);

            boolean isReported = krhboardService.isBoardReported(boardId, reporterId); // 중복 신고 체크
            if (isReported) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 신고한 게시물입니다.");
            }

            krhboardService.reportBoard(report);
            return ResponseEntity.ok("게시물이 성공적으로 신고되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("신고 처리 중 오류가 발생했습니다: " + e.getMessage());
        }
    }   

	
	//댓글 목록 구현
    @GetMapping("/{boardId}/comments")
    public ResponseEntity<List<krhCommentVO>> commentList(@PathVariable int boardId) {
        List<krhCommentVO> comments = krhcommentService.commentList(boardId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
	
    //대댓글 목록 구현
    @GetMapping("/comment/{commentId}/replies")
    public ResponseEntity<?> commentListReply(@PathVariable Integer commentId) {
        if (commentId == null || commentId <= 0) {
            return ResponseEntity.badRequest().body("❌ 잘못된 요청: commentId가 없습니다.");
        }

        List<krhCommentVO> replies = krhcommentService.commentListReply(commentId);
        if (replies == null || replies.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList()); // 빈 배열 반환
        }
        return ResponseEntity.ok(replies);
    }
    
	//댓글 추가 (로그인된 사용자만 가능)
	@PostMapping("/{boardId}/addcomment")
	public ResponseEntity<String> addComment(@PathVariable int boardId, @RequestBody krhCommentVO krhcommentVo, @RequestHeader("Authorization") String token) {
	    // JWT 토큰에서 사용자 이메일 추출
	    String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
	    Claims claims;
	    try {
	    	claims = jwtUtil.extractAllClaims(jwtToken); 
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
	    }

	    String email = claims.getSubject();
	    
	    if (email == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
	    }

	    // 사용자 정보 가져오기
	    User user = userService.getUserByEmail(email);
	    if (user == null) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 이메일로 등록된 사용자가 없습니다.");
	    }

	    // 댓글 정보를 VO에 담기
	    krhcommentVo.setBoardId(boardId);
	    krhcommentVo.setAuthor(user.getName());
	    krhcommentVo.setAuthorId(user.getId());
	    krhcommentVo.setReplyId(0);  // 기본 댓글은 replyId가 0
	    krhcommentVo.setAuthorEmail(email);
	    
	    String boardownerEmail=krhboardService.getUserbyBoardId(boardId);
	    
	    try {
	        // 댓글 추가 서비스 호출
	        krhcommentService.addComment(krhcommentVo);

	        // 작성자와 게시물 주인이 다를 때만 알림 전송
	        if (!email.equals(boardownerEmail)) {
	            Notification notification = new Notification();
	            notification.setReceiverEmail(boardownerEmail);  // 게시물 주인 이메일
	            notification.setBoardId(boardId); //새로 추가
	            String message = user.getName() + "님이 회원님의 게시물에 댓글을 작성하였습니다";
	            notification.setMessage(message);  // 알림 메시지 내용
	            
	            krhnotificationService.insertNotification(notification);
	        }

	        return ResponseEntity.ok("댓글이 성공적으로 추가되었습니다.");
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 추가 중 오류가 발생했습니다.");
	    }
	}
	
	//대댓글 추가
	@PostMapping("/{boardId}/addreply")
	public ResponseEntity<String> addReply(
	        @PathVariable int boardId, 
	        @RequestBody krhCommentVO krhcommentVo, 
	        @RequestHeader("Authorization") String token) {
	    
	    // JWT 토큰에서 사용자 이메일 추출
	    String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
	    Claims claims;
	    
	    System.out.println(krhcommentVo);
	    try {
	        claims = jwtUtil.extractAllClaims(jwtToken); 
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
	    }

	    String email = claims.getSubject();

	    if (email == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
	    }

	    if (krhcommentVo.getReplyId() == 0) {
	        return ResponseEntity.badRequest().body("대댓글은 반드시 부모 댓글 ID가 있어야 합니다.");
	    }

	    // 사용자 정보 가져오기
	    User user = userService.getUserByEmail(email);
	    if (user == null) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 이메일로 등록된 사용자가 없습니다.");
	    }

	    // 부모 댓글의 작성자 이메일 조회 (replyId를 이용하여 부모 댓글 정보 가져오기)
	    System.out.println("전달된 replyId: " + krhcommentVo.getReplyId());
	    krhCommentVO parentComment = krhcommentService.getCommentById(krhcommentVo.getReplyId());
	    if (parentComment == null) {
	        System.out.println("부모 댓글을 찾을 수 없습니다.");
	    } else {
	        System.out.println("부모 댓글: " + parentComment);
	    }

	    String parentCommentAuthorEmail = parentComment.getAuthorEmail(); // 부모 댓글의 작성자 이메일

	    System.out.println(parentCommentAuthorEmail);
	    // 대댓글 정보를 VO에 담기
	    krhcommentVo.setBoardId(boardId);
	    krhcommentVo.setAuthor(user.getName());
	    krhcommentVo.setAuthorId(user.getId());
	    krhcommentVo.setAuthorEmail(email);

	    // 알림 생성 (부모 댓글 작성자에게 알림)
	    if (!email.equals(parentCommentAuthorEmail)) { // 본인에게 알림 보내지 않도록 체크
	        Notification notification = new Notification();
	        notification.setReceiverEmail(parentCommentAuthorEmail); // 부모 댓글 작성자 이메일
	        String message = user.getName() + "님이 회원님의 댓글에 답글을 작성하였습니다."; // 알림 메시지
	        notification.setMessage(message);
            notification.setBoardId(boardId); //새로 추가
	        krhnotificationService.insertNotification(notification); // 알림 저장
	    }

	    // 대댓글 추가 서비스 호출
	    try {
	        krhcommentService.addReply(krhcommentVo);
	        return ResponseEntity.ok("대댓글이 성공적으로 추가되었습니다.");
	    } catch (Exception e) {
	    	e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("대댓글 추가 중 오류가 발생했습니다.");
	    }
	}
	
	//댓글 삭제 (해당 사용자만 가능)
	@DeleteMapping("/{boardId}/deletecomment/{commentId}")
	public ResponseEntity<String> deleteComment(@PathVariable int boardId, 
		    @PathVariable int commentId, 
		    @RequestHeader("Authorization") String token){
		  // JWT 토큰에서 사용자 이메일 추출
	    String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
	    Claims claims;
	    try {
	    	claims = jwtUtil.extractAllClaims(jwtToken); 
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
	    }

	    String email = claims.getSubject();
	    if (email == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
	    }

	    // 사용자 정보 가져오기
	    User user = userService.getUserByEmail(email);
	    if (user == null) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 이메일로 등록된 사용자가 없습니다.");
	    }
	    
        // 댓글 또는 대댓글 정보 조회
        krhCommentVO comment = krhcommentService.findByCommentId(commentId);
        if (comment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("댓글을 찾을 수 없습니다.");
        }

        // 댓글 작성자가 아닌 경우 삭제 불가
        if (comment.getAuthorId() != user.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인 댓글만 삭제할 수 있습니다.");
        }

        // 댓글에 대댓글이 달려있는지 확인
        List<krhCommentVO> replies = krhcommentService.commentListReply(commentId);
        
        if (replies.isEmpty()) {
            // 대댓글이 없으면 댓글을 완전히 삭제
        	krhcommentService.deleteComment(commentId);
        } else {
            // 대댓글이 있으면 댓글 내용을 '삭제된 댓글'로 변경
        	krhcommentService.updateCommentToDeleted(commentId);
        }

        return ResponseEntity.ok("댓글/대댓글이 성공적으로 삭제되었습니다.");
	}
   
	//대댓글 삭제
	@DeleteMapping("/{boardId}/deletereply/{commentId}")
	public ResponseEntity<String> deleteReply(
	    @PathVariable int boardId, 
	    @PathVariable int commentId, 
	    @RequestHeader("Authorization") String token) 
	{
	    // JWT 토큰에서 사용자 이메일 추출
	    String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
	    Claims claims;
	    try {
	    	claims = jwtUtil.extractAllClaims(jwtToken); 
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
	    }
	
	    String email = claims.getSubject();
	    if (email == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
	    }
	
	    // 사용자 정보 가져오기
	    User user = userService.getUserByEmail(email);
	    if (user == null) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 이메일로 등록된 사용자가 없습니다.");
	    }
	
	    // 대댓글 정보 조회 (작성자 확인용)
	    krhCommentVO reply = krhcommentService.findByCommentId(commentId);
	    if (reply == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("대댓글을 찾을 수 없습니다.");
	    }
	
	    // 대댓글 작성자가 아닌 경우 삭제 불가
	    if (reply.getAuthorId() != user.getId()) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인 대댓글만 삭제할 수 있습니다.");
	    }
	
	    // 대댓글 삭제
	    krhcommentService.deleteReply(commentId);
	
	    return ResponseEntity.ok("대댓글이 성공적으로 삭제되었습니다.");
	}

	// 댓글 수정 (해당 사용자만 가능)
	@PutMapping("/{boardId}/updatecomment/{commentId}")
	public ResponseEntity<String> updateComment(
	    @PathVariable int boardId, 
	    @PathVariable int commentId, 
	    @RequestBody krhCommentVO krhCommentVo, 
	    @RequestHeader("Authorization") String token) 
	{
	    // JWT 토큰에서 사용자 이메일 추출
	    String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
	    Claims claims;
	    try {
	    	claims = jwtUtil.extractAllClaims(jwtToken); 
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
	    }

	    String email = claims.getSubject();
	    if (email == null) {
	        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
	    }

	    // 사용자 정보 가져오기
	    User user = userService.getUserByEmail(email);
	    if (user == null) {
	        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 이메일로 등록된 사용자가 없습니다.");
	    }

	    // 댓글 정보 조회 (작성자 확인용)
	    krhCommentVO comment = krhcommentService.findByCommentId(commentId);
	    if (comment == null) {
	        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("댓글을 찾을 수 없습니다.");
	    }

	    // 댓글 작성자가 아닌 경우 수정 불가
	    if (comment.getAuthorId() != user.getId()) {
	        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인 댓글만 수정할 수 있습니다.");
	    }

	    // 댓글 수정
	    comment.setContent(krhCommentVo.getContent());  // 수정된 내용으로 변경
	    try {
	        krhcommentService.updateComment(comment);
	    } catch (Exception e) {
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 수정 중 오류가 발생했습니다.");
	    }

	    return ResponseEntity.ok("댓글이 성공적으로 수정되었습니다.");
	}
	
	// 대댓글 수정 (해당 사용자만 가능)
		@PutMapping("/{boardId}/updatereply/{commentId}")
		public ResponseEntity<String> updateReply(
		    @PathVariable int boardId, 
		    @PathVariable int commentId, 
		    @RequestBody krhCommentVO krhCommentVo, 
		    @RequestHeader("Authorization") String token) 
		{
		    // JWT 토큰에서 사용자 이메일 추출
		    String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
		    Claims claims;
		    try {
		    	claims = jwtUtil.extractAllClaims(jwtToken); 
		    } catch (Exception e) {
		        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
		    }

		    String email = claims.getSubject();
		    if (email == null) {
		        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
		    }

		    // 사용자 정보 가져오기
		    User user = userService.getUserByEmail(email);
		    if (user == null) {
		        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("해당 이메일로 등록된 사용자가 없습니다.");
		    }

		    // 댓글 정보 조회 (작성자 확인용)
		    krhCommentVO comment = krhcommentService.findByCommentId(commentId);
		    if (comment == null) {
		        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("댓글을 찾을 수 없습니다.");
		    }

		    // 댓글 작성자가 아닌 경우 수정 불가
		    if (comment.getAuthorId() != user.getId()) {
		        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인 댓글만 수정할 수 있습니다.");
		    }

		    // 댓글 수정
		    comment.setContent(krhCommentVo.getContent());  // 수정된 내용으로 변경
		    try {
		        krhcommentService.updateReply(comment);
		    } catch (Exception e) {
		        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 수정 중 오류가 발생했습니다.");
		    }

		    return ResponseEntity.ok("댓글이 성공적으로 수정되었습니다.");
		}

		    @PostMapping("/{boardId}/like")
		    public ResponseEntity<String> updateLikeStatus(@PathVariable int boardId, 
		                                                   @RequestBody krhLikeVO boardLikeVO, 
		                                                   @RequestHeader("Authorization") String token) {
		        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
		        Claims claims;

		        try {
		            claims = jwtUtil.extractAllClaims(jwtToken); // JWT에서 클레임 추출
		        } catch (Exception e) {
		            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
		        }

		        String email = claims.getSubject(); // 로그인한 사용자 이메일

		        if (email == null) {
		            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그인이 필요합니다.");
		        }

		        // 요청받은 좋아요/싫어요 상태
		        String likeType = boardLikeVO.getLikeType();
		        
		        if ("like".equals(likeType) || "dislike".equals(likeType)) {
		            krhboardService.updateLikeStatus(boardId, email, likeType);  // 좋아요/싫어요 상태 업데이트
		            System.out.println(email);
		            return ResponseEntity.ok("좋아요 상태가 업데이트되었습니다.");
		        } else {
		            return ResponseEntity.badRequest().body("잘못된 요청입니다.");
		        }
		    }

		    @PostMapping("/{boardId}/removeLike")
		    public ResponseEntity<String> removeLikeStatus(@PathVariable int boardId,
		                                                   @RequestHeader("Authorization") String token) {
		        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
		        Claims claims;

		        try {
		            claims = jwtUtil.extractAllClaims(jwtToken);
		        } catch (Exception e) {
		            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
		        }

		        String email = claims.getSubject(); // 로그인한 사용자 이메일

		        if (email == null) {
		            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("로그인이 필요합니다.");
		        }

		        // 좋아요/싫어요 취소
		        krhboardService.removeLikeStatus(boardId, email);
		        return ResponseEntity.ok("좋아요 상태가 취소되었습니다.");
		    }

		    @GetMapping("/{boardId}/likestatus")
		    public ResponseEntity<Map<String, Object>> getLikeStatus(@PathVariable int boardId,
		                                                              @RequestHeader("Authorization") String token) {
		        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
		        Claims claims;

		        try {
		            claims = jwtUtil.extractAllClaims(jwtToken);
		        } catch (Exception e) {
		            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
		        }

		        String email = claims.getSubject();

		        Map<String, Object> response = new HashMap<>();
		        String likeStatus = krhboardService.getLikeStatus(boardId, email);
		        int likeCount = krhboardService.getLikeCount(boardId);
		        int dislikeCount = krhboardService.getDislikeCount(boardId);

		        response.put("likeStatus", likeStatus);
		        response.put("likeCount", likeCount);
		        response.put("dislikeCount", dislikeCount);

		        return ResponseEntity.ok(response);
		    }
		}