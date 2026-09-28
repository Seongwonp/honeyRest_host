-- =====================================================================
-- screenshot 프로필 전용 데모 데이터 (H2, MySQL 모드)
--   application-screenshot.properties 의 spring.sql.init 으로만 실행된다.
--   날짜는 CURRENT_DATE 기준 상대값이라 언제 띄워도 대시보드/차트가 채워진다.
--   로그인 계정(업체 관리자/최고 관리자)은 local-demo 프로필의 DataInitializer 가 만든다.
--   업체 ↔ 관리자 계정은 이메일로 매칭된다 (company.email = user.email).
-- =====================================================================

-- ---- MySQL 전용 함수 호환 (H2MySqlCompat 이 SQL 의 함수명을 MYSQL_* 로 치환) ----
CREATE ALIAS IF NOT EXISTS MYSQL_DATE_ADD    FOR 'com.honeyrest.honeyrest_host.config.screenshot.H2MySqlCompat.dateAdd';
CREATE ALIAS IF NOT EXISTS MYSQL_DATE_SUB    FOR 'com.honeyrest.honeyrest_host.config.screenshot.H2MySqlCompat.dateSub';
CREATE ALIAS IF NOT EXISTS MYSQL_DATEDIFF    FOR 'com.honeyrest.honeyrest_host.config.screenshot.H2MySqlCompat.dateDiff';
CREATE ALIAS IF NOT EXISTS MYSQL_WEEKDAY     FOR 'com.honeyrest.honeyrest_host.config.screenshot.H2MySqlCompat.weekday';
CREATE ALIAS IF NOT EXISTS MYSQL_DATE_FORMAT FOR 'com.honeyrest.honeyrest_host.config.screenshot.H2MySqlCompat.dateFormat';

-- ---- 지역 / 카테고리 ----
INSERT INTO region (region_id, parent_id, name, level, is_popular, image_url) VALUES
 (1, NULL, '제주', 1, TRUE,  '/uploads/placeholder/region-1.svg'),
 (2, NULL, '서울', 1, TRUE,  '/uploads/placeholder/region-2.svg'),
 (3, NULL, '강원', 1, FALSE, NULL),
 (4, NULL, '부산', 1, TRUE,  NULL),
 (5, 1, '제주시',   2, TRUE,  NULL),
 (6, 2, '종로구',   2, TRUE,  NULL),
 (7, 3, '강릉시',   2, FALSE, NULL),
 (8, 4, '해운대구', 2, TRUE,  NULL);

INSERT INTO accommodation_category (category_id, name, icon_url, sort_order) VALUES
 (1, '호텔', NULL, 1), (2, '펜션', NULL, 2), (3, '한옥', NULL, 3), (4, '리조트', NULL, 4);

-- ---- 업체 ----
INSERT INTO company (company_id, name, business_number, owner_name, phone, email, address, bank_info, commission_rate, status, created_at, updated_at) VALUES
 (1, '허니레스트 스테이', '123-45-67890', '박성원', '02-1234-5678', 'contact@honeyrest.com', '서울특별시 종로구 북촌로 12',
     '{"bank":"국민은행","account":"123-456-789012","holder":"허니레스트 스테이"}' FORMAT JSON, 10.00, 'ACTIVE',
     DATEADD('DAY', -400, CURRENT_TIMESTAMP), DATEADD('DAY', -30, CURRENT_TIMESTAMP)),
 (2, '씨사이드 호텔', '234-56-78901', '김바다', '051-987-6543', 'info@seasidehotel.com', '부산광역시 해운대구 해운대해변로 30',
     '{"bank":"부산은행","account":"101-2020-3030-04","holder":"씨사이드 호텔"}' FORMAT JSON, 12.00, 'ACTIVE',
     DATEADD('DAY', -300, CURRENT_TIMESTAMP), DATEADD('DAY', -10, CURRENT_TIMESTAMP)),
 (3, '어반스테이', '345-67-89012', '이도시', '02-555-7788', 'info@urbanstay.com', '서울특별시 마포구 양화로 45',
     NULL, 10.00, 'PENDING', DATEADD('DAY', -3, CURRENT_TIMESTAMP), DATEADD('DAY', -3, CURRENT_TIMESTAMP));

