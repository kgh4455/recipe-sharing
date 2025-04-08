package com.project.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.mapper.ChatRoomMapper;
import com.project.mapper.UserMapper;
import com.project.model.ChatRoom;
import com.project.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomMapper chatRoomMapper;
    private final UserMapper userMapper;

    // ✅ 유저와 관리자 간 채팅방 가져오기 (없으면 생성)
    public ChatRoom getOrCreateChatRoom(Long userId) {
        // 🔍 관리자 계정 가져오기
        List<User> adminList = userMapper.findAllAdmins();
        if (adminList.isEmpty()) {
            throw new RuntimeException("⚠️ 관리자 계정이 없습니다.");
        }
        User admin = adminList.get(0); // 첫 번째 관리자 선택

        // 🔍 기존 채팅방 조회
        ChatRoom chatRoom = chatRoomMapper.findByUserAndAdmin(userId, admin.getId());

        // ❌ 없으면 생성
        if (chatRoom == null) {
            chatRoom = new ChatRoom();
            chatRoom.setUserId(userId);
            chatRoom.setAdminId(admin.getId());
            chatRoomMapper.createChatRoom(chatRoom);
        }

        // ✅ 유저 정보 추가 (프론트에서 필요)
        User user = userMapper.findUserById(userId);

        if (user != null) {
            chatRoom.setUserName(user.getName());
            chatRoom.setUserEmail(user.getEmail());
            chatRoom.setProfileImageUrl(user.getProfileImage());
        }

        return chatRoom;
    }
 // ChatRoomService.java
    public User getUserInfoByRoomId(Long roomId) {
        ChatRoom room = chatRoomMapper.findChatRoomById(roomId);
        if (room == null) {
            throw new RuntimeException("채팅방이 존재하지 않습니다.");
        }

        Long userId = room.getUserId();
        User user = userMapper.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("해당 유저가 존재하지 않습니다.");
        }

        return user;
    }

    // ✅ 관리자용: 유저 + 채팅방 목록 + 최근 메시지 + 안 읽은 메시지 수
    public List<ChatRoom> getChatRoomsWithDetails(Long adminId, String adminEmail) {
        List<ChatRoom> rooms = chatRoomMapper.findAllRoomsWithUserInfo(adminId);

        for (ChatRoom room : rooms) {
            room.setLastMessage(chatRoomMapper.findLastMessageByRoomId(room.getId()));
            room.setUnreadCount(chatRoomMapper.countUnreadMessages(room.getId(), adminEmail));
        }

        return rooms;
    }

    // ✅ 관리자: 전체 채팅방 리스트 (단순 목록)
    public List<ChatRoom> getAllRoomsForAdmin() {
        return chatRoomMapper.findAllRooms();
    }

    // ✅ 유저: 내 채팅방 조회 (1개만 반환)
    public ChatRoom getRoomForUser(Long userId) {
        return chatRoomMapper.findByUser(userId);
    }

    // ✅ 유저 탈퇴 시 채팅방 삭제
    public void deleteChatRoomByUser(Long userId) {
        chatRoomMapper.deleteByUser(userId);
    }
}