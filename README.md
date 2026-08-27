# BookLog

독서 기록을 관리하는 Android 애플리케이션입니다. 책을 검색해 서재에 담고, 읽은 페이지와 리뷰를 캘린더 기반으로 기록하며, AI 독서 코치와 대화할 수 있습니다.

## 주요 기능

- **소셜 로그인**: 카카오 / 구글 계정으로 로그인 (Firebase Authentication 연동)
- **도서 검색**: 알라딘 Open API를 통한 도서 검색 및 상세 정보 조회
- **독서 기록**: 읽는 중 / 읽음 / 보관 상태별 서재 관리, 캘린더 기반 독서 기록
- **텍스트 인식(OCR)**: ML Kit 한국어 텍스트 인식으로 책 속 문장을 사진으로 촬영해 기록
- **AI 독서 코치**: Gemini 기반 대화형 독서 코치와 책에 대한 이야기 나누기
- **마이페이지**: 독서 통계, 리뷰, 설정 관리

## 기술 스택

- **언어**: Kotlin, Java
- **UI**: Jetpack Compose, XML(View) 혼용
- **아키텍처**: MVVM (ViewModel + LiveData)
- **백엔드**: Firebase (Authentication, Firestore, Cloud Functions, App Check - Play Integrity)
- **네트워킹**: Retrofit2 (알라딘 API 연동)
- **이미지 처리**: Glide, ML Kit Text Recognition (Korean)
- **AI**: Google Generative AI SDK (Gemini)
- **로그인 SDK**: Kakao SDK, Google Identity (Credential Manager)
- **기타**: Markwon(마크다운 렌더링), Material Calendar View

## 시작하기

### 요구 사항

- Android Studio (최신 버전 권장)
- minSdk 28 / targetSdk 36 / compileSdk 36
- JDK 11

### 빌드 설정

이 프로젝트는 API 키 등 민감 정보를 저장소에 포함하지 않습니다. 빌드 전 아래 파일을 직접 준비해야 합니다.

1. **`local.properties`** (프로젝트 루트) 에 다음 키를 추가하세요.

   ```properties
   kakao_native_app_key=YOUR_KAKAO_NATIVE_APP_KEY
   google_web_client_id=YOUR_GOOGLE_WEB_CLIENT_ID
   aladdin_ttb_key=YOUR_ALADIN_TTB_KEY
   ```

2. **`app/google-services.json`**: Firebase 콘솔에서 프로젝트를 생성한 뒤 다운로드하여 `app/` 디렉터리에 위치시키세요.

두 파일 모두 `.gitignore`에 포함되어 있어 커밋되지 않습니다.

### 빌드 및 실행

```bash
./gradlew assembleDebug
```

Android Studio에서 프로젝트를 열고 실행(Run)해도 됩니다.

## 프로젝트 구조

```
app/src/main/java/com/najunho/rememberbooks/
├── Activity/       # 화면 단위 Activity
├── Adapter/        # RecyclerView / ViewPager 어댑터
├── CloudFunctions/ # Firebase Cloud Functions 호출
├── DataClass/      # 데이터 모델
├── Db/             # Firestore 레포지토리
├── Fragments/      # 화면 내 Fragment
├── Retrofit/       # 알라딘 API 네트워크 클라이언트
├── Util/           # 유틸리티(로그인 헬퍼 등)
├── ViewModel/       # 화면별 ViewModel
└── ViewPagerAdpater/
```
