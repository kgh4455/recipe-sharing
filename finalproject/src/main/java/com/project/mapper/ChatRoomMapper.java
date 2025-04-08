package com.project.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import com.project.model.ChatRoom;

@Mapper
public interface ChatRoomMapper {

    // ✅ 유저와 관리자 간 채팅방 조회
    @Select("SELECT * FROM chat_rooms WHERE user_id = #{userId} AND admin_id = #{adminId} LIMIT 1")
    ChatRoom findByUserAndAdmin(@Param("userId") Long userId, @Param("adminId") Long adminId);

    // ✅ 채팅방 생성
    @Insert("INSERT INTO chat_rooms (user_id, admin_id) VALUES (#{userId}, #{adminId})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void createChatRoom(ChatRoom chatRoom);

    // ✅ 모든 채팅방 조회 (관리자용)
    @Select("SELECT * FROM chat_rooms")
    List<ChatRoom> findAllRooms();

    // ✅ 유저 전용 채팅방 조회
    @Select("SELECT * FROM chat_rooms WHERE user_id = #{userId}")
    ChatRoom findByUser(Long userId);

    // ✅ 유저 탈퇴 시 채팅방 삭제
    @Delete("DELETE FROM chat_rooms WHERE user_id = #{userId}")
    void deleteByUser(Long userId);
 // ✅ 관리자 채팅방 목록 + 유저 정보 포함 조회
    @Select("""
        SELECT 
            cr.id AS id,
            cr.user_id AS userId,
            cr.admin_id AS adminId,
            cr.created_at AS createdAt,
            u.name AS userName,
            u.email AS userEmail,
            u.profile_image AS profileImageUrl
        FROM chat_rooms cr
        JOIN users u ON cr.user_id = u.id
        WHERE cr.admin_id = #{adminId}
    """)
    List<ChatRoom> findAllRoomsWithUserInfo(@Param("adminId") Long adminId);

    // ✅ 마지막 메시지 조회
    @Select("""
        SELECT message
        FROM chat_messages
        WHERE room_id = #{roomId}
        ORDER BY sent_at DESC
        LIMIT 1
    """)
    String findLastMessageByRoomId(@Param("roomId") Long roomId);

    // ✅ 안 읽은 메시지 수 조회
    @Select("""
        SELECT COUNT(*)
        FROM chat_messages
        WHERE room_id = #{roomId}
          AND sender_id != #{adminEmail}
          AND is_read = false
    """)
    int countUnreadMessages(@Param("roomId") Long roomId, @Param("adminEmail") String adminEmail);
    
    @Select("SELECT * FROM chat_rooms WHERE id = #{id}")
    ChatRoom findChatRoomById(Long id);  // 이름 바꿈

}
