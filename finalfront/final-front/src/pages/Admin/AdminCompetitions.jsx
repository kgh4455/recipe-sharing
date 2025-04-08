import { useEffect, useState } from "react";
import axios from "axios";
import "../../styles/AdminCompetitions.css";

const AdminCompetitions = () => {
  const [recipes, setRecipes] = useState([]);
  const [selectedRecipe, setSelectedRecipe] = useState(null);
  const [showModal, setShowModal] = useState(false);
  const [tab, setTab] = useState("pending"); // 🔹 현재 탭 상태
  const [visibleRecipes, setVisibleRecipes] = useState(8);

  const categoryMap = {
    1: "한식",
    2: "중식",
    3: "일식",
    4: "양식",
  };

  const fetchRecipes = async () => {
    try {
      const token = localStorage.getItem("token");
      const url =
        tab === "pending"
          ? "http://localhost:8080/admin/user-recipes/pending"
          : "http://localhost:8080/admin/user-recipes/approved";

      const res = await axios.get(url, {
        headers: { Authorization: `Bearer ${token}` },
      });
      setRecipes(res.data);
    } catch (error) {
      console.error("❌ 레시피 불러오기 실패:", error);
    }
  };

  const approveRecipe = async (id) => {
    if (!window.confirm("이 레시피를 승인하시겠습니까?")) return;
    try {
      const token = localStorage.getItem("token");
      await axios.patch(
        `http://localhost:8080/admin/user-recipes/${id}/approve`,
        {},
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      alert("✅ 승인 완료");
      fetchRecipes();
    } catch (error) {
      console.error("❌ 승인 실패:", error);
    }
  };

  const deleteRecipe = async (id) => {
    if (!window.confirm("이 레시피를 삭제하시겠습니까?")) return;
    try {
      const token = localStorage.getItem("token");
      await axios.delete(`http://localhost:8080/admin/user-recipes/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      alert("🗑️ 삭제 완료");
      fetchRecipes();
    } catch (error) {
      console.error("❌ 삭제 실패:", error);
    }
  };

  const openDetailModal = async (id) => {
    try {
      const token = localStorage.getItem("token");
      const res = await axios.get(
        `http://localhost:8080/admin/user-recipes/${id}`,
        {
          headers: { Authorization: `Bearer ${token}` },
        }
      );
      console.log("🔍 상세 레시피 응답:", res.data); // ✅ 이거 꼭 추가!!!
      setSelectedRecipe(res.data);
      setShowModal(true);
    } catch (error) {
      console.error("❌ 상세 레시피 불러오기 실패:", error);
    }
  };

  useEffect(() => {
    fetchRecipes();
  }, [tab]);

  return (
    <div className="adminz-container">
      <h1 className="big-title">🎯 맛있는 도전 관리</h1>

      {/* 🔹 탭 버튼 */}
      <div className="tab-buttons">
        <button
          className={`tab-btn ${tab === "pending" ? "active" : "none"}`}
          onClick={() => setTab("pending")}
        >
          승인 대기
        </button>
        <button
          className={`tab-btn ${tab === "approved" ? "active" : "none"}`}
          onClick={() => setTab("approved")}
        >
          승인 완료
        </button>
      </div>

      {recipes.length === 0 ? (
        <p>📭 레시피가 없습니다.</p>
      ) : (
        <div className="recipea-grid">
          {recipes.map((recipe) => {
            const encodedImg = recipe.foodImg ? encodeURIComponent(recipe.foodImg) : "";
            const imageUrl = encodedImg
              ? `http://localhost:8080/uploads/${encodedImg}`
              : "/default-image.png";

            return (
              <div key={recipe.userRecipesId} className="recipes-card">
                <img
                  className="rc-list-img"
                  src={imageUrl}
                  alt="요리 이미지"
                  onError={(e) => {
                    e.target.src = "/default-image.png";
                  }}
                />
                <h3 className="rc-fn">{recipe.foodName}</h3>
                <p className="ac-p"><strong>작성자:</strong> {recipe.writerName} ({recipe.writerEmail})</p>
                <p className="ac-p"><strong>카테고리:</strong> {categoryMap[recipe.categoryId] || "기타"}</p>
                <p className="ac-p"><strong>조리 시간:</strong> {recipe.foodTime}분</p>
                <button className="approves-btn"  style={{marginTop:"10px"}}  onClick={() => openDetailModal(recipe.userRecipesId)}>레시피 상세보기</button>
                <div className="button-container" style={{marginTop:"10px"}}>
                  {tab === "pending" && (
                    <>
                      <button className="dellbtn" onClick={() => deleteRecipe(recipe.userRecipesId)}>거절</button>
                      <button className="approve-btn" onClick={() => approveRecipe(recipe.userRecipesId)}>승인</button>
                    </>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* 🔹 상세 정보 모달 */}
      {showModal && selectedRecipe && (
        <div className="modal-overlay" onClick={() => setShowModal(false)}>
          <div className="modal-content" onClick={(e) => e.stopPropagation()}>
            <img
              src={`http://localhost:8080/uploads/${encodeURIComponent(selectedRecipe.foodImg)}`}
              alt="대표 이미지"
              className="rc-list-img" // ✅ 클래스명으로 스타일 분리
              onError={(e) => (e.target.src = "/default-image.png")}
            />
            <p className="ac-p"><strong>레시피명:</strong> {selectedRecipe.foodName}</p>
            <p className="ac-p"><strong>카테고리:</strong> {categoryMap[selectedRecipe.categoryId]}</p>
            <p className="ac-p"><strong>조리시간:</strong> {selectedRecipe.foodTime}분</p>
            <p className="ac-p"><strong>재료:</strong> 
            {typeof selectedRecipe.ingredientsss === "string" && selectedRecipe.ingredientsss.trim()
              ? selectedRecipe.ingredientsss.split(',').map((ing, i, arr) => (
                  <span key={i}>{ing.trim()}{i < arr.length - 1 ? ', ' : ''}</span>
                ))
              : "등록된 재료 없음"}
          </p>
            <hr/>
            <br/>
            <br/>
            {[...Array(6)].map((_, i) => {
              const step = selectedRecipe[`step${i + 1}`];
              const stepImg = selectedRecipe[`stepImg${i + 1}`];
              return (
                step && (
                  <div key={i} style={{ marginBottom: "40px" }}>
                    <p><strong>Step {i + 1}:</strong> {step}</p>
                    {stepImg && (
                     <img
                     src={`http://localhost:8080/uploads/${encodeURIComponent(stepImg)}`}
                     alt={`Step ${i + 1}`}
                     className="modal-img"
                     onError={(e) => (e.target.src = "/default-image.png")}
                   />
                    )}
                  </div>
                )
              );
            })}
            <button  className="del-im" onClick={() => setShowModal(false)} >닫기</button>
          </div>
        </div>
      )}
      {visibleRecipes < recipes.length && (
        <button className="load-more" onClick={() => setVisibleRecipes((prev) => prev + 8)}>더보기</button>
      )}
    </div>
  );
};

export default AdminCompetitions;