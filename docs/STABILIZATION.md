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

### 8. 회사 관리자 리소스 소유권 검증 — 완료

정적 점검에서 회사 관리자 경로가 역할(`COMPANY_ADMIN`)만 확인하고 일부 URL/form ID를 로그인 회사와 연결하지 않는 문제를 확인했다. 다른 회사의 ID를 알고 있을 때 예약 상세 또는 상태 변경, 객실 등록, 숙소 일괄 승인 요청에 접근할 수 있는 IDOR 위험이었다.

수정 범위:

- 로그인 이메일에서 회사 ID를 구하고 숙소 → 객실 → 예약의 소유 관계를 공통 검사하는 `CompanyResourceAccessService`를 추가했다.
- 숙소 신규 등록의 `companyId`는 제출값을 신뢰하지 않고 로그인 회사 ID로 덮어쓴다.
- 숙소 일괄 승인 요청은 로그인 회사가 소유한 숙소만 처리한다.
- 객실 등록·수정 화면의 숙소 목록과 제출 대상은 로그인 회사 소유로 제한한다.
- 예약 상세, 취소 승인/거부, 취소, 완료, 노쇼, 직접 생성, 일자 조회에 회사 소유권 검사를 적용했다.
- 잘못 중복된 예약 완료/노쇼 매핑(`/admin/reservations/admin/reservations/...`)을 실제 경로로 수정했다.

검증은 다른 회사 리소스 거부, 객실-숙소 혼합 제출 거부, 정상 소유 리소스 허용을 단위 테스트로 고정했다. 실제 로컬 DB를 변경하는 공격 요청은 수행하지 않았다.

### 9. 예약 상태·재고 규칙을 사용자 저장소와 통일 — 완료

사용자 저장소(`honeyRest_user`)가 예약 생성 시 객실 행 락 + 겹침 검사(409)를 도입했다. 두 저장소는 같은 `reservation` 테이블을 쓰므로 호스트도 같은 규칙으로 맞췄다.

상태 이름:

- 호스트에 `entity/ReservationStatus`를 추가했다. 값은 사용자 저장소와 동일하다: `PENDING`, `CONFIRMED`, `CANCEL_REQUEST`, `COMPLETED`, `NO_SHOW`, `CANCELLED`.
- 재고 점유 상태 `OCCUPYING` = `PENDING`, `CONFIRMED`, `CANCEL_REQUEST`, `COMPLETED`, `NO_SHOW` (`CANCELLED`는 비점유).
- 보고서/대시보드 취소 집계 쿼리의 오타 `'CANCELED'`를 `'CANCELLED'`로 고쳤다(기존에는 취소 건수가 항상 0).
- 오너 서비스의 `"cancel"`/`"CANCEL"` 비교(실제로 존재하지 않는 값)를 `CANCELLED`로 고쳤다.
- 관리자 `getCompanyReservations`의 `validStatuses`는 `ReservationStatus.ALL`로 바꿨다. 기존 목록에 `CANCELLED`가 없어 결제 화면의 미결제(취소) 목록 조회가 예외로 실패했다.
- `db/seed` SQL에는 예약 데이터가 없어 수정할 것이 없었다.
- 상태 값이 바뀌면 `ReservationStatusTest`의 기대값과 양쪽 저장소를 함께 수정한다.

재고(겹침) 검사:

- 재고의 기준은 `room.total_rooms − [checkIn, checkOut)과 겹치는 점유 상태 예약 수`다. 겹침 조건은 `checkIn < 요청 checkOut AND checkOut > 요청 checkIn`(체크아웃 당일 비점유, 예약 1건 = 객실 1개).
- `ReservationInventoryGuard`가 `RoomRepository.findByIdForUpdate`(`PESSIMISTIC_WRITE`)로 객실 행을 잠근 뒤 `ReservationRepository.countOverlapping`으로 세고, `>= totalRooms`이면 `ReservationConflictException`을 던진다.
- 적용 위치: 관리자 예약 생성(기본 `CONFIRMED`), 관리자 예약 수정(비점유 → 점유 전환 또는 점유 중 객실/기간 변경), 오너 예약 등록(기본 `PENDING`), 오너 예약 수정. `CANCEL_REQUEST → CONFIRMED`(취소 거부), `→ COMPLETED`, `→ NO_SHOW`는 점유 상태끼리의 전환이라 검사하지 않는다.
- 관리자 예약 생성이 `room.total_rooms`를 1 줄이고 취소 시 1 늘리던 `decreaseStock`/`increaseStock`을 제거했다. 사용자 저장소는 `total_rooms`를 바꾸지 않으므로 두 방식이 섞이면 객실 수가 오염된다.
- 화면 처리: 생성 컨트롤러가 예외를 잡아 flash `error`로 등록 화면에 토스트를 띄운다. 잡지 못한 경우 `GlobalExceptionHandler`가 409와 사유 메시지를 오류 화면에 보여준다.

