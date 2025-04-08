package com.project.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.mapper.krhBoardMapper;
import com.project.mapper.krhMainMapper;
import com.project.model.krhBoardVO;
import com.project.model.krhLikeVO;
import com.project.model.krhPagingVO;
import com.project.model.krhReportVO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class krhBoardServiceImpl implements krhBoardService{

	private final krhBoardMapper krhboardMapper;
	 private final NotificationService notificationService;
	 
	@Override
	public int countBoard(String findStr) {
		// TODO Auto-generated method stub
		return krhboardMapper.countBoard(findStr);
	}

	@Override
	public Map<String, Object> getBoardList(int page, int size, String findStr) {
		// TODO Auto-generated method stub
		// 페이징을 위한 start 계산
        int start = (page - 1) * size;

        // 게시글 목록 조회 (검색어를 고려한 조회)
        List<krhBoardVO> boardList = krhboardMapper.getBoardList(start, size, findStr);

        // 전체 게시글 수 조회 (검색어를 고려한 카운트)
        int totalCount = krhboardMapper.countBoard(findStr);

        // 전체 페이지 수 계산
        int totalPages = (int) Math.ceil((double) totalCount / size);

        // PageVO 생성
        krhPagingVO pageVO = new krhPagingVO();
        pageVO.setPage(page);
        pageVO.setSize(size);
        pageVO.setStart(start);
        pageVO.setTotalCount(totalCount);
        pageVO.setTotalPages(totalPages);
        pageVO.setFindStr(findStr);

        // 결과를 Map으로 묶어서 반환
        Map<String, Object> result = new HashMap<>();
        result.put("boardList", boardList);
        result.put("pageVO", pageVO);

        return result;
	}
	
	//상세보기
	@Override
	public krhBoardVO getBoardById(int boardId) {
		// TODO Auto-generated method stub
		return krhboardMapper.getBoardById(boardId);
	}
	
	//조회수 증가
	@Override
	public void incrementViews(int boardId) {
		// TODO Auto-generated method stub
		krhboardMapper.incrementViews(boardId);
	}
	
	//삭제하기
	@Override
	public void deleteBoard(int boardId, String authorEmail) {
		// TODO Auto-generated method stub
		krhboardMapper.deleteBoard(boardId, authorEmail);
	}

	//게시글 작성자
//	@Override
//	public String getAuthorIdByBoardId(int boardId) {
//		// TODO Auto-generated method stub
//		return krhboardMapper.getAuthorIdByBoardId(boardId);
//	}

	//게시글 추가
	@Override
	public void insertBoard(krhBoardVO board) {
		// TODO Auto-generated method stub
		krhboardMapper.insertBoard(board);
	}
	
	//게시글 수정
	@Transactional
	@Override
	public void updateBoard(krhBoardVO board) {
		System.out.println("게시글 수정 시작: " + board);
	    int result = krhboardMapper.updateBoard(board);
	    System.out.println("수정된 게시글 수: " + result);
	    if (result == 0) {
	        throw new RuntimeException("게시글 수정에 실패했습니다.");
	    }
	}

	//게시글 신고
		@Override
		public void reportBoard(krhReportVO report) {
			// TODO Auto-generated method stub
			krhboardMapper.reportBoard(report);
			 // ✅ 관리자에게 WebSocket 실시간 알림 전송
		    notificationService.sendReportNotification(
		        "게시글 신고가 접수되었습니다. [신고 사유: " + report.getReason() + "]"
		    );
		}
	
	@Override
	public boolean isBoardReported(int boardId, long reporterId) {
		// TODO Auto-generated method stub
		return krhboardMapper.isBoardReported(boardId, reporterId);
	}
	
	 // 좋아요 상태 업데이트
    public void updateLikeStatus(int boardId, String userEmail, String likeType) {
        // 기존에 좋아요 상태가 있는지 확인하고 업데이트
        krhboardMapper.updateLikeStatus(boardId, userEmail, likeType);
    }

    // 좋아요/싫어요 취소
    public void removeLikeStatus(int boardId, String userEmail) {
        // 좋아요 또는 싫어요를 취소
    	krhboardMapper.removeLikeStatus(boardId, userEmail);
    }

    // 좋아요 상태 조회
    public String getLikeStatus(int boardId, String userEmail) {
        return krhboardMapper.getLikeStatus(boardId, userEmail);
    }

    // 좋아요 카운트 조회
    public int getLikeCount(int boardId) {
        return krhboardMapper.getLikeCount(boardId);
    }

    // 싫어요 카운트 조회
    public int getDislikeCount(int boardId) {
        return krhboardMapper.getDislikeCount(boardId);
    }

	@Override
	public String getUserbyBoardId(int boardId) {
		// TODO Auto-generated method stub
		return krhboardMapper.getUserbyBoardId(boardId);
	}

    
}
