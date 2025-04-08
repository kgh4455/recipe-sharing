// ✅ 수정된 AdminLayout.jsx
import { Outlet } from "react-router-dom";
import AdminHeader from "./AdminHeader";
import Sidebar from "./Sidebar";

const AdminLayout = () => {
  return (
    <div className="admin-page"> {/* ✅ 여기 수정 */}
      <AdminHeader />
      <div className="admin-content">
        <Sidebar />
        <div className="admin-main">
          <Outlet />
        </div>
      </div>
    </div>
  );
};

export default AdminLayout;