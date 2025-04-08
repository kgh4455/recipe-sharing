import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import "../../styles/AdminInquiries.css"; // ✅ 스타일 추가

const AdminInquiries = () => {
  const [inquiries, setInquiries] = useState([]);
  const navigate = useNavigate();

  useEffect(() => {
    fetchInquiries();
}, []);
const fetchInquiries = async () => {
  try {
    const token = localStorage.getItem("token");
    const response = await axios.get("http://localhost:8080/admin/inquiries", {
      headers: { Authorization: `Bearer ${token}` },
    });

    // ✅ 최신순 정렬 (createdAt 기준 내림차순)
    const sorted = response.data.sort(
      (a, b) => new Date(b.createdAt) - new Date(a.createdAt)
    );

    setInquiries(sorted);

  } catch (error) {
    console.error("❌ 문의 목록 불러오기 실패:", error);
  }
};
  return (
    <div className="adminz-container">
      <h1 className="big-title">1:1 문의 관리</h1>
      <table className="thz">
        <tbody className="tdz">
        {inquiries.map((inquiry, index) => (
      <tr key={inquiry.id}>
      <td>{index + 1}</td>
      <td>{inquiry.title}</td>
      <td>{inquiry.userName}</td>
      <td>{inquiry.userEmail || "이메일 없음"}</td>  {/* ✅ userEmail로 변경 */}
      <td>{new Date(inquiry.createdAt).toLocaleDateString()}</td>
      <td>
        {inquiry.reply ? <span className="status-replied">답변 완료</span> : <span className="status-pending">대기 중</span>}
      </td>
      <td>
      <button 
        className="detail-button"
        onClick={() => navigate(`/admin/inquiries/${inquiry.id}`)} // ✅ 상세 페이지 이동 경로 수정
      >
        상세보기
      </button>
      </td>
    </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default AdminInquiries;