-- ---- 숙소 (ACTIVE = 운영 중, PENDING = 승인 대기) ----
INSERT INTO accommodation (accommodation_id, company_id, category_id, main_region_id, sub_region_id, name, address, latitude, longitude,
                           thumbnail, description, amenities, check_in_time, check_out_time, rating, min_price, status) VALUES
 (1, 1, 1, 1, 5, '허니레스트 제주 오션뷰', '제주특별자치도 제주시 애월읍 애월해안로 100', 33.462100, 126.309500,
     '/uploads/placeholder/room-1.svg', '애월 바다가 한눈에 보이는 오션뷰 호텔입니다.', '["WiFi","주차","수영장","조식"]' FORMAT JSON,
     TIMESTAMP '2025-01-01 15:00:00', TIMESTAMP '2025-01-01 11:00:00', 4.7, 120000, 'ACTIVE'),
 (2, 1, 3, 2, 6, '허니레스트 북촌 한옥', '서울특별시 종로구 북촌로 12', 37.582600, 126.983600,
     '/uploads/placeholder/room-2.svg', '북촌 골목 안 전통 한옥 스테이.', '["WiFi","조식","정원"]' FORMAT JSON,
     TIMESTAMP '2025-01-01 16:00:00', TIMESTAMP '2025-01-01 11:00:00', 4.8, 180000, 'ACTIVE'),
 (3, 1, 2, 3, 7, '허니레스트 강릉 솔숲 펜션', '강원특별자치도 강릉시 창해로 350', 37.795100, 128.915600,
     '/uploads/placeholder/room-1.svg', '송정 해변 솔숲 옆 독채 펜션 (신규 등록, 승인 대기).', '["WiFi","주차","바비큐"]' FORMAT JSON,
     TIMESTAMP '2025-01-01 15:00:00', TIMESTAMP '2025-01-01 11:00:00', NULL, 150000, 'PENDING'),
 (4, 2, 1, 4, 8, '씨사이드 해운대 호텔', '부산광역시 해운대구 해운대해변로 30', 35.158700, 129.160400,
     '/uploads/placeholder/room-2.svg', '해운대 해변 앞 비즈니스 호텔.', '["WiFi","주차","피트니스"]' FORMAT JSON,
     TIMESTAMP '2025-01-01 15:00:00', TIMESTAMP '2025-01-01 12:00:00', 4.5, 110000, 'ACTIVE'),
 (5, 2, 4, 4, 8, '씨사이드 기장 리조트', '부산광역시 기장군 기장읍 기장해안로 268', 35.188400, 129.223000,
     '/uploads/placeholder/room-1.svg', '기장 바다 앞 가족형 리조트 (승인 대기).', '["WiFi","주차","키즈풀"]' FORMAT JSON,
     TIMESTAMP '2025-01-01 15:00:00', TIMESTAMP '2025-01-01 11:00:00', NULL, 160000, 'PENDING');

