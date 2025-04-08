package com.project.model;

import lombok.Data;

@Data
public class krhLikeVO {
	public krhLikeVO(int boardId2, String email, String likeStatus, int likeCount2, int dislikeCount2) {
		// TODO Auto-generated constructor stub
	}
	private int boardId; //게시판 아이디 
    private String likeType; // "like", "dislike", "none"
    private int likeCount;
    private int dislikeCount;
    private String userEmail;
}
