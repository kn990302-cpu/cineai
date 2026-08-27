import { useState, useEffect, useRef } from "react";
import { motion } from "motion/react";
import { Sparkles, Send, Star, Clock, AlertCircle, RotateCcw } from "lucide-react";
import { QUICK_PROMPTS } from "./aiData";
import { chatWithAi } from "./api";
import type { ChatMsg, AiMovie, AiMovieWithShowtimes, AiShowtime } from "./types";

// ─── LOCAL HELPERS ─────────────────────────────────────────────────────────────

function StarRating({ r }: { r: number }) {
  return (
    <span className="flex items-center gap-1">
      <Star className="w-3 h-3 fill-amber-400 text-amber-400" />
      <span className="text-amber-400 text-xs font-semibold">{r.toFixed(1)}</span>
    </span>
  );
}

function formatDuration(m: number) {
  return `${Math.floor(m / 60)}g ${m % 60}p`;
}

function getTime() {
  return new Date().toLocaleTimeString("vi", { hour: "2-digit", minute: "2-digit" });
}

// ─── MOVIE RECOMMENDATION CARD ────────────────────────────────────────────────

function MovieRecommendationCard({
  movie,
  onSelect,
  onQuickBook,
}: {
  movie: AiMovieWithShowtimes;
  onSelect: (m: AiMovie) => void;
  onQuickBook?: (m: AiMovie, s: AiShowtime) => void;
}) {
  return (
    <div className="bg-card border border-border rounded-2xl overflow-hidden w-full">
      <div className="flex gap-3 p-3">
        <img
          src={movie.poster}
          alt={movie.title}
          className="w-14 h-20 object-cover rounded-xl flex-none bg-zinc-900"
        />
        <div className="flex-1 min-w-0">
          <h4
            className="text-foreground font-bold text-xs leading-snug line-clamp-2 mb-1"
            style={{ fontFamily: "'Playfair Display', serif" }}
          >
            {movie.title}
          </h4>
          <div className="flex items-center gap-2 mb-2">
            <StarRating r={movie.rating} />
            <span className="text-muted-foreground text-[10px] flex items-center gap-1">
              <Clock className="w-2.5 h-2.5" /> {formatDuration(movie.duration)}
            </span>
          </div>
          <div className="bg-primary/10 border border-primary/15 rounded-lg p-2 mb-2">
            <p className="text-primary text-[10px] leading-snug">
              🎯 Phù hợp với sở thích {movie.genre[0]?.toLowerCase() ?? "phim"} của bạn
            </p>
          </div>
          {/* Nút này -> màn Movie Detail (xem thông tin đầy đủ) */}
          <button
            onClick={() => onSelect(movie)}
            className="w-full bg-primary text-black text-[11px] font-bold py-1.5 rounded-xl active:scale-95 transition-all"
          >
            Xem chi tiết phim
          </button>
        </div>
      </div>

      {/* Chip giờ chiếu nhanh: bấm vào -> nhảy thẳng Seat Map, không qua Movie Detail */}
      {movie.showtimes.length > 0 && (
        <div className="px-3 pb-3 flex gap-2 overflow-x-auto scrollbar-hide">
          {movie.showtimes.slice(0, 3).map(s => (
            <button
              key={s.id}
              onClick={() => onQuickBook?.(movie, s)}
              className="flex-none bg-secondary border border-border text-[10px] font-semibold text-foreground px-2.5 py-1.5 rounded-lg whitespace-nowrap hover:border-primary/40 transition-colors"
            >
              {/* cinema lấy từ DB thật (Thuận join CINEMAS), không hardcode nữa */}
              {s.time} · {s.cinema}
            </button>
          ))}
        </div>
      )}
    </div>
  );
}

// ─── TYPING INDICATOR ─────────────────────────────────────────────────────────

