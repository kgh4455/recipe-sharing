package com.project.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.project.model.Notification;

@Mapper
public interface krhNotificationMapper {
	void insertNotification(Notification notification);
}
