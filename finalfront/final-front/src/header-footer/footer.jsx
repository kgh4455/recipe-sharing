import './footer.css';
import Rabbit from '../assets/rabbit.png'
import { Link } from 'react-router-dom';


export default function Footer(){
  const token = localStorage.getItem("token"); 
    let isAdmin = false;

    if (token) {
        try {
            
            const parts = token.split(".");
            if (parts.length !== 3) {
                throw new Error("잘못된 토큰 형식입니다.");
            }

            const base64Payload = parts[1];
            
           
            const base64 = base64Payload.replace(/-/g, '+').replace(/_/g, '/');
            const decodedPayload = JSON.parse(atob(base64)); 
            console.log("Decoded Payload: ", decodedPayload); 

            
            isAdmin = decodedPayload.role === "ROLE_ADMIN";
        } catch (error) {
            console.error("토큰 디코딩 오류:", error);
        }
    }

    return (
        <footer className='footer'>
          <div className='footer-content'>
          <p>이 프로젝트는 포트폴리오 목적으로 제작되었습니다. 상업적인 용도로 사용하지 않을 것이며, 기존 사이트( )에서 자료를 가져와 활용하였습니다. 이 프로젝트의 목적은 개인적인 학습과 기술 데모를 위한 것입니다. 원작자가 아닌 다른 소스로부터 자료를 가져왔음을 밝힙니다.</p>
          <p>© 2025 레시피 사이트. All rights reserved.</p>
          </div>
        </footer>
      );
}