function TypingIndicator() {
  return (
    <div className="flex gap-2 justify-start">
      <div className="w-7 h-7 rounded-full bg-primary/15 border border-primary/25 flex items-center justify-center flex-none self-end">
        <Sparkles className="w-3 h-3 text-primary" />
      </div>
      <div className="bg-secondary border border-border rounded-2xl rounded-bl-sm px-4 py-3">
        <div className="flex gap-1.5 items-center">
          {[0, 1, 2].map(i => (
            <motion.div
              key={i}
              className="w-2 h-2 rounded-full bg-primary/50"
              animate={{ scale: [1, 1.5, 1], opacity: [0.3, 1, 0.3] }}
              transition={{ repeat: Infinity, duration: 1, delay: i * 0.22 }}
            />
          ))}
        </div>
      </div>
    </div>
  );
}

// ─── MESSAGE BUBBLE ───────────────────────────────────────────────────────────

function MessageBubble({
  msg,
  onMovieSelect,
  onQuickBook,
  onRetry,
}: {
  msg: ChatMsg;
  onMovieSelect: (m: AiMovie) => void;
  onQuickBook?: (m: AiMovie, s: AiShowtime) => void;
  onRetry: () => void;
}) {
  const isUser = msg.role === "user";
  return (
    <div className={`flex gap-2 ${isUser ? "justify-end" : "justify-start"}`}>
      {!isUser && (
        <div className="w-7 h-7 rounded-full bg-primary/15 border border-primary/25 flex items-center justify-center flex-none self-end">
          <Sparkles className="w-3 h-3 text-primary" />
        </div>
      )}
      <div className={`max-w-[82%] flex flex-col gap-2 ${isUser ? "items-end" : "items-start"}`}>

        {/* Bubble chính */}
        <div className={`rounded-2xl px-4 py-3 ${
          isUser
            ? "bg-primary text-black rounded-br-sm"
            : msg.isError
              ? "bg-red-500/10 border border-red-500/30 rounded-bl-sm"
              : "bg-secondary border border-border rounded-bl-sm"
        }`}>
          {msg.isError ? (
            <div className="flex items-start gap-2">
              <AlertCircle className="w-4 h-4 text-red-400 flex-none mt-0.5" />
              <p className="text-sm leading-relaxed text-foreground">{msg.text}</p>
            </div>
          ) : (
            <p className={`text-sm leading-relaxed ${isUser ? "text-black" : "text-foreground"}`}>
              {msg.text}
            </p>
          )}
        </div>

        {/* Nút Thử lại - chỉ hiện khi lỗi */}
        {msg.isError && (
          <button
            onClick={onRetry}
            className="flex items-center gap-1.5 text-red-400 text-xs font-semibold px-3 py-1.5 rounded-full border border-red-400/30 hover:bg-red-400/10 active:scale-95 transition-all"
          >
            <RotateCcw className="w-3 h-3" /> Thử lại
          </button>
        )}

        {/* Card phim gợi ý */}
        {msg.recs?.map(mv => (
          <MovieRecommendationCard
            key={mv.id}
            movie={mv as AiMovieWithShowtimes}
            onSelect={onMovieSelect}
            onQuickBook={onQuickBook}
          />
        ))}

        <span className="text-muted-foreground text-[10px]">{msg.time}</span>
      </div>
    </div>
  );
}

// ─── AI CHAT SCREEN ───────────────────────────────────────────────────────────

interface AiChatScreenProps {
  onMovieSelect: (m: AiMovie) => void;
  onQuickBook?: (m: AiMovie, s: AiShowtime) => void;
}

