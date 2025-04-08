import axios from "axios";

const API_BASE_URL = "http://localhost:8080"; // 백엔드 API 주소

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    "Content-Type": "application/json",
  },
  withCredentials: true, // JWT 인증을 위한 쿠키 포함 (필요한 경우)
});

// ✅ 요청 인터셉터 (JWT 토큰 자동 포함)
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token"); // localStorage에서 토큰 가져오기
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
}, (error) => {
  return Promise.reject(error);
});

export default api;