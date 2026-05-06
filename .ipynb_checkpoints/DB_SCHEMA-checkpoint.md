# HoneyRest Database Schema Specification

이 문서는 HoneyRest 프로젝트의 상세 데이터베이스 스키마 정의서입니다. 제공된 테이블 목록 이미지를 바탕으로 모든 컬럼, 타입, 제약 조건을 상세히 기술하였습니다.

---

## 1. Core Entities (핵심 서비스)

### `user` (사용자 정보 테이블)
| 컬럼명 | 타입 | Nullable | 제약 조건 | 설명 |
| :--- | :--- | :--- | :--- | :--- |
| **user_id** | bigint | NO | PK | 생성 일시 |
| **created_at** | datetime(6) | YES | | 수정 일시 |
| **updated_at** | datetime(6) | YES | | 생년월일 |
| **birth_date** | date | YES | | 이메일 |
| **email** | varchar(100) | NO | UNIQUE | 성별 |
| **gender** | varchar(10) | YES | | 이메일 인증 여부 |
| **is_verified** | bit(1) | NO | | 마지막 로그인 일시 |
| **last_login** | datetime(6) | YES | | 마케팅 수신 동의 여부 |
| **marketing_agree** | bit(1) | YES | | 이름 |
| **name** | varchar(50) | NO | | 비밀번호 해시 |
| **password_hash** | varchar(255) | YES | | 휴대폰 번호 |
| **phone** | varchar(20) | YES | | 현재 포인트 |
| **point** | int | NO | | 프로필 이미지 URL |
| **profile_image** | varchar(500) | YES | | 권한 (USER, ADMIN 등) |
| **role** | varchar(20) | YES | | 소셜 고유 ID |
| **social_id** | varchar(100) | YES | | 소셜 로그인 타입 |
| **social_type** | varchar(20) | YES | | 계정 상태 (ACTIVE 등) |
| **status** | varchar(20) | YES | | |

### `accommodation` (숙소 정보 테이블)
| 컬럼명 | 타입 | Nullable | 제약 조건 | 설명 |
| :--- | :--- | :--- | :--- | :--- |
| **accommodation_id** | bigint | NO | PK | 고유 식별자 |
| **address** | varchar(500) | NO | | 주소 |
| **amenities** | longtext/json | YES | | 편의시설 정보 (JSON) |
| **check_in_time** | datetime(6) | YES | | 체크인 시작 시간 |
| **check_out_time** | datetime(6) | YES | | 체크아웃 마감 시간 |
| **description** | text | YES | | 숙소 설명 |
| **latitude** | decimal(10,6) | YES | | 위도 |
| **longitude** | decimal(10,6) | YES | | 경도 |
| **min_price** | decimal(10,2) | YES | | 최소 요금 |
| **name** | varchar(255) | NO | | 숙소명 |
| **rating** | decimal(2,1) | YES | | 평균 평점 |
| **status** | varchar(20) | YES | | 운영 상태 (active 등) |
| **thumbnail** | varchar(500) | YES | | 대표 이미지 URL |
| **category_id** | int | NO | FK | 숙소 카테고리 고유 ID |
| **company_id** | int | NO | FK | 업체 고유 식별자 |
| **main_region_id** | int | NO | FK | 지역 고유 ID (도/시) |
| **sub_region_id** | int | NO | FK | 세부 지역 ID |

### `room` (객실 정보 테이블)
| 컬럼명 | 타입 | Nullable | 제약 조건 | 설명 |
| :--- | :--- | :--- | :--- | :--- |
| **room_id** | bigint | NO | PK | 객실 고유 ID |
| **created_at** | datetime(6) | YES | | 생성 일시 |
| **updated_at** | datetime(6) | YES | | 수정 일시 |
| **amenities** | longtext/json | YES | | 객실 편의시설 정보 (JSON) |
| **bed_info** | longtext/json | YES | | 침대 정보 (JSON) |
| **description** | text | YES | | 객실 설명 |
| **extra_person_fee** | decimal(10,2) | YES | | 추가 인원 요금 |
| **max_occupancy** | int | NO | | 최대 인원 |
| **name** | varchar(150) | NO | | 객실 이름 |
| **price** | decimal(10,2) | NO | | 기본 요금 |
| **standard_occupancy** | int | NO | | 기준 인원 |
| **status** | varchar(20) | YES | | 객실 상태 |
| **total_rooms** | int | NO | | 전체 객실 수 |
| **type** | varchar(60) | YES | | 객실 타입 |
| **accommodation_id** | bigint | NO | FK | 숙소 ID |

---

## 2. Transactions (거래 및 예약)

