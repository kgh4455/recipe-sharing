package com.project.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.project.mapper.krhBoardMapper;
import com.project.mapper.krhClubMapper;
import com.project.model.krhApplicationRequestVO;
import com.project.model.krhBoardVO;
import com.project.model.krhClubVO;
import com.project.model.krhTagVO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class krhClubServiceImpl implements krhClubService{
	
	private final krhClubMapper krhclubMapper;

	//클럽 목록
	@Override
	public List<krhClubVO> clubList() {
		// TODO Auto-generated method stub
		return krhclubMapper.clubList();
	}
	
	//태그 목록
	@Override
	public List<krhTagVO> tagList() {
		// TODO Auto-generated method stub
		return krhclubMapper.tagList();
	}

	//태그에 해당되는 클럽 목록
	@Override
	public List<krhClubVO> clubtagList(int tagId) {
		// TODO Auto-generated method stub
		return krhclubMapper.clubtagList(tagId);
	}

	//클럽 검색
	@Override
	public List<krhClubVO> searchClub(String keyword) {
		// TODO Auto-generated method stub
		return krhclubMapper.searchClub(keyword);
	}

	@Override
	  @Transactional
	    public void createClub(krhClubVO krhclubVO) {
	        // 동호회 정보 삽입
	        krhclubMapper.insertClub(krhclubVO);

	        // 해시태그가 있을 경우 처리
	        if (krhclubVO.getHashtags() != null && !krhclubVO.getHashtags().isEmpty()) {
	            // 태그 삽입 (중복 방지)
	            krhclubMapper.insertTagIfNotExist(krhclubVO);

	            // 클럽과 태그 연결
	            krhclubMapper.linkPostHashtags(krhclubVO.getClubId(), krhclubVO.getHashtags());
	        }
	    }
	//단건조회
	@Override
	public krhClubVO getClubById(int clubId) {
		// TODO Auto-generated method stub
		return krhclubMapper.getClubById(clubId);
	}

	//동호회 주최자 이메일 get
	@Override
	public String getEmailbyId(int clubId) {
		// TODO Auto-generated method stub
		return krhclubMapper.getEmailbyId(clubId);
	}

	@Override
	public void insertApplication(krhApplicationRequestVO apvo) {
		// TODO Auto-generated method stub
		krhclubMapper.insertApplication(apvo);
	}
}
