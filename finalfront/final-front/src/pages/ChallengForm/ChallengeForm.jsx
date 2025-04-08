import { useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import './ChallengeForm.css';

const RecipeForm = () => {
    const [recipeName, setRecipeName] = useState('');
    const [cookingTime, setCookingTime] = useState('');
    const [categoryId, setCategoryId] = useState(1); // 기본값은 한식 (1)
    const [foodImg, setFoodImg] = useState(null);
    const [ingredients, setIngredients] = useState(['']); // 재료 리스트
    const [step1, setStep1] = useState('');
    const [step2, setStep2] = useState('');
    const [step3, setStep3] = useState('');
    const [step4, setStep4] = useState('');
    const [step5, setStep5] = useState('');
    const [step6, setStep6] = useState('');
    const [stepImg1, setStepImg1] = useState(null);
    const [stepImg2, setStepImg2] = useState(null);
    const [stepImg3, setStepImg3] = useState(null);
    const [stepImg4, setStepImg4] = useState(null);
    const [stepImg5, setStepImg5] = useState(null);
    const [stepImg6, setStepImg6] = useState(null);

    const navigate = useNavigate();

    // 재료 변경 핸들러
    const handleIngredientChange = (index, value) => {
        const newIngredients = [...ingredients];
        newIngredients[index] = value;
        setIngredients(newIngredients);
    };

    // 재료 추가 핸들러
    const addIngredient = () => {
        setIngredients([...ingredients, '']);
    };

    // 재료 삭제 핸들러
    const removeIngredient = (index) => {
        const newIngredients = ingredients.filter((_, i) => i !== index);
        setIngredients(newIngredients);
    };

    
    // 폼 제출 핸들러
    const handleSubmit = async () => {
        const formData = new FormData();
        formData.append('foodName', recipeName);
        formData.append('foodTime', cookingTime);
        formData.append('categoryId', categoryId);
        formData.append('foodImg', foodImg);  // 파일 추가
        ingredients.forEach((ingredient) => {
            formData.append('ingredients', ingredient);  // key는 'ingredients'로 설정
        });
    
        formData.append('step1', step1);
        formData.append('step2', step2);
        formData.append('step3', step3);
        formData.append('step4', step4);
        formData.append('step5', step5);
        formData.append('step6', step6);
        formData.append('stepImg1', stepImg1);  // 파일 추가
        formData.append('stepImg2', stepImg2);  // 파일 추가
        formData.append('stepImg3', stepImg3);  // 파일 추가
        formData.append('stepImg4', stepImg4);  // 파일 추가
        formData.append('stepImg5', stepImg5);  // 파일 추가
        formData.append('stepImg6', stepImg6);  // 파일 추가
        const token = localStorage.getItem('token'); // JWT 토큰 가져오기
        
        try {
            const response = await axios.post("http://localhost:8080/api/urecipe/adduserrecipe", formData, {
                headers: {
                    "Authorization": `Bearer ${token}`, // JWT 토큰 추가
                    'Content-Type': 'multipart/form-data',
                },
            });
            alert("맛있는 도전하기를 완료하였습니다!");
            navigate('/');
        } catch (error) {
            console.error("서버 오류:", error);
            console.log(token);
        }
    };

    return (
        <div className="recipe-form">
            <h1 className="big-title">맛있는 도전하기</h1>
            <div className="gyeonggo2">
                <h3>⚠️ 도전하기 시 주의사항 ⚠️</h3>
                <p>1. 모든 입력란을 정확히 작성해주세요. 필수 입력값이 누락되면 등록이 불가능합니다.</p>
                <p>2. 이미지 파일은 반드시 jpg, png, jpeg 형식만 등록 가능합니다. 용량이 너무 크면 업로드가 실패할 수 있습니다.</p>
                <p>3. 레시피 순서는 최대한 자세하게 작성해주세요. 정확한 설명이 없을 경우 다른 유저가 따라 하기 어려울 수 있습니다.</p>
                <p>4. 부적절한 내용이나 저작권이 있는 이미지는 등록할 수 없습니다. 관리자의 판단에 따라 게시물이 삭제될 수 있습니다.</p>
                <p>5. 한 번 등록된 레시피는 수정이 제한될 수 있습니다. 작성 전 꼭 확인 후 제출해주세요.</p>
            </div>
            <div className="form-group">
                <label className="login-label">레시피 이름</label>
                <input
                    type="text"
                    value={recipeName}
                    onChange={(e) => setRecipeName(e.target.value)}
                    required
                    className="input-field-ur"
                    />
            </div>
            <div className="form-group">
                <label className="login-label">조리 시간 (분)</label>
                <input
                    type="text"
                    value={cookingTime}
                    onChange={(e) => setCookingTime(e.target.value)}
                    required
                     className="input-field-ur"
                     />
            </div>
            <div className="form2-group"></div>
                <label className="login-label">카테고리</label>
                <div style={{marginTop:"10px"}}>
                    <input
                        type="radio"
                        name="category"
                        value="1"
                        checked={categoryId === 1}
                        onChange={() => setCategoryId(1)}
                    />
                    <label>한식</label>
                </div>
                <div>
                    <input
                        type="radio"
                        name="category"
                        value="2"
                        checked={categoryId === 2}
                        onChange={() => setCategoryId(2)}
                    />
                    <label>일식</label>
                </div>
                <div>
                    <input
                        type="radio"
                        name="category"
                        value="3"
                        checked={categoryId === 3}
                        onChange={() => setCategoryId(3)}
                    />
                    <label>중식</label>
                </div>
                <div style={{marginBottom:"40px"}}>
                    <input
                        type="radio"
                        name="category"
                        value="4"
                        checked={categoryId === 4}
                        onChange={() => setCategoryId(4)}
                    />
                    <label>양식</label>
                </div>
                <div className="form-group">

            <label className="login-label">레시피 대표 이미지</label>
                <input
                    type="file"
                    accept="image/*"
                    onChange={(e) => setFoodImg(e.target.files[0])}
                    required
                    className="input-field-ur"
                    />
                    </div>
                    <div className="form-group">

            <label className="login-label">재료</label>
                <div style={{marginBottom:"40px"}}>
                    {ingredients.map((ingredient, index) => (
                        <div key={index} style={{width:"100%"}}>
                            <input
                                type="text"
                                value={ingredient}
                                onChange={(e) => handleIngredientChange(index, e.target.value)}
                                placeholder={`재료 ${index + 1}`}
                                required
                                className="input-field-ur"
                                />
                    <button type="button" onClick={() => removeIngredient(index)} className="delete-btn-board">삭제</button>
                        </div>
                    ))}
                <button  type="button" onClick={addIngredient} className="userrecipe-submit">추가</button>
            </div>
                    </div>
            <div className="form-group">

            <label className="login-label">단계별 설명</label>
                <div className="dangae">
                    <label className="login-label">단계 1</label>
                    <input
                        type="text"
                        value={step1}
                        onChange={(e) => setStep1(e.target.value)}
                        placeholder="1단계"
                        required className="input-field-ur"
                        />
                    <div>        
                    <input
                        type="file"
                        accept="image/*"
                        onChange={(e) => setStepImg1(e.target.files[0])}
                        required
                        />
                    </div>
                </div>
                 <div className="dangae">
                    <label className="login-label">단계 2</label>
                    <input
                        type="text"
                        value={step2}
                        onChange={(e) => setStep2(e.target.value)}
                        placeholder="2단계"
                        required className="input-field-ur"
                    />
                    <div>  
                    <input
                        type="file"
                        accept="image/*"
                        onChange={(e) => setStepImg2(e.target.files[0])}
                        required 
                        />
                    </div>
                </div>
                <div className="dangae">
                    <label className="login-label">단계 3</label>
                    <input
                        type="text"
                        value={step3}
                        onChange={(e) => setStep3(e.target.value)}
                        placeholder="3단계"
                        required className="input-field-ur"
                        />
                    <div>
                        <input
                            type="file"
                            accept="image/*"
                            onChange={(e) => setStepImg3(e.target.files[0])}
                            required
                            />
                    </div>
                </div>
                <div className="dangae">
                    <label className="login-label">단계 4</label>
                    <input
                        type="text"
                        value={step4}
                        onChange={(e) => setStep4(e.target.value)}
                        placeholder="4단계"
                        required className="input-field-ur"
                        />
                    <div>
                    <input
                        type="file"
                        accept="image/*"
                        onChange={(e) => setStepImg4(e.target.files[0])}
                        required
                        />
                    </div>
                </div>
                <div className="dangae">
                    <label className="login-label">단계 5</label>
                    <input
                        type="text"
                        value={step5}
                        onChange={(e) => setStep5(e.target.value)}
                        placeholder="5단계"
                        required className="input-field-ur"
                        />
                    <div>
                    <input
                        type="file"
                        accept="image/*"
                        onChange={(e) => setStepImg5(e.target.files[0])}
                        required
                        />
                    </div>
                </div>
                <div className="dangae">
                    <label className="login-label">단계 6</label>
                    <input
                        type="text"
                        value={step6}
                        onChange={(e) => setStep6(e.target.value)}
                        placeholder="6단계"
                        required className="input-field-ur"
                        />
                    <div>
                    <input
                        type="file"
                        accept="image/*"
                        onChange={(e) => setStepImg6(e.target.files[0])}
                        required
                        />
                    </div>
                </div>
            <div className="button-container">
                <button type="button" onClick={handleSubmit} className="userrecipe-submit">
                    레시피 등록
                </button>
            </div>
                        </div>
        </div>
    );
};

export default RecipeForm;
