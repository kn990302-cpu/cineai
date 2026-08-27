package com.cineai.app.ai.dto;

/**
 * Request body cho POST /api/ai/chat
 * Android (Đức) gửi lên đúng field "message".
 */
public class ChatRequestDto {

    private String message;

    public ChatRequestDto() {
    }

    public ChatRequestDto(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
