import { useEffect, useState, useRef } from 'react';
import axios from 'axios';
import stompClient from '../utils/websocket';

function ChatBox({ roomId, sender, userInfo, senderRole }) {
  const [input, setInput] = useState('');
  const [messages, setMessages] = useState([]);
  const [isConnected, setIsConnected] = useState(false);
  const endRef = useRef(null);
  const subscriptionRef = useRef(null);

  // ✅ 1. 이전 메시지 불러오기
  useEffect(() => {
    if (!roomId || sender == null) return;
    const token = localStorage.getItem('token');
    if (!token) return;

    axios
      .get(`/api/chat/rooms/${roomId}/messages`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      .then((res) => {
        const sorted = [...res.data].sort(
          (a, b) => new Date(a.sentAt) - new Date(b.sentAt)
        );
        setMessages(sorted);
      })
      .catch((err) => {
        console.error('❌ 이전 메시지 불러오기 실패:', err);
      });
  }, [roomId]);

  // ✅ 2. WebSocket 연결
  useEffect(() => {
    if (!roomId || sender == null) return;

    const token = localStorage.getItem('token');
    if (!token) return;

    if (!stompClient.onConnect) {
      stompClient.onConnect = () => {
        setIsConnected(true);
        if (!subscriptionRef.current) {
          subscriptionRef.current = stompClient.subscribe(
            `/topic/chat/${roomId}`,
            (msg) => {
              const data = JSON.parse(msg.body);
              setMessages((prev) => [...prev, data]);
            }
          );
        }
      };

      stompClient.onDisconnect = () => setIsConnected(false);
      stompClient.onStompError = (frame) => console.error('❌ STOMP 오류:', frame);
      stompClient.onWebSocketError = (err) => console.error('❌ WebSocket 오류:', err);
    }

    stompClient.connectHeaders = {
      Authorization: 'Bearer ' + token,
    };

    if (!stompClient.connected && !stompClient.active) {
      stompClient.activate();
    } else if (stompClient.connected && !subscriptionRef.current) {
      subscriptionRef.current = stompClient.subscribe(
        `/topic/chat/${roomId}`,
        (msg) => {
          const data = JSON.parse(msg.body);
          setMessages((prev) => [...prev, data]);
        }
      );
      setIsConnected(true);
    }

    return () => {
      if (subscriptionRef.current) {
        subscriptionRef.current.unsubscribe();
        subscriptionRef.current = null;
      }
      setIsConnected(false);
    };
  }, [roomId, sender]);

  // ✅ 자동 스크롤
  useEffect(() => {
    endRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  // ✅ 메시지 전송
  const handleSend = () => {
    if (!input.trim()) return;
    if (!isConnected) return alert('❌ 연결되지 않음');

    const message = {
      roomId,
      senderId: Number(sender),
      message: input,
      sentAt: new Date().toISOString(),
    };

    stompClient.publish({
      destination: '/app/chat.sendMessage',
      body: JSON.stringify(message),
    });

    setInput('');
  };

  return (
    <div style={{ border: '1px solid #ccc', padding: 10, borderRadius: 10, width: '100%' }}>
      <div style={{ height: 300, overflowY: 'auto', marginBottom: 10 }}>
        {messages.map((msg, i) => {
          const isMine = String(msg.senderId) === String(sender);
          let displayName = '';

          if (isMine) {
            displayName = '나';
          } else {
            displayName = senderRole === 'ROLE_ADMIN'
              ? userInfo?.name || '유저'
              : '관리자';
          }

          return (
            <div key={i} style={{ textAlign: isMine ? 'right' : 'left', marginBottom: 10 }}>
              <div style={{ fontSize: 12, fontWeight: 'bold', color: '#555' }}>{displayName}</div>

              <div
                style={{
                  display: 'inline-block',
                  backgroundColor: isMine ? '#FFA575' : '#f1f0f0',
                  padding: '8px 12px',
                  borderRadius: 10,
                  maxWidth: '70%',
                }}
              >
                {msg.message}
              </div>

              <div style={{ fontSize: 10, marginTop: 3 }}>
                {new Date(msg.sentAt).toLocaleTimeString()}
              </div>
            </div>
          );
        })}
        <div ref={endRef} />
      </div>

      <div style={{ display: 'flex', gap: 10 }}>
        <input
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="메시지를 입력하세요"
            className="input-field-ur"
          style={{ flex: 1, padding: 10 }}
        />
        <button
          onClick={handleSend}
          disabled={!isConnected}
          style={{ marginBottom:"20px",background: isConnected ? '#FFA575' : '#aaa', color: '#fff' }}
        >
          전송
        </button>
      </div>
    </div>
  );
}

export default ChatBox;