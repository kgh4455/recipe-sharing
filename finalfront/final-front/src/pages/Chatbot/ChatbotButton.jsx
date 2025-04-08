import './ChatbotButton.css';  // CSS 파일 임포트
import Rabbit from "../../assets/rabbit.png";  // 이미지 경로 수정

const ChatbotButton = ({ onClick }) => {
  return (
    <button className="chatbot-button" onClick={onClick}>
      <img src={Rabbit} alt="Chatbot" />
    </button>
  );
};

export default ChatbotButton;