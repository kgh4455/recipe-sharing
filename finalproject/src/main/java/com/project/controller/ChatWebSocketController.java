package com.project.controller;

import com.project.model.ChatMessage;
import com.project.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

//ChatWebSocketController.java
import com.project.model.User;
import com.project.mapper.UserMapper; // ✅ 추가

@RequiredArgsConstructor
@Controller
public class ChatWebSocketController {

 private final SimpMessagingTemplate messagingTemplate;
 private final ChatMessageService chatMessageService;
 private final UserMapper userMapper; // ✅ 주입

 @MessageMapping("/chat.sendMessage")
 public void sendMessage(@Payload ChatMessage message) {
     // ✅ senderId로 유저 정보 조회
     User sender = userMapper.findUserById(message.getSenderId());
     if (sender != null) {
         message.setSenderName(sender.getName()); // ✅ 이름 설정
     }

     // 1. DB 저장
     chatMessageService.saveMessage(message);

     // 2. WebSocket 브로드캐스트
     messagingTemplate.convertAndSend("/topic/chat/" + message.getRoomId(), message);
     // ✅ 알림도 같이 보내야 함 (예시로 notify/chat 사용)
     messagingTemplate.convertAndSend("/topic/notify/chat", message); 
 }
}

