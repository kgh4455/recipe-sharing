import { Outlet } from "react-router-dom";
import "./Layout.css";
import Footer from "../header-footer/footer";
import Header from "../header-footer/header";

export default function Layout(){
  return (
    <div className="layout-main">
      <Header />
        {/* <div className="content-container">
        <Sidebar />
        </div>  */}
        <div className="pages">
          <Outlet />
        </div>
      <Footer />
    </div>
  );
};

