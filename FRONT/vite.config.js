import { fileURLToPath, URL } from "node:url";
import { defineConfig } from "vite";
import vue from "@vitejs/plugin-vue";
import vueDevTools from "vite-plugin-vue-devtools"; // 'vue-'를 'vite-'로 수정

export default defineConfig({
  plugins: [vue(), vueDevTools()],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
  // 개발 서버 설정 (npm run dev)
  server: {
    port: 5173,
    allowedHosts: [".ngrok-free.app"],
    proxy: {
      // 모든 API 요청은 Spring Boot를 통해 처리
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
      },
      "/uploads": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
      },
    },
  },
  // 프리뷰 서버 설정 (npm run preview)
  preview: {
    port: 4173,
    proxy: {
      // 모든 API 요청은 Spring Boot를 통해 처리
      "/api": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
      },
      "/uploads": {
        target: "http://localhost:8080",
        changeOrigin: true,
        secure: false,
      },
    },
  },
});