`@Version` 제거:

- 호스트 `Reservation`에 `@Version version` 필드가 있었지만 사용자 저장소 Flyway(V1~V9)와 `DB_SCHEMA.md`의 `reservation` 테이블에는 `version` 컬럼이 없다(`VERSION`은 Spring Batch 테이블에만 있다).
- 호스트는 `ddl-auto=validate`이므로 Flyway로 새로 만든 DB에서는 검증에 실패한다. 기존 로컬 DB에서 기동된 것은 과거 `ddl-auto=update` 등으로 Flyway 밖에서 컬럼이 생긴 것으로 본다.
- 동시성은 객실 행 비관적 락이 담당하므로 `@Version`을 제거했다. 기존 DB에 남은 `version` 컬럼은 매핑되지 않을 뿐 동작에 영향이 없다.

`price_calendar.available_room`:

- `available_room`은 화면 표시용 스냅샷이다. 월간 캘린더를 저장(`bulkUpsert`)할 때의 계산값이 기록될 뿐, 예약 가능 여부를 판단하는 기준이 아니다.
- 재고의 진실은 항상 `room.total_rooms − 겹치는 점유 예약 수`이며, 사용자/호스트 예약 생성은 이 값만 본다.
- 스냅샷 계산도 같은 기준을 쓰도록 월간 캘린더 조회(`findOverlappedReservationsForMonth`)와 일별 캘린더(`getCalendarData`, 관리자/오너)를 `OCCUPYING` 상태만 세도록 맞췄다. 관리자 일별 캘린더는 기존에 취소 예약까지 세고 있었다.

남은 불일치:

- 호스트 `Reservation.accommodationName`(`accommodation_name`, NOT NULL)도 사용자 저장소 Flyway 스키마와 `DB_SCHEMA.md`에 없다. Flyway로 만든 DB에서는 호스트 스키마 검증이 여전히 실패하므로 사용자 저장소에 컬럼 추가 마이그레이션을 두거나 호스트 매핑을 제거하는 결정이 필요하다.
- 오너 예약 수정 화면(`owner/reservation/modify.html`)은 `POST /owner/reservation/modify`로 제출하지만 해당 매핑이 없다. `modifyReservation`에 재고 검사는 넣었으나 화면 흐름은 연결되어 있지 않다.
- 저장소 JPQL의 상태 문자열(`'CANCELLED'`, `in ('CONFIRMED', ...)`)은 철자만 맞춘 문자열 그대로다.

검증: `ReservationServiceImplInventoryTest`(6), `OReservationServiceInventoryTest`(5), `ReservationStatusTest`(3) Mockito/단위 테스트 추가.

### 10. 테스트 프로필 H2 전환과 CI — 완료

- `./gradlew test`가 MySQL·`application_security.properties`·Firebase 없이 통과한다(52개 전부 성공, 이전에는 46개 중 10개가 로컬 MySQL 부재로 실패).
- test 프로필(`src/test/resources/application-test.properties`)은 H2 인메모리 DB를 MySQL 모드로 쓰고 `ddl-auto=create-drop`으로 엔티티 매핑에서 스키마를 만든다. 운영의 `validate`와 달리 실제 MySQL 스키마와의 차이는 잡지 못한다.
- H2에서 `user`가 예약어라 `NON_KEYWORDS=USER`를 URL에 넣었다. 엔티티의 `TEXT`/`JSON` columnDefinition은 H2에서 그대로 생성된다.
- 비밀값은 테스트용 더미(`jwt.secret` 32바이트 이상)를 넣고, `app.storage.type=local`로 FirebaseConfig를 끈다. `DataInitializer`는 `local-demo` 프로필 전용이라 테스트에서 실행되지 않는다.
- 통합 테스트는 `@SpringBootTest` + `@Transactional`로 `support/JpaTestFixtures`가 업체/숙소/객실/사용자/예약을 직접 만들고 롤백한다. 하드코딩 ID·빈 본문 테스트를 없앴다(`ReservationServiceImplTest` 재작성, `OReservationRepositoryStatusQueryTest` 추가 — 상태 포함/제외 페이징 JPQL 4종의 목록과 count 일치 검증).
- GitHub Actions(`.github/workflows/ci.yml`)가 push/PR(main)마다 JDK 17로 `./gradlew build`를 실행하고, 실패 시 테스트 리포트를 아티팩트로 올린다.

