import api from "./api";

/**  
 * ===========================================
 * ✅ 타로 관련 기능 (레시피 추천 포함)
 * ===========================================
 */
export const tarotApi = {
  /** 🔹 랜덤 타로 카드 4장 가져오기 */
  getRandomCards: () => api.get("/tarot/cards"),

  /** 🔹 유저가 타로 카드 선택 (하루 2번 제한) */
  selectCard: (tarotCardId) => api.post("/tarot/select", { tarotCardId }, { withCredentials: true }),

  /** 🔹 랜덤 추천 레시피 가져오기 */
  getRandomRecipe: () => api.get("/tarot/random-recipe"),
};
// ✅ `myPageApi` 내보내기
export default tarotApi;