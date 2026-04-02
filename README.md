# 🐝 HoneyRest – 감성 숙소 예약 플랫폼 (Admin System)
🏨 업체 관리자 (Company Admin) – **김민경**  
🛡️ 총 관리자 (Super Admin) – **설현오**  
👤 전체 총괄 / DB 설계 / 기술 방향 결정 – **박성원 (팀장)**

---

## 🎥 HoneyRest 광고 영상

[![HoneyRest 광고 영상](https://github.com/Seongwonp/honeyRest_user/blob/main/%E1%84%92%E1%85%A5%E1%84%82%E1%85%B5%E1%84%85%E1%85%A6%E1%84%89%E1%85%B3%E1%84%90%E1%85%B3.gif?raw=true)](https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/video%2F%E1%84%92%E1%85%A5%E1%84%82%E1%85%B5%E1%84%85%E1%85%A6%E1%84%89%E1%85%B3%E1%84%90%E1%85%B3.mp4?alt=media&token=1d89a752-00e0-4c82-b6c0-94723c57cc70)

> 🎬 클릭하면 전체 광고 영상을 볼 수 있습니다.

---

## 📖 목차

- [📌 프로젝트 개요](#-프로젝트-개요)
- [🧑‍💻 주요 기능](#-주요-기능)
  - [🏨 업체 관리자 (Company Admin)](#-업체-관리자-company-admin)
  - [🛡️ 총 관리자 (Super Admin)](#️-총-관리자-super-admin)
- [🗂️ 프로젝트 구조](#️-프로젝트-구조)
- [🏗️ 시스템 아키텍처](#️-시스템-아키텍처)
- [🗃️ 데이터베이스 설계 (ERD)](#️-데이터베이스-설계-erd)
- [📋 주요 테이블 요약](#-주요-테이블-요약)
- [📦 기술 스택](#-기술-스택)
- [⚙️ 실행 전 필수 설정](#️-실행-전-필수-설정)
- [🖥️ 주요 화면 캡처](#️-주요-화면-캡처)
- [🎬 관리자 시연 영상](#-관리자-시연-영상)
- [📝 프로젝트 발표 자료](#-프로젝트-발표-자료)
- [💭 프로젝트 회고](#-프로젝트-회고)
- [🙋 개발자 정보](#-개발자-정보)

---

## 📌 프로젝트 개요

**HoneyRest**는 감성 숙소 예약을 위한 **풀스택 웹 플랫폼**입니다.  
본 레포지토리는 **업체 관리자(Company Admin)** 와 **총 관리자(Super Admin)** 기능을 포함한  
**Thymeleaf 기반의 관리자 시스템**입니다.

관리자 권한을 2단계로 구분하여, 실제 숙박업체 운영 현장과 본사 총괄 관리 구조를 반영했습니다.

> "운영 현장(업체)과 본사(총괄)를 분리 관리 → 권한·데이터 보안 강화"

- **프로젝트 기간**: 2025.08.04 ~ 2025.09.04 (총 4주)
- **팀원 구성**:
  - 👤 박성원 (팀장) – 사용자(User) 영역 개발 총괄 & 광고 영상 제작 / 전체 DB 설계 및 ERD 작성 / 관리자 시스템 기술 방향 결정 / 전체 시스템 통합 및 코드 리뷰
  - 🏨 김민경 – 업체 관리자(Company Admin) 시스템 개발
  - 🛡️ 설현오 – 총 관리자(Super Admin) 시스템 개발

> 전체 서비스 구조에서 본 시스템은 **숙박업체 사업자**와 **플랫폼 운영자**가  
> 각자의 권한 범위 내에서 숙소, 예약, 매출, 유저를 체계적으로 관리할 수 있도록 설계되었습니다.

---

## 🧑‍💻 주요 기능

### 🏨 업체 관리자 (Company Admin)

> 자신이 소유 / 운영하는 숙소와 객실만 관리할 수 있는 사업자 전용 관리 시스템

#### 🏠 숙소 & 객실 관리
- 숙소 등록 / 수정 / 삭제 (이름, 주소, 편의시설, 사진 업로드)
- 객실 등록 (룸 타입, 침대 구성, 최대 인원, 요금, 재고)
- **가격/재고 캘린더**: 날짜별 요금 및 재고 자동 수정

#### 📅 예약 현황 관리
- 전체 예약 목록 조회 및 상태 변경 (`CONFIRMED → COMPLETED → CANCELED`)
- 예약 상세 내용 및 고객 요청사항 확인
- 환불 요청 처리 및 환불 완료 영수증 발행

#### 📊 매출 통계 대시보드
- 기간 선택 (최근 7일 / 최근 30일 / 월별)
- KPI 지표: 총 매출, 총 주문 수, 평균 주문 금액
- 막대 + 선 그래프 시각화 (Chart.js)

#### 💬 고객 리뷰 & 문의 관리
- 고객 1:1 문의 목록 및 상세 답변
- 리뷰 목록 조회 및 이미지 확인
- 리뷰 답변 / 숨김 처리

---

### 🛡️ 총 관리자 (Super Admin)

> 전체 시스템을 모니터링하고 운영 정책을 총괄하는 슈퍼 관리자 시스템

#### 🏢 업체 & 숙소 & 객실 관리
- 전체 업체 목록 조회 및 상세 정보 확인
- 숙소 목록 조회, 숙소별 객실 현황 확인
- 비활성 업체 / 비활성 숙소 / 예약 취소 대기 목록 별도 관리
- 업체 및 숙소 허용 여부(활성/비활성) 처리

#### 📅 예약 & 매출 관리
- 전체 예약 리스트 조회 (업체 / 숙소 / 객실 단위)
- **숙소 캘린더**: 객실별 예약 현황 월간 달력 뷰
- 업체별 매출 현황 그래프 조회

#### 👥 유저 & 업체 관리자 계정 관리
- 전체 유저 목록 조회 및 상세 정보 확인
- 업체 관리자 계정 생성 및 권한 부여

#### 🎟️ 쿠폰 / 이벤트 관리
- 쿠폰 발행 및 목록 관리
- 업체별 쿠폰 적용 허용 여부 설정

---

## 🗂️ 프로젝트 구조

```plaintext
HoneyRest/
├── frontend/               # 사용자 프론트엔드 (React)
│
├── user-api-backend/       # 사용자 API 백엔드 (Spring Boot)
│
├── admin/                  # 관리자 시스템 (Thymeleaf 기반)
│   ├── company/            # 업체 관리자 (Company Admin)
│   └── super/              # 총 관리자 (Super Admin)
│
└── README.md               # 전체 프로젝트 설명
```

- [👤 User API (Spring Boot + React) 바로가기](https://github.com/Seongwonp/honeyRest_user)

---

## 🏗️ 시스템 아키텍처

```
사용자(user)페이지 (React)      ─┐
호스트(owner)페이지 (Thymeleaf) ─┼──▶ 백엔드 (Spring Boot) ──▶ ORM (JPA/Hibernate) ──▶ DB (MariaDB)
총관리자(admin)페이지(Thymeleaf)─┘         │                          │
                                        Redis (캐시)          Firebase Storage (파일)
                                        OpenAPI (Swagger)
```

| 레이어 | 기술 |
|--------|------|
| 사용자 페이지 | React (SPA) |
| 관리자 페이지 | Thymeleaf (SSR) |
| 백엔드 | Spring Boot |
| ORM | JPA (Hibernate) |
| 캐시 | Redis |
| 파일 저장소 | Firebase Storage |
| DB | MariaDB |
| API 문서화 | Swagger (OpenAPI) |

---

## 🗃️ 데이터베이스 설계 (ERD)

HoneyRest의 데이터베이스는 **숙소 중심**과 **예약/리뷰 중심**의 두 가지 도메인으로 구성된  
**도메인 중심의 관계형 구조**로 설계되었습니다.  
확장성과 무결성을 고려해 정규화된 테이블로 구성되어 있으며,  
JPA 기반 ORM 매핑을 통해 엔티티와 DB가 유기적으로 연결됩니다.

### 🏠 숙소 중심 ERD 포인트

- `accommodation` 중심 → 객실, 가격, 이미지 등 핵심 정보 통합
- 객실별 상세 정보 및 날짜별 가격/재고 관리 (`price_calendar`) → 유연한 숙박 운영
- 태그, 카테고리, 지역 등 다양한 분류 체계 → 필터링 및 검색 기능 강화
- 숙소 등록 회사(`company`) 및 등록 요청(`accommodation_request_map`) 기능 포함
- 사용자 위시리스트, 취소 정책 테이블 설계 포함

### 📋 주요 테이블 요약

| 테이블명 | 설명 |
|----------|------|
| `User` | 사용자 정보, 권한, 알림 설정 등 |
| `Accommodation` | 숙소 정보, 위치, 태그, 이미지 |
| `Room` | 객실 정보, 재고, 가격, 캘린더 |
| `price_calendar` | 날짜별 요금 및 재고 관리 |
| `Reservation` | 예약 내역, 상태, 결제 정보 |
| `Review` | 리뷰 내용, 평점, 신고 여부 |
| `Company` | 숙박업체 정보, 계정, 정산 정보 |
| `Admin` | 관리자 계정, 권한, 통계 |
| `Coupon` | 할인 쿠폰, 조건, 유저 연결 |
| `Payment` | 결제 정보 |
| `payment_detail` | 결제 상세 정보 분리 (보안성 확보) |
| `WishList` | 관심 숙소 저장 기능 |
| `Policy` | 취소 정책, 운영 기준 |

---

## 📦 기술 스택

### 🎨 프론트엔드 (Thymeleaf 기반)

| 구분 | 기술 / 라이브러리 | 역할 / 설명 | 사용처 / 특징 |
|------|------------------|-------------|--------------|
| **프레임워크** | Thymeleaf | 서버사이드 렌더링(SSR) | 관리 페이지 HTML 템플릿 |
| **CSS / 스타일링** | Bootstrap | UI 컴포넌트, 반응형 디자인 | 버튼, 모달, 테이블 등 모든 스타일 |
| **템플릿** | Mazer | 대시보드 레이아웃, 사이드바/네비게이션 | 관리 UI 기본 틀 제공 |
| **차트** | Chart.js | 데이터 시각화 | 매출 통계, 예약 현황 등 |
| **아이콘** | Iconly | 심플한 아이콘 제공 | 버튼 아이콘 |

### ⚙️ 백엔드 (Spring Boot 기반)

| 구분 | 기술 / 라이브러리 | 역할 / 설명 | 사용처 / 특징 |
|------|------------------|-------------|--------------|
| **프레임워크** | Spring Boot | 백엔드 애플리케이션 | REST API, DI, 설정 관리 |
| **템플릿 엔진** | Thymeleaf | View 렌더링 | 관리자 페이지 출력 |
| **아키텍처 패턴** | MVC 패턴 | Controller-Service-Repository 구조 | 명확한 계층 분리 |
| **보안 / 인증** | Spring Security | 인증/인가 관리 | 세션 기반 로그인 |
| **DB** | MariaDB | 관계형 DB | 관리자 페이지의 사용자, 숙소, 예약, 쿠폰, 이벤트 등 전체 데이터 저장/관리 |
| **ORM** | Spring Data JPA | 엔티티 기반 CRUD 처리 | MariaDB와 연동 |
| **파일 업로드** | Firebase Storage | 이미지, 파일 저장 | 숙소 이미지, 룸 이미지, 배너 등 업로드 |
| **API 문서화** | SpringDoc OpenAPI / Swagger UI | REST API 문서화 | `/swagger-ui.html` 제공 |
| **환경/보안 관리** | application_security.properties | 민감 정보 분리 | DB 패스워드, Firebase 키 등 |

---

## ⚙️ 실행 전 필수 설정

### 1️⃣ Redis 설치 및 실행

HoneyRest는 Redis를 캐싱 서버로 사용합니다.

#### 📌 macOS (Homebrew)

```bash
brew install redis
brew services start redis   # Redis 실행
brew services stop redis    # Redis 중지
```

#### 📌 Ubuntu / Linux

```bash
sudo apt update
sudo apt install redis-server
sudo systemctl enable redis-server.service
sudo systemctl start redis-server
```

#### 📌 Windows
- Redis for Windows (예: Memurai) 설치 후 실행
- 실행 후 기본 포트 6379 사용

#### 설치 확인
```bash
redis-cli ping
# PONG 이 출력되면 정상 실행
```

---

### 2️⃣ 환경 변수 설정 (`application_security.properties`)

본 프로젝트 실행을 위해서는 민감 정보가 담긴 설정 파일을 반드시 직접 작성해야 합니다.  
아래 항목들을 실제 서비스 키 값으로 교체해주세요.

```properties
# Database password
spring.datasource.password=YOUR_DB_PASSWORD

# JWT secret key
jwt.secret-key-value=YOUR_SECURE_JWT_SECRET_KEY

# Firebase secretKey.json
fire.base.secretKey=YOUR_SECRET_KEY

# Google OAuth2
google.client-id=YOUR_GOOGLE_CLIENT_ID
google.client-secret=YOUR_GOOGLE_CLIENT_SECRET

# Kakao OAuth2
kakao.client-id=YOUR_KAKAO_CLIENT_ID
kakao.client-secret=YOUR_KAKAO_CLIENT_SECRET

# Gmail SMTP
gmail.username=YOUR_GMAIL_ADDRESS
gmail.access-token=YOUR_GMAIL_APP_PASSWORD
```

#### 🚨 주의사항

- `application_security.properties`는 반드시 `.gitignore`에 포함해야 합니다.
- 깃허브 공개 저장소에는 절대 올리지 마세요.
- 팀 협업 시에는 `application_security.properties.example` 파일로 형식만 공유하고, 실제 키 값은 각 개발자가 직접 채워넣어야 합니다.

---

## 🖥️ 주요 화면 캡처

### 🏨 업체 관리자 (Company Admin)

#### 숙소 & 객실 관리

> 숙소 선택 → 객실 등록 → 가격/재고 캘린더 수정 → 저장

- 숙소 목록에서 등록/수정/삭제 가능
- 객실별 룸 타입, 침대 구성, 요금, 재고 등록
- 날짜별 가격/재고 캘린더에서 일괄 수정 지원

#### 예약 현황

> 예약 상태 흐름: `CONFIRMED → COMPLETED → CANCELED`

- 전체 예약 목록 및 상태 변경 기능
- 예약 상세 / 요청사항 확인
- 환불 요청 처리 및 환불 완료 영수증 발행

#### 매출 통계

- 기간별(최근 7일 / 30일 / 월별) KPI 대시보드
- 총 매출, 총 주문 수, 평균 주문 금액
- 막대 + 선 그래프로 매출 추이 시각화

#### 고객 리뷰 관리

- 고객 1:1 문의 목록 및 상세 답변
- 리뷰 이미지 확인 및 답변/숨김 처리

---

### 🛡️ 총 관리자 (Super Admin)

#### 업체 & 숙소 & 객실 관리

> 전체 숙소 리스트 → 업체 리스트 → 업체 상세 → 숙소 상세 → 객실 상세

- 전체 업체/숙소/객실을 계층 구조로 탐색
- 비활성 업체 / 비활성 숙소 / 예약 취소 대기 별도 관리

#### 예약 & 매출 관리

- 전체 예약 리스트 조회
- 숙소 캘린더 뷰: 객실별 예약 현황을 월간 달력으로 시각화
- 업체별 매출 현황 그래프

#### 쿠폰 / 유저 / 계정 관리

- 쿠폰 발행 및 적용 허용 여부 처리
- 전체 유저 목록 조회 및 관리
- 업체 관리자 계정 생성 및 권한 부여

> 추가 화면은 아래 시연 영상에서 확인 가능합니다.

---

## 🎬 관리자 시연 영상

HoneyRest 관리자 시스템의 전체 기능을 실제 화면 기반으로 시연한 영상입니다.  
**총 관리자(Super Admin) → 업체 관리자(Company Admin)** 순서로 진행됩니다.

---

### 📺 총 관리자 + 업체 관리자 전체 흐름 시연

> 총 관리자의 업체/숙소/예약/유저 관리부터 업체 관리자의 숙소 등록, 예약 처리, 매출 확인까지의 전체 흐름을 담은 영상입니다.

🔗 [시연 영상 보러가기](https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/video%2Fcom_admin.mp4?alt=media&token=43dc80bc-dbaf-4d24-8488-3ce704b3b140)

---

## 📝 프로젝트 발표 자료

> HoneyRest의 전체 기획, 기능 흐름, 기술 스택, 시연 화면 등을 담은 발표용 PPT입니다.

📄 [HoneyRest 발표 자료 (PDF)](https://github.com/user-attachments/files/22292418/HoneyRest.pdf)

---

## 💭 프로젝트 회고

### 🏨 김민경 – 업체 관리자 (Company Admin) 담당

이번 최종 프로젝트는 설레는 기대감과 함께 긴장도 컸습니다.  
진행 과정에서 흥미와 성취감을 느끼는 순간도 있었지만, 반복되는 오류와 시행착오로 어려움을 겪기도 했습니다.  
특히 JPA를 활용한 개발 과정은 새로운 개념을 배우고 적용해 나가는 값진 시간이었으며,  
문제 상황에서는 팀원들과의 적극적인 소통을 통해 해결 능력을 넓힐 수 있었습니다.

아쉬운 점이 있다면, 다소 짧게 느껴진 프로젝트 기간으로 인해 구현하지 못한 기능들이 남았다는 것입니다.  
그럼에도 끝까지 협력하며 프로젝트를 완성할 수 있었던 것은 팀원들의 헌신과 지원 덕분입니다.

---

### 🛡️ 설현오 – 총 관리자 (Super Admin) 담당

이번 프로젝트를 통해 단순히 개발 기술을 익히는 것뿐만 아니라  
전체적인 흐름을 관리하고 조율하는 역할의 중요성을 깊이 체감할 수 있었습니다.  
프론트와 백엔드까지 종합적으로 고려해야 했기 때문에 부담도 있었지만 그만큼 배운 점도 많았습니다.

특히 프로젝트 초반에 설계와 기획을 얼마나 세밀하게 준비하느냐가  
이후 진행 속도와 완성도에 큰 영향을 준다는 것을 느꼈습니다.  
팀원들과의 꾸준한 소통이 문제 해결의 핵심이었고, 혼자가 아닌 팀으로서 성장하는 경험을 할 수 있었습니다.

이번 프로젝트는 저에게 큰 도전이자 값진 배움의 시간이었고,  
이후 더 나은 개발자로 성장할 수 있는 발판이 되었다고 생각합니다.

---

## 🙋 개발자 정보

### 🏨 김민경 (Minkyung Kim) – 업체 관리자(Company Admin) 총괄

- 숙박 업체 관리자 시스템 전체 설계 및 개발
- 숙소/객실 등록, 예약 현황, 매출 통계, 리뷰 관리 구현
- PPT 제작 참여

---

### 🛡️ 설현오 (Hyuno Seol) – 총 관리자(Super Admin) 총괄

- 총 관리자 시스템 전체 설계 및 개발
- 업체/숙소/예약/유저 전체 관리 및 쿠폰 시스템 구현
- 백엔드 명세서 작성
- PPT 제작 참여

---

> 💛 User API 및 전체 프로젝트 총괄은 **박성원**이 담당했습니다.  
> 👉 [User API 레포지토리 바로가기](https://github.com/Seongwonp/honeyRest_user)