### 11. 숙소 승인 우회 차단과 관리자 E2E — 완료

승인 우회:

- 회사 관리자 숙소 수정 폼(`/admin/accommodations/edit/{id}`)의 `status` 값을 그대로 저장해, `ACTIVE`를 고르면 총관리자 승인 없이 숙소가 노출됐다. 신규 등록도 폼의 `status`(ACTIVE)를 그대로 받았다.
- `AccommodationStatusPolicy`로 규칙을 고정했다. 회사 관리자는 `PENDING`(승인 요청)·`INACTIVE`(운영 중지)로만 바꿀 수 있고, 현재 상태 그대로 제출(ACTIVE 숙소의 일반 정보 수정)은 변경 없음으로 처리한다. `ACTIVE`·`REJECTED`는 총관리자 `OAccommodationService.approve/reject`(PENDING에서만 전환)로만 만들어진다. 신규 등록은 항상 `PENDING`.
- 컨트롤러는 이미지 삭제·업로드 같은 부수효과 전에 먼저 검증하고, 서비스 `update`에서도 같은 규칙을 한 번 더 적용한다. 수정 폼은 `PENDING`/`INACTIVE`만 고를 수 있고(기존 `DISABLED` 선택지는 목록 필터와 같은 `INACTIVE`로 통일), 현재 상태가 그 외(ACTIVE 등)이면 그 값은 "현재 상태 유지" 선택지로만 보인다.
- 총관리자 화면에 승인 버튼이 없어 승인 경로를 쓸 수 없었다. 비활성/대기 목록(`/owner/accommodation/inActive/list`)의 PENDING 행에 승인·거절 버튼을 추가했다.

E2E 작성 중 드러난 문제:

- CSRF 403: STATELESS 설정에서 `SessionManagementFilter`가 JWT로 인증된 매 요청을 새 인증으로 보고 `CsrfAuthenticationStrategy`로 토큰을 교체했다. 교체된 토큰은 큰 화면에서 응답 커밋 후에 읽혀 `Set-Cookie`가 빠졌고, 브라우저에서 객실 등록·예약 완료·숙소 승인 등 폼 POST가 403이 됐다(curl처럼 정적 리소스를 받지 않는 클라이언트에서는 재현이 어려웠다). 토큰 교체를 실제 로그인 요청(POST `/auth/login`)으로 한정했다.
- 가격 캘린더 진입(`/admin/price/page`) 리다이렉트 URL에 `NotificationInterceptor`가 넣은 `_notifyCancelCount`가 쿼리스트링으로 붙었다. 리다이렉트 응답에는 알림 값을 싣지 않는다.
- 예약 완료·노쇼·취소·직접 생성 후 존재하지 않는 `/admin/reservations/list(-all)`로 리다이렉트해 500 화면이 떴다. 상세 화면 또는 예약 현황(`/my?q=예약번호`)으로 보내고, 상세 화면에 체크아웃 완료·노쇼 버튼을 추가했다.
- 객실 등록 후 `/admin/rooms/list` → `list_all` 이중 리다이렉트에서 flash가 소비돼 성공 토스트가 보이지 않았다. `list_all`로 바로 보낸다.

검증:

```text
./gradlew test            87 tests, 0 failures
cd e2e && npx playwright test   9 passed (반복 실행 모두 통과)
```

### 12. 도메인 상태 전이 이전, SOLID 정리, 버그 수정 — 완료

공유 도메인 서브모듈(`libs/honeyrest-user`)을 사용자 저장소 `a612071`로 재고정했다. 새 도메인은 `Reservation`의 상태/취소 사유 setter를 없애고 `ReservationStatus.canTransition` 전이 표와 `changeStatus`·`approveCancelRequest`·`rejectCancelRequest`·`complete`·`markNoShow`·`cancel`만 남겼다. 호스트는 수정 없이 컴파일됐지만 아래처럼 전이 표를 우회하는 경로가 남아 있어 모두 옮겼다.

#### A. 도메인 전이로 이전

