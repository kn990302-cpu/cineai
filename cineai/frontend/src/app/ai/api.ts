import type { AiChatResponse } from "./types";

// ─── Cấu hình ─────────────────────────────────────────────────────────────────
// Khi chạy emulator Android: đổi thành "http://10.0.2.2:8080"
// Khi chạy prototype web: "http://localhost:8080"
const BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "http://localhost:8080";

// ─── Gọi AI Chat ──────────────────────────────────────────────────────────────
// Thay thế hoàn toàn phần mock AI_REPLIES + setTimeout trong aiData.ts cũ.
// Backend (Spring Boot của Thuận + Khang) expose endpoint:
//   POST /api/ai/chat
//   Body: { "message": "câu hỏi của người dùng" }
//   Response: AiChatResponse (xem types.ts)

export async function chatWithAi(message: string): Promise<AiChatResponse> {
  try {
    const res = await fetch(`${BASE_URL}/api/ai/chat`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ message }),
    });

    if (!res.ok) {
      // Backend trả 503 khi Gemini lỗi, 400 khi input rỗng...
      // Lấy field "error" từ body nếu có, fallback về text chung.
      let errMsg = "Trợ lý AI tạm thời không phản hồi, vui lòng thử lại.";
      try {
        const body = await res.json();
        if (body?.error) errMsg = body.error;
      } catch {
        // ignore parse error
      }
      throw new Error(errMsg);
    }

    return res.json() as Promise<AiChatResponse>;
  } catch (error) {
    if (error instanceof TypeError) {
      throw new Error("Không thể kết nối tới backend AI. Hãy đảm bảo server đang chạy và CORS đã được bật.");
    }
    throw error;
  }
}
