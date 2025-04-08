import { Routes, Route, NavLink } from "react-router-dom";
import AdminUsers from "./AdminUsers";
import AdminUserDeletions from "./AdminUserDeletions";
import AdminRecipes from "./AdminRecipes"; 
import AdminStatistics from "./AdminStatistics"; 
import AdminBoards from "./AdminBoards"; 
import AdminInquiries from "./AdminInquiries"; 
import AdminCompetitions from "./AdminCompetitions"; 
import RecipeForm from "./RecipeForm"; // ✅ 레시피 추가/수정 폼 가져오기
import "../../styles/AdminPage.css"; // ✅ CSS 경로 수정!
import AdminInquiryDetail from "./AdminInquiryDetail"; // ✅ 상세보기 페이지 추가
import AdminReportList from "./AdminReportList";

const AdminPage = () => {
  return (
    <div className="admin-page">
        <Routes>
          <Route path="users" element={<AdminUsers />} />
          <Route path="user-deletion" element={<AdminUserDeletions />} />
          <Route path="recipes" element={<AdminRecipes />} />
          <Route path="recipes/add" element={<RecipeForm isEditing={false} />} /> {/* ✅ 추가 */}
          <Route path="recipes/edit/:id" element={<RecipeForm isEditing={true} />} /> {/* ✅ 수정 */}
          <Route path="statistics" element={<AdminStatistics />} />
          <Route path="boards" element={<AdminBoards />} />
          <Route path="notifications" element={<AdminReportList />} />
          <Route path="inquiries" element={<AdminInquiries />} />
          <Route path="competitions" element={<AdminCompetitions />} />
          <Route path="inquiries/:id" element={<AdminInquiryDetail />} /> {/* ✅ 상세 페이지 추가 */}
        </Routes>
    </div>
  );
};

export default AdminPage;