### `reservation` (예약 정보 테이블)
| 컬럼명 | 타입 | Nullable | 제약 조건 | 설명 |
| :--- | :--- | :--- | :--- | :--- |
| **reservation_id** | bigint | NO | PK | 예약 고유 ID |
| **created_at** | datetime(6) | YES | | 생성 일시 |
| **updated_at** | datetime(6) | YES | | 수정 일시 |
| **cancel_reason** | text | YES | | 취소 사유 |
| **check_in_date** | date | NO | | 체크인 날짜 |
| **check_out_date** | date | NO | | 체크아웃 날짜 |
| **discount_amount** | decimal(10,2) | YES | | 총 할인 금액 |
| **guest_count** | int | NO | | 투숙 인원 |
| **guest_name** | varchar(100) | NO | | 투숙객 성함 |
| **guest_phone** | varchar(20) | NO | | 투숙객 전화번호 |
| **original_price** | decimal(10,2) | YES | | 할인 전 원가 |
| **price** | decimal(10,2) | NO | | 최종 결제 금액 |
| **reservation_number** | varchar(50) | NO | UNIQUE | 고유 예약 번호 |
| **room_name** | varchar(255) | NO | | 객실명 |
| **special_requests** | text | YES | | 특별 요청 사항 |
| **status** | varchar(20) | NO | | 예약 상태 |
| **accommodation_id** | bigint | NO | FK | 숙소 ID |
| **room_id** | bigint | NO | FK | 객실 ID |
| **user_id** | bigint | YES | FK | 사용자 ID |

### `payment` (결제 정보 테이블)
| 컬럼명 | 타입 | Nullable | 제약 조건 | 설명 |
| :--- | :--- | :--- | :--- | :--- |
| **payment_id** | bigint | NO | PK | 결제 고유 식별자 |
| **created_at** | datetime(6) | YES | | 생성 일시 |
| **updated_at** | datetime(6) | YES | | 수정 일시 |
| **amount** | decimal(10,2) | NO | | 결제 금액 |
| **payment_date** | datetime(6) | YES | | 결제 일시 |
| **payment_method** | varchar(30) | NO | | 결제 수단 |
| **payment_status** | varchar(20) | NO | | 결제 상태 |
| **pg_provider** | varchar(50) | YES | | PG사 (TOSS 등) |
| **receipt_url** | varchar(500) | YES | | 영수증 URL |
| **transaction_id** | varchar(100) | YES | | 거래 ID |
| **user_id** | bigint | YES | | 결제자 ID |
| **reservation_id** | bigint | NO | FK | 예약 ID |

### `payment_detail` (결제 상세 내역)
| 컬럼명 | 타입 | Nullable | 제약 조건 | 설명 |
| :--- | :--- | :--- | :--- | :--- |
| **payment_detail_id** | bigint | NO | PK | 결제 상세 고유 ID |
| **created_at** | datetime(6) | YES | | 생성 일시 |
| **updated_at** | datetime(6) | YES | | 수정 일시 |
| **card_company** | varchar(50) | YES | | 카드사명 |
| **card_number** | varchar(50) | YES | | 카드번호 |
| **installment_months** | int | YES | | 할부 개월 수 |
| **toss_payment_key** | varchar(100) | YES | | 토스 결제 키 |
| **virtual_account_bank** | varchar(50) | YES | | 가상계좌 은행 |
| **virtual_account_expire** | datetime(6) | YES | | 가상계좌 만료일 |
| **virtual_account_holder** | varchar(50) | YES | | 가상계좌 예금주 |
| **virtual_account_number** | varchar(50) | YES | | 가상계좌 번호 |
| **payment_id** | bigint | NO | FK | 결제 ID |

---

## 3. Support & Promotions (지원 및 프로모션)

### `company` (숙소 운영 회사/업체)
| 컬럼명 | 타입 | Nullable | 제약 조건 | 설명 |
| :--- | :--- | :--- | :--- | :--- |
| **company_id** | int | NO | PK | 업체 고유 식별자 |
| **created_at** | datetime(6) | YES | | 생성 일시 |
| **updated_at** | datetime(6) | YES | | 수정 일시 |
| **address** | varchar(255) | YES | | 사업장 주소 |
| **bank_info** | longtext/json | YES | | 은행 정보 (JSON) |
| **business_number** | varchar(50) | NO | | 사업자 등록 번호 |
| **commission_rate** | decimal(5,2) | YES | | 수수료율 |
| **email** | varchar(100) | YES | | 이메일 |
| **name** | varchar(200) | NO | | 업체명 |
| **owner_name** | varchar(20) | YES | | 대표자 이름 |
| **phone** | varchar(20) | YES | | 연락처 |
| **status** | varchar(20) | YES | | 상태 |

