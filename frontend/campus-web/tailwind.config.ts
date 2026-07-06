import type { Config } from "tailwindcss";

const config: Config = {
  content: ["./app/**/*.{js,ts,jsx,tsx}", "./components/**/*.{js,ts,jsx,tsx}", "./lib/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        ink: "#16202A",
        mint: "#2BB7A8",
        coral: "#F26A4F",
        sun: "#F5B84B",
        cloud: "#F6F8FB"
      }
    }
  },
  plugins: []
};

export default config;
