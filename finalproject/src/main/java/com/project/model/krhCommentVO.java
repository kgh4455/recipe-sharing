package com.project.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class krhCommentVO {
	private int commentId;        // 댓글 고유 ID (자동 증가) (대댓글 포함)
    private LocalDateTime createdAt;  // 댓글 작성 시간(대댓글 포함)
    private LocalDateTime updatedAt;  // 댓글 수정 시간(대댓글 포함)
    private String content;       // 댓글 내용(대댓글 포함)
    private String author;        // 댓글 작성자(대댓글 포함)
    private long authorId; //댓글 작성자 고유 아이디(대댓글 포함)
    private boolean isEdited;     // 댓글 수정 여부(대댓글 포함))
    private int replyId; //대댓글 Id ( 댓글 고유 ID와 같으면 같은 거..
    private int boardId; //게시글 아이디
    private String authorEmail;

    List<krhCommentVO> replies; //목록? 이지 않을까 함..
}
