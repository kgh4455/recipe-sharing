import MyLayout from "../../components/MyPage/MyLayout";
import MySidebar from "../../components/MyPage/MySidebar";
import { Routes, Route } from "react-router-dom";
import UserInfo from "./UserInfo";
import UserEdit from "./UserEdit";
import UserActivity from "./UserActivity";
import Notifications from "./Notifications";
import UserWithdrawal from "./UserWithdrawal";
import InquiryList from "./InquiryList"; // ✅ 문의 목록 추가
import InquiryForm from "./InquiryForm"; // ✅ 문의 작성 폼 추가
import InquiryDetail from "./InquiryDetail"; // ✅ 문의 상세 페이지 추가
import "../../styles/MyPage.css";

export default function MyPage({ user }) {
  return (
   <div className="mypages-container">
    <MyLayout user={user}>
        <div className="mypages-content">
          <Routes>
            <Route index element={<UserInfo />} /> {/* ✅ 기본 경로 설정 */}
            <Route path="user-edit" element={<UserEdit />} />
            <Route path="activity" element={<UserActivity />} />
            <Route path="notifications" element={<Notifications />} />
            

            {/* ✅ 문의 관련 라우팅 추가 */}
            <Route path="inquiry" element={<InquiryList />} /> {/* 문의 목록 */}
            <Route path="inquiry/new" element={<InquiryForm />} /> {/* 문의 작성 */}
            <Route path="inquiry/:id" element={<InquiryDetail />} /> {/* 문의 상세 페이지 */}
            <Route path="withdrawal" element={<UserWithdrawal />} />
          </Routes>
        </div>
    </MyLayout>
    </div>
  );
}