- 관리자 `ReservationServiceImpl.updateReservation`: `Reservation.update`가 상태를 그대로 대입하던 것을, 상태를 가린 변경값으로 필드만 반영하고 상태는 `changeStatus`(전이 표 검사)로 바꾼다.
- 취소 승인/거부·체크아웃 완료·노쇼: `ReservationRepository`의 JPQL 일괄 UPDATE 4개를 지우고, 예약을 잠금 읽기(`findByIdForUpdate`)로 가져와 도메인 메서드를 부른다. 재고 검사·검색 캐시 무효화 호출은 그대로 둔다.
- 오너 `OReservationService.modifyReservation`: 폼 값으로 엔티티를 새로 만들어 `save`(덮어쓰기)하던 것을, 기존 엔티티를 읽어 도메인 `update`와 `changeStatus`로 바꾼다. 예약번호·원가·할인액·객실명은 예약 시점 스냅샷이라 이 화면에서 바뀌지 않는다(도메인 `update`가 받지 않는 값).
- 두 계층에 복사돼 있던 점유 전이 판단(`enteringOccupying/occupyingChanged/leavingOccupying`)을 `serviceCommon/ReservationModificationService`(`prepare` → 연관 엔티티 조회 → `apply`) 하나로 합쳤다. 관리자에만 있던 체크인 < 체크아웃 검증과 전이 표 사전 검사가 오너에도 적용되고, 취소로 바뀌면 환불(C1)까지 같은 규칙으로 처리한다.

#### B. SOLID 정리

- 숙소 등록/수정 흐름(썸네일 업로드 → 저장 → 환불 규정 → 이미지 → 서브 이미지)을 컨트롤러에서 `AccommodationRegistrationService`(단일 `@Transactional`)로 옮겼다. 파일은 트랜잭션에 참여하지 않으므로 `storage/UploadCompensation`이 커밋되지 않으면 그 요청에서 올린 파일을 지우고, DB에서 뗀 파일은 커밋 후에만 지운다. 등록 시 썸네일·서브 이미지를 두 번 올리던 중복 업로드와, 썸네일 삭제 토글이 숙소 ID를 이미지 ID로 넘겨 엉뚱한 이미지를 지우던 문제도 고쳤다.
- `OSalesService`: 모든 예약을 메모리로 읽어 여섯 번 복사된 반복문으로 합산하던 것을 `OSalesRepository`의 GROUP BY 쿼리(일별/월별, 전체·업체·숙소 범위는 null 조건) 두 개로 바꿨다. 관리자 매출과 같은 기준(결제일, `payment_status='DONE'`, 예약 미취소)이며 시작일·종료일을 모두 포함한다(과거 일별 전체 매출은 `isAfter`로 시작일이 빠졌다). MySQL과 H2에서 같은 쿼리가 돌도록 JPQL로 썼다.
- `SalesChartServiceImpl`의 문자열 `switch(mode)` 두 개와 복사된 날짜 변환을 `ChartMode` enum(`parse`, 단위별 조회·행 변환, `toLocalDate` 한 곳)으로 바꿨다.
- 상태 문자열 리터럴을 상수로 바꿨다. 도메인에 없는 값은 호스트 `type` 패키지에 `AccommodationStatus`·`CompanyStatus`(대소문자 무시 `isActive`)·`RoomStatus`·`UserStatus`·`PaymentStatus`·`UserCouponStatus`·`PointHistoryType`으로 두었다.
- LSP/ISP: 구현이 예외만 던지던 `PriceCalendarService.stats`를 지웠다. `approveCancelRequest`는 null 대신 갱신된 DTO를 돌려준다.
- DIP: `RoomController`(회사 조회 → `CompanyResourceAccessService.currentCompanyId`), `AccommodationController`·`RegionController`(지역 → `RegionService`), 오너 `DashboardController`(회원 → `OUserService`)가 리포지토리를 직접 쓰지 않는다. `RoomServiceImpl`·`AccommodationServiceImpl`은 `new ObjectMapper()` 대신 Spring 빈을 주입받는다.
- 단일 구현용 인터페이스는 새로 만들지 않았다(`TossCancelClient`는 HTTP/No-op 두 구현). 두 관리자 계층은 합치지 않고 공통 규칙만 `serviceCommon`으로 뽑았다.

#### C. 버그 수정

