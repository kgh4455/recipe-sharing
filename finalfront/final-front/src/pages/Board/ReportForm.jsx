import { useState } from "react";
import axios from "axios";
import { useParams } from "react-router-dom";
import "./ReportForm.css";

function ReportForm() {
  const { boardId } = useParams();
  const [selectedReason, setSelectedReason] = useState("");
  const [customReason, setCustomReason] = useState("");
  
  const reportReasons = [
    "스팸홍보/도배입니다.",
    "음란물 입니다.",
    "불법정보를 포함하고 있습니다.",
    "청소년에게 유해한 내용입니다.",
    "욕설/생명경시/혐오/차별적 표현입니다.",
    "개인정보가 노출되었습니다.",
    "불쾌한 표현이 있습니다.",
    "기타"
  ];

  // 신고 제출 처리
  const handleSubmit = async (e) => {
    e.preventDefault();
  
    const token = localStorage.getItem("token");
  
    if (!token) {
      alert("로그인 후 신고할 수 있습니다.");
      return;
    }
  
    const reportData = {
      reason: selectedReason === "기타" ? customReason : selectedReason,
    };
  
    console.log("reportData:", reportData);  // reportData 확인
  
    try {
      const response = await axios.post(
        `http://localhost:8080/api/board/${boardId}/report`, 
        reportData,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      alert("신고가 접수되었습니다.");
      window.close(); // 팝업창 닫기
    } catch (error) {
      console.error("신고 실패:", error);
      alert("신고 실패: " + error.response?.data || error.message);  // 에러 메시지 출력
    }
  };

  return (
    <div className="report-popup">
      <h1 className="report-title">게시글 신고</h1>
      <form onSubmit={handleSubmit}>
        {reportReasons.map((reason, index) => (
          <div key={index} className="report-reason-item">
            <input
              type="radio"
              id={`reason-${index}`}
              name="reportReason"
              value={reason}
              checked={selectedReason === reason}
              onChange={(e) => setSelectedReason(e.target.value)}
            />
            <label htmlFor={`reason-${index}`}>{reason}</label>
          </div>
        ))}

        {selectedReason === "기타" && (
          <div className="report-custom-reason">
            <label htmlFor="custom-reason">기타 사유를 입력해주세요.</label>
            <textarea
              id="custom-reason"
              value={customReason}
              onChange={(e) => setCustomReason(e.target.value)}
              rows="4"
            />
          </div>
        )}
        <br />
        <button
          type="submit"
          className="report-button-submit"
        >
          제출하기
        </button>
      </form>
    </div>
  );
}

export default ReportForm;
