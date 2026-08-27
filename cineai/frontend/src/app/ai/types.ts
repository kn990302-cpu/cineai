// ─── Chat message (giữ nguyên) ───────────────────────────────────────────────

export interface ChatMsg {
  id: string;
  role: "user" | "ai";
  text: string;
  recs?: AiMovie[];
  time: string;
  isError?: boolean; // true khi AI gateway lỗi -> hiển thị bubble đỏ
}

// ─── Dữ liệu phim dùng trong UI chatbot ──────────────────────────────────────
// Map từ DB của Thuận:
//   MOVIES.MOVIE_ID  -> id
//   MOVIES.TITLE     -> title
//   MOVIES.POSTER    -> poster
//   MOVIES.DURATION  -> duration  (phút)
//   MOVIES.RATING    -> rating
//   GENRES.NAME[]    -> genre  (join qua MOVIE_GENRES)

export interface AiMovie {
  id: string;
  title: string;
  poster: string;
  genre: string[];
  duration: number;
  rating: number;
}

// ─── Giờ chiếu ───────────────────────────────────────────────────────────────
// Map từ DB của Thuận:
//   SHOWTIMES.SHOWTIME_ID  -> id
//   SHOWTIMES.START_TIME   -> time  (backend format sẵn thành "HH:mm")
//   CINEMAS.NAME           -> cinema (join qua CINEMA_ROOMS)

export interface AiShowtime {
  id: string;
  time: string;
  cinema: string; // thêm mới để không hardcode "CGV Q.1" như trước
}

// ─── Kiểu JSON backend trả về cho POST /api/ai/chat ─────────────────────────
// Thuận + Khang (backend) cần đảm bảo response khớp đúng interface này
// để Đức bind thẳng vào Compose mà không cần đổi tên field.

export interface AiChatResponse {
  replyText: string;
  movies: AiMovieWithShowtimes[];
}

export interface AiMovieWithShowtimes extends AiMovie {
  showtimes: AiShowtime[];
}
