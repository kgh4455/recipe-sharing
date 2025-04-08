package com.project.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.mapper.krhNotificationMapper;
import com.project.model.Notification;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class krhNotificationServiceImpl implements krhNotificationService {
	
	@Autowired
	private krhNotificationMapper krhnotificationMapper;
	
	@Override
	public void insertNotification(Notification notification) {
		// TODO Auto-generated method stub
		krhnotificationMapper.insertNotification(notification);
	}

}
