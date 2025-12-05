# 🌙 MonggleMonggle 실행 가이드

프로젝트를 실행하기 위한 가이드입니다.  
**AI_API**, **BACK**, **FRONT** 세 개의 서버를 모두 실행해야 정상 동작합니다.

---

## 📋 사전 요구사항

| 서비스 | 요구사항                   |
| ------ | -------------------------- |
| AI_API | Python 3.10+               |
| BACK   | Java 17+, Gradle           |
| FRONT  | Node.js 20.19+ 또는 22.12+ |

---

## 1️⃣ AI_API (FastAPI) - Port 8000

Python FastAPI 기반 AI 서비스입니다.

### 설치 및 실행

```bash
# 프로젝트 루트에서 실행

# 1. AI_API 디렉토리로 이동
cd AI_API

# 2. 가상환경 생성 (최초 1회)
python -m venv venv

# 3. 가상환경 활성화
# Windows
venv\Scripts\activate
# Mac/Linux
source venv/bin/activate

# 4. 의존성 설치 (최초 1회 또는 requirements.txt 변경 시)
pip install -r requirements.txt

# 5. 서버 실행
python main.py
```

### 또는 uvicorn으로 직접 실행

```bash
cd AI_API
uvicorn main:app --host 0.0.0.0 --port 8000 --reload
```

✅ 실행 확인: http://localhost:8000/docs (Swagger UI)

---

## 2️⃣ BACK (Spring Boot) - Port 8080

Java Spring Boot 기반 백엔드 서비스입니다.

### 설치 및 실행

```bash
# 프로젝트 루트에서 실행

# 1. BACK 디렉토리로 이동
cd BACK

# 2. 빌드 및 실행
# Windows
gradlew.bat bootRun
# Mac/Linux
./gradlew bootRun
```

### 또는 빌드 후 JAR 파일로 실행

```bash
cd BACK

# 빌드
# Windows
gradlew.bat build
# Mac/Linux
./gradlew build

# JAR 실행
java -jar build/libs/*.jar
```

✅ 실행 확인: http://localhost:8080/swagger-ui.html

---

## 3️⃣ FRONT (Vue.js + Vite) - Port 5173

Vue.js 기반 프론트엔드 서비스입니다.

### 설치 및 실행

```bash
# 프로젝트 루트에서 실행

# 1. FRONT 디렉토리로 이동
cd FRONT

# 2. 의존성 설치 (최초 1회 또는 package.json 변경 시)
npm install

# 3. 개발 서버 실행
npm run dev
```

✅ 실행 확인: http://localhost:5173

---

## 🚀 한 번에 모두 실행하기 (터미널 3개 필요)

### 터미널 1 - AI_API

```bash
cd AI_API
python -m venv venv && venv\Scripts\activate && pip install -r requirements.txt
python main.py
```

### 터미널 2 - BACK

```bash
cd BACK
gradlew.bat bootRun
```

### 터미널 3 - FRONT

```bash
cd FRONT
npm install && npm run dev
```

---

## 📌 포트 정리

| 서비스             | 포트 | URL                   |
| ------------------ | ---- | --------------------- |
| AI_API (FastAPI)   | 8000 | http://localhost:8000 |
| BACK (Spring Boot) | 8080 | http://localhost:8080 |
| FRONT (Vue.js)     | 5173 | http://localhost:5173 |

---

## ⚠️ 주의사항

1. **AI_API**: `models/` 폴더에 `gemma-2-9b-it-dream-q4_k_m.gguf` 모델 파일이 필요합니다.
2. **BACK**: `application.yaml`에서 데이터베이스 연결 정보를 확인하세요.
3. **FRONT**: API 호출을 위해 AI_API와 BACK 서버가 먼저 실행되어 있어야 합니다.

---

## 🔧 환경변수 설정 (필요 시)

### AI_API (.env 파일 생성)

```env
GMS_API_KEY=your_api_key_here
```

---

Happy Coding! 🎉
