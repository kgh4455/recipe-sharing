import { useState, useEffect } from "react";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import "../../styles/AdminRecipeStyles.css";
import "../../styles/AdminRecipes.css";

const AdminRecipes = () => {
  const [recipes, setRecipes] = useState([]);
  const [visibleRecipes, setVisibleRecipes] = useState(8);
  const [search, setSearch] = useState("");
  const [category, setCategory] = useState("");
  const [weather, setWeather] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    fetchRecipes();
  }, []);

  const fetchRecipes = async (keyword = "", categoryId = "", weatherId = "") => {
    try {
      const token = localStorage.getItem("token");
      const response = await axios.get("http://localhost:8080/admin/recipes", {
        headers: { Authorization: `Bearer ${token}` },
        params: { keyword, categoryId, weatherId },
      });
      setRecipes(response.data);
    } catch (error) {
      console.error("❌ 레시피 불러오기 실패:", error);
    }
  };

  const handleSearch = () => {
    fetchRecipes(search, category, weather);
    setVisibleRecipes(8); // 검색 시 초기 개수로 설정
  };

  const handleDelete = async (id) => {
    if (!window.confirm("정말 삭제하시겠습니까?")) return;
    try {
      const token = localStorage.getItem("token");
      await axios.delete(`http://localhost:8080/admin/recipes/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      alert("레시피 삭제 완료!");
      fetchRecipes();
    } catch (error) {
      console.error("❌ 레시피 삭제 실패:", error);
    }
  };

  return (
    <div className="adminz-container">
      <h1 className="big-title">레시피 관리</h1>
      <div className="recipes-search">
        <select className="category-filter" value={category} onChange={(e) => setCategory(e.target.value)}>
          <option value="">카테고리 선택</option>
          <option value="1">한식</option>
          <option value="2">중식</option>
          <option value="3">일식</option>
          <option value="4">양식</option>
        </select>
        <select className="weather-filter" value={weather} onChange={(e) => setWeather(e.target.value)}>
          <option value="">날씨 선택</option>
          <option value="1">맑음</option>
          <option value="2">비</option>
          <option value="3">눈</option>
          <option value="4">흐림</option>
        </select>
        <input
          type="text"
          className="search-bar"
          placeholder="검색어 입력..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
        />
        <button className="rc-se" onClick={handleSearch}>검색</button>
      <button className="us-edit-button" onClick={() => navigate("/admin/recipes/add")}>레시피 추가</button>
      </div>
      <div className="recipes-grid">
        {recipes.slice(0, visibleRecipes).map((recipe) => {
          let imageUrl = recipe.foodImg ? `http://localhost:8080/uploads/${encodeURIComponent(recipe.foodImg)}` : "/default-image.png";
          return (
            <div key={recipe.recipesId} className="recipes-card">
              <img src={imageUrl} alt={recipe.foodName} className="rc-list-img" onError={(e) => (e.target.src = "/default-image.png")} />
              <h3 className="rc-fn">{recipe.foodName}</h3>
              <div className="recipea-buttons">
                <button className="del-im" style={{width:"50%"}} onClick={() => navigate(`/admin/recipes/edit/${recipe.recipesId}`)}>수정</button>
                <button className="del-im" style={{width:"50%"}} onClick={() => handleDelete(recipe.recipesId)}>삭제</button>
              </div>
            </div>
          );
        })}
      </div>
      {visibleRecipes < recipes.length && (
        <button className="load-more" onClick={() => setVisibleRecipes((prev) => prev + 8)}>더보기</button>
      )}
    </div>
  );
};

export default AdminRecipes;