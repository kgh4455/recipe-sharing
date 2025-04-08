package com.project.model;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class krhApplicationRequestVO {
	    private int clubId;             // 신청한 동호회 ID
	    private String applicantName;   // 신청자 이름
	    private String applicantEmail;  // 신청자 이메일
	    private int applicantAge;       // 신청자 나이
	    private String applicantGender; // 신청자 성별
	    private LocalDateTime applyDate;// 신청 날짜
	    private String recruiterEmail; //주최자 이메일
	    private boolean privacyAgreement; // 개인정보 동의 여부
}