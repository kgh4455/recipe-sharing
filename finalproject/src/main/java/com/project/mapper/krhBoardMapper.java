package com.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.project.model.krhBoardVO;
import com.project.model.krhReportVO;

@Mapper
public interface krhBoardMapper {
	//페이징 처리된 전체 게시글 수 조회 + 검색
	int countBoard(@Param("findStr")String findStr);
	
	//페이징된 목록 조회 + 검색
	List<krhBoardVO>getBoardList(@Param("start") int start, @Param("size") int size, @Param("findStr") String findStr);
	
	//단건 상세보기
	krhBoardVO getBoardById(int boardId); 
	
	//조회수 증가
	void incrementViews(int boardId);
	
//	//게시글의 작성자 조회
//	String getAuthorIdByBoardId(@Param("boardId") int boardId);
	
	//게시글 삭제
	void deleteBoard(@Param("boardId") int boardId, @Param("authorEmail") String authorEmail);

	//게시글 추가
	void insertBoard(krhBoardVO board);
	
	//게시글 수정
	int updateBoard(krhBoardVO board);
	
	//게시글 신고
	void reportBoard(krhReportVO report);
	
	//유저의 좋아요 상태 확인 
	String getLikeStatus(@Param("boardId") int boardId, @Param("userEmail") String userEmail);
	
	//좋아요 싫어요 상태 업데이트
	void updateLikeStatus(@Param("boardId") int boardId, @Param("userEmail") String userEmail, @Param("status") String status);
	
	// 좋아요 갯수
	int getLikeCount(int boardId);
	
	//싫어요 갯수
	int getDislikeCount(int boardId);
	
	//좋아요 싫어요 취소
	void removeLikeStatus(@Param("boardId") int boardId, @Param("userEmail") String userEmail);

	boolean isBoardReported(int boardId, long reporterId);
	
	String getUserbyBoardId(int boardId);
}
