import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import axios from "axios";
import '../Recipes/List.css';
import { FaStar } from "react-icons/fa";
import { FaRegStar } from "react-icons/fa6";


export default function Popular() {
    const [recipes, setRecipes] = useState([]);
    const [favorites, setFavorites] = useState({});
    const [token, setToken] = useState(localStorage.getItem('token'));
    const [visibleCount, setVisibleCount] = useState(8);
    const [commentCounts, setCommentCounts] = useState({});

  //top으로 이동
  const scrollToTop = () => {
    window.scrollTo({ top: 0, behavior: "smooth" });
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

    useEffect(() => {
        const fetchRecipes = () => {
            axios.get(`http://localhost:8080/api/recipes/popular`)
                .then(response => {
                    setRecipes(response.data);
                })
                .catch(error => {
                    console.log("오류 발생", error);
                });
        };
        fetchRecipes();
    }, []);




    const handleClick = (recipesId) => {
        axios.put(`http://localhost:8080/api/recipes/${recipesId}/increase-view`)
            .then(response => {
                console.log("조회수 증가 : ", response.data);
            })
            .catch(error => {
                console.log("에러", error);
            });
    }

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
        <div className="recipes-main">
            <h1 className="big-title">인기 레시피</h1>
            <div className="recipes-grid">
                {recipes.slice(0, visibleCount).map((recipe) => (
                    <div key={recipe.recipesId} className="recipes-card">
                        <Link to={`/list/${recipe.recipesId}`} onClick={() => handleClick(recipe.recipesId)}>
                            <img className="rc-list-img" src={`http://localhost:8080/uploads/api/userrecipes/${recipe.foodImg}`} alt={recipe.foodName}/>
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
            {visibleCount < recipes.length && (
                <button className="load-more" onClick={() => setVisibleCount(visibleCount + 8)}>
                    더보기
                </button>
            )}
        </div>
    );
}