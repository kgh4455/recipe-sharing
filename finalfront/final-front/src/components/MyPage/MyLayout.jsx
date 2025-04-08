
import './MyLayout.css';

export default function MyLayout({ children }) {
  return (
    <div className="mypage-layout">
      <main className="mypage-main">
        {children}
      </main>
    </div>
  );
}