package com.project.mapper;

import com.project.model.Inquiry;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface InquiryMapper {

    // 특정 회원의 문의 조회
    @Select("SELECT * FROM inquiries WHERE user_email = #{email}")
    List<Inquiry> getUserInquiries(String email);

    // 모든 문의 조회 (관리자용)
    @Select("SELECT * FROM inquiries")
    List<Inquiry> getAllInquiries();

    // 특정 문의 조회 (ID 기반)
    @Select("SELECT * FROM inquiries WHERE id = #{id}")
    Inquiry findById(Long id);

    // 문의 등록
    @Insert("INSERT INTO inquiries(user_email, title, content) VALUES(#{email}, #{title}, #{content})")
    void insertInquiry(Inquiry inquiry);

    // 문의 답변 등록
    @Update("UPDATE inquiries SET reply=#{reply} WHERE id=#{id}")
    void replyInquiry(@Param("id") Long id, @Param("reply") String reply);

    // 🔹 특정 문의 삭제 (email 확인 불필요)
    @Delete("DELETE FROM inquiries WHERE id=#{id}")
    void deleteInquiry(@Param("id") Long id);
}