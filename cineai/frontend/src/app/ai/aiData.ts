// Gợi ý nhanh hiển thị dưới ô nhập — người dùng bấm để điền sẵn vào input.
// Không cần gọi API, chỉ là shortcut UX.
export const QUICK_PROMPTS = [
  "Phim hot nhất",
  "Phim < 2 tiếng",
  "Phim lãng mạn",
  "Bom tấn hành động",
];

// AI_REPLIES mock đã bị xoá — thay bằng chatWithAi() trong api.ts
// gọi backend thật (POST /api/ai/chat -> Spring Boot -> Gemini -> Oracle DB).
