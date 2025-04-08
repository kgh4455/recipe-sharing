package com.project.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ChatMessage {
    private Long id;
    private Long roomId;
    private Long senderId;
    private String senderName;
    private String message;
    private Boolean isRead;
    private LocalDateTime sentAt;
}