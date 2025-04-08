package com.project.service;


import java.util.Map;

import org.apache.ibatis.annotations.Param;
import org.springframework.http.ResponseEntity;

import com.project.model.krhBoardVO;
import com.project.model.krhLikeVO;
import com.project.model.krhReportVO;

public interface krhBoardService {
	//페이징 처리된 전체 게시글 수 조회 + 검색
	int countBoard(String findStr);
	
	//페이징된 목록 조회 + 검색
	Map<String,Object>getBoardList(int page, int size, String findStr);
	
	//단건 상세보기
	krhBoardVO getBoardById(int boardId);
	
	//조회수 추가
	void incrementViews(int boardId);

	//게시글 삭제
	void deleteBoard(int boardId, String authorEmail);
	
	//게시글 주인
	//int getAuthorIdByBoardId(int boardId);
	
	//게시글 추가
	void insertBoard(krhBoardVO board);
	
	//게시글 수정
	void updateBoard(krhBoardVO board);
	
	//게시글 신고
	void reportBoard(krhReportVO report);
	
	boolean isBoardReported(int boardId, long reporterId);
	
	//좋아요 싫어요 관련
	void updateLikeStatus(int boardId, String userEmail, String likeType);
	void removeLikeStatus(int boardId, String userEmail);
	String getLikeStatus(int boardId, String userEmail);
	int getLikeCount(int boardId);
	int getDislikeCount(int boardId);
	
	String getUserbyBoardId(int boardId);
}
