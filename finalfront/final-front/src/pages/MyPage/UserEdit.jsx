import React, { useState, useEffect } from "react";
import axios from "axios";
import "../../styles/UserEdit.css";
import "../../styles/FormStyles.css"; // ✅ 공통 CSS 적용
import { useNavigate } from "react-router-dom";

export default function UserEdit() {
    const navigate = useNavigate();
    const [user, setUser] = useState({
        email: "",
        password: "",
        name: "",
        phoneNumber: "",
        profileImage: "", // 기본적으로 이미지 URL 저장
    });

    const [selectedFile, setSelectedFile] = useState(null); // 선택된 파일
    const [preview, setPreview] = useState(null); // 미리보기 URL
    const [deleteImage, setDeleteImage] = useState(false); // ✅ 프로필 이미지 삭제 여부
    const email = localStorage.getItem("email");

// 🔹 사용자 정보 불러오기 (useEffect)
useEffect(() => {
    const token = localStorage.getItem("token");
    if (!token) {
        alert("로그인이 필요합니다.");
        return;
    }

    axios.get(`http://localhost:8080/user/get-user?email=${email}`, {
        headers: { Authorization: `Bearer ${token}` },
    })
    .then(res => {
        setUser({
            email: res.data.email,
            name: res.data.name,
            phoneNumber: res.data.phoneNumber,
            password: "",
            profileImage: res.data.profileImage || "",
        });

        let imagePath = res.data.profileImage;

        // ✅ URL 중복 변환 방지
        if (!imagePath || imagePath === "null") {
            imagePath = "/images/default-profile.jpg"; // 기본 이미지
        } else if (!imagePath.startsWith("http")) {
            imagePath = `http://localhost:8080/uploads/${imagePath}`;
        }

        console.log("🖼 [미리보기 설정] URL:", imagePath);
        setPreview(imagePath);
    })
    .catch(err => console.error("❌ [Error] 사용자 정보 불러오기 실패:", err));
}, []);
    

    // 🔹 입력값 변경 핸들러 (이름, 비밀번호, 전화번호)
    const handleChange = (e) => {
        setUser({ ...user, [e.target.name]: e.target.value });
    };

    // 🔹 이미지 업로드 핸들러
    const handleImageUpload = (e) => {
        const file = e.target.files[0];
        if (file) {
            setSelectedFile(file);
            setPreview(URL.createObjectURL(file)); // ✅ 미리보기 즉시 적용
            setDeleteImage(false); // ✅ 새 이미지 선택 시 삭제 플래그 해제
            console.log("📷 [새 이미지 선택]:", file.name); // ✅ 콘솔 로그 추가
        }
    };

    // 🔹 프로필 이미지 삭제
    const handleDeleteImage = () => {
        setPreview("/images/default-profile.jpg"); // 기본 이미지로 변경
        setSelectedFile(null);
        setDeleteImage(true); // ✅ 이미지 삭제 요청 설정
        console.log("❌ [이미지 삭제됨]"); // ✅ 콘솔 로그 추가
    };

    // 🔹 전화번호 입력 핸들러 (010 고정, 4자리 입력 시 자동으로 - 추가)
    const handlePhoneChange = (e) => {
        let value = e.target.value.replace(/\D/g, ""); // 숫자만 입력 허용
        value = value.replace(/^010/, ""); // "010"이 중복 입력되지 않도록 방지

        if (value.length > 8) value = value.slice(0, 8); // 8자리 초과 입력 방지

        let formattedNumber = "010"; // 010 고정
        if (value.length > 4) {
            formattedNumber += "-" + value.slice(0, 4) + "-" + value.slice(4);
        } else if (value.length > 0) {
            formattedNumber += "-" + value;
        }

        setUser({ ...user, phoneNumber: formattedNumber });
    };

     // 🔹 회원 정보 수정 요청
     const handleSubmit = () => {
        const token = localStorage.getItem("token");
        if (!token) {
            alert("로그인이 필요합니다.");
            return;
        }

        const formData = new FormData();
        formData.append("email", user.email);
        formData.append("password", user.password || "");
        formData.append("name", user.name);
        formData.append("phoneNumber", user.phoneNumber);

        // ✅ 프로필 이미지 처리 (새 이미지 업로드 or 삭제)
        if (selectedFile) {
            formData.append("file", selectedFile);
        } else if (deleteImage) {
            formData.append("deleteImage", "true"); // ✅ 삭제 요청을 보냄
        }

        console.log("📡 [회원 정보 수정 요청] FormData 확인:", Object.fromEntries(formData.entries())); // ✅ 콘솔 로그 추가

        axios.put("http://localhost:8080/user/update", formData, {
            headers: {
                Authorization: `Bearer ${token}`,
                "Content-Type": "multipart/form-data",
            },
        })
        .then((res) => {
            console.log("✅ [API 응답 데이터]:", res.data);

            // ✅ 프로필 이미지 URL 업데이트
            let newImageUrl = res.data.profileImage;
            if (newImageUrl && !newImageUrl.startsWith("http")) {
                newImageUrl = `http://localhost:8080/uploads/${newImageUrl}`;
            }

            console.log("🖼 [수정 완료 후 미리보기 설정]:", newImageUrl); // ✅ 콘솔 로그 추가
            setPreview(newImageUrl);

            alert("회원 정보 수정 완료!");
            window.location.reload();
        })
        .catch(err => {
            console.error("❌ [Error] 회원 정보 수정 실패:", err.response);
            alert(`수정 실패: ${err.response?.data?.message || "서버 오류"}`);
        });
    };

    return (
        <div className="user-edit-container">
          <h1 className="big-title">회원 정보 수정</h1>
          <div className="image-upload-wrapper">
            <div className="image-preview-wrapper">
              {/* 파일 선택이 이미지 위로! */}
              <input
                type="file"
                className="file-input"
                accept="image/*"
                onChange={handleImageUpload}
              />
              {/* 이미지 미리보기 */}
              {preview && (
                <img
                  src={preview}
                  alt="미리보기"
                  className="image-preview"
                  onError={(e) => {
                    e.target.src = "/images/default-profile.jpg";
                  }}
                />
              )}
            </div>
          </div>
              {/* 삭제 버튼은 이미지 아래 */}
              {user.profileImage && !deleteImage && (
                <button type="button" className="del-img" onClick={handleDeleteImage}>
                  이미지 삭제
                </button>
              )}
                  {/* 🔹 기본 정보 입력 필드들 */}
          <div className="form-group">
            <label>이메일</label>
            <input style={{fontFamily:"NEXON Lv1 Gothic OTF"}}  type="email" name="email" value={user.email} readOnly />
          </div>
      
          <div className="form-group">
            <label>비밀번호</label>
            <input style={{fontFamily:"NEXON Lv1 Gothic OTF"}} 
              type="password"
              name="password"
              value={user.password}
              onChange={handleChange}
            />
          </div>
      
          <div className="form-group">
            <label>이름</label>
            <input style={{fontFamily:"NEXON Lv1 Gothic OTF"}} 
              type="text"
              name="name"
              value={user.name}
              onChange={handleChange}
            />
          </div>
      
          <div className="form-group">
            <label>전화번호</label>
            <input style={{fontFamily:"NEXON Lv1 Gothic OTF"}} 
              type="text"
              name="phoneNumber"
              value={user.phoneNumber}
              onChange={handlePhoneChange}
              maxLength={13}
            />
          </div>
      
          {/* 🔹 버튼 그룹 */}
         {/* 🔹 버튼 가로 정렬 */}
    <div className="button-container">
          <button className="del-im" onClick={() => navigate("/mypage")}>
            회원 정보 보기
          </button>
      <button className="us-edit-button" onClick={handleSubmit}>수정 완료</button>
    </div>
        </div>
      );
      
}