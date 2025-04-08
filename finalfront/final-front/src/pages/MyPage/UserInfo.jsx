import { useEffect, useState } from 'react';
import axios from 'axios';
import '../../styles/UserInfo.css'; // 스타일 따로 분리

export default function UserInfo() {
  const [user, setUser] = useState({});

  useEffect(() => {
    const email = localStorage.getItem('email');
    const token = localStorage.getItem('token');
    console.log("👀 이미지 파일명:", user.profileImage);
    if (email && token) {
      axios.get(`http://localhost:8080/user/get-user?email=${email}`, {
        headers: {
          Authorization: `Bearer ${token}` // JWT 헤더 추가
        }
      })
        .then(response => {
          setUser(response.data);
        })
        .catch(error => {
          console.error("에러 발생:", error);
        });
    }
  }, []);

  return (
    <div className="user-info-card">
      <h1 className="big-title">회원 정보</h1>
      {user.profileImage && (
          <div className="profile-image-box">
            <img
            src={
              user.profileImage
                ? user.profileImage.startsWith("http")
                  ? user.profileImage // URL이면 그대로
                  : `http://localhost:8080/uploads/${user.profileImage}` // 파일명만 있으면 경로 붙이기
                : "/images/default-profile.jpg" // 기본 이미지
            }
            alt="프로필 이미지"
            className="profile-image"
          />
          </div>
        )}
      <div className="user-info-detail">
        <p><strong className="ui-t">이름</strong> {user.name}</p>
        <p><strong className="ui-t">이메일</strong> {user.email}</p>
        <p><strong className="ui-t">전화번호</strong> {user.phoneNumber}</p>
        <p><strong className="ui-t">가입일</strong> {user.createdAt ? new Date(user.createdAt).toLocaleDateString() : "-"}</p>
        <p><strong className="ui-t">이메일 인증 여부</strong> {user.isVerified ? "인증 완료 ✅" : "미인증 ❌"}</p>

     
      </div>
    </div>
  );
}