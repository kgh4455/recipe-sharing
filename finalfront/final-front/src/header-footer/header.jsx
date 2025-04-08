import { Link, useLocation, useNavigate } from "react-router-dom";
import { useEffect, useState } from "react";
import { FiMenu, FiX } from "react-icons/fi";
import Rabbit from "../assets/rabbit.png";
import "./header.css";

export default function Header({ isLoggedIn, setIsLoggedIn }) {
  const [activeDropdown, setActiveDropdown] = useState(null);  // 상태 추가
  const [isAdmin, setIsAdmin] = useState(false);
  const [isMobileMenuOpen, setIsMobileMenuOpen] = useState(false);

  const navigate = useNavigate(); 
  const location = useLocation();

  const handleLogout = () => {
      localStorage.removeItem("token"); // 토큰 삭제
      localStorage.removeItem("email");
      setIsLoggedIn(false); 
      setIsAdmin(false);
      navigate('/'); // 메인 페이지로 리다이렉션
  };

  useEffect(() => {
      const token = localStorage.getItem("token");
      if (token) {
          try {
              const parts = token.split(".");
              if (parts.length !== 3) {
                  throw new Error("잘못된 토큰 형식입니다.");
              }

              const base64Payload = parts[1];
              const base64 = base64Payload.replace(/-/g, '+').replace(/_/g, '/');
              const decodedPayload = JSON.parse(atob(base64));
              
              
              setIsLoggedIn(true); 
              setIsAdmin(decodedPayload.role === "ROLE_ADMIN");
              
          } catch (error) {
              console.error("토큰 디코딩 오류:", error);
              setIsLoggedIn(false);
              setIsAdmin(false); 
          }
      }else{
          setIsLoggedIn(false); 
          setIsAdmin(false); 
      }
  }, [isLoggedIn,setIsAdmin]);

  useEffect(() => {
      if (isLoggedIn) {
          // 이미 로그인된 경우, 새로고침을 한 번만 실행하도록 설정
          const isFirstLoad = sessionStorage.getItem("firstLoad");
          if (!isFirstLoad) {
              sessionStorage.setItem("firstLoad", "true");
              window.location.reload();
          }
      }
  }, [isLoggedIn]);


  const handlePageClick = (path) => {
      if (location.pathname === path) {
          window.location.reload(); // 이미 해당 페이지가 활성화되어 있으면 새로고침
      }
  };

  return (
    <header className="header">
      <div className="header-logo">
        <h1 style={{ color: "#FF8746" }}>
          냠
          <Link to={"/"}>
            <img src={Rabbit} alt="Rabbit" />
          </Link>
          냠
        </h1>
      </div>

      <div
        className="mobile-menu-icon"
        onClick={() => setIsMobileMenuOpen(!isMobileMenuOpen)}
      >
        {isMobileMenuOpen ? <FiX size={30} /> : <FiMenu size={30} />}
      </div>

      <nav className={`nav-links ${isMobileMenuOpen ? "active" : ""}`}>
        <div
          className="category-dropdown"
          onMouseEnter={() => setActiveDropdown("category")}
          onMouseLeave={() => setActiveDropdown(null)}
        >
          <Link
            to="/list"
            className={location.pathname === "/list" ? "active" : ""}
          >
            ▼ 레시피 목록
          </Link>
          {activeDropdown === "category" && (
            <div className="dropdown">
              <Link
                to={"/Korean"}
                className={location.pathname === "/Korean" ? "active" : ""}
              >
                한식
              </Link>
              <Link
                to={"/Chinese"}
                className={location.pathname === "/Chinese" ? "active" : ""}
              >
                일식
              </Link>
              <Link
                to={"/Japanese"}
                className={location.pathname === "/Japanese" ? "active" : ""}
              >
                중식
              </Link>
              <Link
                to={"/Western"}
                className={location.pathname === "/Western" ? "active" : ""}
              >
                양식
              </Link>
              <Link
                to={"/Popular"}
                className={location.pathname === "/Popular" ? "active" : ""}
              >
                인기 레시피
              </Link>
              <Link
                to={"/Challenge"}
                className={location.pathname === "/Challenge" ? "active" : ""}
              >
                맛있는 도전
              </Link>
              <Link
                to={"/Ocr"}
                className={location.pathname === "/Ocr" ? "active" : ""}
              >
                냉장고 털이
              </Link>
            </div>
          )}
        </div>

        <Link
          to="/clublist"
          className={location.pathname === "/clublist" ? "active" : ""}
        >
          다함께 요리하자
        </Link>
        <Link
          to="/challengeform"
          className={location.pathname === "/challengeform" ? "active" : ""}
        >
          맛있는 도전하기
        </Link>
        <Link
          to="/boardlist"
          className={location.pathname === "/boardlist" ? "active" : ""}
        >
          요리 고민방
        </Link>

        <div className="mypage">
          {isLoggedIn ? (
            isAdmin ? (
              <div
              className="mypage-dropdown"
              onMouseEnter={() => setActiveDropdown("admin")}
              onMouseLeave={() => setActiveDropdown(null)}
            >

              <button className="admin-btn"><Link to="/admin/users">▼ 관리자 페이지</Link></button>
              {activeDropdown === "admin" && (
                <div className="dropdown">
                  <Link to="/admin/users">회원 관리</Link>
                  <Link to="/admin/user-deletion">회원 탈퇴 요청 관리</Link>
                  <Link to="/admin/recipes">레시피 관리</Link>
                  <Link to="/admin/statistics">통계 관리</Link>
                  <Link to="/admin/boards">요리고민방 관리</Link>
                  <Link to="/admin/notifications">신고 목록</Link>
                  <Link to="/admin/inquiries">1:1 문의 관리</Link>
                  <Link to="/admin/competitions">맛있는 도전 관리</Link>
                  <Link to="/admin/chat">고객 센터</Link>
                </div>
              )}
            </div>
            ) : (
              <div
                className="mypage-dropdown"
                onMouseEnter={() => setActiveDropdown("mypage")}
                onMouseLeave={() => setActiveDropdown(null)}
              >
              <Link
                to="/mypage"
              //   className={location.pathname === "/mypage" ? "active" : ""}
             >
                ▼ 마이페이지
              </Link>
              {activeDropdown === "mypage" && (
                <div className="dropdown">
                  <Link
                    to={"/mypage/user-edit"}
                    className={location.pathname === "/user-edit" ? "active" : ""}
                  >
                    회원 정보 수정
                  </Link>
                  <Link
                    to={"/mypage/activity"}
                    className={location.pathname === "/activity" ? "active" : ""}
                  >
                    내 활동
                  </Link>
                  <Link
                    to={"/mypage/notifications"}
                    className={location.pathname === "/notifications" ? "active" : ""}
                  >
                    알림
                  </Link>
                  <Link
                    to={"/mypage/inquiry"}
                    className={location.pathname === "/inquiry" ? "active" : ""}
                  >
                    1:1 문의 목록
                  </Link>
                  <Link
                    to={"/mypage/inquiry/new"}
                    className={location.pathname === "/inquiry-write" ? "active" : ""}
                  >
                    1:1 문의 작성
                  </Link>
                  <Link
                    to={"/mypage/withdrawal"}
                    className={location.pathname === "/withdrawal" ? "active" : ""}
                  >
                    회원 탈퇴
                  </Link>
                  <Link
                  to={"mypage/chat"}
                    className={location.pathname==="/chat" ? "active":""}
                  >
                    고객센터
                    </Link>
                  
                </div>
              )}
            </div>
          )
        ) : (
        <></>
        )}

          {isLoggedIn ? (
                    <>
                    
                        <button onClick={handleLogout} className="logout-btn">로그아웃</button>
                    </>
                ) : (
                    <>
                        <button><Link to={'/login'}>로그인</Link></button>
                    </>
                )}
         </div>
      </nav>
    </header>
  );
}
