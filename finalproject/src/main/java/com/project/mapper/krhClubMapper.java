package com.project.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.project.model.krhApplicationRequestVO;
import com.project.model.krhClubVO;
import com.project.model.krhTagVO;

@Mapper
public interface krhClubMapper {
	//클럽 전체 목록 조회
	List<krhClubVO> clubList();
	
	//태그 10개 목록
	List<krhTagVO> tagList();
	
	//태그에 맞는 클럽 조회
	List<krhClubVO> clubtagList(@Param("tagId") int tagId);
	
	//검색어에 따른 클럽 검색
	List<krhClubVO> searchClub(String keyword);
	
	 // 게시글 삽입
    void insertClub(krhClubVO krhclubVO);

    // 해시태그 삽입
    void insertTagIfNotExist(krhClubVO krhclubVO);

    // 게시물과 해시태그 연결
    void linkPostHashtags(int clubId, List<String> hashtags);
    
    //클럽 단건 조회
    krhClubVO getClubById(int clubId);
    
    //클럽 신청을 위한 주최자의 이메일 갖고오기
    String getEmailbyId(int clubId);
    
    //클럽 신청
    void insertApplication(krhApplicationRequestVO apvo);
}
