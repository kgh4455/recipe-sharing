import { useState } from "react";
import axios from "axios";
import "../../styles/SendNotificationModal.css";

const SendNotificationModal = ({ onClose }) => {
  const [email, setEmail] = useState("");
  const [message, setMessage] = useState("");

  const sendNotification = async () => {
    if (!email || !message) {
      alert("이메일과 메시지를 모두 입력해주세요.");
      return;
    }

    try {
      const token = localStorage.getItem("token");

      await axios.post(
        "http://localhost:8080/admin/notifications",
        {
          receiverEmail: email,             // ✅ 백엔드와 일치하게 수정
          message: `[관리자] ${message}`,   // ✅ 관리자 메시지 prefix 자동 추가
        },
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      alert("✅ 알림 전송 완료!");
      onClose();
    } catch (error) {
      console.error("❌ 알림 전송 실패:", error);
      alert("❌ 알림 전송 중 오류 발생");
    }
  };

  return (
    <div className="modal-overlay" onClick={onClose}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <h3 style={{color:"#FF8746", marginBottom:"40px"}}>📢 유저에게 알림 보내기</h3>
        <div className="form-group">
          <label>이메일</label>
          <input
            type="email"
            style={{fontFamily:"NEXON Lv1 Gothic OTF"}} 
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="example@example.com"
          />
        </div>

        <div className="form-group">
          <label>메시지</label>
          <textarea
            rows={4}
            style={{fontFamily:"NEXON Lv1 Gothic OTF", marginBottom:"0px"}} 
            value={message}
            onChange={(e) => setMessage(e.target.value)}
            placeholder="전송할 알림 내용을 입력하세요."
            className="title-input"
          />
        </div>
        <div className="button-container">
          <button className="del-img" onClick={onClose}>돌아가기</button>
        <button className="plu-sno" onClick={sendNotification}>보내기</button>
        </div>
      </div>
    </div>
  );
};

export default SendNotificationModal;