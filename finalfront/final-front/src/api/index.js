// ✅ 먼저 필요한 API 모듈들을 가져옴
import userApi from "./userApi";
import myPageApi from "./myPageApi";
import adminApi from "./adminApi";
import tarotApi from "./tarotApi";

// ✅ Named exports
export { userApi, myPageApi, adminApi, tarotApi };

// ✅ 마지막에 `export default` 선언 (초기화된 후 실행)
const api = {
  userApi,
  myPageApi,
  adminApi,
  tarotApi,
};

export default api;