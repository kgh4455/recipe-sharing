package com.project.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class krhReportVO {
	int reportId; //신고 번호
	int boardId; //게시글 번호
	long reporterId; //신고자 아이디=user.id
	String reporter; //신고자 이름 =user.name
	String reason; //신고 사유
	String description; //추가 설명 (선택적)
	LocalDateTime reportedAt; //시간
}