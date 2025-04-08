import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // 💥 스프링 서버 주소
        changeOrigin: true,
        secure: false,
      },
    },
  },
  define: {
    global: {},         // 🔧 global 변수 정의
    'process.env': {},  // (필요한 경우) process.env도 빈 값으로 설정
  },
});