1. 환불 없는 취소: 관리자 취소와 취소 승인이 예약 상태만 바꿨다. `serviceCommon/refund/ReservationRefundService`가 같은 트랜잭션에서 결제 `DONE → CANCELED`(조건부 UPDATE, 토스 응답 철자), 사용 포인트 환불(잔액 + `point_history` REFUND 행), 쿠폰 복원(`ISSUED`, `coupon_usage` 삭제), 토스 결제 취소를 처리한다. 두 번째 호출은 바꿀 것이 없어 아무것도 하지 않는다(멱등). 토스 호출은 `TossCancelClient`로 감싸고, `toss.secret-key`(환경변수 `TOSS_SECRET_KEY`)가 비었거나 screenshot 프로필이면 No-op 구현이 경고 로그만 남긴다. 토스 취소가 실패하면 트랜잭션 전체가 롤백된다(`ALREADY_CANCELED_PAYMENT`는 성공으로 본다). 관리자 매출(`SalesStatRepository`)·대시보드 매출(`DashboardReportRepository.sumRevenue`)은 취소된 예약을 뺀다.
2. 문의 답변이 항상 400: 컨트롤러가 `relpyText`(오타)를 받았다. 목록 모달·상세 폼 모두 `replyText`로 맞추고, 상세 화면의 폼 안 폼(삭제 버튼이 답변 저장으로 제출되던 문제)을 분리했다.
3. 가격 캘린더 수정: 컨트롤러가 `yearMonth`를, 모달은 `ym`을 보내 저장이 항상 400이었다(`ym`으로 통일). 셀의 `price`에 그날 체크인 매출 합계를 넣어 모달이 매출을 가격으로 미리 채웠다. 이제 `price`는 설정된 1박 가격(`price_calendar`, 없으면 객실 기본가), `revenue`는 체크인 매출(표시 전용)이다. 사용자 API가 읽지 않는 `available_room`을 바꾸던 "재고 0 = 판매중지" 입력은 없애고 재고는 표시 전용이라고 안내한다. 수정 모달 스크립트가 레이아웃 fragment 밖에 있어 버튼이 동작하지 않던 문제와 저장 토스트가 보이지 않던 문제(`toast` → `success`)도 고쳤다.
4. 상세 캐시 미무효화: 숙소/객실/이미지/환불 규정/태그를 바꿔도 사용자 API 상세 캐시가 TTL까지 남았다. 사용자 API `AccommodationCacheKeys`를 `cache/AccommodationCacheKeys`로 복사(동기화 경고 주석, 리터럴 키 테스트)하고, `SearchCacheInvalidator.evictAccommodationAfterCommit`이 커밋 후 숙소별로 한 번 `allFor(id)` 키를 지운다(Redis 구현 DEL, No-op 구현은 아무것도 하지 않음).
5. 락 순서: 예약 생성/수정에서 일반 SELECT가 객실 락보다 먼저 실행돼, REPEATABLE READ 스냅샷 때문에 겹침 수가 락을 기다리는 동안 커밋된 예약을 놓칠 수 있었다. 생성은 객실 락을 트랜잭션 첫 문장으로 옮기고, 수정은 예약 행 잠금 읽기 → 객실 락 → 연관 엔티티 조회 순서로 바꿨다. 겹침 수는 `countOverlappingForUpdate`(겹치는 예약 ID를 `FOR UPDATE`로 읽어 개수 계산. `COUNT ... FOR UPDATE`는 H2가 지원하지 않음)로 센다. 운영 프로필에 `spring.datasource.hikari.transaction-isolation=TRANSACTION_READ_COMMITTED`를 추가했다(사용자 API와 같은 값).
6. 관리자 로그인: 없는 이메일이면 NPE(500)가 나 계정 존재 여부가 드러났다. 틀린 비밀번호와 같은 400 메시지로 응답한다.

남은 사항:

- 도메인 `ReservationChanges`가 객실명·원가·할인액을 받지 않아, 오너 화면에서 객실을 바꾸면 예약의 객실명 스냅샷은 그대로 남는다. 필요하면 사용자 저장소의 도메인에 해당 필드를 추가한다.
- 운영에서 `TOSS_SECRET_KEY`가 비어 있으면 PG 환불이 호출되지 않는다(경고 로그). 배포 환경변수에 반드시 넣는다.
- `AccommodationCacheKeys`는 사본이다. 사용자 API가 키를 바꾸면 함께 고치고 `AccommodationCacheKeysTest`를 갱신한다.
- 쓰이지 않는 `ReportRepository`는 `payment_status='COMPLETED'`(실제 값은 `DONE`)를 쓰고 있다. 쓰기 전에 고친다.

검증:

```text
./gradlew test                   136 tests, 0 failures
cd e2e && npx playwright test    10 passed (가격 캘린더 수정 시나리오 07 추가)
```

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
