package com.project.model;

import lombok.Data;

@Data
public class krhPagingVO {
	private int page;           // 현재 페이지
    private int size;           // 한 페이지당 게시글 개수
    private int start;          // 시작 인덱스 (페이징 쿼리에서 사용)
    private int totalCount;     // 전체 게시글 수
    private int totalPages;     // 전체 페이지 수
    private String findStr;     // 검색어
}