INSERT INTO accommodation_image (accommodation_id, image_url, image_type, sort_order, created_at, updated_at) VALUES
 (1, '/uploads/placeholder/room-1.svg', 'MAIN', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
 (2, '/uploads/placeholder/room-2.svg', 'MAIN', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
 (3, '/uploads/placeholder/room-1.svg', 'MAIN', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
 (4, '/uploads/placeholder/room-2.svg', 'MAIN', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
 (5, '/uploads/placeholder/room-1.svg', 'MAIN', 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- ---- 객실 ----
INSERT INTO room (room_id, accommodation_id, name, type, price, max_occupancy, standard_occupancy, extra_person_fee,
                  bed_info, amenities, description, total_rooms, status, created_at, updated_at) VALUES
 (1, 1, '디럭스 오션뷰 더블', '디럭스', 120000, 3, 2, 20000, '{"double":1}' FORMAT JSON, '["WiFi","욕조"]' FORMAT JSON, '오션뷰 더블룸', 8, 'ACTIVE', DATEADD('DAY', -200, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
 (2, 1, '프리미어 트윈',       '프리미어', 150000, 4, 2, 20000, '{"single":2}' FORMAT JSON, '["WiFi"]' FORMAT JSON, '트윈 베드 객실', 5, 'ACTIVE', DATEADD('DAY', -200, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
 (3, 1, '오션 스위트',         '스위트', 280000, 4, 2, 30000, '{"king":1}' FORMAT JSON, '["WiFi","욕조","라운지"]' FORMAT JSON, '거실 분리형 스위트', 2, 'ACTIVE', DATEADD('DAY', -200, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
 (4, 2, '사랑채 온돌방',       '온돌', 180000, 2, 2, 0, '{"yo":2}' FORMAT JSON, '["WiFi"]' FORMAT JSON, '전통 온돌방', 3, 'ACTIVE', DATEADD('DAY', -180, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
 (5, 2, '안채 가족실',         '패밀리', 240000, 5, 4, 25000, '{"yo":4}' FORMAT JSON, '["WiFi","주방"]' FORMAT JSON, '4인 가족실', 2, 'ACTIVE', DATEADD('DAY', -180, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
 (6, 3, '솔숲 독채',           '독채', 150000, 6, 4, 20000, '{"queen":2}' FORMAT JSON, '["WiFi","바비큐"]' FORMAT JSON, '독채 펜션', 4, 'ACTIVE', DATEADD('DAY', -5, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
 (7, 4, '스탠다드 더블',       '스탠다드', 110000, 2, 2, 15000, '{"double":1}' FORMAT JSON, '["WiFi"]' FORMAT JSON, '비즈니스 더블', 20, 'ACTIVE', DATEADD('DAY', -250, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
 (8, 4, '오션 디럭스 트윈',    '디럭스', 160000, 3, 2, 20000, '{"single":2}' FORMAT JSON, '["WiFi","욕조"]' FORMAT JSON, '해변 전망 트윈', 10, 'ACTIVE', DATEADD('DAY', -250, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP);

INSERT INTO room_image (room_id, image_url, sort_order, created_at, updated_at)
SELECT room_id, CASE WHEN MOD(room_id, 2) = 1 THEN '/uploads/placeholder/room-1.svg' ELSE '/uploads/placeholder/room-2.svg' END, 1,
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
  FROM room;

-- ---- 요금 캘린더: 오늘 -7일 ~ +45일, 주말(금/토) 20% 할증 ----
INSERT INTO price_calendar (room_id, date, price, available_room, created_at, updated_at)
SELECT r.room_id,
       DATEADD('DAY', s.x, CURRENT_DATE),
       CASE WHEN ISO_DAY_OF_WEEK(DATEADD('DAY', s.x, CURRENT_DATE)) IN (5, 6) THEN r.price * 1.2 ELSE r.price END,
       GREATEST(r.total_rooms - MOD(s.x + r.room_id, 3), 0),
       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
  FROM room r, SYSTEM_RANGE(-7, 45) s;

-- ---- 일반 회원(예약자) ----
INSERT INTO user (user_id, email, name, phone, role, status, point, is_verified, marketing_agree, gender, birth_date, created_at, updated_at, last_login) VALUES
 (1, 'minji.kim@example.com',   '김민지', '010-1111-2222', 'GENERAL', 'ACTIVE',    3000, TRUE, TRUE,  'F', DATE '1994-03-12', DATEADD('DAY', -320, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, DATEADD('HOUR', -3, CURRENT_TIMESTAMP)),
 (2, 'junho.lee@example.com',   '이준호', '010-2222-3333', 'GENERAL', 'ACTIVE',    1500, TRUE, FALSE, 'M', DATE '1990-07-01', DATEADD('DAY', -280, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, DATEADD('DAY', -1, CURRENT_TIMESTAMP)),
 (3, 'seoyeon.park@example.com','박서연', '010-3333-4444', 'GENERAL', 'ACTIVE',       0, TRUE, TRUE,  'F', DATE '1998-11-23', DATEADD('DAY', -150, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, DATEADD('DAY', -2, CURRENT_TIMESTAMP)),
 (4, 'hyunwoo.choi@example.com','최현우', '010-4444-5555', 'GENERAL', 'ACTIVE',    5000, TRUE, TRUE,  'M', DATE '1987-01-30', DATEADD('DAY', -90, CURRENT_TIMESTAMP),  CURRENT_TIMESTAMP, DATEADD('DAY', -5, CURRENT_TIMESTAMP)),
 (5, 'jiwoo.jung@example.com',  '정지우', '010-5555-6666', 'GENERAL', 'ACTIVE',     700, TRUE, FALSE, 'F', DATE '2000-05-05', DATEADD('DAY', -40, CURRENT_TIMESTAMP),  CURRENT_TIMESTAMP, DATEADD('DAY', -7, CURRENT_TIMESTAMP)),
 (6, 'dohyun.kang@example.com', '강도현', '010-6666-7777', 'GENERAL', 'SUSPENDED',    0, TRUE, FALSE, 'M', DATE '1992-09-17', DATEADD('DAY', -200, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP, DATEADD('DAY', -60, CURRENT_TIMESTAMP));

-- ---- 예약: 상태별 샘플 (ReservationStatus 값) ----
INSERT INTO reservation (reservation_id, user_id, room_id, accommodation_id, accommodation_name, room_name, reservation_number,
                         check_in_date, check_out_date, guest_count, guest_name, guest_phone, price, original_price, discount_amount,
                         status, cancel_reason, special_requests, created_at, updated_at) VALUES
 (1, 1, 1, 1, '허니레스트 제주 오션뷰', '디럭스 오션뷰 더블', 'HR-DEMO-0001', DATEADD('DAY', 3, CURRENT_DATE),  DATEADD('DAY', 5, CURRENT_DATE),  2, '김민지', '010-1111-2222', 240000, 240000, 0,     'PENDING',        NULL, '늦은 체크인 예정입니다.', DATEADD('HOUR', -5, CURRENT_TIMESTAMP),  CURRENT_TIMESTAMP),
 (2, 2, 2, 1, '허니레스트 제주 오션뷰', '프리미어 트윈',      'HR-DEMO-0002', DATEADD('DAY', 1, CURRENT_DATE),  DATEADD('DAY', 3, CURRENT_DATE),  3, '이준호', '010-2222-3333', 290000, 300000, 10000, 'CONFIRMED',      NULL, NULL, DATEADD('DAY', -6, CURRENT_TIMESTAMP),  CURRENT_TIMESTAMP),
 (3, 3, 4, 2, '허니레스트 북촌 한옥',   '사랑채 온돌방',      'HR-DEMO-0003', CURRENT_DATE,                      DATEADD('DAY', 2, CURRENT_DATE),  2, '박서연', '010-3333-4444', 360000, 360000, 0,     'CONFIRMED',      NULL, '조식 2인 추가', DATEADD('DAY', -12, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
 (4, 4, 3, 1, '허니레스트 제주 오션뷰', '오션 스위트',        'HR-DEMO-0004', DATEADD('DAY', 10, CURRENT_DATE), DATEADD('DAY', 12, CURRENT_DATE), 2, '최현우', '010-4444-5555', 560000, 560000, 0,     'CANCEL_REQUEST', '일정 변경으로 취소 요청드립니다.', NULL, DATEADD('DAY', -9, CURRENT_TIMESTAMP),  CURRENT_TIMESTAMP),
 (5, 5, 5, 2, '허니레스트 북촌 한옥',   '안채 가족실',        'HR-DEMO-0005', DATEADD('DAY', 6, CURRENT_DATE),  DATEADD('DAY', 7, CURRENT_DATE),  4, '정지우', '010-5555-6666', 240000, 250000, 10000, 'CANCEL_REQUEST', '가족 사정으로 방문이 어렵습니다.', NULL, DATEADD('DAY', -4, CURRENT_TIMESTAMP),  CURRENT_TIMESTAMP),
 (6, 1, 4, 2, '허니레스트 북촌 한옥',   '사랑채 온돌방',      'HR-DEMO-0006', DATEADD('DAY', -5, CURRENT_DATE), DATEADD('DAY', -3, CURRENT_DATE), 2, '김민지', '010-1111-2222', 360000, 360000, 0,     'COMPLETED',      NULL, NULL, DATEADD('DAY', -20, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
 (7, 6, 1, 1, '허니레스트 제주 오션뷰', '디럭스 오션뷰 더블', 'HR-DEMO-0007', DATEADD('DAY', -8, CURRENT_DATE), DATEADD('DAY', -7, CURRENT_DATE), 2, '강도현', '010-6666-7777', 120000, 120000, 0,     'NO_SHOW',        NULL, NULL, DATEADD('DAY', -15, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
 (8, 2, 3, 1, '허니레스트 제주 오션뷰', '오션 스위트',        'HR-DEMO-0008', DATEADD('DAY', -2, CURRENT_DATE), DATEADD('DAY', 1, CURRENT_DATE),  2, '이준호', '010-2222-3333', 840000, 840000, 0,     'CANCELLED',      '항공편 결항', NULL, DATEADD('DAY', -25, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
 (9, 3, 2, 1, '허니레스트 제주 오션뷰', '프리미어 트윈',      'HR-DEMO-0009', DATEADD('DAY', 14, CURRENT_DATE), DATEADD('DAY', 17, CURRENT_DATE), 4, '박서연', '010-3333-4444', 450000, 450000, 0,     'CONFIRMED',      NULL, '유아용 침대 요청', DATEADD('DAY', -2, CURRENT_TIMESTAMP),  CURRENT_TIMESTAMP),
 (10, 4, 5, 2, '허니레스트 북촌 한옥',  '안채 가족실',        'HR-DEMO-0010', DATEADD('DAY', 20, CURRENT_DATE), DATEADD('DAY', 22, CURRENT_DATE), 5, '최현우', '010-4444-5555', 505000, 505000, 0,     'PENDING',        NULL, NULL, DATEADD('HOUR', -1, CURRENT_TIMESTAMP),  CURRENT_TIMESTAMP),
 (11, 5, 7, 4, '씨사이드 해운대 호텔',  '스탠다드 더블',      'HR-DEMO-0011', DATEADD('DAY', 2, CURRENT_DATE),  DATEADD('DAY', 4, CURRENT_DATE),  2, '정지우', '010-5555-6666', 220000, 220000, 0,     'CONFIRMED',      NULL, NULL, DATEADD('DAY', -3, CURRENT_TIMESTAMP),  CURRENT_TIMESTAMP),
 (12, 1, 8, 4, '씨사이드 해운대 호텔',  '오션 디럭스 트윈',   'HR-DEMO-0012', DATEADD('DAY', -4, CURRENT_DATE), DATEADD('DAY', -2, CURRENT_DATE), 2, '김민지', '010-1111-2222', 320000, 320000, 0,     'COMPLETED',      NULL, NULL, DATEADD('DAY', -18, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP);

-- ---- 지난 6개월 이용 완료 예약 (매출 차트용, 약 2일 간격) ----
INSERT INTO reservation (reservation_id, user_id, room_id, accommodation_id, accommodation_name, room_name, reservation_number,
                         check_in_date, check_out_date, guest_count, guest_name, guest_phone, price, original_price, discount_amount,
                         status, cancel_reason, special_requests, created_at, updated_at)
SELECT 100 + s.x,
       u.user_id, rm.room_id, a.accommodation_id, a.name, rm.name,
       'HR-DEMO-' || (1000 + s.x),
       DATEADD('DAY', -(s.x * 2) + 3, CURRENT_DATE),
       DATEADD('DAY', -(s.x * 2) + 3 + 1 + MOD(s.x, 2), CURRENT_DATE),
       2, u.name, u.phone,
       rm.price * (1 + MOD(s.x, 2)), rm.price * (1 + MOD(s.x, 2)), 0,
       CASE WHEN MOD(s.x, 11) = 0 THEN 'CANCELLED' ELSE 'COMPLETED' END,
       CASE WHEN MOD(s.x, 11) = 0 THEN '개인 사정' ELSE NULL END,
       NULL,
       DATEADD('DAY', -(s.x * 2), CURRENT_TIMESTAMP), DATEADD('DAY', -(s.x * 2), CURRENT_TIMESTAMP)
  FROM SYSTEM_RANGE(1, 90) s
  JOIN room rm ON rm.room_id = CASE MOD(s.x, 7) WHEN 0 THEN 1 WHEN 1 THEN 2 WHEN 2 THEN 4 WHEN 3 THEN 1
                                                 WHEN 4 THEN 5 WHEN 5 THEN 7 ELSE 3 END
  JOIN accommodation a ON a.accommodation_id = rm.accommodation_id
  JOIN user u ON u.user_id = 1 + MOD(s.x, 5);

-- ---- 결제: 예약 생성 시각에 결제 (취소는 CANCEL, 대기 예약은 PENDING) ----
INSERT INTO payment (reservation_id, user_id, amount, payment_method, payment_status, transaction_id, pg_provider, receipt_url,
                     payment_date, created_at, updated_at)
SELECT r.reservation_id, r.user_id, r.price,
       CASE MOD(r.reservation_id, 3) WHEN 0 THEN 'CARD' WHEN 1 THEN 'EASY_PAY' ELSE 'TRANSFER' END,
       CASE r.status WHEN 'CANCELLED' THEN 'CANCEL' WHEN 'PENDING' THEN 'PENDING' ELSE 'DONE' END,
       'demo-tx-' || r.reservation_id, 'TOSS', NULL,
       r.created_at, r.created_at, r.created_at
  FROM reservation r;

-- ---- 리뷰 ----
INSERT INTO review (reservation_id, user_id, accommodation_id, room_id, rating, cleanliness_rating, service_rating, facilities_rating, location_rating,
                    content, reply, like_count, status, created_at, updated_at) VALUES
 (6, 1, 2, 4, 5.0, 5.0, 5.0, 4.5, 5.0, '한옥 특유의 분위기가 너무 좋았어요. 조식도 정갈했습니다.', '소중한 후기 감사합니다. 다음에도 편히 쉬다 가세요!', 12, 'VISIBLE', DATEADD('DAY', -2, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
 (12, 1, 4, 8, 4.0, 4.0, 4.5, 4.0, 5.0, '해운대 바로 앞이라 위치가 최고입니다.', NULL, 3, 'VISIBLE', DATEADD('DAY', -1, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP);

INSERT INTO review (reservation_id, user_id, accommodation_id, room_id, rating, cleanliness_rating, service_rating, facilities_rating, location_rating,
                    content, reply, like_count, status, created_at, updated_at)
SELECT r.reservation_id, r.user_id, r.accommodation_id, r.room_id,
       CASE MOD(r.reservation_id, 4) WHEN 0 THEN 3.5 WHEN 1 THEN 5.0 WHEN 2 THEN 4.5 ELSE 4.0 END,
       4.5, 4.5, 4.0, 5.0,
       CASE MOD(r.reservation_id, 5)
            WHEN 0 THEN '바다 전망이 정말 멋졌어요. 다음에 또 오고 싶어요.'
            WHEN 1 THEN '직원분들이 친절하고 객실이 깨끗했습니다.'
            WHEN 2 THEN '위치는 좋은데 주차가 조금 불편했어요.'
            WHEN 3 THEN '가족 여행으로 딱 좋았습니다. 아이들이 좋아했어요.'
            ELSE '가격 대비 만족스러운 숙소였습니다.' END,
       CASE WHEN MOD(r.reservation_id, 3) = 0 THEN '방문해 주셔서 감사합니다!' ELSE NULL END,
       MOD(r.reservation_id, 9),
       CASE WHEN r.reservation_id = 107 THEN 'HIDDEN' ELSE 'VISIBLE' END,
       DATEADD('DAY', 1, CAST(r.check_out_date AS TIMESTAMP)), DATEADD('DAY', 1, CAST(r.check_out_date AS TIMESTAMP))
  FROM reservation r
 WHERE r.reservation_id BETWEEN 101 AND 118 AND r.status = 'COMPLETED';

-- ---- 문의 ----
INSERT INTO inquiry (user_id, accommodation_id, category, title, content, reply, is_replied, created_at, updated_at) VALUES
 (1, 1, '예약', '체크인 시간 변경 가능할까요?', '비행기 도착이 늦어 밤 11시쯤 체크인해도 될까요?', '네, 프런트는 24시간 운영합니다.', TRUE,  DATEADD('DAY', -3, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
 (2, 1, '시설', '수영장 운영 시간 문의', '야외 수영장은 몇 시까지 이용 가능한가요?', NULL, FALSE, DATEADD('HOUR', -6, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
 (3, 2, '주차', '주차 가능 여부', '차량 1대 주차 가능한가요?', '인근 공영주차장 이용을 안내드립니다.', TRUE, DATEADD('DAY', -5, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP),
 (4, 2, '기타', '반려동물 동반 문의', '소형견 동반 투숙이 가능한지 궁금합니다.', NULL, FALSE, DATEADD('HOUR', -2, CURRENT_TIMESTAMP), CURRENT_TIMESTAMP);

-- ---- 시스템 에러 로그 (최고 관리자 > 시스템) ----
INSERT INTO error_log (occurred_at, request_url, request_method, error_class, message, stack_trace, resolved) VALUES
 (DATEADD('MINUTE', -35, CURRENT_TIMESTAMP), '/admin/reservations/day', 'GET', 'java.lang.IllegalArgumentException', '유효하지 않은 날짜 형식: 2025-13-01', 'java.lang.IllegalArgumentException: 유효하지 않은 날짜 형식', FALSE),
 (DATEADD('HOUR', -4, CURRENT_TIMESTAMP),    '/admin/rooms/edit/99', 'GET', 'jakarta.persistence.EntityNotFoundException', '객실을 찾을 수 없습니다. id=99', 'jakarta.persistence.EntityNotFoundException: ...', FALSE),
 (DATEADD('HOUR', -20, CURRENT_TIMESTAMP),   '/admin/price/grid-cells', 'GET', 'org.springframework.dao.DataIntegrityViolationException', 'could not execute statement', 'org.springframework.dao.DataIntegrityViolationException: ...', TRUE),
 (DATEADD('DAY', -2, CURRENT_TIMESTAMP),     '/owner/accommodation/7/modify', 'POST', 'java.lang.IllegalStateException', '이미 처리된 승인 요청입니다.', 'java.lang.IllegalStateException: ...', TRUE),
 (DATEADD('DAY', -3, CURRENT_TIMESTAMP),     '/admin/reviews/list', 'GET', 'java.lang.NullPointerException', 'Cannot invoke "String.length()" because "keyword" is null', 'java.lang.NullPointerException: ...', TRUE);

-- ---- 명시적 ID 로 넣은 테이블의 IDENTITY 시작값을 비켜 준다 (DataInitializer/화면 등록과 충돌 방지) ----
ALTER TABLE region ALTER COLUMN region_id RESTART WITH 100;
ALTER TABLE accommodation_category ALTER COLUMN category_id RESTART WITH 100;
ALTER TABLE company ALTER COLUMN company_id RESTART WITH 100;
ALTER TABLE accommodation ALTER COLUMN accommodation_id RESTART WITH 100;
ALTER TABLE room ALTER COLUMN room_id RESTART WITH 100;
ALTER TABLE user ALTER COLUMN user_id RESTART WITH 100;
ALTER TABLE reservation ALTER COLUMN reservation_id RESTART WITH 1000;
