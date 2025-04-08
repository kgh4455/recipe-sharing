import { useState, useEffect } from 'react';
import axios from 'axios';
import { useParams, useNavigate } from 'react-router-dom'; // useNavigate 추가
import './ClubDetail.css';

const ClubDetail = () => {
  const { clubId } = useParams();
  const navigate = useNavigate(); // 페이지 이동을 위한 useNavigate 사용
  const [clubDetail, setClubDetail] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isLoggedIn, setIsLoggedIn] = useState(false); // 로그인 상태 관리
  const token = localStorage.getItem("token");
  const [isClosed, setIsClosed] = useState(false); // 모집 종료 여부 상태

  let userEmail = null;
  if (token) {
    try {
      // Base64URL → Base64 변환 (패딩 추가)
      const base64 = token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/");
      const decodedToken = JSON.parse(atob(base64)); // 디코딩
      userEmail = decodedToken.sub; // ✅ 이메일은 'sub' 필드에서 가져오기
    } catch (error) {
      console.error("토큰 디코딩 실패:", error);
    }
  }

  useEffect(() => {    
    const fetchClubDetail = async () => {
      if (!clubId) {
        console.error('클럽 ID가 없습니다!');
        return;
      }

      try {
        const response = await axios.get(`http://localhost:8080/api/club/${clubId}`);
        setClubDetail(response.data);
        console.log(response.data);
        setLoading(false);
        // 모집 마감 여부 확인 (현재 날짜와 비교)
        const currentDate = new Date();
        const clubDate = new Date(response.data.date); // 모집 종료 날짜
        if (currentDate > clubDate) {
          setIsClosed(true); // 모집 마감
        }

      } catch (err) {
        console.error('동호회 정보를 불러오는 데 실패했습니다.', err);
        setError('동호회 정보를 불러오는 데 실패했습니다.');
        setLoading(false);
      }
    };

    fetchClubDetail();

    // 로그인 상태 확인
    const token = localStorage.getItem("token");
    if (token) {
      setIsLoggedIn(true);
    }
  }, [clubId]);

  if (loading) {
    return <div>로딩 중...</div>;
  }

  if (error) {
    return <div>{error}</div>;
  }

  const KakaoMap = ({ location }) => {
    useEffect(() => {

      if (!location) {
        console.error("주소 정보가 없습니다.");
        return;
      }
      const script = document.createElement("script");
      script.src = `https://dapi.kakao.com/v2/maps/sdk.js?appkey=91a07292b70edf8453a8b097ce93a028&libraries=services`;
      script.async = true;
      document.head.appendChild(script);

      script.onload = () => {
        if (window.kakao) {
          const { kakao } = window;
          const geocoder = new kakao.maps.services.Geocoder();
          geocoder.addressSearch(location, (result, status) => {
            if (status === kakao.maps.services.Status.OK) {
              const container = document.getElementById("map");
              const options = {
                center: new kakao.maps.LatLng(result[0].y, result[0].x),
                level: 3,
              };
              const map = new kakao.maps.Map(container, options);
              new kakao.maps.Marker({
                position: options.center,
                map,
              });
            } else {
              console.error("주소 변환 실패", status);
            }
          });
        } else {
          console.error("카카오맵 SDK 로딩 실패");
        }
      };

      script.onerror = () => {
        console.error("카카오맵 스크립트 로딩 실패");
      };
    }, [location]);

    return <div id="map" style={{ width: "100%", height: "500px" }}></div>;
  };

  return (
    <div className="club-detail">
      {clubDetail && (
        <div>
          {/* 클럽 이미지 표시 */}
          {clubDetail.clubImage && (
            <img
              src={`http://localhost:8080/uploads/clubimage/${clubDetail.clubImage}`}
              alt={`${clubDetail.clubName} 이미지`}
              className="club-image"
            />
          )}
          <div style={{ marginBottom: "80px" }}>
          <h1 style={{ color: "#FFA575" }}>{clubDetail.clubName}</h1>
          </div>
          <h3 style={{ color: "#FFA575" }}>이런 분을 찾아요!</h3>
          {/* 클럽 설명 */}
          <div
            style={{ marginBottom: "80px" }}
            dangerouslySetInnerHTML={{ __html: clubDetail.clubFeatures }}
          />
          <div>
            {/* 카카오맵 */}
            <KakaoMap location={clubDetail.location} />
            <h3 style={{ color: "#FFA575" }}>장소: {clubDetail.location}</h3>
            <div style={{ color: "#5E5E5E", marginBottom: "80px" }}>
              자세한 주소는 모임 참여시 안내드립니다.
            </div>
          </div>
          <div className="clubdetail1">
            <h3 style={{ color: "#FFA575" }}>모집 일정</h3>
            <div style={{ marginBottom: "80px" }}>
              {clubDetail.createdAt.split('T')[0]} ~ {clubDetail.date}
            </div>
          </div>

          {/* 돌아가기 및 모임 신청 버튼 */}
          <div className="button-container">
            <button className="back-button-club" onClick={() => navigate('/clublist')}>
              돌아가기
            </button>
            {isLoggedIn && userEmail !== clubDetail.recruiterEmail &&!isClosed &&( // 로그인한 사용자와 주최자가 다를 때만 신청 버튼 보이기
              <button className="apply-button" onClick={() => navigate(`/club/${clubDetail.clubId}/apply`)}>
                신청하기
              </button>
            )}
             {isClosed && ( // 모집 마감일 경우
              <button className="apply-button" disabled style={{color:"black", backgroundColor:"#eeeeee"}}>
                모집 마감
              </button>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default ClubDetail;