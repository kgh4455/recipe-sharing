// 📁 utils/websocket.js
import { Client } from '@stomp/stompjs';

const token = localStorage.getItem('token');

const stompClient = new Client({
  brokerURL: `ws://localhost:8080/ws?token=${token}`, // ✅ 진짜 WebSocket 사용
  reconnectDelay: 5000,
  debug: (str) => console.log('[STOMP DEBUG]:', str),
  onStompError: (frame) => console.error('❌ STOMP 오류 발생:', frame),
  onWebSocketError: (e) => console.error('❌ WebSocket 오류 발생: ', e),
});

export default stompClient;