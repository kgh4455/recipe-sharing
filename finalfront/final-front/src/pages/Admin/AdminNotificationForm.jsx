import { useState } from "react";
import axios from "axios";
import "../../styles/AdminNotifications.css"; // 기존 스타일 그대로 재사용

const AdminNotificationForm = () => {
  const [receiverEmail, setReceiverEmail] = useState("");
  const [message, setMessage] = useState("");

  const handleSend = async () => {
    if (!receiverEmail || !message) {
      alert("이메일과 메시지를 모두 입력해주세요.");
      return;
    }

    const token = localStorage.getItem("token");
    console.log("📌 전송 요청 전 토큰:", token);

    // 디코드된 JWT 페이로드 확인
    try {
      const base64Url = token?.split(".")[1];
      const decodedPayload = JSON.parse(atob(base64Url));
      console.log("📌 디코드된 JWT Payload:", decodedPayload);
    } catch (err) {
      console.warn("⚠️ JWT 디코딩 실패: 토큰 형식 확인 필요");
    }

    try {
      const response = await axios.post(
        "http://localhost:8080/admin/send-notification", // ✅ 백엔드에서 지원하는 경로
        { receiverEmail, message },
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );
      console.log("✅ 전송 응답:", response.data);
      alert("✅ 알림이 성공적으로 전송되었습니다!");
      setReceiverEmail("");
      setMessage("");
    } catch (error) {
      console.error("❌ 알림 전송 실패:", error);
      if (error.response) {
        console.log("❌ 응답 상태 코드:", error.response.status);
        console.log("❌ 응답 데이터:", error.response.data);
      }
      alert("❌ 알림 전송에 실패했습니다.");
    }
  };

  return (
    <div className="admin-notifications-container">
      <h2>📨 일반 알림 전송</h2>
      <div className="send-notification">
        <input
          type="email"
          placeholder="받는 사람 이메일"
          value={receiverEmail}
          onChange={(e) => setReceiverEmail(e.target.value)}
        />
        <input
          type="text"
          placeholder="알림 메시지"
          value={message}
          onChange={(e) => setMessage(e.target.value)}
        />
        <button onClick={handleSend}>📩 알림 전송</button>
      </div>
    </div>
  );
};

export default AdminNotificationForm;