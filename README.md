# 🧑‍🍳 레시피 기반 커머스 & 커뮤니티 플랫폼

> 요리 레시피 공유부터 쇼핑 연동, 커뮤니티까지!  
> AI 추천, 챗봇, 실시간 채팅, 관리자 통계 등 실무형 풀스택 웹 프로젝트

---

## 📌 프로젝트 소개

이 프로젝트는 **요리 레시피**를 중심으로 다양한 기능이 통합된 커뮤니티 플랫폼입니다.  
사용자는 레시피 공유, 1:1 문의, 타로 기반 추천을 통해 편리하게 요리 경험을 나누고,  
관리자는 실시간 채팅과 신고 처리, 통계 분석 등을 통해 플랫폼을 관리합니다.

- 팀 프로젝트 / 3인 협업 / 총 6주 개발
- 프론트 + 백엔드 직접 개발 (Spring Boot + React)
- 실시간 WebSocket, JWT 인증, 관리자 통계 포함

---

## 🧩 주요 기능

### 👤 사용자
- 회원가입 / 로그인 / 비밀번호 찾기
- 레시피 등록 / 수정 / 삭제 / 즐겨찾기
- 1:1 문의 작성 / 조회
- 게시글 신고 기능
- 타로 기반 레시피 추천
- 실시간 관리자와 1:1 채팅
- 마이페이지(내 정보 수정, 알림 확인 등)

### 🛠 관리자
- 회원 관리 / 게시글 관리 / 공모전 레시피 승인
- 1:1 문의 답변, 사용자 알림 전송
- 실시간 채팅 및 신고 접수 처리
- 통계 시각화 (Google Charts: Pie, Bar, Line)

---

## 🛠 기술 스택

| 구분         | 기술                                         |
|--------------|----------------------------------------------|
| **Frontend** | React, Vite, Axios, React Router DOM         |
| **Backend**  | Spring Boot, Spring Security, MyBatis        |
| **Database** | MySQL                                        |
| **Etc**      | WebSocket, JWT, Google Charts, Daum 주소 API |

---

## 🖼 주요 화면 캡처

### 🔐 로그인 / 계정 찾기 / 회원가입
- 로그인  
  ![로그인](./screenshots/로그인.png)

- 아이디 찾기  
  ![아이디찾기1](./screenshots/아이디찾기1.png)

- 비밀번호 찾기  
  ![비밀번호찾기1](./screenshots/비밀번호찾기1.png)  
  ![비밀번호찾기2](./screenshots/비밀번호찾기2.png)

- 비밀번호 재설정  
  ![비밀번호재설정폼](./screenshots/비밀번호재설정폼.png)

- 회원가입  
  ![회원가입](./screenshots/회원가입.png)

---

### 🧑‍💻 마이페이지
- 문의 작성  
  ![(마이페이지)1대1문의 작성](./screenshots/(마이페이지)1대1문의 작성.png)

- 문의 목록  
  ![(마이페이지)1대1문의목록](./screenshots/(마이페이지)1대1문의목록.png)

- 내 활동  
  ![(마이페이지)내활동](./screenshots/(마이페이지)내활동.png)

- 즐겨찾기  
  ![(마이페이지)즐겨찾기](./screenshots/(마이페이지)즐겨찾기.png)

- 내가 쓴 댓글  
  ![(마이페이지)내댓글](./screenshots/(마이페이지)내댓글.png)

- 내가 쓴 게시글  
  ![(마이페이지)내게시글](./screenshots/(마이페이지)내게시글.png)

---

### 🛠 관리자 기능

- 문의 답변  
  ![(관리자)1대1문의답변](./screenshots/(관리자)1대1문의답변.png)

- 문의 상세 확인  
  ![(관리자)1대1문의상세확인](./screenshots/(관리자)1대1문의상세확인.png)

- 공모전 레시피 상세  
  ![(관리자)맛있는도전상세보기](./screenshots/(관리자)맛있는도전상세보기.png)

- 회원 통계  
  ![(관리자)회원통계보기](./screenshots/(관리자)회원통계보기.png)

- 알림 전송  
  ![(관리자)회원에게알림보내기](./screenshots/(관리자)회원에게알림보내기.png)

- 실시간 채팅 목록  
  ![(관리자)실시간채팅목록조회](./screenshots/(관리자)실시간채팅목록조회.png)

