package com.cineai.app.ai.controller;

import com.cineai.app.ai.dto.ChatRequestDto;
import com.cineai.app.ai.dto.ChatResponseDto;
import com.cineai.app.ai.service.AiService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoint chính mà Đức gọi từ ChatApi (Retrofit) trong app Android.
 * Khớp đúng với path đã note trong tài liệu đặc tả: POST /api/ai/chat
 */
@RestController
@RequestMapping("/api/ai")
@CrossOrigin(
    origins = {"http://localhost:5173", "http://127.0.0.1:5173", "http://localhost:5174", "http://127.0.0.1:5174", "http://localhost:3000", "http://127.0.0.1:3000"},
        allowedHeaders = "*",
        methods = {org.springframework.web.bind.annotation.RequestMethod.GET, org.springframework.web.bind.annotation.RequestMethod.POST, org.springframework.web.bind.annotation.RequestMethod.PUT, org.springframework.web.bind.annotation.RequestMethod.DELETE, org.springframework.web.bind.annotation.RequestMethod.OPTIONS}
)
public class AiController {

    private final AiService aiService;

    public AiController(AiService aiService) {
        this.aiService = aiService;
    }

    @PostMapping("/chat")
    public ChatResponseDto chat(@RequestBody ChatRequestDto request) {
        return aiService.chat(request.getMessage());
    }
}
