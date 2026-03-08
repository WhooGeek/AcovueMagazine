# 📸 Acovue Magazine (Backend)

> **Acovue Magazine**은 사용자들이 일상의 기록을 매거진처럼 아름답게 남기고 소통할 수 있는 서비스입니다.  
> 본 레포지토리는 백엔드 API 서버를 담당합니다.

---

## 🛠 Tech Stack

- **Framework:** Spring Boot 3.3.4
- **Language:** Java 21 (Temurin)
- **Database:** MySQL (AWS RDS), Redis
- **Security:** Spring Security, OAuth2 Client, JWT (Stateless)
- **Infra:** AWS EC2, GitHub Actions, Docker, Docker Hub
- **Frontend (Partner):** React (Vercel Deployment)

---

## 🌟 Key Features

- **OAuth2 & JWT Auth:** 구글/네이버 소셜 로그인 및 JWT 기반의 무상태(Stateless) 인증 시스템.
- **Magazine Posts:** 게시물 작성, 수정, 삭제 및 페이징 조회 기능 (Specification 활용).
- **Social Interaction:** 댓글 작성 및 좋아요 기능을 통한 유저 간 소통.
- **Real-time Session:** Redis를 활용한 효율적인 세션 및 리프레시 토큰 관리.
- **Image Storage:** AWS S3를 활용한 고해상도 이미지 업로드 및 관리.

---

## 🚀 CI/CD Architecture

본 프로젝트는 현대적인 개발 워크플로우를 위해 **GitHub Actions**와 **Docker**를 활용한 배포 자동화를 구축했습니다.

1. **CI:** GitHub `main` 브랜치 푸시 시 Gradle 빌드 및 Docker 이미지 생성.
2. **Push:** 생성된 이미지를 **Docker Hub**에 안전하게 보관.
3. **CD:** SSH를 통해 **AWS EC2**에 접속하여 최신 이미지를 `pull` 받고 컨테이너를 재시작.
4. **Environment:** 서버의 `.env` 파일을 활용한 안전한 환경 변수 주입.

---

## ⚙️ How to Run (Local)

1. 프로젝트 루트에 `.env` 파일을 생성하고 필요한 환경 변수를 설정합니다. (DB_URL, JWT_SECRET 등)
2. `./gradlew build` 명령어로 프로젝트를 빌드합니다.
3. 생성된 JAR 파일을 실행하거나 Docker를 통해 컨테이너로 띄웁니다.

### Docker 실행 예시
```bash
docker build -t acovue-magazine .
docker run -d -p 8080:8080 --env-file .env acovue-magazine
```

---

## 📂 Project Structure

```text
src/main/java/com/AcovueMagazine
├── Member      # 사용자 관리 및 인증 (Security, OAuth2, JWT)
├── Post        # 게시물 관리 및 페이징 조회
├── Comment     # 댓글 시스템
├── Like        # 좋아요 기능
├── AboutMe     # 사용자 프로필/소개 관리
├── Common      # 공통 예외 처리 및 응답 포맷
└── Config      # 전역 설정 (SecurityConfig, PasswordEncoderConfig 등)
```
