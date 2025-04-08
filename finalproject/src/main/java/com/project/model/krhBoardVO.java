package com.project.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class krhBoardVO {
	int boardId; //고유번호
	String title; //제목
	String content; //내용
	LocalDateTime createdAt; //작성 시간
	LocalDateTime updatedAt; //수정 시간
	int views; //조회수
	String author; //작성자 이름
	long authorId; //작성자 아이디 (구분값)
	int like; //좋아요
	int dislike; //싫어요
	String authorEmail; //작성자 이메일
}