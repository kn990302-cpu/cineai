import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import { AiChatScreen } from "./app/ai/AiChatScreen";
import "./styles.css";

createRoot(document.getElementById("root")!).render(
	<StrictMode>
		<AiChatScreen onMovieSelect={() => undefined} />
	</StrictMode>,
);