### `coupon` (쿠폰 정보 테이블)
| 컬럼명 | 타입 | Nullable | 제약 조건 | 설명 |
| :--- | :--- | :--- | :--- | :--- |
| **coupon_id** | bigint | NO | PK | 고유 식별자 |
| **created_at** | datetime(6) | YES | | 생성 일시 |
| **updated_at** | datetime(6) | YES | | 수정 일시 |
| **code** | varchar(50) | YES | UNIQUE | 쿠폰 코드 |
| **discount_type** | varchar(255) | NO | | 할인 유형 |
| **discount_value** | decimal(10,2) | NO | | 할인 금액/율 |
| **end_date** | datetime(6) | NO | | 종료 일시 |
| **is_active** | bit(1) | NO | | 활성 여부 |
| **max_discount_amount** | decimal(10,2) | YES | | 최대 할인 금액 |
| **min_order_amount** | decimal(10,2) | YES | | 최소 주문 금액 |
| **name** | varchar(50) | NO | | 쿠폰 이름 |
| **start_date** | datetime(6) | NO | | 시작 일시 |
| **target_id** | bigint | YES | | 적용 대상 ID |
| **target_type** | varchar(20) | NO | | 적용 대상 유형 |

### `user_coupon` (사용자 보유 쿠폰)
| 컬럼명 | 타입 | Nullable | 제약 조건 | 설명 |
| :--- | :--- | :--- | :--- | :--- |
| **user_coupon_id** | bigint | NO | PK | 고유 식별자 |
| **expired_at** | datetime(6) | NO | | 만료 일시 |
| **issued_at** | datetime(6) | NO | | 발급 일시 |
| **status** | varchar(20) | NO | | 상태 (ISSUED, USED 등) |
| **used_at** | datetime(6) | YES | | 사용 일시 |
| **coupon_id** | bigint | NO | FK | 쿠폰 ID |
| **user_id** | bigint | NO | FK | 사용자 ID |

---

## 4. Metadata & Logs (메타데이터 및 로그)

### `region` (지역 정보 마스터)
| 컬럼명 | 타입 | Nullable | 제약 조건 | 설명 |
| :--- | :--- | :--- | :--- | :--- |
| **region_id** | int | NO | PK | 지역 고유 ID |
| **image_url** | varchar(255) | YES | | 대표 이미지 URL |
| **is_popular** | bit(1) | YES | | 인기 지역 여부 |
| **level** | int | NO | | 지역 레벨 |
| **name** | varchar(100) | NO | | 지역명 |
| **parent_id** | int | YES | FK | 상위 지역 ID |

### `accommodation_category` (숙소 카테고리)
| 컬럼명 | 타입 | Nullable | 제약 조건 | 설명 |
| :--- | :--- | :--- | :--- | :--- |
| **category_id** | int | NO | PK | 카테고리 고유 ID |
| **icon_url** | varchar(255) | YES | | 카테고리 아이콘 이미지 URL |
| **name** | varchar(100) | NO | | 카테고리명 |
| **sort_order** | int | YES | | 정렬 순서 |

---

## 5. Spring Batch Metadata (배치 작업 관리)
*이 테이블들은 Spring Batch 프레임워크에 의해 자동 관리됩니다.*

### `BATCH_JOB_INSTANCE`
| 컬럼명 | 타입 | Nullable | 제약 조건 | 설명 |
| :--- | :--- | :--- | :--- | :--- |
| **JOB_INSTANCE_ID** | bigint | NO | PK | 배치 작업 인스턴스 고유 ID |
| **VERSION** | bigint | YES | | 버전 정보 |
| **JOB_NAME** | varchar(100) | NO | | 배치 작업 이름 |
| **JOB_KEY** | varchar(32) | NO | UNIQUE | 배치 작업 고유 키 |

### `BATCH_JOB_EXECUTION`
| 컬럼명 | 타입 | Nullable | 제약 조건 | 설명 |
| :--- | :--- | :--- | :--- | :--- |
| **JOB_EXECUTION_ID** | bigint | NO | PK | 배치 작업 실행 고유 ID |
| **VERSION** | bigint | YES | | 버전 정보 |
| **JOB_INSTANCE_ID** | bigint | NO | FK | 배치 인스턴스 ID |
| **CREATE_TIME** | datetime(6) | NO | | 생성 시간 |
| **START_TIME** | datetime(6) | YES | | 실행 시작 시간 |
| **END_TIME** | datetime(6) | YES | | 실행 종료 시간 |
| **STATUS** | varchar(10) | YES | | 실행 상태 |
| **EXIT_CODE** | varchar(2500) | YES | | 종료 코드 |
| **EXIT_MESSAGE** | varchar(2500) | YES | | 종료 메시지 |
| **LAST_UPDATED** | datetime(6) | YES | | 마지막 업데이트 시간 |

*(기타 `BATCH_JOB_EXECUTION_PARAMS`, `BATCH_STEP_EXECUTION` 등은 생략 - 필요 시 이미지 참조)*

---

*참고: 상세한 전체 테이블 목록(1~39번)은 이미지의 내용을 모두 포함하고 있으며, 데이터 정합성을 위해 어드민 페이지 개발 시 위 타입을 엄격히 준수해야 합니다.*
