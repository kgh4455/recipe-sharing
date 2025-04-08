import './Home.css';
import axios from "axios";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import banner from '../assets/banner.jpg';
import './Recipes/List.css';
import { FaStar } from "react-icons/fa";
import { FaRegStar } from "react-icons/fa6";

export default function Home() {
  const [weather, setWeather] = useState({});
  const [error, setError] = useState(null);
  const [recipes, setRecipes] = useState([]);
  const [popularRecipes, setPopularRecipes] = useState([]); // 인기 레시피 추가
  const [latestRecipes, setLatestRecipes] = useState([]); // 최신 레시피 추가
  const [currentDate, setCurrentDate] = useState(""); // currentDate 상태 추가
  const [typeRecipes, setTypeRecipes] = useState([]); 
  const [isLoggedIn, setIsLoggedIn] = useState(false); // 로그인 상태
  const [userName, setUserName] = useState(null); // 사용자 이름
  const [favorites, setFavorites] = useState({});
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [commentCounts, setCommentCounts] = useState({});

  useEffect(() => {
    const token = localStorage.getItem("token"); // localStorage에서 토큰을 가져옵니다.
    if (token) {
      const username = getUserNameFromToken(token); // 토큰에서 사용자 이름을 추출합니다.
      if (username) {
        setUserName(username); // 사용자 이름을 상태에 저장
        setIsLoggedIn(true); // 로그인 상태로 설정
      } else {
        setIsLoggedIn(false); // 사용자 이름이 없으면 로그인 상태 아님
      }
    } else {
      setIsLoggedIn(false); // 토큰이 없으면 로그인 상태 아님
    }

    
    // 현재 날짜 설정
    const now = new Date();
    setCurrentDate(now.toLocaleDateString("ko-KR", {
      year: "numeric",
      month: "long",
      day: "numeric",
      weekday: "long"
    }));
  }, []);
  //top으로 이동
  const scrollToTop = () => {
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  
  function getUserNameFromToken(token) {
    if (!token) return null; // 토큰이 없으면 null 반환
    
    try {
      // Base64 URL 디코딩 (패딩 추가)
      const base64Url = token.split(".")[1];
      const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/");
      
      // 디코딩된 JSON을 객체로 변환
      const decodedToken = JSON.parse(atob(base64));
      
      // 'sub' 필드에서 사용자 이름 추출 (백엔드에서 sub 필드에 사용자 이름을 저장한다고 가정)
      return decodedToken.sub; // JWT의 'sub' 필드가 사용자 이름을 포함
    } catch (error) {
      console.error("토큰 디코딩 오류:", error);
      return null;
    }
  }

  useEffect(() => {
    const fetchWeather = async () => {
      try {
        const now = new Date();
        const year = now.getFullYear();
        const month = String(now.getMonth() + 1).padStart(2, '0');
        const date = String(now.getDate()).padStart(2, '0');
        const hours = String(now.getHours()).padStart(2, '0');
        const minutes = '00';

        const response = await axios.get('http://localhost:8080/api/weather', {
          params: {
            baseDate: `${year}${month}${date}`,
            baseTime: `${hours}${minutes}`,
            nx: 60,
            ny: 127,
          },
          responseType: 'text'
        });

        const parser = new DOMParser();
        const xmlDoc = parser.parseFromString(response.data, 'text/xml');
        const items = xmlDoc.getElementsByTagName('item');
        const weatherData = {};

        for (let i = 0; i < items.length; i++) {
          const category = items[i].getElementsByTagName('category')[0].textContent;
          const value = items[i].getElementsByTagName('obsrValue')[0].textContent;

          if (category === 'T1H') {
            weatherData.temperature = `${value}°C`;
          } else if (category === 'PTY') {
            weatherData.precipitation = value;
          }
        }

        if (weatherData.precipitation === '0') {
          weatherData.precipitation = '맑음';
        } else if (weatherData.precipitation === '1') {
          weatherData.precipitation = '비';
        } else if (weatherData.precipitation === '2') {
          weatherData.precipitation = '흐림';
        } else if (weatherData.precipitation === '3') {
          weatherData.precipitation = '눈';
        }

        setWeather(weatherData);
        setError(null);
        const recipeResponse = await axios.get('http://localhost:8080/api/weather/recipe', {
          params: { precipitation: weatherData.precipitation }
        });
        setRecipes(recipeResponse.data);

        // 인기 레시피 가져오기
        const popularResponse = await axios.get('http://localhost:8080/api/main/popular');
        setPopularRecipes(popularResponse.data);

        // 최신 레시피 가져오기
        const latestResponse = await axios.get('http://localhost:8080/api/main/recent');
        setLatestRecipes(latestResponse.data);

        const token = localStorage.getItem("token");
        if (token) {
          const response = await axios.get('http://localhost:8080/api/main/recommend', {
            headers: {
              Authorization: `Bearer ${token}`
            }
          });
          setTypeRecipes(response.data);
        }

      } catch (error) {
        setError("날씨 정보를 가져오는 데 실패했습니다.", error);
      }
    };
    fetchWeather();
  }, []);

  const renderWeatherIcon = (precipitation) => {
    switch (precipitation) {
      case '맑음':
        return '☀️';
      case '비':
        return '🌧️';
      case '흐림':
        return '☁️';
      case '눈':
        return '❄️';
      default:
        return null;
    }
  };

  const handleClick = (recipeId) => {
    console.log("클릭된 레시피 ID: ", recipeId);
    axios.put(`http://localhost:8080/api/recipes/${recipeId}/increase-view`)
      .then(response => {
        console.log("조회수 증가 : ", response.data);
      })
      .catch(error => {
        console.log("에러", error);
      });
  };
  useEffect(() => {
    const token = localStorage.getItem('token');
    setToken(token);

    // 유효하지 않은 토큰 처리 (예: 만료된 토큰)
    if (token) {
        axios.get(`http://localhost:8080/api/recipes`)
            .then(response => {
                setRecipes(response.data);
            })
            .catch(error => {
                console.log("데이터를 불러오는 중 오류 발생:", error);
                alert("토큰이 만료되었거나 유효하지 않습니다.");
                localStorage.removeItem('token');
                setToken(null);  // 토큰 상태 초기화
            });
            axios.get(`http://localhost:8080/api/recipes/favorites`, {
                headers: { 'Authorization': `Bearer ${token}` }
            })
            .then(response => {
                const favoritesData = response.data.favorites;  // response.data.favorites 확인
                const favoritesObj = {};
                favoritesData.forEach(recipe => {
                    favoritesObj[recipe.recipesId] = true;
                });
                setFavorites(favoritesObj);
            })
            .catch(error => {
                console.log("즐겨찾기 정보를 불러오는 중 오류 발생:", error);
                alert("즐겨찾기 정보를 불러오는 데 실패했습니다.");
            });
        } else {
            // 토큰이 없으면 즐겨찾기 정보를 초기화
            setFavorites({});
        }
}, []);

  const handleFavorite = (recipesId) => {
    if (!token) {
        alert("로그인이 필요합니다!");  // 로그인되지 않았을 때 알림
        return;
    }

    const isCurrentlyFavorite = favorites[recipesId];
    const method = isCurrentlyFavorite ? 'DELETE' : 'POST';

    axios({
        method: method,
        url: `http://localhost:8080/api/recipes/${recipesId}/favorite`,
        headers: { 'Authorization': `Bearer ${token}` }
    })
        .then(response => {
            console.log(isCurrentlyFavorite ? "즐겨찾기 삭제 성공" : "즐겨찾기 추가 성공", response);
            setFavorites(prev => ({ ...prev, [recipesId]: !isCurrentlyFavorite }));
        })
        .catch(error => {
            console.log(isCurrentlyFavorite ? "즐겨찾기 삭제 실패" : "즐겨찾기 추가 실패", error);
            alert("즐겨찾기 작업에 실패했습니다.");
        });
}
 const fetchCommentCount = async (recipeId) => {
        try {
            const response = await fetch(`http://localhost:8080/api/recipe/review/count/${recipeId}`, {
                method: "GET",
                headers: {
                    "Content-Type": "application/json",
                    "Accept": "application/json",
                },
            });
    
            if (!response.ok) {
                throw new Error(`HTTP error! Status: ${response.status}`);
            }
    
            const data = await response.json();  // JSON 데이터 가져오기
            console.log(`🔍 recipeId: ${recipeId}, 댓글 수: ${data.count}`);
            return data.count;  // 댓글 수 반환
        } catch (error) {
            console.error("댓글 수 가져오기 실패:", error);
            return 0; // 오류 발생 시 0으로 반환
        }
    };

    useEffect(() => {
        const fetchAllComments = async () => {
            const counts = {};
            for (const recipe of recipes) {
                counts[recipe.recipesId] = await fetchCommentCount(recipe.recipesId);
            }
            console.log("댓글 데이터:", counts); 
            setCommentCounts(counts);
        };
    
        if (recipes.length > 0) {
            fetchAllComments();
        }
    }, [recipes]);
  return (
    <div className="home">
      <Link to={'/tarot'}>
        <img
          src={banner}
          alt="banner"
          style={{ borderRadius: '15px'}} // 여기서 border-radius 값을 원하는 대로 설정
          className="tarobanner"
        />
      </Link>
      <h2 className="small-title">☀️ 오늘의 날씨에 어울리는 레시피! 🌧️</h2>
        <div className="weather">
          <div className="weather-head">
            <p>{currentDate}</p> {/* currentDate 출력 */}
          </div>
          <div className="weather-card">
            {error && <p className="error">{error}</p>}
            {Object.keys(weather).length > 0 && (
              <p>{weather.temperature} {weather.precipitation} {renderWeatherIcon(weather.precipitation)}  </p>
            )}
          </div>
        </div>
          <div className="recipes-grid">
            {recipes.map((recipe, index) => (
              <div key={index} className="recipes-card">
                <Link to={`/list/${recipe.recipesId}`} onClick={() => handleClick(recipe.recipesId)}>
                      <img className="rc-list-img"  src={`http://localhost:8080/uploads/api/userrecipes/${recipe.foodImg}`} alt={recipe.foodName}/>
                  </Link>
                  <h3 className="rc-fn">{recipe.foodName}</h3>
                  <div className="recipes-grid-btn" style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                    <a onClick={() => handleFavorite(recipe.recipesId)}>
                      {favorites[recipe.recipesId] ? <FaStar size={24} color="gold" /> : <FaRegStar />}
                    </a>
                    <div style={{ display: "flex", gap: "10px" }}>
                      <p>💬 {commentCounts[recipe.recipesId]}</p>
                      <p>👀 {recipe.view}</p>
                    </div>
                  </div>
              </div>
            ))}
          </div>

      <h2 className="small-title">🔥 베스트 레시피 🔥</h2>
      <div className="recipes-grid">
        {popularRecipes.map((recipe, index) => (
          <div key={index} className="recipes-card">
           <Link to={`/list/${recipe.recipesId}`} onClick={() => handleClick(recipe.recipesId)}>
                      <img className="rc-list-img"  src={`http://localhost:8080/uploads/api/userrecipes/${recipe.foodImg}`} alt={recipe.foodName}/>
                  </Link>
                  <h3 className="rc-fn">{recipe.foodName}</h3>
                  <div className="recipes-grid-btn" style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                    <a onClick={() => handleFavorite(recipe.recipesId)}>
                      {favorites[recipe.recipesId] ? <FaStar size={24} color="gold" /> : <FaRegStar />}
                    </a>
                    <div style={{ display: "flex", gap: "10px" }}>
                      <p>💬 {commentCounts[recipe.recipesId]}</p>
                      <p>👀 {recipe.view}</p>
                    </div>
                  </div>
          </div>
        ))}
      </div>

      <h2 className="small-title">🆕 새로 나온 레시피 🆕</h2>
      <div className="recipes-grid">
        {latestRecipes.map((recipe, index) => (
          <div key={index} className="recipes-card">
           <Link to={`/list/${recipe.recipesId}`} onClick={() => handleClick(recipe.recipesId)}>
                      <img className="rc-list-img"  src={`http://localhost:8080/uploads/api/userrecipes/${recipe.foodImg}`} alt={recipe.foodName}/>
                  </Link>
                  <h3 className="rc-fn">{recipe.foodName}</h3>
                  <div className="recipes-grid-btn" style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                    <a onClick={() => handleFavorite(recipe.recipesId)}>
                      {favorites[recipe.recipesId] ? <FaStar size={24} color="gold" /> : <FaRegStar />}
                    </a>
                    <div style={{ display: "flex", gap: "10px" }}>
                      <p>💬 {commentCounts[recipe.recipesId]}</p>
                      <p>👀 {recipe.view}</p>
                    </div>
                  </div>
          </div>
        ))}
      </div>

      {isLoggedIn && (
        <>
          <h2 className="small-title">🧡{userName}님이 좋아할만한 레시피 🧡</h2>
          <div className="recipes-grid">
            {typeRecipes.length > 0 ? (
              typeRecipes.map((recipe, index) => (
                <div key={index} className="recipes-card">
                  <Link to={`/list/${recipe.recipesId}`} onClick={() => handleClick(recipe.recipesId)}>
                      <img className="rc-list-img"  src={`http://localhost:8080/uploads/api/userrecipes/${recipe.foodImg}`} alt={recipe.foodName}/>
                  </Link>
                  <h3 className="rc-fn">{recipe.foodName}</h3>
                  <div className="recipes-grid-btn" style={{ display: "flex", justifyContent: "space-between", alignItems: "center" }}>
                    <a onClick={() => handleFavorite(recipe.recipesId)}>
                      {favorites[recipe.recipesId] ? <FaStar size={24} color="gold" /> : <FaRegStar />}
                    </a>
                    <div style={{ display: "flex", gap: "10px" }}>
                      <p>💬 {commentCounts[recipe.recipesId]}</p>
                      <p>👀 {recipe.view}</p>
                    </div>
                  </div>
                </div>
              ))
            ) : (
              <p>회원님의 관심 목록을 기반으로 제공되는 서비스입니다. 관심 목록에 레시피를 추가해보세요!</p>
            )}
          </div>
        </>
      )}
    </div>
  );
}
