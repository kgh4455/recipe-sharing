import { Route, Routes, useLocation, useNavigate } from 'react-router-dom';
import './App.css';
import { useState, useEffect, useRef } from 'react';
import axios from 'axios';

// 페이지 컴포넌트들
import Home from './pages/Home';
import List from './pages/Recipes/List';
import Korean from './pages/Korean/Korean';
import Chinese from './pages/Chinese/Chinese';
import Western from './pages/Western/Western';
import Popular from './pages/Popular/Popular';
import Challenge from './pages/Challenge/Challenge';
import Japanese from './pages/Japanese/Japanese';
import ListDetail from './pages/Recipes/ListDetail';
import MyPage from './pages/MyPage/MyPage';
import BoardList from './pages/Board/BoardList';
import BoardDetail from './pages/Board/BoardDetail';
import BoardWrite from './pages/Board/BoardWrite';
import BoardUpdate from './pages/Board/BoardUpdate';
import ReportForm from './pages/Board/ReportForm';
import ClubList from './pages/Club/ClubList';
import ClubDetail from './pages/Club/ClubDetail';
import ClubWrite from './pages/Club/ClubWrite';
import ClubApply from './pages/Club/ClubApply';
import Chatbot from './pages/Chatbot/Chatbot';
import ChatbotButton from './pages/Chatbot/ChatbotButton';
import Login from './pages/Auth/Login';
import RegisterModal from './pages/Auth/RegisterModal';
import Header from './header-footer/header';
import Footer from './header-footer/footer';
import ChallengeForm from './pages/ChallengForm/ChallengeForm';
import Ocr from './pages/Ocr/Ocr';
import TarotPage from "./pages/Tarot/TarotPage";
import AdminPage from "./pages/Admin/AdminPage";
import ProtectedAdminRoute from "./components/Admin/ProtectedAdminRoute";
import FindAccount from "./pages/Auth/FindAccount";
import ChallengeDetail from './pages/Challenge/ChallengeDetail';
import AdminNotificationHandler from "./component/AdminNotificationHandler.jsx";
import ChatNotificationHandler from "./component/ChatNotificationHandler";
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import MyChatPage from './pages/MyPage/MyChatPage';
import AdminChatPage from './pages/Admin/AdminChatPage';

function App() {
  const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem("token"));
  const [isChatbotOpen, setIsChatbotOpen] = useState(false);
  const location = useLocation(); // 현재 경로 확인
  const navigate = useNavigate();
  const hasShownAlertRef = useRef(false);
  const firstEffectDone = useRef(false);
  // 챗봇 열기/닫기
  const toggleChatbot = () => {
    setIsChatbotOpen((prev) => !prev);
  };

  // report 페이지에서는 헤더/푸터 숨기기
  const isReportPage = location.pathname.startsWith("/report/");
  useEffect(() => {
    const handleStorageChange = () => {
      setIsLoggedIn(!!localStorage.getItem("token"));
    };
    window.addEventListener("storage", handleStorageChange);
    return () => window.removeEventListener("storage", handleStorageChange);
  }, [setIsLoggedIn]);

  useEffect(() => {
    if (location.pathname !== "/mypage/notifications") {
      hasShownAlertRef.current = false;
    }
  }, [location.pathname]);

  useEffect(() => {
    if (!firstEffectDone.current) {
      firstEffectDone.current = true;
      return;
    }

    const checkAdminNotification = async () => {
      const token = localStorage.getItem("token");
      if (!token || hasShownAlertRef.current) return;
      hasShownAlertRef.current = true;

      try {
        const res = await axios.get("http://localhost:8080/api/notifications/unread", {
          headers: { Authorization: `Bearer ${token}` },
        });

        const unreadAdminAlert = res.data.find(
          (n) => n.message?.startsWith("[관리자]") && !n.isRead
        );

        if (unreadAdminAlert) {
          alert("⚠️ 관리자 알림을 먼저 확인해주세요.");
          navigate("/mypage/notifications");
        }
      } catch (error) {
        console.error("❌ 관리자 알림 확인 실패:", error);
      }
    };

    checkAdminNotification();
  }, [location.pathname, navigate]);
  
  return (
    <>
    {/* ✅ WebSocket 알림 컴포넌트 추가! */}
    <AdminNotificationHandler userRole="admin" />
    <ChatNotificationHandler />
    <ToastContainer position="top-right" autoClose={4000} />
      {!isReportPage && <Header isLoggedIn={isLoggedIn} setIsLoggedIn={setIsLoggedIn} />}

      <Routes>
        {/* ✅ 유저용 채팅 (MyPage) */}
        <Route path="/mypage/chat" element={<MyChatPage />} />
        {/* ✅ 관리자 채팅 */}
        <Route path="/admin/chat" element={<AdminChatPage />} />
        <Route path="/" element={<Home />} />
        <Route path="login" element={<Login setIsLoggedIn={setIsLoggedIn} />} />
        <Route pacth="register" element={<RegisterModal />} />
        <Route path="list" element={<List />} />
        <Route path="list/:id" element={<ListDetail />} />
        <Route path="korean" element={<Korean />} />
        <Route path="japanese" element={<Japanese />} />
        <Route path="chinese" element={<Chinese />} />
        <Route path="western" element={<Western />} />
        <Route path="popular" element={<Popular />} />
        <Route path="challenge" element={<Challenge />} />
        <Route path="challengeform" element={<ChallengeForm />} />
        <Route path="challenge/:id" element={<ChallengeDetail/>}/>
        <Route path="mypage/*" element={<MyPage />} />
        <Route path="boardlist" element={<BoardList />} />
        <Route path="boardlist/:boardId" element={<BoardDetail />} />
        <Route path="boardwrite" element={<BoardWrite />} />
        <Route path="edit/:boardId" element={<BoardUpdate />} />
        <Route path="clublist" element={<ClubList />} />
        <Route path="club/:clubId" element={<ClubDetail />} />
        <Route path="clubwrite" element={<ClubWrite />} />
        <Route path="club/:clubId/apply" element={<ClubApply />} />
        <Route path="/find-account" element={<FindAccount />} />
        <Route path="/tarot" element={<TarotPage />} />
        <Route
          path="/admin/*"
          element={
            <ProtectedAdminRoute>
              <AdminPage />
            </ProtectedAdminRoute>
          }
        />
        <Route path="ocr" element={<Ocr />} />
        {/* report 페이지 (팝업 스타일) */}
        <Route path="report/:boardId" element={<ReportForm />} />
      </Routes>

      {!isReportPage && (
        <>
          <ChatbotButton onClick={toggleChatbot} />
          {isChatbotOpen && <Chatbot />}
          <Footer />
        </>
      )}
    </>
  );
}

export default App;
