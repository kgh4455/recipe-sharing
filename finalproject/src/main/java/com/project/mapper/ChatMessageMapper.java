package com.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.project.model.ChatMessage;

@Mapper
public interface ChatMessageMapper {

	 // ✅ 메시지 저장
    @Insert("INSERT INTO chat_messages (room_id, sender_id, message, sent_at, is_read) " +
            "VALUES (#{roomId}, #{senderId}, #{message}, #{sentAt}, #{isRead})")
    void insertMessage(ChatMessage message);

    // ✅ 채팅방의 메시지 조회
    @Select("SELECT * FROM chat_messages WHERE room_id = #{roomId} ORDER BY sent_at ASC")
    List<ChatMessage> findByRoomId(Long roomId);

    // ✅ 메시지 읽음 처리
    @Update("UPDATE chat_messages SET is_read = true WHERE room_id = #{roomId} AND sender_id != #{userId}")
    void markAsRead(@Param("roomId") Long roomId, @Param("userId") Long userId);
}