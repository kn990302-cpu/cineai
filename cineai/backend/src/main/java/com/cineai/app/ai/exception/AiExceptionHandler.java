package com.cineai.app.ai.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Bất kỳ lỗi nào trong AI Gateway (Gemini timeout, JSON không parse được, không
 * tìm được phim phù hợp...) đều rơi vào đây, trả về đúng field "error" mà
 * bên Android/Compose đã thiết kế để hiển thị bubble đỏ + nút Thử lại.
 */
@RestControllerAdvice
public class AiExceptionHandler {

    @ExceptionHandler(AiGatewayException.class)
    public ResponseEntity<Map<String, String>> handleAiGatewayException(AiGatewayException ex) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE) // 503
                .body(Map.of("error", ex.getMessage()));
    }
}
