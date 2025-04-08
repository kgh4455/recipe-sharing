package com.project.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import com.project.config.CustomUserDetails;
import com.project.model.Notification;
import com.project.service.NotificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/notifications")  // ✅ 여기만 수정!
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // ✅ 관리자 알림 조회 (프론트에서 /api/notifications/unread 요청)
    @GetMapping("/unread")
    public ResponseEntity<List<Notification>> getUnreadAdminNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        String email = userDetails.getUsername();
        List<Notification> unread = notificationService.getUnreadAdminNotifications(email);
        return ResponseEntity.ok(unread);
    }

    // ✅ 알림 전송
    @PostMapping
    public ResponseEntity<String> sendNotification(@RequestBody Map<String, String> payload) {
        String receiverEmail = payload.get("receiverEmail");
        String message = payload.get("message");

        if (receiverEmail == null || receiverEmail.isEmpty()) {
            return ResponseEntity.badRequest().body("receiverEmail 값이 필요합니다.");
        }

        if (!message.startsWith("[관리자]")) {
            message = "[관리자] " + message;
        }

        notificationService.sendNotification(receiverEmail, message);
        return ResponseEntity.ok("알림이 성공적으로 전송되었습니다.");
    }

    // ✅ 알림 읽음 처리
    @PatchMapping("/read/{id}")
    public ResponseEntity<String> readNotification(@PathVariable Long id) {
        notificationService.markNotificationAsRead(id);
        return ResponseEntity.ok("Notification marked as read.");
    }

    // ✅ 알림 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteNotification(@PathVariable Long id) {
        notificationService.deleteNotification(id);
        return ResponseEntity.ok("Notification deleted.");
    }
}