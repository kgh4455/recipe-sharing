package com.project.service;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.project.mapper.krhCommentMapper;
import com.project.mapper.krhMainMapper;
import com.project.model.krhCommentVO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class krhCommentServiceImpl implements krhCommentService {
	private final krhCommentMapper krhcommentMapper;
	
	@Override
	public List<krhCommentVO> commentList(int boardId) {
		// TODO Auto-generated method stub
		return krhcommentMapper.commentList(boardId) ;
	}

	@Override
	public List<krhCommentVO> commentListReply(int commentId) {
		// TODO Auto-generated method stub
		return krhcommentMapper.commentListReply(commentId);
	}

	@Override
	public void addComment(krhCommentVO krhcommentVo) {
		// TODO Auto-generated method stub
		krhcommentMapper.addComment(krhcommentVo);
	}

	@Override
	public void addReply(krhCommentVO krhcommentVo) {
		// TODO Auto-generated method stub
		krhcommentMapper.addReply(krhcommentVo);
	}

	@Override
	public void deleteComment(int commentId) {
		// TODO Auto-generated method stub
		krhcommentMapper.deleteComment(commentId);
	}

	@Override
	public void updateComment(krhCommentVO krhcommentVo) {
		// TODO Auto-generated method stub
		krhcommentMapper.updateComment(krhcommentVo);
	}

	@Override
	public void deleteReply(int replyId) {
		// TODO Auto-generated method stub
		krhcommentMapper.deleteReply(replyId);
	}

	@Override
	public krhCommentVO findByCommentId(int commentId) {
		// TODO Auto-generated method stub
		return krhcommentMapper.findByCommentId(commentId);
	}

	@Override
	public void updateCommentToDeleted(int commentId) {
		// TODO Auto-generated method stub
		krhcommentMapper.updateCommentToDeleted(commentId);
	}

	@Override
	public void updateReply(krhCommentVO krhcommentVo) {
		// TODO Auto-generated method stub
		krhcommentMapper.updateReply(krhcommentVo);
	}

	@Override
	public krhCommentVO getCommentById(int replyId) {
		// TODO Auto-generated method stub
		return krhcommentMapper.getCommentById(replyId);
	}
	
	
	
}
