import { Link, useLocation } from "react-router-dom";
import './Sidebar.css';

const Sidebar = () => {
  const location = useLocation();

  const handleLinkClick = (path) => {
      if (location.pathname === path) {
          window.location.reload(); 
      }
  };
    return (
      <div className="sidebar_main">
        <div className="sidebar_component">
        <ul className="sidebar-list">
          <li>
            <Link to="/list" className={location.pathname === "/list" ? "active" : ""} 
            onClick={() => handleLinkClick("/list")}>레시피 목록</Link>
          </li>
          <li>
            <Link to="/korean" className={location.pathname === "/korean" ? "active" : ""}  
            onClick={() => handleLinkClick("/korean")}>한식</Link>
          </li>
          <li className="">
            <Link to="/japanese" className={location.pathname === "/japanese" ? "active" : ""}  
            onClick={() => handleLinkClick("/japanes")}>일식</Link>
          </li>
          <li className="">
            <Link to="/chinese" className={location.pathname === "/chinese" ? "active" : ""}  
            onClick={() => handleLinkClick("/chinese")}>중식</Link>
          </li>
          <li className="">
            <Link to="/western" className={location.pathname === "/western" ? "active" : ""}  
            onClick={() => handleLinkClick("/western")}>양식</Link>
          </li>
          <li className="">
            <Link to="/popular" className={location.pathname === "/popular" ? "active" : ""}  
            onClick={() => handleLinkClick("/popular")}>인기 레시피</Link>
          </li>
          <li className="">
            <Link to="/challenge" className={location.pathname === "/challenge" ? "active" : ""}  
            onClick={() => handleLinkClick("/challenge")}>맛있는 도전</Link>
          </li>
        </ul>
        </div>
      </div>
    );
  };

export default Sidebar;