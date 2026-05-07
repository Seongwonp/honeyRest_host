# 🐝 HoneyRest – 감성 숙소 예약 플랫폼 (Admin System)
🏨 업체 관리자 (Company Admin) – **김민경**  
🛡️ 총 관리자 (Super Admin) – **설현오**  
👤 전체 총괄 / DB 설계 / 기술 방향 결정 – **박성원 (팀장)**

---

## 🎥 HoneyRest 광고 영상

[![HoneyRest 광고 영상](https://github.com/Seongwonp/honeyRest_user/blob/main/%E1%84%92%E1%85%A5%E1%84%82%E1%85%B5%E1%84%85%E1%85%A6%E1%84%89%E1%85%B3%E1%84%90%E1%85%B3.gif?raw=true)](https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/video%2F%E1%84%92%E1%85%A5%E1%84%82%E1%85%B5%E1%84%85%E1%85%A6%E1%84%89%E1%85%B3%E1%84%90%E1%85%B3.mp4?alt=media&token=1d89a752-00e0-4c82-b6c0-94723c57cc70)

> 🎬 클릭하면 전체 광고 영상을 볼 수 있습니다.

> - [👤 사용자(User) 페이지 바로가기](https://github.com/Seongwonp/honeyRest_user)

---

## 📖 목차

- [📌 프로젝트 개요](#-프로젝트-개요)
- [🧑‍💻 주요 기능](#-주요-기능)
  - [🏨 업체 관리자 (Owner)](#-업체-관리자-owner)
  - [🛡️ 총 관리자 (Super Admin)](#️-총-관리자-super-admin)
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
- [🔧 프로젝트 완료 후 리팩토링 및 개선](#-프로젝트-완료-후-리팩토링-및-개선-박성원)

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

### 🏨 업체 관리자 (Owner)

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

#### 🏢 업체 정보 관리
- 본인 업체 정보 등록 / 수정 (상호명, 사업자번호, 대표자, 연락처)
- 업체 비활성화 요청 처리

#### 🎟️ 쿠폰 관리
- 숙소별 쿠폰 생성 및 목록 조회
- 할인 유형, 적용 기간, 최소 주문 금액 설정

#### 👥 고객(유저) 조회
- 예약 고객 목록 페이징 조회
- 고객 상세 정보 및 예약 이력 확인

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

#### 💬 1:1 문의 관리
- 전체 유저 1:1 문의 목록 조회 (숙소·답변 여부 필터)
- 문의 상세 확인 및 답변 처리

#### 💰 포인트 / 환불 관리
- 전체 결제 내역 조회 및 포인트 적립 현황 확인
- 환불 요청 목록 및 환불 처리

#### ⭐ 리뷰 관리
- 전체 리뷰 목록 조회 및 상세 확인
- 신고된 리뷰 처리 및 숨김 관리

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
| **런타임** | Java 17 | LTS 버전 | Jakarta EE 기반 |
| **프레임워크** | Spring Boot 3.5.4 | 백엔드 애플리케이션 | REST API, DI, 설정 관리 |
| **템플릿 엔진** | Thymeleaf + Layout Dialect | View 렌더링 | 관리자/오너 페이지 출력 |
| **아키텍처 패턴** | MVC 패턴 | Controller-Service-Repository 구조 | 명확한 계층 분리 |
| **보안 / 인증** | Spring Security + JJWT | 인증/인가 + JWT 토큰 관리 | 세션 기반 로그인, JWT 쿠키 |
| **DB** | MariaDB | 관계형 DB | 사용자, 숙소, 예약, 쿠폰 등 전체 데이터 |
| **ORM** | Spring Data JPA (Hibernate) | 엔티티 기반 CRUD | MariaDB 연동, N+1 최적화 |
| **JWT** | JJWT 0.12.x | JWT 토큰 생성 및 검증 | 쿠키 기반 무상태 인증 |
| **동적 쿼리** | QueryDSL | 타입 안전 JPQL 생성 | 검색/필터 쿼리 |
| **객체 매핑** | MapStruct + ModelMapper | DTO ↔ Entity 변환 자동화 | 계층 간 매핑 간소화 |
| **파일 업로드** | Firebase Storage | 이미지, 파일 저장 | 숙소·룸·배너 이미지 업로드 |
| **API 문서화** | SpringDoc OpenAPI / Swagger UI | REST API 문서화 | `/swagger-ui.html` (SUPER_ADMIN 전용) |
| **AOP** | Spring AOP | 공통 관심사 처리 | 로깅, 인증 공통 처리 |
| **모니터링** | Spring Actuator | 애플리케이션 상태·지표 확인 | 헬스체크 엔드포인트 |
| **환경/보안 관리** | application-secret.properties | 민감 정보 분리 | DB 패스워드, Firebase 키 등 |

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

### 2️⃣ 환경 변수 설정 (`application-secret.properties`)

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

- `application-secret.properties`는 반드시 `.gitignore`에 포함해야 합니다.
- 깃허브 공개 저장소에는 절대 올리지 마세요.
- 팀 협업 시에는 `application-secret.properties.example` 파일로 형식만 공유하고, 실제 키 값은 각 개발자가 직접 채워넣어야 합니다.

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

#### 업체 정보 관리

- 업체 정보 등록 / 수정 / 비활성화 처리

#### 쿠폰 관리

- 쿠폰 생성 (할인 유형, 적용 숙소, 사용 기간 설정)
- 발행된 쿠폰 목록 조회

#### 고객 조회

- 예약 고객 페이징 목록 및 상세 정보 확인

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

#### 1:1 문의 관리

- 전체 유저 문의 목록 조회 (숙소·답변 여부 필터)
- 문의 상세 확인 및 답변 처리

#### 포인트 / 환불 관리

- 전체 결제 내역 및 포인트 조회
- 환불 요청 처리 및 환불 완료 처리

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

---

### 👤 박성원 (Seongwon Park) – 팀장 / 전체 총괄

- 전체 DB 설계 및 ERD 작성
- 사용자(User) 페이지 개발 총괄
- 관리자 시스템 기술 방향 결정 및 전체 코드 리뷰
- 광고 영상 제작

---

## 🔧 프로젝트 완료 후 리팩토링 및 개선 (박성원)

> 프로젝트 제출 이후 코드 품질, 성능, 보안을 전반적으로 개선한 작업입니다.

### ⚡ 백엔드 성능 최적화 (N+1 쿼리 제거)

- `ReservationRepository` 전체 쿼리에 `JOIN FETCH` 적용 → 예약 목록 조회 시 N+1 쿼리 제거
- `Page<Entity>` 쿼리를 `value` + `countQuery` 분리 구조로 변경 → Hibernate 메모리 페이징 경고 해소
- `OAccommodationRepository`에 `@EntityGraph` 적용 → 연관 엔티티 일괄 로딩
- `AccommodationQueryImpl.search()` 생성자 인수 누락 버그 수정 (`regionName` 누락)
- `AccommodationRepository.findCompanyIdByAccommodationId()` 파생 쿼리 오류 → `@Query`로 교체
- `ReservationServiceImpl.toDto()` 이중 lazy load 제거 (`r.getRoom().getAccommodation()` → `r.getAccommodation()`)

### 🔒 보안 강화

- JWT 시크릿 키를 `application.properties`에서 분리 → `application-secret.properties` (`.gitignore` 적용)
- JWT 쿠키 `Secure` 플래그를 `false` 하드코딩 → HTTPS 환경 자동 감지로 변경
- Swagger UI (`/swagger-ui/**`, `/v3/api-docs/**`) 접근을 `SUPER_ADMIN` 전용으로 제한
- `show-sql=false`, Hibernate `BasicBinder=warn` → SQL 파라미터 값(비밀번호 등) 로그 노출 차단
- Spring Security·Web 로그 레벨 `debug → warn` → 인증 흐름 상세 정보 노출 차단
- JWT 필터에서 요청마다 이메일/role이 로그에 찍히던 문제 제거 (PII 노출 방지)
- 디버그 쓰레기 로그 (`aaaa...`, `bbbb...` 등) 전체 제거
- **소유권 검증 강화**: `AccommodationController` · `RoomController` detail/edit/delete 등 10개 메서드에 로그인 사용자의 companyId 검증 추가 → 타 업체 리소스 무단 접근 차단
- **에러 메시지 정보 노출 차단**: `GlobalExceptionHandler`의 `EntityNotFoundException` · `IllegalArgumentException` 핸들러에서 `e.getMessage()` 제거 → 내부 스택 정보 클라이언트 노출 방지
- **CSRF 방어 강화**: `ACCESS_TOKEN` 쿠키에 `SameSite=Lax` 속성 추가 (`jakarta.servlet.http.Cookie` → `ResponseCookie` 교체)
- **JWT 시크릿 강화**: 기본값 문자열 → 80자 이상 고강도 랜덤 시크릿으로 교체
- **로그 파이프라인 개선**: Owner 서비스 3개(`OAccommodationServiceImpl`, `ORoomServiceImpl`, `OCompanyService`)의 `e.printStackTrace()` 6개소 전부 `@Log4j2` + `log.warn()` 으로 교체
- `spring.jpa.hibernate.ddl-auto=update` → `validate` 변경 → 운영 환경 스키마 자동 변경 사고 방지
- `logging.level.com.honeyrest=debug` → `info` 변경 → 운영 로그 최적화

### 🗑️ 불필요 파일 정리

- 사용하지 않는 템플릿 80개 이상 삭제 (`application/`, `component/`, `extra-component/`, `ui/`, `form/`, `table/`, `chart/`, 미사용 레이아웃 파일 등)
- 전체 템플릿 파일 수: 약 150개 → **72개**로 축소

### 🎨 관리자 페이지 테마 분리 및 반응형 개편

- **역할별 테마 분리**: `honey-theme.css`(업체관리자 / 앰버 계열)와 `owner-theme.css`(총관리자 / 네이비+블루 계열)로 분리 → 역할이 시각적으로 명확히 구분
- `owner-theme.css` 신규 작성: CSS 변수 `--primary: #3B82F6`, 사이드바 `#0F2044` 등 SUPER_ADMIN 전용 팔레트 정의, `honey-theme.css` 호환 별칭(`--honey`, `--honey-soft` 등) 포함
- `owner/layout/base.html` 레이아웃에서 테마 파일 교체 적용
- **Owner 목록 페이지 8개 전면 재작성**: 업체·숙소·객실·예약·리뷰·유저 목록 페이지를 `table-responsive` + `hide-md` / `hide-sm` 반응형 컬럼 제어 구조로 통일
- KPI 카드, 상태 칩(`badge-chip`, `chip-ok`, `chip-wait` 등), 툴바, 페이지네이션 등 공통 컴포넌트 CSS 클래스로 일관화
- 인라인 `<style>` 블록 제거 후 CSS 변수(`--honey`, `--honey-soft` 등)로 통일
- 미사용 템플릿 파일 9개 삭제 (`admin/rooms/list.html`, `auth/logout.html`, 빈 레이아웃, 미사용 프래그먼트 등)

### 🔔 알림 시스템 구현

- `NotificationInterceptor` (HandlerInterceptor) 구현 → 모든 admin/owner 페이지에 취소요청 건수 자동 주입
- 사이드바 예약 취소 메뉴에 뱃지(badge) 표시 → 미처리 취소요청 수 실시간 확인
- 기존 `alert()` 5개 → Bootstrap Toast 알림으로 교체 (성공: 초록, 정보: 파랑, 오류: 빨강, 3.5초 자동 닫힘)
- `admin/layout/base.html`, `owner/layout/base.html` 공통 적용 → 개별 페이지 수정 불필요

### 📦 빌드 & 의존성 정리

- `build.gradle` 내 중복 선언 제거: `spring-boot-starter-web` · `security` · `validation` · `data-jpa` · `firebase-admin` 각 2회 선언 → 1회로 통합
- 미사용 의존성 제거: `mybatis-spring-boot-starter`(코드베이스 미사용), `com.auth0:java-jwt`(실제로는 JJWT 사용), `spring-cloud-starter-aws`(`s3.enabled=false` 비활성, 코드 미사용), `spring-boot-starter-amqp`(RabbitMQ 미사용)
- 빌드 의존성 정리로 컴파일 시간 단축 및 불필요한 AutoConfiguration 로딩 제거
