package com.cineai.app.ai.service;

import com.cineai.app.ai.exception.AiGatewayException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Gọi thẳng REST endpoint của Gemini (generateContent), không dùng SDK riêng
 * để hạn chế thêm dependency vào project Spring Boot đang có của Thuận.
 *
 * Tài liệu chính thức có thể đổi field/model theo thời gian - trước khi dùng
 * thật, kiểm tra lại tại: https://ai.google.dev/gemini-api/docs
 */
@Component
public class GeminiClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model:gemini-2.0-flash}")
    private String model;

    public GeminiClient(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Gửi prompt cho Gemini, ép trả về JSON thuần (responseMimeType application/json),
     * trả lại đúng chuỗi JSON text bên trong response - AiService sẽ parse tiếp
     * thành GeminiRawResponseDto.
     */
    public String generateJson(String prompt) {
        String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                + model + ":generateContent?key=" + apiKey;

        Map<String, Object> body = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{Map.of("text", prompt)})
                },
                "generationConfig", Map.of(
                        "responseMimeType", "application/json",
                        "temperature", 0.4
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(
                    url, new HttpEntity<>(body, headers), String.class);

            JsonNode root = objectMapper.readTree(response.getBody());
            JsonNode textNode = root
                    .path("candidates").path(0)
                    .path("content").path("parts").path(0)
                    .path("text");

            if (textNode.isMissingNode() || textNode.asText().isBlank()) {
                throw new AiGatewayException("Gemini không trả về nội dung hợp lệ.");
            }
            return textNode.asText();

        } catch (RestClientException e) {
            String message = "Không kết nối được tới trợ lý AI, vui lòng thử lại.";
            if (e instanceof HttpClientErrorException.Unauthorized || e instanceof HttpClientErrorException.BadRequest) {
                message = "API key Gemini không hợp lệ hoặc đã hết hạn. Vui lòng kiểm tra lại key trong file application-local.properties.";
            } else if (e instanceof HttpClientErrorException.TooManyRequests) {
                message = "Gemini đang bị giới hạn số request. Hãy thử lại sau vài giây.";
            }
            throw new AiGatewayException(message, e);
        } catch (Exception e) {
            throw new AiGatewayException("Lỗi xử lý phản hồi từ trợ lý AI.", e);
        }
    }
}
