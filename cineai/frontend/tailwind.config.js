/** @type {import('tailwindcss').Config} */
export default {
	content: ["./index.html", "./src/**/*.{ts,tsx}"],
	theme: {
		extend: {
			colors: {
				background: "#101113",
				card: "#181a1f",
				secondary: "#202329",
				border: "#343840",
				foreground: "#f5f5f4",
				"muted-foreground": "#a1a1aa",
				primary: "#f6c453"
			}
		}
	},
	plugins: []
};
