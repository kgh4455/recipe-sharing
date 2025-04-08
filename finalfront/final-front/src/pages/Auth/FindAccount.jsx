import { useState } from "react";
import axios from "axios";
import "../../styles/FindAccount.css"; 
import "../../styles/GlobalStyles.css";

export default function FindAccount() {
  const [activeTab, setActiveTab] = useState("find-id");
  const [email, setEmail] = useState("");
  const [phoneNumber, setPhoneNumber] = useState("");
  const [name, setName] = useState("");
  const [verificationCode, setVerificationCode] = useState("");
  const [isCodeSent, setIsCodeSent] = useState(false);
  const [isVerified, setIsVerified] = useState(false);
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [showResetModal, setShowResetModal] = useState(false);
  const [foundUserId, setFoundUserId] = useState("");

  // ✅ 아이디 찾기
  const handleFindId = async () => {
    console.log("아이디 찾기 요청:", { name, phoneNumber });
    try {
      const response = await axios.post("http://localhost:8080/user/find-id", {
        name,
        phoneNumber,
      });
      console.log("아이디 찾기 성공:", response.data);
      setFoundUserId(response.data.userId);
      alert(`찾은 아이디: ${response.data.userId}`);
    } catch (error) {
      console.error("아이디 찾기 실패:", error.response?.data || error.message);
      alert("아이디 찾기 실패: " + (error.response?.data || "서버 오류"));
    }
  };

  // ✅ 전화번호 자동 포맷팅 (010-xxxx-xxxx)
  const handlePhoneInput = (e) => {
    let value = e.target.value.replace(/\D/g, ""); // 숫자만 입력
    if (value.length > 3 && value.length <= 7) {
      value = value.replace(/(\d{3})(\d+)/, "$1-$2");
    } else if (value.length > 7) {
      value = value.replace(/(\d{3})(\d{4})(\d+)/, "$1-$2-$3");
    }
    setPhoneNumber(value);
  };

  // ✅ 인증번호 발송
  const handleSendCode = async () => {
    console.log("인증번호 요청:", { email, phoneNumber });
    try {
      const response = await axios.post("http://localhost:8080/user/send-verification-code", {
        email,
        phoneNumber,
      });
      console.log("인증번호 전송 성공:", response.data);
      alert("인증번호가 전송되었습니다.");
      setIsCodeSent(true);
    } catch (error) {
      console.error("인증번호 요청 실패:", error.response?.data || error.message);
      alert("인증번호 발송 실패: " + (error.response?.data || "서버 오류"));
    }
  };

  // ✅ 인증번호 확인
  const handleVerifyCode = async () => {
    console.log("인증번호 확인 요청:", { email, verificationCode });
    try {
      await axios.post("http://localhost:8080/user/confirm-email", {
        email,
        code: verificationCode,
      });
      alert("인증되었습니다.");
      setIsVerified(true);
      setShowResetModal(true); // ✅ 비밀번호 재설정 모달 열기
    } catch (error) {
      alert("인증 실패: " + (error.response?.data || "서버 오류"));
    }
  };

  // ✅ 비밀번호 재설정
  const handleResetPassword = async () => {
    if (newPassword !== confirmPassword) {
      alert("비밀번호가 일치하지 않습니다.");
      return;
    }
  
    try {
      await axios.post("http://localhost:8080/user/reset-password", {
        email,
        newPassword, // ✅ 인증번호 제거
      });
  
      alert("비밀번호가 변경되었습니다.");
      setShowResetModal(false);
    } catch (error) {
      alert("비밀번호 변경 실패: " + error.response?.data || "서버 오류");
    }
  };

  return (
    <div className="find-account-container">
      <h1 className="big-title">FIND ID / PASSWORD</h1>

      {/* 🔹 탭 메뉴 */}
      <div className="tabs">
        <button
          className={`tab-button ${activeTab === "find-id" ? "active" : "none"}`}
          onClick={() => setActiveTab("find-id")}
        >
          아이디 찾기
        </button>
        <button
          className={`tab-button ${activeTab === "find-password" ? "active" : "none"}`}
          onClick={() => setActiveTab("find-password")}
        >
          비밀번호 찾기
        </button>
      </div>

      {/* ✅ 아이디 찾기 폼 */}
      {activeTab === "find-id" && (
        <div className="tab-content">
          <label className="login-label">이름</label>
          <input type="text" className="login-input" value={name} onChange={(e) => setName(e.target.value)} />

          <label className="login-label">전화번호</label>
          <input type="text" className="login-input" value={phoneNumber} onChange={handlePhoneInput} maxLength="13" />
          <button className="find-button" onClick={handleFindId}>찾기</button>
        </div>
      )}

      {/* ✅ 비밀번호 찾기 폼 */}
      {activeTab === "find-password" && (
        <div className="tab-content">
        {/* ✅ 이메일 입력 + 인증번호 받기 버튼 한 줄 정렬 */}
        <label className="login-label">이메일</label>
          <input type="email" className="login-input" value={email} onChange={(e) => setEmail(e.target.value)} />

          {/* ✅ 전화번호 입력 (다시 추가됨) */}
        <label  className="login-label">전화번호</label>
        <input type="text" className="login-input" value={phoneNumber} onChange={handlePhoneInput} maxLength="13" />
        {!isCodeSent && (
          <button className="find-button" onClick={handleSendCode}>인증번호 받기</button>
        )}
          {isCodeSent && (
            <>
              <label className="login-label" >인증번호</label>
                <input type="text" className="l" value={verificationCode} onChange={(e) => setVerificationCode(e.target.value)} />
                <button className="find-button" onClick={handleVerifyCode}>인증번호 확인</button>
            </>
          )}
        </div>
      )}

      {/* ✅ 비밀번호 재설정 모달 */}
      {showResetModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <h2 className="modal-title">비밀번호 재설정</h2>
            <input type="password" className="login-input" placeholder="새 비밀번호" value={newPassword} onChange={(e) => setNewPassword(e.target.value)} />
            <input type="password" className="login-input" placeholder="비밀번호 확인" value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} />

            <button className="find-button" onClick={handleResetPassword}>비밀번호 변경</button>
            <button className="cancel-button" onClick={() => setShowResetModal(false)}>닫기</button>
          </div>
        </div>
      )}
    </div>
  );
}