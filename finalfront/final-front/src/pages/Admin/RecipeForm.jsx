import React, { useState, useEffect } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import "../../styles/RecipeForm.css"; // ✅ 스타일 적용

const categoryOptions = [
  { id: 1, label: "한식" },
  { id: 2, label: "중식" },
  { id: 3, label: "일식" },
  { id: 4, label: "양식" },
];

const weatherOptions = [
  { id: 1, label: "맑음" },
  { id: 2, label: "비" },
  { id: 3, label: "눈" },
  { id: 4, label: "흐림" },
];

const RecipeForm = ({ isEditing }) => {
  const navigate = useNavigate();
  const { id } = useParams();

  const [recipe, setRecipe] = useState({
    foodName: "",
    foodTime: "",
    categoryId: "",
    foodImg: null,
    existingFoodImg: "",
    ingredients: [],
    steps: [{ description: "", image: null, existingImage: "" }],
    weatherId: null,
    deletedImages: [] // ✅ 삭제된 이미지 목록을 저장하는 배열 추가
  });
 /** ✅ 단계 추가 (+ 버튼) */
/** ✅ 단계 추가 (+ 버튼) */
const addStep = () => {
  setRecipe((prevRecipe) => {
    if (prevRecipe.steps.length >= 6) {
      alert("최대 6단계까지만 추가할 수 있습니다.");
      return prevRecipe;
    }

    return {
      ...prevRecipe,
      steps: [
        ...prevRecipe.steps,
        { description: "", image: null, existingImage: "" }, // 새 단계 추가
      ],
    };
  });
};

/** ✅ 단계 삭제 */
const removeStep = (index) => {
  setRecipe((prevRecipe) => {
    // ✅ 1개 이상의 단계가 있을 때만 삭제
    if (prevRecipe.steps.length > 1) {
      const updatedSteps = prevRecipe.steps.filter((_, i) => i !== index);
      return { ...prevRecipe, steps: updatedSteps };
    }
    // ✅ 마지막 단계라면 내용을 비우기만 함 (단계 자체는 유지)
    else {
      return {
        ...prevRecipe,
        steps: [{ description: "", image: null, existingImage: "" }]
      };
    }
  });
};
const removeImage = (index) => {
  setRecipe((prevRecipe) => {
    let updatedSteps = [...prevRecipe.steps];
    let updatedDeletedImages = prevRecipe.deletedImages ? [...prevRecipe.deletedImages] : []; // ✅ undefined 방지

    if (index === -1) {
      // ✅ 대표 이미지 삭제 시
      if (prevRecipe.existingFoodImg) {
        updatedDeletedImages.push(prevRecipe.existingFoodImg);
        deleteImage(prevRecipe.existingFoodImg); // ✅ 서버에서 이미지 삭제 요청
      }
      return { 
        ...prevRecipe, 
        foodImg: null, 
        existingFoodImg: "", 
        deletedImages: updatedDeletedImages 
      };
    } else {
      // ✅ 단계별 이미지 삭제 시
      if (updatedSteps[index].existingImage) {
        updatedDeletedImages.push(updatedSteps[index].existingImage);
        deleteImage(updatedSteps[index].existingImage); // ✅ 서버에서 이미지 삭제 요청
      }
      updatedSteps[index] = { ...updatedSteps[index], image: null, existingImage: "" };
      return { ...prevRecipe, steps: updatedSteps, deletedImages: updatedDeletedImages };
    }
  });
};
/** ✅ 재료 추가 */
const addIngredient = () => {
  setRecipe((prevRecipe) => ({
    ...prevRecipe,
    ingredients: [...prevRecipe.ingredients, ""],
  }));
};

/** ✅ 재료 삭제 */
const removeIngredient = (index) => {
  setRecipe((prevRecipe) => ({
    ...prevRecipe,
    ingredients: prevRecipe.ingredients.filter((_, i) => i !== index),
  }));
};

/** ✅ 재료 입력값 변경 */
const handleIngredientChange = (index, value) => {
  setRecipe((prevRecipe) => {
    const updatedIngredients = [...prevRecipe.ingredients];
    updatedIngredients[index] = value;
    return { ...prevRecipe, ingredients: updatedIngredients };
  });
};

  /** ✅ 기존 레시피 데이터를 불러오기 (수정 모드) */
/** ✅ 기존 레시피 데이터를 불러오기 (수정 모드) */
useEffect(() => {
  if (isEditing && id) {
    const token = localStorage.getItem("token");

    axios
      .get(`http://localhost:8080/admin/recipes/${id}`, {
        headers: { Authorization: token ? `Bearer ${token}` : "" },
      })
      .then((response) => {
        console.log("🔍 API 응답 데이터:", response.data);

        const {
          foodName,
          foodTime,
          categoryId,
          foodImg,
          ingredients,
          weatherId,
          ...stepsData
        } = response.data;

        // ✅ 단계별 설명 배열 변환
        const steps = Array(6)
          .fill({ description: "", image: null, existingImage: "" })
          .map((_, index) => ({
            description: stepsData[`step${index + 1}`] || "",
            existingImage: stepsData[`stepImg${index + 1}`]
              ? `http://localhost:8080/uploads/${encodeURIComponent(stepsData[`stepImg${index + 1}`])}`
              : "",
            image: null,
          }));

        setRecipe((prev) => ({
          ...prev,
          foodName,
          foodTime,
          categoryId,
          foodImg: null,
          existingFoodImg: foodImg
            ? `http://localhost:8080/uploads/${encodeURIComponent(foodImg)}`
            : "",
          ingredients: Array.isArray(ingredients) ? ingredients : [],
          weatherId,
          steps,
        }));
      })
      .catch((error) => {
        console.error("❌ 레시피 불러오기 실패:", error);
      });
  }
}, [isEditing, id]); // ✅ useEffect 끝

  
          

  
  
  
  


  /** ✅ 입력값 핸들러 */
  const handleChange = (e) => {
    setRecipe({ ...recipe, [e.target.name]: e.target.value });
  };

  /** ✅ 이미지 업로드 핸들러 */
  const handleImageUpload = (e, index) => {
    const file = e.target.files[0];

    if (index === -1) {
      setRecipe({
        ...recipe,
        foodImg: file || recipe.foodImg,
        existingFoodImg: file ? URL.createObjectURL(file) : recipe.existingFoodImg,
      });
    } else {
      const updatedSteps = [...recipe.steps];
      updatedSteps[index] = {
        ...updatedSteps[index],
        image: file || updatedSteps[index].image,
        existingImage: file ? URL.createObjectURL(file) : updatedSteps[index].existingImage,
      };
      setRecipe({ ...recipe, steps: updatedSteps });
    }
  };
  /** ✅ 단계별 설명 입력 */
const handleStepChange = (index, value) => {
  setRecipe((prevRecipe) => {
    const updatedSteps = [...prevRecipe.steps];
    updatedSteps[index] = { ...updatedSteps[index], description: value };
    return { ...prevRecipe, steps: updatedSteps };
  });
};
const deleteImage = async (fileName) => {
  try {
      // 백엔드로 보낼 URL을 올바르게 수정
      const cleanedFileName = fileName.replace("http://localhost:8080/uploads/", ""); 

      await axios.delete(`http://localhost:8080/uploads/${cleanedFileName}`);
      console.log("✅ 이미지 삭제 성공");
  } catch (error) {
      console.error("❌ 이미지 삭제 실패", error);
  }
};


  /** ✅ 제출 (추가/수정) */
/** ✅ 제출 (추가/수정) */
const handleSubmit = async () => {
  // ✅ 필수 입력값 검증
  if (!recipe.categoryId || isNaN(recipe.categoryId)) {
    alert("❌ 카테고리를 선택하세요.");
    return;
  }

  if (!recipe.weatherId || isNaN(recipe.weatherId)) {
    alert("❌ 날씨 정보를 선택하세요.");
    return;
  }

  // ✅ 토큰 확인
  const token = localStorage.getItem("token");
  console.log("📢 저장된 토큰:", token);
  if (!token) {
    alert("❌ 로그인 정보가 없습니다. 다시 로그인해주세요.");
    navigate("/login");
    return;
  }

  const formData = new FormData();
  formData.append("foodName", recipe.foodName);
  formData.append("foodTime", recipe.foodTime);
  formData.append("categoryId", recipe.categoryId);
  formData.append("weatherId", recipe.weatherId);

/** ✅ 재료 데이터 정리 */
let ingredientsData;
try {
  // ✅ 이미 배열이면 그대로 사용하고, 문자열이면 JSON으로 변환
  if (!Array.isArray(recipe.ingredients)) {
    recipe.ingredients = JSON.parse(recipe.ingredients);
  }

  // ✅ 모든 요소를 문자열로 변환 + 불필요한 문자 제거
  ingredientsData = recipe.ingredients.flat().map((ing) => {
    if (typeof ing === "string") {
      return ing.trim().replace(/[\[\]"]/g, ""); // ✅ `[]`, `"` 제거
    } else if (typeof ing === "object" && ing.name) {
      return ing.name.trim();
    }
    return "";
  }).filter(ing => ing !== ""); // ✅ 빈 값 제거

} catch (error) {
  console.error("❌ 재료 목록 파싱 실패:", error);
  ingredientsData = [];
}

// ✅ 최종적으로 배열인지 확인 (디버깅용)
console.log("✅ 변환된 재료 목록:", ingredientsData);

formData.append("ingredients", JSON.stringify(ingredientsData));
  // ✅ 대표 이미지 처리
  if (recipe.foodImg instanceof File) {
    formData.append("foodImg", recipe.foodImg);
  } else if (!recipe.existingFoodImg) {
    formData.append("foodImg", "DELETE"); // ✅ 삭제된 경우 'DELETE' 전송
  }

 // ✅ 단계별 설명 및 이미지 추가 (삭제된 단계는 "DELETE" 전송)
for (let i = 0; i < 6; i++) {
  const step = recipe.steps[i];
  if (step) {
    formData.append(`step${i + 1}`, step.description || null);
    
    if (step.image instanceof File) {
      formData.append(`stepImg${i + 1}`, step.image);
    } else if (!step.existingImage) {
      formData.append(`deleteStepImg${i + 1}`, true); // ✅ 삭제 요청
    }
  } else {
    formData.append(`step${i + 1}`, null);
    formData.append(`deleteStepImg${i + 1}`, true);
  }
}

  // ✅ 삭제된 이미지 목록 추가
  if (Array.isArray(recipe.deletedImages) && recipe.deletedImages.length > 0) {
    formData.append("deletedImages", JSON.stringify(recipe.deletedImages));
  }

  try {
    const headers = {
      Authorization: `Bearer ${token}`,
      "Content-Type": "multipart/form-data",
    };

    console.log("📢 API 요청:", isEditing ? `PUT /admin/recipes/${id}` : "POST /admin/recipes");
    console.log("📢 전송할 재료 목록:", ingredientsData);
    
    if (isEditing) {
      await axios.put(`http://localhost:8080/admin/recipes/${id}`, formData, { headers });
      alert("✅ 레시피가 수정되었습니다!");
    } else {
      await axios.post("http://localhost:8080/admin/recipes", formData, { headers });
      alert("✅ 레시피가 추가되었습니다!");
    }

    navigate("/admin/recipes");
  } catch (error) {
    console.error("❌ 요청 실패:", error.response?.data || error.message);
    alert("❌ 요청 중 오류가 발생했습니다. 다시 시도해주세요.");
  }
};


  return (
    <div className="recipe-form">
      <h1 className="big-title">{isEditing ? "레시피 수정" : "레시피 추가"}</h1>
  
      {/* ✅ 레시피명 입력 */}
      <div className="form-group">
      <label>레시피명</label>
      <input className="input-field-ur" type="text" name="foodName" value={recipe.foodName} onChange={handleChange} />
      </div>
  
      {/* ✅ 대표 이미지 업로드 */}
      <div className="form-group">
      <label>대표 이미지 추가</label>
      <input type="file" accept="image/*" onChange={(e) => handleImageUpload(e, -1)} />
      {recipe.existingFoodImg && <img src={recipe.existingFoodImg} alt="대표 이미지" className="preview-img" />}
      </div>
      
      {/* ✅ 소요 시간 입력 */}
      <div className="form-group">
      <label>소요 시간 (분)</label>
      <input type="number" name="foodTime" value={recipe.foodTime} onChange={handleChange} />
      </div>

      {/* ✅ 카테고리 선택 */}
      <div className="form-group">
      <label>카테고리 선택</label>
      <select className="input-field-ur" name="categoryId" value={recipe.categoryId} onChange={handleChange}>
        <option value="">카테고리 선택</option>
        {categoryOptions.map((option) => (
          <option key={option.id} value={option.id}>{option.label}</option>
        ))}
      </select>
      </div>
      
      {/* ✅ 날씨 선택 */}
      <div className="form-group">
      <label>날씨 선택</label>
      <select className="input-field-ur" name="weatherId" value={recipe.weatherId} onChange={handleChange}>
        <option value="">날씨 선택</option>
        {weatherOptions.map((option) => (
          <option key={option.id} value={option.id}>{option.label}</option>
        ))}
      </select>
  </div>

     {/* ✅ 재료 입력 */}
    <label className="login-label">재료 목록</label>
    {recipe.ingredients.map((ingredient, index) => {
      let value = "";

      try {
        if (typeof ingredient === "string") {
          value = ingredient.replace(/[\[\]"]/g, "").trim();
        } else if (typeof ingredient === "object" && ingredient.name) {
          // ❌ JSON.parse()는 제거하고, 문자열만 정리
          value = ingredient.name.replace(/[\[\]"]/g, "").trim();
        } else {
          value = String(ingredient).trim();
        }
      } catch (e) {
        console.error("❌ 재료 렌더링 파싱 오류:", ingredient);
        value = "";
      }

  return (
    <div key={index} className="ingredient-container">
      <input
        type="text"
        value={value}
        className="input-field-ur"
        onChange={(e) => handleIngredientChange(index, e.target.value)}

      />
      <button className="delete-btn-board" type="button" onClick={() => removeIngredient(index)}>삭제</button>
    </div>
  );
})}
<button style={{marginBottom:"40px"}} type="button" onClick={addIngredient} className="userrecipe-submit">추가</button>

<div className="dangae">

    {/* ✅ 단계별 설명 및 이미지 업로드 */}
<label className="login-label">단계별 설명</label>
{recipe.steps.map((step, index) => {
  const isEmptyStep =
  (!step.description || step.description.trim() === "" || step.description === "null") &&
  (!step.existingImage || step.existingImage.trim() === "");
  
  
  // ✅ 수정 모드일 때만 빈 단계 숨기기
  if (isEditing && isEmptyStep) return null;
  
  return (
    <div key={index} className="step-container">
      <div className="form-group">
      <label className="login-label">단계 {index + 1}</label>
      <input
        type="text"
        className="input-field-ur"
        value={step.description === "null" ? "" : step.description}
        onChange={(e) => handleStepChange(index, e.target.value)}
        />
        <input type="file" accept="image/*" onChange={(e) => handleImageUpload(e, index)} />

        {step.existingImage && (
          <div className="image-preview">
            <img
              src={step.existingImage}
              alt={`단계 ${index + 1} 이미지`}
              className="preview-img"
              />
            <button type="button" onClick={() => removeImage(index)}>삭제</button>
          </div>
        )}

      <button type="button" className="delete-btn-board" onClick={() => removeStep(index)}>삭제</button>
        </div>
    </div>
  );
})}

{/* ✅ 최대 6단계까지만 추가 가능 */}
{recipe.steps.length < 6 && (
  <button type="button" onClick={addStep} className="userrecipe-submit">단계 추가</button>
)}
</div>
  
  <div className="button-container">
      {/* ✅ 제출 버튼 */}
      <button onClick={handleSubmit} className="userrecipe-submit">
        {isEditing ? "수정하기" : "레시피 등록"}
      </button>
    </div>
    </div>
  );
  
};

export default RecipeForm;