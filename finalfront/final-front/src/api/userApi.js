import api from "./api";

/**  
 * ===========================================
 * ✅ 유저 관련 기능 (로그인, 회원가입, 아이디/비밀번호 찾기)
 * ===========================================
 */
export const userApi = {
  // ✅ 이메일 중복 체크
  checkEmailExists: (email) => api.get(`/user/check-email?email=${email}`),

  // ✅ 휴대폰 번호 중복 체크
  checkPhoneExists: (phoneNumber) => api.get(`/user/check-phone?phoneNumber=${phoneNumber}`),

  // ✅ 회원가입 (FormData 전송: JSON(user) + 이미지)
  register(formData) {
    return api.post("/user/register", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  },



  // 🔹 로그인 (JWT 토큰 반환)
  login: (credentials) => api.post("/user/login", credentials),


  // 🔹 이메일 인증번호 전송
  verifyEmail: (email) => api.post("/user/verify-email", { email }),

  // 🔹 이메일 인증 확인
  confirmEmail: (email, code) => api.post("/user/confirm-email", { email, code }),

  // 🔹 아이디 찾기 (이름 + 휴대폰 번호 기반)
  findUserId: (name, phoneNumber) => api.post("/user/find-id", { name, phoneNumber }),

  // 🔹 비밀번호 찾기 - 인증번호 전송
  sendVerificationCode: (email, phoneNumber) => api.post("/user/send-verification-code", { email, phoneNumber }),

  // 🔹 비밀번호 재설정
  resetPassword: (data) => api.post("/user/reset-password", data),
};

// ✅ `userApi` 내보내기
export default userApi;