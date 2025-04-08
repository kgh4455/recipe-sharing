import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import "../../styles/AdminInquiryDetail.css"; // ✅ CSS 파일 import

const AdminInquiryDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const [inquiry, setInquiry] = useState(null);
  const [reply, setReply] = useState("");

  useEffect(() => {
    fetchInquiryDetail();
  }, []);

  const fetchInquiryDetail = async () => {
    try {
      const token = localStorage.getItem("token");
      const response = await axios.get(`http://localhost:8080/admin/inquiries/${id}`, {
        headers: { Authorization: `Bearer ${token}` }
      });
      setInquiry(response.data);
      setReply(response.data.reply || "");
    } catch (error) {
      console.error("❌ 문의 상세 불러오기 실패:", error);
    }
  };

  const handleReplySubmit = async () => {
    if (!reply.trim()) {
      alert("답변 내용을 입력하세요.");
      return;
    }
    try {
      const token = localStorage.getItem("token");
      await axios.patch(
        `http://localhost:8080/admin/inquiries/reply/${id}`,
        { reply },
        { headers: { Authorization: `Bearer ${token}`, "Content-Type": "application/json" } }
      );
      alert("답변이 수정되었습니다.");
      navigate("/admin/inquiries");
    } catch (error) {
      console.error("❌ 답변 수정 실패:", error);
    }
  };
  const handleDeleteInquiry = async () => {
    if (!window.confirm("정말로 삭제하시겠습니까?")) return;
  
    try {
      const token = localStorage.getItem("token");
      await axios.delete(`http://localhost:8080/admin/inquiries/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      alert("문의가 삭제되었습니다.");
      navigate("/admin/inquiries"); // 삭제 후 목록 페이지로 이동
    } catch (error) {
      console.error("❌ 문의 삭제 실패:", error);
      alert("문의 삭제에 실패했습니다. 다시 시도해주세요.");
    }
  };
  if (!inquiry) return <p>문의 정보를 불러오는 중...</p>;

  return (
    <div className="adminz-container">
      {/* ✅ 문의 기본 정보 */}
      <div className="inq-de">
        <h2 className="inquirys-title">{inquiry.title}</h2>
        <p className="inquirys-date"><strong>작성자:</strong> {inquiry.userName}</p>
        <p className="inquirys-date"><strong>이메일:</strong> {inquiry.userEmail}</p>
        <p className="inquirys-date"><strong>작성일:</strong> {new Date(inquiry.createdAt).toLocaleString()}</p>
        <p className="inquirys-date"><strong>상태:</strong> {inquiry.reply ? "답변 완료" : "대기 중"}</p>
        <hr/>
      </div>
      {/* ✅ 문의 내용 박스 */}
      <div className="boxx">
        <h3 style={{color:"#FF8746"}}>문의 내용</h3>
        <p style={{marginBottom:"40px"}}>{inquiry.content}</p>

      {/* ✅ 답변 박스 */}
        <h3 style={{color:"#FF8746"}}>답변</h3>
        <textarea
          value={reply}
          onChange={(e) => setReply(e.target.value)}
          rows="4"
          className="title-input"
          />
      </div>

      {/* ✅ 버튼 정렬 */}
      <div className="button-container">
      <button className="del-img" onClick={() => navigate(-1)}>돌아가기</button>
        <button className="del-img" onClick={handleDeleteInquiry}>문의 삭제</button>
        <button className="del-img" onClick={() => setReply("")}>답변 삭제</button>
        <button className="us-edit-button" style={{marginBottom:"40px"}}onClick={handleReplySubmit}>답변 저장</button>
      </div>
    </div>
  );
};

export default AdminInquiryDetail;