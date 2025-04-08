package com.project.service;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.project.model.krhCommentVO;

public interface krhCommentService {
	//댓글 목록 조회
	List<krhCommentVO> commentList(int boardId);
		
	//대댓글 목록 조회
	List<krhCommentVO> commentListReply(int commentId);
	
	//댓글 추가
	void addComment(krhCommentVO krhcommentVo);
		
	//대댓글 추가
	void addReply(krhCommentVO krhcommentVo);
		
	//댓글 수정
	void updateComment(krhCommentVO krhcommentVo);
		
	//댓글 삭제
	void deleteComment(int commentId);

	//대댓글 삭제
	void deleteReply(int replyId);

    // 대댓글 정보 조회
	krhCommentVO findByCommentId(int commentId);

    // 댓글을 삭제된 상태로 업데이트
	void updateCommentToDeleted(int commentId);
	
	//대댓글 수정
	void updateReply(krhCommentVO krhcommentVo);

	krhCommentVO getCommentById(int replyId);

}