- 채팅 진행 중  
  ![(관리자)실시간채팅중](./screenshots/(관리자)실시간채팅중.png)

- 신고 목록  
  ![(관리자)신고글목록조회](./screenshots/(관리자)신고글목록조회.png)

- 신고 상세  
  ![(관리자)신고글목록상세조회](./screenshots/(관리자)신고글목록상세조회.png)

- 관리자 유저 목록  
  ![(관리자)고객목록조회](./screenshots/(관리자)고객목록조회.png)  
  ![(관리자)고객목록조회최종](./screenshots/(관리자)고객목록조회최종.png)

- 최종 신고 보기  
  ![(관리자)신고글목록조회최종](./screenshots/(관리자)신고글목록조회최종.png)  
  ![(관리자)신고글목록상세조회최종](./screenshots/(관리자)신고글목록상세조회최종.png)

---

# 🎁 연예인 굿즈 쇼핑몰 프로젝트

## 📌 프로젝트 소개

사용자와 관리자를 위한 **굿즈 쇼핑몰 커머스 플랫폼**입니다.  
회원가입부터 상품 주문, 관리자 승인 및 회원 관리까지 구현된 **풀스택 웹 서비스**로,  
Spring Boot (백엔드)와 React (프론트엔드)를 연동한 **통합형 시스템**입니다.

> 🧑‍💻 **개발환경**  
> - 백엔드: Spring Boot, MyBatis, MySQL  
> - 프론트엔드: React.js + Vite  
> - 기타: Git, Postman, JSP (일부 admin), CSS

---

## 📁 프로젝트 구조

```bash
finalproject/
├── final-front/                  # React 프론트엔드
│   ├── node_modules/
│   ├── public/
│   ├── src/
│   │   ├── api/
│   │   ├── assets/
│   │   ├── component/
│   │   ├── components/
│   │   ├── header-footer/
│   │   ├── pages/
│   │   ├── styles/
│   │   └── utils/
│   ├── App.jsx
│   ├── index.html
│   ├── package.json
│   ├── vite.config.js
│   └── README.md
│
├── backend/                      # Spring Boot 백엔드
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/project/
│   │   │   │   ├── config/
│   │   │   │   ├── controller/
│   │   │   │   ├── mapper/
│   │   │   │   ├── model/
│   │   │   │   └── service/
│   │   │   └── resources/
│   │   │       ├── mapper/
│   │   │       ├── static/uploads/
│   │   │       └── application.properties
│   │   └── test/java/com/project/
│   │       └── FinalprojectApplicationTests.java
│   ├── pom.xml
│   └── target/
│
├── screenshots/                  # 실행 화면 캡처 이미지
├── uploads/                      # 사용자 업로드 이미지
└── README.md


---

## 💡 트러블슈팅 사례

### 1. WebSocket 인증 문제
JWT 토큰이 WebSocket과 호환되지 않아 `JwtAuthFilter`와 `HandshakeInterceptor`를 따로 구성해 해결

### 2. 이미지 경로 문제
서버 저장 경로와 프론트 요청 경로 불일치 → `WebMvcConfigurer`로 `uploads/` 경로 매핑해 해결

### 3. Google Charts 오류
차트 데이터 타입 오류 → X축 값을 숫자로 변경하여 `"Data column(s) for axis #0 cannot be of type string"` 해결

---

## 👨‍💻 김동하의 기여도

- 프론트엔드 전체 구현 (React)
  - 로그인, 회원가입, 마이페이지, 관리자 페이지 UI 개발
- 실시간 채팅, WebSocket 기반 알림 기능 개발
- 이미지 업로드/삭제 처리 및 URL 경로 매핑
- 관리자 통계 시각화 구현 (Google Charts)
- 프로젝트 구조 및 디자인 통합 작업 담당

---

## 📎 GitHub 링크

- 👉 [프로젝트 저장소 바로가기](https://github.com/kgh4455/recipe-sharing)

---

## 🙋‍♂️ 느낀 점

이 프로젝트를 통해 프론트와 백엔드의 실전 연동을 경험하고,  
에러를 직접 해결하며 개발 역량을 키울 수 있었습니다.  
특히 실시간 채팅, 통계, 관리자 기능처럼 복잡한 기능들을 구현하며  
**“완성도 있는 웹 서비스란 무엇인지”** 고민하고 배운 소중한 경험이었습니다.
