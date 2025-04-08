package com.project.service;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.transaction.annotation.Transactional;

import com.project.model.krhApplicationRequestVO;
import com.project.model.krhClubVO;
import com.project.model.krhTagVO;

public interface krhClubService {
	
	//클럽 전체 목록 조회
	List<krhClubVO> clubList();
	
	//태그 10개 목록
	List<krhTagVO> tagList();
	
	//태그에 맞는 클럽 조회
	List<krhClubVO> clubtagList(int tagId);
	
	//검색어에 따른 클럽 검색
	List<krhClubVO> searchClub(String keyword);

	// 동호회 생성
	void createClub(krhClubVO krhclubVO);

	//동호회 단건 조회
	krhClubVO getClubById(int clubId);
	
	//동호회 주최자 이메일 get
	String getEmailbyId(int clubId);
	
	//동호회 신청
	void insertApplication(krhApplicationRequestVO apvo);
}
