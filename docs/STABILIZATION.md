# HoneyRest 안정화 작업 기록

이 문서는 사용자 백엔드, 호스트 백엔드, React 사용자 화면을 다시 실행 가능한 상태로 만드는 작업의 진행 상황과 검증 결과를 기록한다.

## 작업 원칙

- 컴파일과 테스트를 먼저 통과시킨 뒤 서버 실행 및 기능 검증을 진행한다.
- 기존 작업 파일과 자동 생성된 `.DS_Store`는 안정화 커밋에 포함하지 않는다.
- 비밀번호, API 키, 서비스 계정 파일 등 로컬 보안 설정은 문서와 Git에 기록하지 않는다.
- 각 단계는 수정, 자동 검증, 문서 갱신, 범위별 커밋 순으로 완료한다.

## 저장소 기준 상태

점검일: 2026-08-09

| 프로젝트 | 기준 브랜치 | 기준 커밋 | 확인 사항 |
| --- | --- | --- | --- |
| 호스트 백엔드 | `main` | `dc0db4f` | 원격 `origin/main`과 동일했으나 컴파일 오류 25개 발생 |
| 사용자 백엔드 | `main` | `8c563db` | 기본 포트 8080, 로컬 안정화 변경 존재 |
| React 사용자 화면 | `master` | `2ed81db` | Vite 개발 서버 5173, 로컬 UI 안정화 변경 존재 |

호스트 백엔드의 기본 포트는 안정화 과정에서 8081로 분리했다. 사용자 백엔드는 8080, React 개발 서버는 5173을 사용한다. 필요한 경우 `SERVER_PORT` 환경 변수로 호스트 포트를 변경할 수 있다.

## 단계별 진행 상황

### 1. 현재 상태 고정 — 완료

- 세 저장소의 브랜치, 기준 커밋 및 변경 파일을 확인했다.
- 호스트 저장소는 `git pull --ff-only` 결과 최신 상태였다.
- 로컬 보안 설정 파일이 Git 추적 대상이 아님을 확인했다.
- 호스트 수정 작업은 `codex/host-compile-fix` 브랜치로 분리했다.

### 2. 호스트 백엔드 컴파일 복구 — 완료

원인:

- Spring Boot 3/JPA 코드에서 Joda-Time의 `LocalDate`를 잘못 import했다.
- JPA Repository에서 MyBatis의 `@Param`을 잘못 import했다.
- Lombok `@Builder`가 필드 초기값을 무시해 기본값이 사라질 수 있었다.

수정:

- 보고서 Repository 및 Projection의 날짜 타입을 `java.time.LocalDate`로 통일했다.
- 예약 Repository의 파라미터 애너테이션을 `org.springframework.data.repository.query.Param`으로 변경했다.
- `isVerified`, `isRead` 기본값에 `@Builder.Default`를 적용했다.

검증:

```text
./gradlew test
BUILD SUCCESSFUL in 24s
```

컴파일 오류 25개와 Lombok 기본값 경고 4개가 제거됐다. 남은 메시지는 일부 기존 API의 deprecation 안내이며 빌드 실패 원인은 아니다.

### 3. 호스트 백엔드 실행 검증 — 부분 완료

실행 환경:

- 호스트 기본 포트를 8081로 분리하고 `SERVER_PORT` 환경 변수로 재정의할 수 있게 했다.
- Spring Boot 3.5.4가 8081에서 정상 기동됐다.
- MySQL 연결, JPA EntityManager, 35개 Repository 초기화가 성공했다.

스모크 테스트:

| 경로 또는 기능 | 결과 |
| --- | --- |
| 공용 로그인 화면 `/auth/login` | 200 |
| 미인증 `/admin/dashboard` | 로그인 화면으로 302 |
| 미인증 `/owner/dashboard` | 로그인 화면으로 302 |
| 회사 관리자 로그인 | 성공, `/admin/dashboard`로 302 |
| 회사 관리자 대시보드 | 200 |
| 숙소 목록 | 200 |
| 객실 목록 | 200 |
| 예약 목록 | 200 |
| 리뷰 목록 | 200 |
| 매출 보고서 | 200 |

확인된 제약:

- 총관리자 계정은 DB에 이미 존재해 `DataInitializer`가 초기 비밀번호를 갱신하지 않는다. 현재 로컬 DB 비밀번호가 소스의 최초 시드 값과 달라 총관리자 로그인 이후 흐름은 검증하지 못했다. 데이터 보호를 위해 비밀번호를 임의로 초기화하지 않았다.
- `/actuator/health`는 현재 인증 대상으로 설정되어 미인증 요청이 302로 응답한다.
- 기동 로그에 Commons Logging 중복 및 Spring Security AuthenticationProvider 구성 경고가 있으나 실행을 막지는 않는다.

## 전체 안정화 순서

1. 기준 상태 기록
2. 호스트 컴파일 복구
3. 호스트 실행 및 API 점검
4. 사용자 인증/예약 API 회귀 테스트
5. DB 무결성 및 데모 데이터 보완
6. 삭제된 이미지와 Firebase 오류 정리
7. React 핵심 사용자 흐름 및 lint 오류 정리
8. Redis 캐시 키와 API 소유권 검증
9. 통합 데모 리허설 및 README 최신화
