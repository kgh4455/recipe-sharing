import { useState, useRef, useEffect } from "react";
import "./Chatbot.css";  // CSS 파일 임포트
import ChatbotButton from "./ChatbotButton"; // 챗봇 버튼 컴포넌트 임포트
import { BsChatDotsFill } from "react-icons/bs";
import { PiRabbitFill } from "react-icons/pi";

const Chatbot = () => {
  const [messages, setMessages] = useState([]);
  const [userMessage, setUserMessage] = useState("");
  const [isOpen, setIsOpen] = useState(false);
  const chatBoxRef = useRef(null);

  // 메시지 전송 함수
  const sendChat = async () => {
    if (userMessage.trim() === "") return;

    // 사용자 메시지 추가
    const newMessages = [...messages, { type: "user", text: userMessage }];
    setMessages(newMessages);
    setUserMessage("");

    try {
      const response = await fetch("http://localhost:8080/chatbot/ask", {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ message: userMessage }),
      });

      const botResponse = await response.text();
      setMessages([...newMessages, { type: "bot", text: botResponse }]);
    } catch (error) {
      alert("서버 요청 실패!");
    }
  };

  // 엔터 키 입력 처리
  const handleKeyPress = (event) => {
    if (event.key === "Enter") {
      event.preventDefault();
      sendChat();
    }
  };

  // 채팅 스크롤을 가장 아래로 이동
  useEffect(() => {
    if (chatBoxRef.current) {
      chatBoxRef.current.scrollTop = chatBoxRef.current.scrollHeight;
    }
  }, [messages]);

  // 챗봇 열기/닫기
  const toggleChatbot = () => {
    setIsOpen(!isOpen);
  };

  return (
    <div>
      {/* 챗봇 버튼 */}
      <ChatbotButton onClick={toggleChatbot} />

      {/* 챗봇 창 */}
      {isOpen && (
        <div className="chatbot-container">
          <div className="chatbot-header">
            <p className="mb-1"><BsChatDotsFill /><PiRabbitFill /> 톡톡이와 대화하기</p>
          </div>
          <div
            ref={chatBoxRef}
            className="chatbot-body"
          >
            {messages.map((msg, index) => (
              <div
                key={index}
                className={`chatbot-message ${
                  msg.type === "user" ? "chatbot-message-user" : "chatbot-message-bot"
                }`}
              >
                {msg.text}
              </div>
            ))}
          </div>
          <div className="chatbot-footer">
            <input
              type="text"
              className="form-control"
              placeholder="메시지를 입력하세요..."
              value={userMessage}
              onChange={(e) => setUserMessage(e.target.value)}
              onKeyPress={handleKeyPress}
              style={{fontFamily:"NEXON Lv1 Gothic OTF"}}
            />
            <button className="chattbtn" onClick={sendChat}>
              전송
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Chatbot;
