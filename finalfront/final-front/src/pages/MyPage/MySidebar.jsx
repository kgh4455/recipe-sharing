import { Link } from 'react-router-dom';
import '../MyPage/MySidebar.css';

export default function MySidebar() {
  return (
    <nav className="mypage-sidebar">
      <ul>
        <li><Link to="/mypage/info">닉네임, 이메일 표시</Link></li>
        <li>  <Link to="/mypage/user-edit">회원 정보 수정</Link></li>
        <li><Link to="/mypage/activity">내 활동</Link></li>
        <li><Link to="/mypage/notifications">알림</Link></li>
        <li><Link to="/mypage/inquiries">1:1 문의 목록</Link></li>
        <li><Link to="/mypage/inquiries/create">1:1 문의 작성</Link></li>
        <li><Link to="/mypage/withdrawal">회원 탈퇴</Link></li>
      </ul>
    </nav>
  );
}