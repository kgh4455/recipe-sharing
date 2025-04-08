import { useState,useEffect } from "react";
import "../../styles/registerModal.css";
import userApi from "../../api/userApi"; // API 불러오기
import { useNavigate } from "react-router-dom";
import "../../styles/login.css";

function RegisterModal({ onClose }) {
  const [email, setEmail] = useState("");
  const [code, setCode] = useState(""); // ✅ 변경: verificationCode -> code
  const [phoneNumber, setPhoneNumber] = useState("");
  const [isEmailValid, setIsEmailValid] = useState(false); // ✅ 이메일 유효성 상태 추가
  const [name, setName] = useState("");
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [image, setImage] = useState(null);
  const [isCodeSent, setIsCodeSent] = useState(false);
  const [isCodeConfirmed, setIsCodeConfirmed] = useState(false);
  const navigate = useNavigate();

  const [errors, setErrors] = useState({
    email: "",
    code: "",
    phoneNumber: "",
    name: "",
    password: "",
    confirmPassword: "",
    isCodeConfirmed,
  });
  /** ✅ useEffect 추가: 휴대폰 중복 검사 결과 변경 시 콘솔 출력 및 UI 업데이트 */
  useEffect(() => {
    console.log("📢 errors 상태 업데이트 감지됨:", errors);
  }, [errors]);
   /** ✅ 이메일 중복 확인 */
   const checkEmailExists = async (email) => {
    console.log("📡 이메일 중복 확인 요청:", email); // 🔍 요청 로그 추가
    if (!email.includes("@") || !email.includes(".")) {
      setErrors((prev) => ({ ...prev, email: "올바른 이메일 형식이 아닙니다." }));
      setIsEmailValid(false);
      return;
    }
    try {
      const response = await userApi.checkEmailExists(email);
      console.log("✅ 이메일 중복 확인 응답:", response.data); // 🔍 응답 로그 추가
      if (response.data.exists) {
        setErrors((prev) => ({ ...prev, email: "이미 사용 중인 이메일입니다." }));
        setIsEmailValid(false);
      } else {
        setErrors((prev) => ({ ...prev, email: "" }));
        setIsEmailValid(true);
      }
    } catch (error) {
      console.error("❌ 이메일 확인 중 오류:", error);
      setErrors((prev) => ({ ...prev, email: "이메일 확인 중 오류 발생." }));
      setIsEmailValid(false);
    }
  };


  /** ✅ 이메일 인증번호 전송 */
  const sendVerificationCode = async () => {
    if (!email) {
      setErrors((prev) => ({ ...prev, email: "이메일을 입력하세요." }));
      return;
    }
    try {
      await userApi.verifyEmail(email);
      setIsCodeSent(true);
      alert("인증번호가 전송되었습니다.");
    } catch (error) {
      setErrors((prev) => ({ ...prev, email: "인증번호 전송 실패." }));
    }
  };

   /** ✅ 인증번호 확인 */
   const confirmVerificationCode = async () => {
    if (!code) {  // ✅ verificationCode → code
      setErrors((prev) => ({ ...prev, code: "인증번호를 입력하세요." })); 
      return;
    }
  
    try {
      const response = await userApi.confirmEmail(email, code); // ✅ verificationCode → code
  
      console.log("✅ 백엔드 응답 전체:", response); 
      console.log("✅ 백엔드 응답 데이터:", response.data); 
  
      if (typeof response.data === "string" && response.data.includes("successfully")) {
        setIsCodeConfirmed(true);
        setErrors((prev) => ({ ...prev, code: "" }));
        console.log("✅ 인증 성공! isCodeConfirmed가 true로 변경됨.");
        alert("인증 성공!");
      } else {
        setErrors((prev) => ({ ...prev, code: "인증번호가 올바르지 않습니다." }));
      }
    } catch (error) {
      console.error("🚨 인증번호 확인 중 오류:", error.response ? error.response.data : error);
      setErrors((prev) => ({ ...prev, code: "인증 확인 중 오류 발생." }));
    }
  };
  
  
  
  

  /** ✅ 대표 이미지 업로드 */
  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      const imageUrl = URL.createObjectURL(file);
      setImage(imageUrl);
    }
  };

  /** ✅ 숫자만 입력 가능하도록 휴대폰 번호 필터링 */
  const handlePhoneNumberChange = (e) => {
    let input = e.target.value.replace(/[^0-9]/g, ""); // 숫자만 남김
    if (input.length > 11) return;
  
    // ✅ 번호 길이 검증 (11자리가 아니면 에러 메시지 표시)
    if (input.length !== 11) {
      setErrors((prev) => ({ ...prev, phoneNumber: "올바른 전화번호 형식이 아닙니다." }));
    } else {
      setErrors((prev) => ({ ...prev, phoneNumber: "" })); // 에러 초기화
    }
  
    // ✅ DB 형식(010-1111-2222)에 맞게 자동 포맷팅
    let formattedNumber = input;
    if (input.length >= 4 && input.length < 8) {
      formattedNumber = `${input.slice(0, 3)}-${input.slice(3)}`;
    } else if (input.length >= 8) {
      formattedNumber = `${input.slice(0, 3)}-${input.slice(3, 7)}-${input.slice(7)}`;
    }
  
    setPhoneNumber(formattedNumber);
  
    // ✅ 번호 길이가 정확할 때만 중복 검사 실행
    if (input.length === 11) {
      checkPhoneExists(formattedNumber);
    }
  };
  
  /** ✅ 휴대폰 중복 검사 */
  const checkPhoneExists = async (phoneNumber) => {
    const formattedPhone = phoneNumber.replace(/-/g, ""); // 하이픈 제거
    console.log("📡 휴대폰 번호 중복 확인 요청:", formattedPhone);
  
    try {
      const response = await userApi.checkPhoneExists(formattedPhone);
  
      // ✅ 백엔드에서 받은 데이터 전체 확인
      console.log("✅ 백엔드 응답 전체:", response);
      console.log("✅ 백엔드 응답 데이터:", response.data);
  
      if (typeof response.data === "boolean") {  // ✅ true / false 여부 체크
        console.log("✅ 중복 체크 결과:", response.data);
  
        setErrors((prev) => ({
          ...prev,
          phoneNumber: response.data ? "이미 사용 중인 전화번호입니다." : "",
        }));
  
        // ✅ 상태 강제 반영 (리액트 비동기 문제 해결)
        setTimeout(() => {
          setErrors((prev) => ({ ...prev }));
        }, 50);
      } else {
        console.log("🚨 백엔드 응답이 이상함! exists 값이 없음.");
        setErrors((prev) => ({ ...prev, phoneNumber: "번호 확인 중 오류 발생." }));
      }
    } catch (error) {
      console.error("🚨 휴대폰 중복 확인 오류:", error);
      setErrors((prev) => ({ ...prev, phoneNumber: "번호 확인 중 오류 발생." }));
    }
  };
  

  /** ✅ 회원가입 요청 */
  const handleRegister = async () => {
    console.log("✅ 회원가입 요청 데이터:", {
      email,
      name,
      phoneNumber,
      password,
      isVerified: isCodeConfirmed,
    });
  
    try {
      const formData = new FormData();
  
      // 1. JSON 데이터를 Blob으로 감싸서 user로 추가
      const userData = {
        email,
        name,
        phoneNumber,
        password,
        isVerified: isCodeConfirmed,
      };
      const userBlob = new Blob([JSON.stringify(userData)], { type: "application/json" });
      formData.append("user", userBlob);
  
      // 2. 이미지가 있을 경우에만 추가
      const fileInput = document.querySelector("input[type='file']");
      if (fileInput && fileInput.files.length > 0) {
        formData.append("profileImage", fileInput.files[0]);
      }
  
      const response = await userApi.register(formData); // <-- formData로 변경됨
      console.log("✅ 회원가입 응답:", response.data);
      alert("회원가입 성공!");
      onClose(); // 모달 닫기
    } catch (error) {
      console.error("🚨 회원가입 실패:", error.response ? error.response.data : error);
      alert("회원가입 실패. 다시 시도해주세요.");
    }
  };
  


  
  

  return (
    <div className="modal-overlay">
      <div className="register-modal">
        <button className="close-button" onClick={onClose}>✖</button>
        <h1 className="big-title">회원가입</h1>
        <div className="modals-content">
          {/* ✅ 이메일 입력 */}
            <label className="login-label">이메일</label>
            <div className="input-row">
              <input
                type="email"
                className="login2-input"
                value={email}
                onChange={(e) => {
                  setEmail(e.target.value);
                  checkEmailExists(e.target.value);
                }}
              />
            {/* ✅ 이메일이 유효할 때만 버튼 표시 */}
            {isEmailValid && (
                <button className="button-primary" onClick={sendVerificationCode}>
                  인증번호 전송
                </button>
              )}
            {errors.email && <p className="error-text">{errors.email}</p>}
          </div>
          {/* ✅ 인증번호 입력 */}
          {isCodeSent && (
            <div className="inputss-group">
              <label className="login-label">인증번호</label>
              <div className="input-row">  {/* ✅ 한 줄로 배치 */}
                <input 
                  type="text" 
                  className="login2-input"
                  value={code} 
                  onChange={(e) => setCode(e.target.value)} 
                />
                <button className="button-primary" onClick={confirmVerificationCode}>
                  인증번호 확인
                </button>
              </div>
              {errors.code && <p className="error-text">{errors.code}</p>}
            </div>
          )}

          {/* ✅ 이름 입력 */}
          <label className="login-label">이름</label>
          <input type="text" className="login-input" value={name} onChange={(e) => setName(e.target.value)} />

          {/* ✅ 휴대폰 번호 입력 (자동 포맷팅 + 중복 검사) */}
            <label className="login-label">휴대폰 번호</label>
            <input 
              type="text" 
              className="login-input"
              value={phoneNumber} 
              onChange={handlePhoneNumberChange} 
            />
            {errors.phoneNumber && <p className="error-text">{errors.phoneNumber}</p>}

            {/* ✅ 대표 이미지 업로드 */}
            <div className="image-upload-container">
              <div className="image-preview">
                {image ? (
                  <img src={image} alt="Profile Preview" className="profile-preview" />
                ) : (
                  <div className="profile-placeholder">이미지</div>
                )}
              </div>
              <label className="upload-label">
                대표 이미지 업로드
                <input type="file" onChange={handleImageChange} />
              </label>
            </div>

            {/* ✅ 비밀번호 입력 */}
              <label className="login-label">비밀번호</label>
              <input type="password" className="login-input" value={password} onChange={(e) => setPassword(e.target.value)} />

            {/* ✅ 비밀번호 확인 */}
              <label className="login-label">비밀번호 확인</label>
              <input type="password" className="login-input" value={confirmPassword} onChange={(e) => setConfirmPassword(e.target.value)} />
              {password !== confirmPassword && <p className="error-text">비밀번호가 일치하지 않습니다.</p>}

              {/* ✅ 가입 버튼 (이메일 인증이 완료되지 않으면 비활성화) */}
              <div className="buttonr-container">
              <button className="button-secondary" onClick={onClose}>돌아가기</button>
              <button className="button-primary" onClick={handleRegister} disabled={errors.phoneNumber !== ""} // ✅ 중복 에러가 있으면 버튼 비활성화
              >
              가입하기</button>
            </div>
        </div>
      </div>
    </div>
  );
}

export default RegisterModal;