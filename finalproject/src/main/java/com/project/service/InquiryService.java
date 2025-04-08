package com.project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.mapper.InquiryMapper;
import com.project.model.Inquiry;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class InquiryService {
    private final InquiryMapper inquiryMapper;

    // 회원의 모든 문의 조회
    public List<Inquiry> getUserInquiries(String email) {
        return inquiryMapper.getUserInquiries(email);
    }

    // 모든 문의 조회 (관리자용)
    public List<Inquiry> getAllInquiries() {
        return inquiryMapper.getAllInquiries();
    }

    // 문의 등록
    public void insertInquiry(Inquiry inquiry) {
        inquiryMapper.insertInquiry(inquiry);
    }

    // 문의 답변 등록
    public void replyInquiry(Long id, String reply) {
        inquiryMapper.replyInquiry(id, reply);
    }

    // 문의 삭제
    public void deleteInquiry(Long id) {
        inquiryMapper.deleteInquiry(id);
    }
}