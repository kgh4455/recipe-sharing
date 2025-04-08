package com.project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.mapper.ChatMessageMapper;
import com.project.model.ChatMessage;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageMapper chatMessageMapper;

    // 메시지 저장
    public void saveMessage(ChatMessage message) {
        chatMessageMapper.insertMessage(message);
    }

    // 채팅방의 메시지 모두 조회
    public List<ChatMessage> getMessagesByRoomId(Long roomId) {
        return chatMessageMapper.findByRoomId(roomId);
    }

    // 메시지 읽음 처리
    public void markMessagesAsRead(Long roomId, Long userId) {
        chatMessageMapper.markAsRead(roomId, userId);
    }
}