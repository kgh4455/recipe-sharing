package com.project.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.project.mapper.NotificationMapper;
import com.project.model.Notification;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationMapper notificationMapper;

    // 🔹 알림 전송
    public void sendNotification(String email, String message) {
        Notification notification = new Notification();
        notification.setReceiverEmail(email);
        notification.setMessage(message);
        notification.setIsRead(false);
        notification.setCreatedAt(LocalDateTime.now());

        notificationMapper.sendNotification(notification);
    }

    // 🔹 안 읽은 관리자 알림만 가져오기 (이메일 기반)
    public List<Notification> getUnreadAdminNotifications(String email) {
        return notificationMapper.selectUnreadAdminNotifications(email);
    }

    // 🔹 알림 읽음 처리
    public void markNotificationAsRead(Long notificationId) {
        notificationMapper.markNotificationAsRead(notificationId);
    }

    // 🔹 알림 삭제
    public void deleteNotification(Long notificationId) {
        notificationMapper.deleteNotification(notificationId);
    }
    
} 