export function AiChatScreen({ onMovieSelect, onQuickBook }: AiChatScreenProps) {
  const [msgs, setMsgs] = useState<ChatMsg[]>([
    {
      id: "0",
      role: "ai",
      text: "Xin chào! Tôi là CineAI 🎬 Trợ lý điện ảnh thông minh. Hãy cho tôi biết bạn muốn xem thể loại nào, hoặc tâm trạng hôm nay — tôi sẽ gợi ý phim hoàn hảo nhất!",
      time: "09:00",
    },
  ]);
  const [input, setInput] = useState("");
  const [thinking, setThinking] = useState(false);
  const [lastQuery, setLastQuery] = useState<string | null>(null);
  const listRef = useRef<HTMLDivElement>(null);

  // Gọi API thật, xử lý cả Success và Error
  const requestAiReply = async (query: string) => {
    setThinking(true);
    setLastQuery(query);
    try {
      const data = await chatWithAi(query);
      setMsgs(prev => [...prev, {
        id: (Date.now() + 1).toString(),
        role: "ai",
        text: data.replyText,
        recs: data.movies,
        time: getTime(),
      }]);
    } catch (err) {
      const errMsg = err instanceof Error
        ? err.message
        : "Trợ lý AI tạm thời không phản hồi, vui lòng thử lại.";
      setMsgs(prev => [...prev, {
        id: (Date.now() + 1).toString(),
        role: "ai",
        text: errMsg,
        time: getTime(),
        isError: true,
      }]);
    } finally {
      setThinking(false);
    }
  };

  const send = () => {
    if (!input.trim() || thinking) return;
    const text = input;
    setMsgs(prev => [...prev, {
      id: Date.now().toString(),
      role: "user",
      text,
      time: getTime(),
    }]);
    setInput("");
    requestAiReply(text);
  };

  // Thử lại câu hỏi gần nhất mà không cần gõ lại
  const retry = () => {
    if (!lastQuery || thinking) return;
    requestAiReply(lastQuery);
  };

  useEffect(() => {
    listRef.current?.scrollTo({ top: listRef.current.scrollHeight, behavior: "smooth" });
  }, [msgs, thinking]);

  return (
    <div className="flex-1 flex flex-col bg-background overflow-hidden">
      {/* Header */}
      <div className="flex items-center gap-3 px-5 pt-12 pb-4 border-b border-border flex-none">
        <div className="w-10 h-10 rounded-full bg-primary/15 border border-primary/25 flex items-center justify-center">
          <Sparkles className="w-5 h-5 text-primary" />
        </div>
        <div>
          <h2 className="text-foreground font-bold text-base">CineAI Trợ lý</h2>
          <p className="flex items-center gap-1 text-xs text-green-400">
            <span className="w-1.5 h-1.5 rounded-full bg-green-400 inline-block" />
            Đang hoạt động
          </p>
        </div>
      </div>

      {/* Message list */}
      <div ref={listRef} className="flex-1 overflow-y-auto px-4 py-4 space-y-4 scrollbar-hide">
        {msgs.map(m => (
          <MessageBubble
            key={m.id}
            msg={m}
            onMovieSelect={onMovieSelect}
            onQuickBook={onQuickBook}
            onRetry={retry}
          />
        ))}
        {thinking && <TypingIndicator />}
      </div>

      {/* Input area */}
      <div className="flex-none border-t border-border bg-card px-4 pt-3 pb-4">
        <div className="flex gap-2 overflow-x-auto scrollbar-hide mb-3">
          {QUICK_PROMPTS.map(p => (
            <button
              key={p}
              onClick={() => setInput(p)}
              className="flex-none bg-secondary border border-border text-muted-foreground text-[11px] px-3 py-1.5 rounded-full whitespace-nowrap hover:border-primary/40 hover:text-foreground transition-colors"
            >
              {p}
            </button>
          ))}
        </div>
        <div className="flex items-end gap-2">
          <textarea
            value={input}
            onChange={e => setInput(e.target.value)}
            onKeyDown={e => {
              if (e.key === "Enter" && !e.shiftKey) {
                e.preventDefault();
                send();
              }
            }}
            placeholder="Nhập tin nhắn..."
            rows={1}
            className="flex-1 bg-secondary border border-border rounded-2xl px-4 py-2.5 text-foreground text-sm outline-none focus:border-primary/50 transition-colors resize-none placeholder:text-muted-foreground"
            style={{ minHeight: 42, maxHeight: 112 }}
          />
          <button
            onClick={send}
            disabled={!input.trim() || thinking}
            className="w-11 h-11 rounded-full bg-primary flex items-center justify-center flex-none disabled:opacity-30 hover:bg-primary/90 active:scale-90 transition-all"
          >
            <Send className="w-4 h-4 text-black" />
          </button>
        </div>
      </div>
    </div>
  );
}
