-- ============================================================
-- 목데이터 실행 순서 (FK 의존성 순서 엄수)
-- 1. insert.sql          ← 지금 이 파일 (기초 데이터)
--    : region, accommodation_category, accommodation_tag,
--      company, banner, coupon, event
-- 2. insert_pk_1-20.sql  (accommodation 1~20 + 관련 데이터)
-- 3. insert_pk_21-30.sql (accommodation 21~30 + 관련 데이터)
-- 4. insert_pk_31-33.sql (accommodation 31~33 + 관련 데이터)
-- ============================================================

INSERT INTO accommodation_category (icon_url, name, sort_order)
VALUES
    ('https://cdn-icons-png.flaticon.com/512/235/235861.png', '호텔/리조트', 1),
    ('https://cdn-icons-png.flaticon.com/512/235/235862.png', '펜션/풀빌라', 2),
    ('https://cdn-icons-png.flaticon.com/512/235/235863.png', '모텔', 3),
    ('https://cdn-icons-png.flaticon.com/512/235/235864.png', '게스트하우스', 4),
    ('https://cdn-icons-png.flaticon.com/512/235/235865.png', '캠핑/글램핑', 5),
    ('https://cdn-icons-png.flaticon.com/512/10507/10507013.png','한옥',6);



-- 취향
INSERT INTO accommodation_tag (category, name) VALUES
                                                   ('취향', '감성숙소'),
                                                   ('취향', '연인추천'),
                                                   ('취향', '가족여행숙소'),
                                                   ('취향', '친구끼리'),
                                                   ('취향', '혼자여행'),
                                                   ('취향', '프라이빗'),
                                                   ('취향', '힐링'),
                                                   ('취향', '조용한'),
                                                   ('취향', '파티룸'),
                                                   ('취향', '뷰맛집'),
                                                   ('취향', '인생샷'),
                                                   ('취향', '노을맛집'),
                                                   ('취향', '해돋이명소'),
                                                   ('취향', '야경좋은'),
                                                   ('취향', '숲속숙소'),
                                                   ('취향', '오션뷰'),
                                                   ('취향', '루프탑'),
                                                   ('취향', '테라스'),
                                                   ('취향', '복층구조'),
                                                   ('취향', '감성조명'),
                                                   ('취향', '빈티지풍'),
                                                   ('취향', '미드센추리'),
                                                   ('취향', '한옥스타일'),
                                                   ('취향', '모던인테리어'),
                                                   ('취향', '북유럽풍'),
                                                   ('취향', '자연친화적');

-- 객실 내 시설
INSERT INTO accommodation_tag (category, name) VALUES
                                                   ('객실내시설', '스파/월풀'),
                                                   ('객실내시설', '객실스파'),
                                                   ('객실내시설', '미니바'),
                                                   ('객실내시설', '무선인터넷'),
                                                   ('객실내시설', '에어컨'),
                                                   ('객실내시설', '욕실용품'),
                                                   ('객실내시설', '샤워실'),
                                                   ('객실내시설', '개인콘센트'),
                                                   ('객실내시설', 'TV'),
                                                   ('객실내시설', '넷플릭스'),
                                                   ('객실내시설', 'OTT'),
                                                   ('객실내시설', '블루투스스피커'),
                                                   ('객실내시설', '커피머신'),
                                                   ('객실내시설', '냉장고'),
                                                   ('객실내시설', '전자레인지'),
                                                   ('객실내시설', '취사도구'),
                                                   ('객실내시설', '침대형 온돌'),
                                                   ('객실내시설', '침대 2개'),
                                                   ('객실내시설', '소파베드'),
                                                   ('객실내시설', '책상'),
                                                   ('객실내시설', '방음시설'),
                                                   ('객실내시설', '흡연가능'),
                                                   ('객실내시설', '금연객실');

-- 공용 시설
INSERT INTO accommodation_tag (category, name) VALUES
                                                   ('시설', '수영장'),
                                                   ('시설', '온수풀'),
                                                   ('시설', '바베큐장'),
                                                   ('시설', '사우나'),
                                                   ('시설', '찜질방'),
                                                   ('시설', '피트니스'),
                                                   ('시설', '레스토랑'),
                                                   ('시설', '카페'),
                                                   ('시설', '매점'),
                                                   ('시설', '공용주방'),
                                                   ('시설', '공용샤워실'),
                                                   ('시설', '공용화장실'),
                                                   ('시설', '공용세탁기'),
                                                   ('시설', '건조기'),
                                                   ('시설', '탈수기'),
                                                   ('시설', '라운지'),
                                                   ('시설', '루프탑바'),
                                                   ('시설', '캠프파이어존'),
                                                   ('시설', '개인사물함'),
                                                   ('시설', '스프링클러'),
                                                   ('시설', '엘리베이터'),
                                                   ('시설', '주차장'),
                                                   ('시설', '전기차충전소');

-- 반려동물
INSERT INTO accommodation_tag (category, name) VALUES
                                                   ('반려동물', '반려견동반'),
                                                   ('반려동물', '반려묘동반'),
                                                   ('반려동물', '펫 전용 객실'),
                                                   ('반려동물', '펫 전용 어메니티'),
                                                   ('반려동물', '펫 놀이터'),
                                                   ('반려동물', '펫 동반 레스토랑'),
                                                   ('반려동물', '펫 전용 수영장'),
                                                   ('반려동물', '펫 케어 서비스'),
                                                   ('반려동물', '펫 픽업 가능');

-- 서비스/운영
INSERT INTO accommodation_tag (category, name) VALUES
                                                   ('운영정보', '조식제공'),
                                                   ('운영정보', '무료주차'),
                                                   ('운영정보', '픽업서비스'),
                                                   ('운영정보', '짐보관가능'),
                                                   ('운영정보', '비대면체크인'),
                                                   ('운영정보', '당일예약 가능'),
                                                   ('운영정보', '24시간 체크인'),
                                                   ('운영정보', '연박특가'),
                                                   ('운영정보', '쿠폰할인'),
                                                   ('운영정보', '할인특가'),
                                                   ('운영정보', '무한대실'),
                                                   ('운영정보', '얼리체크인'),
                                                   ('운영정보', '레이트체크아웃'),
                                                   ('운영정보', '예약확정 빠름'),
                                                   ('운영정보', '실시간예약'),
                                                   ('운영정보', '블랙회원전용'),
                                                   ('운영정보', '리뷰좋은'),
                                                   ('운영정보', '청결우수'),
                                                   ('운영정보', '방역강화');

-- 숙소등급/분류
INSERT INTO accommodation_tag (category, name) VALUES
                                                   ('등급', '5성급'),
                                                   ('등급', '4성급'),
                                                   ('등급', '풀빌라'),
                                                   ('등급', '글램핑'),
                                                   ('등급', '캠핑'),
                                                   ('등급', '게스트하우스'),
                                                   ('등급', '호텔'),
                                                   ('등급', '모텔'),
                                                   ('등급', '리조트'),
                                                   ('등급', '한옥'),
                                                   ('등급', '민박');

-- 위치/접근성
INSERT INTO accommodation_tag (category, name) VALUES
                                                   ('위치', '해변근처'),
                                                   ('위치', '산속'),
                                                   ('위치', '도심'),
                                                   ('위치', '역세권'),
                                                   ('위치', '버스터미널 근처'),
                                                   ('위치', '공항근처'),
                                                   ('위치', '스키장근처'),
                                                   ('위치', '관광지근처'),
                                                   ('위치', '맛집거리'),
                                                   ('위치', '카페거리'),
                                                   ('위치', '쇼핑몰근처'),
                                                   ('위치', '조용한동네'),
                                                   ('위치', '번화가'),
                                                   ('위치', '바닷가 산책로'),
                                                   ('위치', '자전거도로 인접'),
                                                   ('위치', '낚시 가능'),
                                                   ('위치', '트레킹 가능'),
                                                   ('위치', '등산로 인접');

INSERT INTO banner (
    banner_id,
    created_at,
    updated_at,
    end_date,
    image_url,
    is_active,
    position,
    sort_order,
    start_date,
    target_url,
    title
) VALUES
      (5, '2025-08-14 12:03:19.849516', '2025-08-14 12:03:19.849516', '2026-10-13 12:03:18.416000',
       'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/banner%2F5f186eea-79ad-45c2-a422-a1d7c1a5adf4_bg1.jpg?alt=media&token=261e3b9e-f6c8-4eff-b905-f6be32f85a02',
       true, 'MAIN_TOP', 1, '2025-08-14 12:03:18.416017', 'http://localhost:8080/banner/', 'Banner1'),

      (6, '2025-08-14 12:12:18.867578', '2025-08-14 12:12:18.867578', '2026-10-13 12:12:18.044000',
       'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/banner%2Fc993e30c-16b3-4285-9f61-df31353e213f_bg2.jpg?alt=media&token=0cef6741-ccd4-4502-b46b-95bcd16fe9e5',
       true, 'MAIN_TOP', 2, '2025-08-14 12:12:18.044169', 'http://localhost:8080/banner/', 'Banner2'),

      (7, '2025-08-14 12:12:31.568848', '2025-08-14 12:12:31.568848', '2026-10-13 12:12:30.555000',
       'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/banner%2Ff6784ed5-05cd-4d57-a523-2e44c1ab6cca_bg3.jpg?alt=media&token=ee68f0af-5dbe-492c-97e9-70349727c013',
       true, 'MAIN_TOP', 3, '2025-08-14 12:12:30.555029', 'http://localhost:8080/banner/', 'Banner3'),

      (8, '2025-08-14 12:12:42.634693', '2025-08-14 12:12:42.634693', '2026-10-13 12:12:41.406000',
       'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/banner%2F85d5d806-cccd-497c-af33-04e4eedc9af6_bg4.jpg?alt=media&token=a910b4cd-24f7-492b-a5f7-27f821193981',
       true, 'MAIN_TOP', 4, '2025-08-14 12:12:41.406428', 'http://localhost:8080/banner/', 'Banner4'),

      (9, '2025-08-14 17:12:42.393110', '2025-08-14 17:12:42.393110', '2025-10-13 17:12:41.096907',
       'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/banner%2F2fe977eb-cfc1-47e4-abeb-0053b33679c3_banner5.jpg?alt=media&token=7835bd85-da14-4f48-b044-81fdf13eb7f6',
       true, 'MAIN_TOP', 5, '2025-08-14 17:12:41.096587', 'http://localhost:8080/banner/', 'Banner5'),

      (10, '2025-09-02 09:42:12.314964', '2025-09-02 09:42:12.314964', '2035-07-12 09:42:11.234959',
       'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/banner%2F1e824ea9-9b1f-4524-9e9f-6ed52eae2fd7_banner6.jpg?alt=media&token=512df1c1-0c1d-4dea-8ff8-b7bf0ab247c1',
       true, 'MAIN_TOP', 6, '2025-09-02 09:42:11.234482', 'http://localhost:8080/banner/', 'banner6'),

      (11, '2025-09-02 09:45:37.635903', '2025-09-02 09:45:37.635903', '2035-07-12 09:45:37.308287',
       'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/banner%2F44999693-6599-4355-b026-200787893ee5_banner7.jpg?alt=media&token=4aa23d55-2fd7-4840-ae31-38788e237dfa',
       true, 'MAIN_TOP', 7, '2025-09-02 09:45:37.308269', 'http://localhost:8080/banner/', 'banner7'),

      (12, '2025-09-02 09:46:11.791726', '2025-09-02 09:46:11.791726', '2035-07-12 09:46:11.478775',
       'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/banner%2F7a052e73-682c-4ddc-9f9c-2b82e20b07b8_banner8.jpg?alt=media&token=d297bbac-b029-4a5c-b7b2-61a4631730d0',
       true, 'MAIN_TOP', 8, '2025-09-02 09:46:11.478545', 'http://localhost:8080/banner/', 'banner8'),

      (13, '2025-09-02 09:47:45.267826', '2025-09-02 09:47:45.267826', '2035-07-12 09:47:44.959573',
       'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/banner%2F3395cbc9-a29f-4b21-9b96-946d3aaab7eb_banner9.jpg?alt=media&token=2266edc2-c6e3-4383-8e82-ba2cae65f7f4',
       true, 'MAIN_TOP', 9, '2025-09-02 09:47:44.959463', 'http://localhost:8080/banner/', 'banner9'),

      (14, '2025-09-02 09:48:00.115967', '2025-09-02 09:48:00.115967', '2035-07-12 09:47:59.778547',
       'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/banner%2F8e802829-1bc9-42e8-800b-02ac97d4e8f2_banner10.jpg?alt=media&token=46d7a7a6-c658-4dbb-a13e-563c1121e172',
       true, 'MAIN_TOP', 10, '2025-09-02 09:47:59.778501', 'http://localhost:8080/banner/', 'banner10');


INSERT INTO company (
    company_id,
    created_at,
    updated_at,
    address,
    bank_info,
    business_number,
    commission_rate,
    email,
    name,
    owner_name,
    phone,
    status
) VALUES
      (1,
       '2025-08-17 23:11:15.000000',
       '2025-08-17 23:11:15.000000',
       '서울시 강남구 테헤란로 123',
       '{"bank": "카카오뱅크", "account": "3333-12-3456789"}',
       '123-45-67890',
       10.00,
       'contact@honeyrest.com',
       '허니레스트컴퍼니',
       '박성원',
       '010-1234-5678',
       'ACTIVE'),

      (2,
       '2025-08-25 09:10:01.000000',
       '2025-08-25 09:10:01.000000',
       '부산광역시 해운대구 우동 456',
       '{"bank": "국민은행", "account": "123-456-789012"}',
       '987-65-43210',
       12.50,
       'info@seasidehotel.com',
       '바다사랑',
       '김바다',
       '010-2345-6789',
       'ACTIVE'),

      (3,
       '2025-08-26 10:00:00.000000',
       '2025-08-26 10:00:00.000000',
       '서울특별시 마포구 합정동 358-1',
       '{"bank": "신한은행", "account": "110-123-456789"}',
       '111-22-33333',
       10.00,
       'info@urbanstay.com',
       '어반스테이',
       '이도시',
       '010-3456-7890',
       'ACTIVE'),

      (4,
       '2025-08-26 11:00:00.000000',
       '2025-08-26 11:00:00.000000',
       '전북특별자치도 전주시 완산구 전동 1가 1-1',
       '{"bank": "하나은행", "account": "230-456-789012"}',
       '222-33-44444',
       8.00,
       'info@hanokhospitality.com',
       '한옥호스피탈리티',
       '박한옥',
       '010-4567-8901',
       'ACTIVE'),

      (5,
       '2025-08-26 12:00:00.000000',
       '2025-08-26 12:00:00.000000',
       '경기도 가평군 가평읍 읍내리 101-2',
       '{"bank": "우리은행", "account": "1002-789-012345"}',
       '333-44-55555',
       9.00,
       'info@natureretreat.com',
       '자연쉼터',
       '최자연',
       '010-5678-9012',
       'ACTIVE'),

      (6,
       '2025-08-26 13:00:00.000000',
       '2025-08-26 13:00:00.000000',
       '경상북도 경주시 황남동 240-1',
       '{"bank": "농협", "account": "302-0123-4567-81"}',
       '444-55-66666',
       9.50,
       'info@gyeongjustay.com',
       '경주스테이',
       '김경주',
       '010-6789-0123',
       'ACTIVE');


INSERT INTO coupon (
    coupon_id,
    created_at,
    updated_at,
    code,
    discount_type,
    discount_value,
    end_date,
    is_active,
    max_discount_amount,
    min_order_amount,
    name,
    start_date,
    target_id,
    target_type
) VALUES
      (1, '2025-09-01 10:00:00.000000', '2025-09-01 10:00:00.000000', 'HEALING10', 'PERCENT', 10.00, '2025-09-30 23:59:59.000000', true, 60000.00, 80000.00, '힐링타임 10% 할인쿠폰', '2025-09-01 00:00:00.000000', NULL, 'ALL'),

      (2, '2025-09-01 10:05:00.000000', '2025-09-01 10:05:00.000000', 'CAMP4000', 'FIXED', 4000.00, '2025-10-30 23:59:59.000000', true, NULL, 20000.00, '불멍 캠핑장 4천원 할인', '2025-09-01 00:00:00.000000', 5, 'CATEGORY'),

      (3, '2025-09-01 10:10:00.000000', '2025-09-01 10:10:00.000000', 'VACANCE10', 'PERCENT', 10.00, '2025-10-15 23:59:59.000000', true, 60000.00, 80000.00, '호캉스 사전예약 10% 할인', '2025-09-01 00:00:00.000000', NULL, 'ALL'),

      (4, '2025-09-01 10:15:00.000000', '2025-09-01 10:15:00.000000', 'BUNDLE10', 'PERCENT', 10.00, '2025-10-25 23:59:59.000000', true, NULL, NULL, '기차+숙소 묶음 할인', '2025-09-01 00:00:00.000000', NULL, 'BUNDLE'),

      (5, '2025-09-01 10:20:00.000000', '2025-09-01 10:20:00.000000', 'TOSSPAY5000', 'FIXED', 5000.00, '2025-10-25 23:59:59.000000', true, NULL, NULL, '토스페이 결제 추가 할인', '2025-09-01 00:00:00.000000', NULL, 'PAYMENT'),

      (6, '2025-09-01 10:25:00.000000', '2025-09-01 10:25:00.000000', 'HOTELTOP3', 'FIXED', 30000.00, '2025-11-25 23:59:59.000000', true, NULL, 100000.00, '호텔 TOP3 특가 할인', '2025-09-01 00:00:00.000000', 1, 'CATEGORY');


INSERT INTO event (
    event_id,
    created_at,
    updated_at,
    description,
    end_date,
    image_url,
    is_active,
    start_date,
    target_url,
    title
) VALUES
      (4, '2025-09-04 10:12:07.296513', '2025-09-04 10:12:07.296513',
       '가끔은 아무것도 하지 않는 게 가장 필요한 순간입니다.   잔디밭에 누워 하늘을 바라보며, 친구와 나누는 소소한 이야기 속에서 우리는 진짜 행복을 발견하곤 하죠.   HoneyRest는 그런 순간을 위해 준비했습니다.   지금 진행 중인 이벤트에 참여하고, 당신만의 힐링 타임을 만들어보세요.   쿠폰도 챙기고, 마음도 챙기고—행복은 멀리 있지 않아요.   지금 바로 참여하고, 당신의 일상에 작은 쉼표를 선물하세요.',
       '2025-09-30 23:59:59.000000',
       'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/event%2Fea5f4938-c3cc-4545-a81a-e2c1fafe8965_Event1.gif?alt=media&token=202f9005-48b2-4388-a036-0b7d79ec0142',
       true,
       '2025-09-04 00:00:00.000000',
       'https://honeyrest.com/event/healing-time',
       '🌿 힐링이 필요한 당신에게, HoneyRest 이벤트'),

      (5, '2025-09-04 10:16:57.263972', '2025-09-04 10:16:57.263972',
       '캠핑하면 역시 불멍이죠./n 지친 심신을 달래는 ‘불멍 때리기 여행’이 요즘 대세!/n 타닥타닥 장작불의 불꽃을 하염없이 바라보며, 아무 생각 없이 멍 때리는 그 순간—진짜 힐링이 시작됩니다./n HoneyRest가 추천하는 불멍하기 좋은 캠핑장에서 당신만의 조용한 밤을 보내보세요./n 캠핑클럽과 함께하는 이번 이벤트에 참여하면, 불멍 특화 캠핑장 쿠폰과 함께 특별한 혜택도 드려요./n 🔥 지금 바로 참여하고, 타닥타닥 불꽃 속으로 떠나보세요.',
       '2025-10-30 23:59:59.000000',
       'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/event%2FEvent2.gif?alt=media&token=4b6423d3-7e30-475e-8fbd-337175341aef',
       true,
       '2025-09-04 00:00:00.000000',
       'https://honeyrest.com/event/firecamping',
       '불멍하기 좋은 캠핑장 추천 이벤트 🔥'),

      (6, '2025-09-04 10:19:19.349518', '2025-09-04 10:19:19.349518',
       '허니레스트가 준비한 국내 숙소 할인 혜택!/n 지금 미리 예약하면, 여행도 호캉스도 더 가볍게 떠날 수 있어요./n 60일 전 사전 예약 시, 최대 10% 할인 쿠폰팩 제공!/n (8만원 이상 구매 시 최대 6만원 할인 가능)/n 여행을 계획 중이라면 지금이 바로 기회입니다./n HoneyRest와 함께 더 스마트하게 떠나보세요.',
       '2025-10-15 23:59:59.000000',
       'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/event%2Fb6160131-8ee7-4b32-9ab3-3cdeb1ad0bdb_event6.jpg?alt=media&token=d82481a5-e173-4d28-85b6-5e4922b76f0d',
       true,
       '2025-09-04 00:00:00.000000',
       'https://honeyrest.com/event/vacance-package',
       '여행 · 호캉스 가기 좋은 할인 혜택 이벤트 ✈️'),

      (7, '2025-09-04 10:20:43.559821', '2025-09-04 10:20:43.559821',
       'HoneyRest가 준비한 여름맞이 복합 할인 이벤트!/n 레저/티켓, 교통수단, 결제 수단까지 모두 챙겨드립니다./n 레저/티켓 쿠폰: 2만원 이상 구매 시 최대 4천원 할인/n 교통수단 쿠폰: 기차 + 국내숙소 함께 담으면 10% 묶음 할인/n 토스페이 추가 할인: 라이베이로 결제 시 5천원 추가 할인/n 지금 바로 참여하고, 여행 준비를 더 스마트하게 시작하세요!/n 혜택은 쌓이고, 여행은 가벼워집니다.',
       '2025-10-25 23:59:59.000000',
       'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/event%2F6da56757-18dc-4af2-8065-e62471610aa8_event7.jpg?alt=media&token=a2da736a-0d34-477c-be44-17751a777e3c',
       true,
       '2025-09-04 00:00:00.000000',
       'https://honeyrest.com/event/vacance-benefits',
       '놀라운 이벤트와 결제 혜택이 와르르 ♥ VACANCE PACKAGE'),

      (8, '2025-09-04 10:21:33.254528', '2025-09-04 10:21:33.254528',
       'HoneyRest가 엄선한 국내 호텔 TOP 3 특가 혜택!/n 지금 예약하면 최대 80%까지 할인된 가격으로 럭셔리 숙소를 즐길 수 있어요./n 1. 강릉 샌샤인비치 호텔 ★ 4성급/n    - 정상가: 480,000원 → 특가: 98,200원 (79% 할인)/n 2. 딸기스테이 강남 ★ 3성급/n    - 정상가: 363,000원 → 특가: 99,000원 (72% 할인)/n 3. 썬라이즈 팰리스 ★ 4성급/n    - 정상가: 360,000원 → 특가: 114,880원 (68% 할인)/n 지금 바로 예약하고, 특별한 하루를 더 특별하게 만들어보세요.',
       '2025-11-25 23:59:59.000000',
       'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/event%2Fac33aa4c-22a3-42f9-bcde-5d2fb3277888_event8.jpg?alt=media&token=18cc9f00-9162-498a-a284-8acb23bcde8f',
       true,
       '2025-09-04 00:00:00.000000',
       'https://honeyrest.com/event/hotel-top3-deals',
       '국내 호텔 TOP 3 특가 이벤트 ✨ 최대 80% 할인'),

      (9, '2025-09-04 10:25:40.556553', '2025-09-04 10:25:40.556553',
       '나의 여행을 더욱 다채롭게!/n 푸른 바다, 햇살 가득한 해변, 그리고 나만의 세계를 넓히는 특별한 경험/n 허니레스트와 함께 떠나는 여름 여행은 단순한 휴식이 아닌 새로운 시작입니다./n 지금 바로 예약하고, 당신만의 색깔로 여름을 채워보세요./n 숙박은 HONEYREST에서!',
       '2025-09-25 23:59:59.000000',
       'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/event%2F4f97ac31-d8f4-4163-8452-f1ea6d621657_event9.jpg?alt=media&token=d808aff2-4dbe-40b4-b3a6-e4cb66c6327a',
       true,
       '2025-07-04 00:00:00.000000',
       'https://honeyrest.com/event/summer-vacance',
       'SUMMER VACANCE — 나의 여행을 더욱 다채롭게!');



-- 시도 등록
INSERT INTO region (region_id, name, level, is_popular)
VALUES (1, '서울특별시', 1, 1);
INSERT INTO region (region_id, name, level, is_popular)
VALUES (2, '부산광역시', 1, 1);
INSERT INTO region (region_id, name, level, is_popular)
VALUES (3, '대구광역시', 1, 1);
INSERT INTO region (region_id, name, level, is_popular)
VALUES (4, '인천광역시', 1, 1);
INSERT INTO region (region_id, name, level, is_popular)
VALUES (5, '광주광역시', 1, 1);
INSERT INTO region (region_id, name, level, is_popular)
VALUES (6, '대전광역시', 1, 1);
INSERT INTO region (region_id, name, level, is_popular)
VALUES (7, '울산광역시', 1, 1);
INSERT INTO region (region_id, name, level, is_popular)
VALUES (8, '세종특별자치시', 1, 1);
INSERT INTO region (region_id, name, level, is_popular)
VALUES (9, '경기도', 1, 1);
INSERT INTO region (region_id, name, level, is_popular)
VALUES (10, '강원특별자치도', 1, 1);
INSERT INTO region (region_id, name, level, is_popular)
VALUES (11, '충청북도', 1, 1);
INSERT INTO region (region_id, name, level, is_popular)
VALUES (12, '충청남도', 1, 1);
INSERT INTO region (region_id, name, level, is_popular)
VALUES (13, '전북특별자치도', 1, 1);
INSERT INTO region (region_id, name, level, is_popular)
VALUES (14, '전라남도', 1, 1);
INSERT INTO region (region_id, name, level, is_popular)
VALUES (15, '경상북도', 1, 1);
INSERT INTO region (region_id, name, level, is_popular)
VALUES (16, '경상남도', 1, 1);
INSERT INTO region (region_id, name, level, is_popular)
VALUES (17, '제주특별자치도', 1, 1);

-- 서울특별시 전체 자치구
INSERT INTO region (name, level, parent_id, is_popular)
VALUES
-- 인기 지역
('강남구', 2, 1, 1),
('서초구', 2, 1, 1),
('송파구', 2, 1, 1),
('마포구', 2, 1, 1),
('종로구', 2, 1, 1),
('중구', 2, 1, 1),
('영등포구', 2, 1, 1),
('성동구', 2, 1, 1),
('동작구', 2, 1, 1),
('은평구', 2, 1, 1),
('노원구', 2, 1, 1),
('광진구', 2, 1, 1),
('양천구', 2, 1, 1),
('강서구', 2, 1, 1),

-- 비인기 지역 (주거 중심)
('강북구', 2, 1, 0),
('도봉구', 2, 1, 0),
('중랑구', 2, 1, 0),
('성북구', 2, 1, 0),
('구로구', 2, 1, 0),
('금천구', 2, 1, 0);



INSERT INTO region (name, level, parent_id, is_popular)
VALUES
-- 인기 지역
('해운대구', 2, 2, 1),
('수영구', 2, 2, 1),
('부산진구', 2, 2, 1),
('중구', 2, 2, 1),
('남구', 2, 2, 1),
('동래구', 2, 2, 1),
('기장군', 2, 2, 1),

-- 비인기 지역
('서구', 2, 2, 0),
('동구', 2, 2, 0),
('영도구', 2, 2, 0),
('북구', 2, 2, 0),
('사하구', 2, 2, 0),
('금정구', 2, 2, 0),
('강서구', 2, 2, 0),
('연제구', 2, 2, 0),
('사상구', 2, 2, 0);


INSERT INTO region (name, level, parent_id, is_popular)
VALUES
-- 인기 지역
('중구', 2, 3, 1),
('수성구', 2, 3, 1),
('동구', 2, 3, 1),
('남구', 2, 3, 1),

-- 비인기 지역
('서구', 2, 3, 0),
('북구', 2, 3, 0),
('달서구', 2, 3, 0),
('달성군', 2, 3, 0),
('군위군', 2, 3, 0);

INSERT INTO region (name, level, parent_id, is_popular)
VALUES
-- 인기 지역
('중구', 2, 4, 1),
('연수구', 2, 4, 1),
('남동구', 2, 4, 1),
('부평구', 2, 4, 1),
('계양구', 2, 4, 1),

-- 비인기 지역
('동구', 2, 4, 0),
('서구', 2, 4, 0),
('미추홀구', 2, 4, 0),
('강화군', 2, 4, 0),
('옹진군', 2, 4, 0);

INSERT INTO region (name, level, parent_id, is_popular)
VALUES
-- 인기 지역
('동구', 2, 5, 1),
('서구', 2, 5, 1),
('남구', 2, 5, 1),

-- 비인기 지역
('북구', 2, 5, 0),
('광산구', 2, 5, 0);

INSERT INTO region (name, level, parent_id, is_popular)
VALUES
-- 인기 지역
('서구', 2, 6, 1),
('유성구', 2, 6, 1),
('중구', 2, 6, 1),

-- 비인기 지역
('동구', 2, 6, 0),
('대덕구', 2, 6, 0);

INSERT INTO region (name, level, parent_id, is_popular)
VALUES
-- 인기 지역
('남구', 2, 7, 1),
('중구', 2, 7, 1),
('동구', 2, 7, 1),

-- 비인기 지역
('북구', 2, 7, 0),
('울주군', 2, 7, 0);

INSERT INTO region (name, level, parent_id, is_popular)
VALUES
-- 인기 지역 (행복도시 중심)
('어진동', 2, 8, 1),
('도담동', 2, 8, 1),
('종촌동', 2, 8, 1),
('아름동', 2, 8, 1),
('고운동', 2, 8, 1),
('보람동', 2, 8, 1),
('새롬동', 2, 8, 1),
('다정동', 2, 8, 1),
('한솔동', 2, 8, 1),

-- 비인기 지역 (외곽 읍·면)
('금남면', 2, 8, 0),
('부강면', 2, 8, 0),
('소정면', 2, 8, 0),
('전의면', 2, 8, 0),
('전동면', 2, 8, 0),
('연서면', 2, 8, 0),
('장군면', 2, 8, 0),
('연기면', 2, 8, 0),
('세종동', 2, 8, 0);

INSERT INTO region (name, level, parent_id, is_popular)
VALUES
-- 인기 지역
('수원시', 2, 9, 1),
('성남시', 2, 9, 1),
('고양시', 2, 9, 1),
('용인시', 2, 9, 1),
('부천시', 2, 9, 1),
('남양주시', 2, 9, 1),
('안산시', 2, 9, 1),
('화성시', 2, 9, 1),
('평택시', 2, 9, 1),
('의정부시', 2, 9, 1),
('시흥시', 2, 9, 1),
('김포시', 2, 9, 1),
('광명시', 2, 9, 1),
('하남시', 2, 9, 1),

-- 비인기 지역
('군포시', 2, 9, 0),
('오산시', 2, 9, 0),
('이천시', 2, 9, 0),
('안성시', 2, 9, 0),
('양주시', 2, 9, 0),
('구리시', 2, 9, 0),
('여주시', 2, 9, 0),
('동두천시', 2, 9, 0),
('과천시', 2, 9, 0),
('연천군', 2, 9, 0),
('가평군', 2, 9, 0),
('양평군', 2, 9, 0);

INSERT INTO region (name, level, parent_id, is_popular)
VALUES
-- 인기 지역
('강릉시', 2, 10, 1),
('속초시', 2, 10, 1),
('춘천시', 2, 10, 1),
('원주시', 2, 10, 1),
('동해시', 2, 10, 1),
('태백시', 2, 10, 1),

-- 비인기 지역
('홍천군', 2, 10, 0),
('횡성군', 2, 10, 0),
('평창군', 2, 10, 0),
('정선군', 2, 10, 0),
('영월군', 2, 10, 0),
('삼척시', 2, 10, 0),
('인제군', 2, 10, 0),
('고성군', 2, 10, 0),
('양양군', 2, 10, 0),
('철원군', 2, 10, 0),
('화천군', 2, 10, 0),
('양구군', 2, 10, 0);

INSERT INTO region (name, level, parent_id, is_popular)
VALUES
-- 인기 지역
('청주시', 2, 11, 1),
('충주시', 2, 11, 1),
('제천시', 2, 11, 1),

-- 비인기 지역
('보은군', 2, 11, 0),
('옥천군', 2, 11, 0),
('영동군', 2, 11, 0),
('증평군', 2, 11, 0),
('진천군', 2, 11, 0),
('괴산군', 2, 11, 0),
('단양군', 2, 11, 0),
('음성군', 2, 11, 0);

INSERT INTO region (name, level, parent_id, is_popular)
VALUES
-- 인기 지역
('천안시', 2, 12, 1),
('아산시', 2, 12, 1),
('서산시', 2, 12, 1),
('당진시', 2, 12, 1),
('공주시', 2, 12, 1),
('보령시', 2, 12, 1),

-- 비인기 지역
('논산시', 2, 12, 0),
('계룡시', 2, 12, 0),
('금산군', 2, 12, 0),
('부여군', 2, 12, 0),
('서천군', 2, 12, 0),
('청양군', 2, 12, 0),
('홍성군', 2, 12, 0),
('예산군', 2, 12, 0),
('태안군', 2, 12, 0);

INSERT INTO region (name, level, parent_id, is_popular)
VALUES
-- 인기 지역
('전주시', 2, 13, 1),
('군산시', 2, 13, 1),
('익산시', 2, 13, 1),
('남원시', 2, 13, 1),
('정읍시', 2, 13, 1),

-- 비인기 지역
('김제시', 2, 13, 0),
('완주군', 2, 13, 0),
('고창군', 2, 13, 0),
('부안군', 2, 13, 0),
('임실군', 2, 13, 0),
('순창군', 2, 13, 0),
('진안군', 2, 13, 0),
('무주군', 2, 13, 0),
('장수군', 2, 13, 0);

INSERT INTO region (name, level, parent_id, is_popular)
VALUES
-- 인기 지역
('여수시', 2, 14, 1),
('순천시', 2, 14, 1),
('목포시', 2, 14, 1),
('광양시', 2, 14, 1),
('나주시', 2, 14, 1),

-- 비인기 지역
('담양군', 2, 14, 0),
('곡성군', 2, 14, 0),
('구례군', 2, 14, 0),
('고흥군', 2, 14, 0),
('보성군', 2, 14, 0),
('화순군', 2, 14, 0),
('장흥군', 2, 14, 0),
('강진군', 2, 14, 0),
('해남군', 2, 14, 0),
('영암군', 2, 14, 0),
('무안군', 2, 14, 0),
('함평군', 2, 14, 0),
('영광군', 2, 14, 0),
('장성군', 2, 14, 0),
('완도군', 2, 14, 0),
('진도군', 2, 14, 0),
('신안군', 2, 14, 0);

INSERT INTO region (name, level, parent_id, is_popular)
VALUES
-- 인기 지역
('경주시', 2, 15, 1),
('포항시', 2, 15, 1),
('안동시', 2, 15, 1),
('구미시', 2, 15, 1),
('영주시', 2, 15, 1),

-- 비인기 지역
('김천시', 2, 15, 0),
('영천시', 2, 15, 0),
('상주시', 2, 15, 0),
('문경시', 2, 15, 0),
('칠곡군', 2, 15, 0),
('의성군', 2, 15, 0),
('청송군', 2, 15, 0),
('영양군', 2, 15, 0),
('영덕군', 2, 15, 0),
('예천군', 2, 15, 0),
('봉화군', 2, 15, 0),
('울진군', 2, 15, 0),
('울릉군', 2, 15, 0);

INSERT INTO region (name, level, parent_id, is_popular)
VALUES
-- 인기 지역
('창원시', 2, 16, 1),
('김해시', 2, 16, 1),
('거제시', 2, 16, 1),
('통영시', 2, 16, 1),
('진주시', 2, 16, 1),
('양산시', 2, 16, 1),

-- 비인기 지역
('밀양시', 2, 16, 0),
('사천시', 2, 16, 0),
('의령군', 2, 16, 0),
('함안군', 2, 16, 0),
('창녕군', 2, 16, 0),
('고성군', 2, 16, 0),
('남해군', 2, 16, 0),
('하동군', 2, 16, 0),
('산청군', 2, 16, 0),
('함양군', 2, 16, 0),
('거창군', 2, 16, 0),
('합천군', 2, 16, 0);

INSERT INTO region (name, level, parent_id, is_popular)
VALUES
-- 인기 지역
('제주시', 2, 17, 1),
('서귀포시', 2, 17, 1);

-- 1. 전체 초기화
UPDATE region
SET is_popular = 0;

-- 부산
UPDATE region
SET is_popular = 1,
    image_url = 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/region%2Fbusan.jpg?alt=media&token=cc119862-5efd-4448-a65d-cb3dc53af302'
WHERE name = '부산광역시';

-- 대구
UPDATE region
SET is_popular = 1,
    image_url = 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/region%2Fdaegu.jpeg?alt=media&token=8bec9403-5bc5-4f49-b95b-72575cddeceb'
WHERE name = '대구광역시';

-- 대전
UPDATE region
SET is_popular = 1,
    image_url = 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/region%2Fdaejeon.jpg?alt=media&token=be5b25ba-2a40-4420-8b84-2b729fb5cd26'
WHERE name = '대전광역시';

-- 강릉
UPDATE region
SET is_popular = 1,
    image_url = 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/region%2Fgangneung.jpg?alt=media&token=11e41dfe-3b6a-4c23-9ba2-fb22c9468681'
WHERE name = '강원특별자치도';

-- 거제
UPDATE region
SET is_popular = 1,
    image_url = 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/region%2Fgeoje.jpg?alt=media&token=4bbbc1f8-f5e7-4957-bd15-d20f08e52476'
WHERE name = '거제시';

-- 기장
UPDATE region
SET is_popular = 1,
    image_url = 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/region%2Fgijang.jpeg?alt=media&token=27300e1c-2e26-4567-a40b-0e602fe92aad'
WHERE name = '기장군';

-- 경주
UPDATE region
SET is_popular = 1,
    image_url = 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/region%2Fgyeongju.jpg?alt=media&token=ff35686f-123d-470b-857f-9897b49b3bc5'
WHERE name = '경상북도';

-- 해운대
UPDATE region
SET is_popular = 1,
    image_url = 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/region%2Fhaeundae.jpeg?alt=media&token=01bbdd9a-76d7-4b5f-b275-42c572a32246'
WHERE name = '해운대구';

-- 인천
UPDATE region
SET is_popular = 1,
    image_url = 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/region%2Fincheon.jpg?alt=media&token=4b44c0d4-64b0-4da0-9448-09149bf5fbbe'
WHERE name = '인천광역시';

-- 제주도
UPDATE region
SET is_popular = 1,
    image_url = 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/region%2Fjeju.jpeg?alt=media&token=a25454ec-70a6-40f3-88f6-3a1542311b31'
WHERE name = '제주특별자치도';

-- 포항
UPDATE region
SET is_popular = 1,
    image_url = 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/region%2Fpohang.jpg?alt=media&token=84e1c378-cc77-4e5d-bb39-c7e45132eef9'
WHERE name = '포항시';

-- 서울
UPDATE region
SET is_popular = 1,
    image_url = 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/region%2Fseoul.jpg?alt=media&token=78780a66-72b4-464d-a95d-ff6f9afbfeb5'
WHERE name = '서울특별시';

-- 속초
UPDATE region
SET is_popular = 1,
    image_url = 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/region%2Fsokcho.jpg?alt=media&token=d0f49cae-d0ed-40b9-a6c6-60d3a77f6119'
WHERE name = '속초시';

-- 여수
UPDATE region
SET is_popular = 1,
    image_url = 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/region%2Fyeosu.jpg?alt=media&token=e1075744-5657-440d-b8cf-94f97b79ac1d'
WHERE name = '여수시';




UPDATE accommodation_tag SET icon_name = 'FaHeart' WHERE tag_id = 1;
UPDATE accommodation_tag SET icon_name = 'FaUserFriends' WHERE tag_id = 2;
UPDATE accommodation_tag SET icon_name = 'FaChild' WHERE tag_id = 3;
UPDATE accommodation_tag SET icon_name = 'FaUsers' WHERE tag_id = 4;
UPDATE accommodation_tag SET icon_name = 'FaUser' WHERE tag_id = 5;
UPDATE accommodation_tag SET icon_name = 'FaLock' WHERE tag_id = 6;
UPDATE accommodation_tag SET icon_name = 'FaSpa' WHERE tag_id = 7;
UPDATE accommodation_tag SET icon_name = 'FaVolumeMute' WHERE tag_id = 8;
UPDATE accommodation_tag SET icon_name = 'FaGlassCheers' WHERE tag_id = 9;
UPDATE accommodation_tag SET icon_name = 'FaBinoculars' WHERE tag_id = 10;
UPDATE accommodation_tag SET icon_name = 'FaCameraRetro' WHERE tag_id = 11;
UPDATE accommodation_tag SET icon_name = 'FaSun' WHERE tag_id = 12;
UPDATE accommodation_tag SET icon_name = 'FaRegSun' WHERE tag_id = 13;
UPDATE accommodation_tag SET icon_name = 'FaCity' WHERE tag_id = 14;
UPDATE accommodation_tag SET icon_name = 'FaTree' WHERE tag_id = 15;
UPDATE accommodation_tag SET icon_name = 'FaWater' WHERE tag_id = 16;
UPDATE accommodation_tag SET icon_name = 'FaBuilding' WHERE tag_id = 17;
UPDATE accommodation_tag SET icon_name = 'FaHome' WHERE tag_id = 18;
UPDATE accommodation_tag SET icon_name = 'FaLayerGroup' WHERE tag_id = 19;
UPDATE accommodation_tag SET icon_name = 'FaLightbulb' WHERE tag_id = 20;
UPDATE accommodation_tag SET icon_name = 'FaWarehouse' WHERE tag_id = 21;
UPDATE accommodation_tag SET icon_name = 'FaCouch' WHERE tag_id = 22;
UPDATE accommodation_tag SET icon_name = 'FaPagelines' WHERE tag_id = 23;
UPDATE accommodation_tag SET icon_name = 'FaThLarge' WHERE tag_id = 24;
UPDATE accommodation_tag SET icon_name = 'FaSnowflake' WHERE tag_id = 25;
UPDATE accommodation_tag SET icon_name = 'FaLeaf' WHERE tag_id = 26;


-- 객실내시설
UPDATE accommodation_tag SET icon_name = 'FaHotTub' WHERE tag_id = 27;
UPDATE accommodation_tag SET icon_name = 'FaSpa' WHERE tag_id = 28;
UPDATE accommodation_tag SET icon_name = 'FaGlassMartiniAlt' WHERE tag_id = 29;
UPDATE accommodation_tag SET icon_name = 'FaWifi' WHERE tag_id = 30;
UPDATE accommodation_tag SET icon_name = 'FaSnowflake' WHERE tag_id = 31;
UPDATE accommodation_tag SET icon_name = 'FaBath' WHERE tag_id = 32;
UPDATE accommodation_tag SET icon_name = 'FaShower' WHERE tag_id = 33;
UPDATE accommodation_tag SET icon_name = 'FaPlug' WHERE tag_id = 34;
UPDATE accommodation_tag SET icon_name = 'FaTv' WHERE tag_id = 35;
UPDATE accommodation_tag SET icon_name = 'FaFilm' WHERE tag_id = 36;
UPDATE accommodation_tag SET icon_name = 'FaPlayCircle' WHERE tag_id = 37;
UPDATE accommodation_tag SET icon_name = 'FaVolumeUp' WHERE tag_id = 38;
UPDATE accommodation_tag SET icon_name = 'FaCoffee' WHERE tag_id = 39;
UPDATE accommodation_tag SET icon_name = 'FaSnowflake' WHERE tag_id = 40;
UPDATE accommodation_tag SET icon_name = 'FaMicrowave' WHERE tag_id = 41;
UPDATE accommodation_tag SET icon_name = 'FaUtensils' WHERE tag_id = 42;
UPDATE accommodation_tag SET icon_name = 'FaBed' WHERE tag_id = 43;
UPDATE accommodation_tag SET icon_name = 'FaBed' WHERE tag_id = 44;
UPDATE accommodation_tag SET icon_name = 'FaCouch' WHERE tag_id = 45;
UPDATE accommodation_tag SET icon_name = 'FaBook' WHERE tag_id = 46;
UPDATE accommodation_tag SET icon_name = 'FaVolumeMute' WHERE tag_id = 47;
UPDATE accommodation_tag SET icon_name = 'FaSmoking' WHERE tag_id = 48;
UPDATE accommodation_tag SET icon_name = 'FaBan' WHERE tag_id = 49;

-- 시설
UPDATE accommodation_tag SET icon_name = 'FaSwimmingPool' WHERE tag_id = 50;
UPDATE accommodation_tag SET icon_name = 'FaWater' WHERE tag_id = 51;
UPDATE accommodation_tag SET icon_name = 'FaFire' WHERE tag_id = 52;
UPDATE accommodation_tag SET icon_name = 'FaHotTub' WHERE tag_id = 53;
UPDATE accommodation_tag SET icon_name = 'FaSpa' WHERE tag_id = 54;
UPDATE accommodation_tag SET icon_name = 'FaDumbbell' WHERE tag_id = 55;
UPDATE accommodation_tag SET icon_name = 'FaUtensils' WHERE tag_id = 56;
UPDATE accommodation_tag SET icon_name = 'FaCoffee' WHERE tag_id = 57;
UPDATE accommodation_tag SET icon_name = 'FaStore' WHERE tag_id = 58;
UPDATE accommodation_tag SET icon_name = 'FaKitchenSet' WHERE tag_id = 59;
UPDATE accommodation_tag SET icon_name = 'FaShower' WHERE tag_id = 60;
UPDATE accommodation_tag SET icon_name = 'FaToilet' WHERE tag_id = 61;
UPDATE accommodation_tag SET icon_name = 'FaTshirt' WHERE tag_id = 62;
UPDATE accommodation_tag SET icon_name = 'FaTshirt' WHERE tag_id = 63;
UPDATE accommodation_tag SET icon_name = 'FaSync' WHERE tag_id = 64;
UPDATE accommodation_tag SET icon_name = 'FaChair' WHERE tag_id = 65;
UPDATE accommodation_tag SET icon_name = 'FaGlassCheers' WHERE tag_id = 66;
UPDATE accommodation_tag SET icon_name = 'FaCampground' WHERE tag_id = 67;
UPDATE accommodation_tag SET icon_name = 'FaLock' WHERE tag_id = 68;
UPDATE accommodation_tag SET icon_name = 'FaFireExtinguisher' WHERE tag_id = 69;
UPDATE accommodation_tag SET icon_name = 'FaSortAmountUp' WHERE tag_id = 70;
UPDATE accommodation_tag SET icon_name = 'FaCar' WHERE tag_id = 71;
UPDATE accommodation_tag SET icon_name = 'FaChargingStation' WHERE tag_id = 72;

-- 반려동물
UPDATE accommodation_tag SET icon_name = 'FaDog' WHERE tag_id = 73;
UPDATE accommodation_tag SET icon_name = 'FaCat' WHERE tag_id = 74;
UPDATE accommodation_tag SET icon_name = 'FaHome' WHERE tag_id = 75;
UPDATE accommodation_tag SET icon_name = 'FaBone' WHERE tag_id = 76;
UPDATE accommodation_tag SET icon_name = 'FaPaw' WHERE tag_id = 77;
UPDATE accommodation_tag SET icon_name = 'FaUtensils' WHERE tag_id = 78;
UPDATE accommodation_tag SET icon_name = 'FaSwimmingPool' WHERE tag_id = 79;
UPDATE accommodation_tag SET icon_name = 'FaHandsHelping' WHERE tag_id = 80;
UPDATE accommodation_tag SET icon_name = 'FaCarSide' WHERE tag_id = 81;

-- 운영정보
UPDATE accommodation_tag SET icon_name = 'FaBreadSlice' WHERE tag_id = 82;
UPDATE accommodation_tag SET icon_name = 'FaParking' WHERE tag_id = 83;
UPDATE accommodation_tag SET icon_name = 'FaCarSide' WHERE tag_id = 84;
UPDATE accommodation_tag SET icon_name = 'FaSuitcase' WHERE tag_id = 85;
UPDATE accommodation_tag SET icon_name = 'FaUserSecret' WHERE tag_id = 86;
UPDATE accommodation_tag SET icon_name = 'FaCalendarDay' WHERE tag_id = 87;
UPDATE accommodation_tag SET icon_name = 'FaClock' WHERE tag_id = 88;
UPDATE accommodation_tag SET icon_name = 'FaTags' WHERE tag_id = 89;
UPDATE accommodation_tag SET icon_name = 'FaTicketAlt' WHERE tag_id = 90;
UPDATE accommodation_tag SET icon_name = 'FaPercent' WHERE tag_id = 91;
UPDATE accommodation_tag SET icon_name = 'FaInfinity' WHERE tag_id = 92;
UPDATE accommodation_tag SET icon_name = 'FaArrowCircleUp' WHERE tag_id = 93;
UPDATE accommodation_tag SET icon_name = 'FaArrowCircleDown' WHERE tag_id = 94;
UPDATE accommodation_tag SET icon_name = 'FaBolt' WHERE tag_id = 95;
UPDATE accommodation_tag SET icon_name = 'FaClock' WHERE tag_id = 96;
UPDATE accommodation_tag SET icon_name = 'FaUserShield' WHERE tag_id = 97;
UPDATE accommodation_tag SET icon_name = 'FaThumbsUp' WHERE tag_id = 98;
UPDATE accommodation_tag SET icon_name = 'FaBroom' WHERE tag_id = 99;
UPDATE accommodation_tag SET icon_name = 'FaShieldVirus' WHERE tag_id = 100;

-- 등급
UPDATE accommodation_tag SET icon_name = 'FaCrown' WHERE tag_id = 101;
UPDATE accommodation_tag SET icon_name = 'FaMedal' WHERE tag_id = 102;
UPDATE accommodation_tag SET icon_name = 'FaHome' WHERE tag_id = 103;
UPDATE accommodation_tag SET icon_name = 'FaCampground' WHERE tag_id = 104;
UPDATE accommodation_tag SET icon_name = 'FaCampground' WHERE tag_id = 105;
UPDATE accommodation_tag SET icon_name = 'FaUserFriends' WHERE tag_id = 106;
UPDATE accommodation_tag SET icon_name = 'FaHotel' WHERE tag_id = 107;
UPDATE accommodation_tag SET icon_name = 'FaBed' WHERE tag_id = 108;
UPDATE accommodation_tag SET icon_name = 'FaUmbrellaBeach' WHERE tag_id = 109;
UPDATE accommodation_tag SET icon_name = 'FaPagelines' WHERE tag_id = 110;
UPDATE accommodation_tag SET icon_name = 'FaHome' WHERE tag_id = 111;

-- 위치
UPDATE accommodation_tag SET icon_name = 'FaUmbrellaBeach' WHERE tag_id = 112;
UPDATE accommodation_tag SET icon_name = 'FaMountain' WHERE tag_id = 113;
UPDATE accommodation_tag SET icon_name = 'FaCity' WHERE tag_id = 114;
UPDATE accommodation_tag SET icon_name = 'FaTrain' WHERE tag_id = 115;
UPDATE accommodation_tag SET icon_name = 'FaBus' WHERE tag_id = 116;
UPDATE accommodation_tag SET icon_name = 'FaPlane' WHERE tag_id = 117;
UPDATE accommodation_tag SET icon_name = 'FaSkiing' WHERE tag_id = 118;
UPDATE accommodation_tag SET icon_name = 'FaLandmark' WHERE tag_id = 119;
UPDATE accommodation_tag SET icon_name = 'FaUtensils' WHERE tag_id = 120;
UPDATE accommodation_tag SET icon_name = 'FaCoffee' WHERE tag_id = 121;
UPDATE accommodation_tag SET icon_name = 'FaShoppingBag' WHERE tag_id = 122;
UPDATE accommodation_tag SET icon_name = 'FaHome' WHERE tag_id = 123;
UPDATE accommodation_tag SET icon_name = 'FaMapMarkerAlt' WHERE tag_id = 124;
UPDATE accommodation_tag SET icon_name = 'FaWalking' WHERE tag_id = 125;
UPDATE accommodation_tag SET icon_name = 'FaBicycle' WHERE tag_id = 126;
UPDATE accommodation_tag SET icon_name = 'FaFish' WHERE tag_id = 127;
UPDATE accommodation_tag SET icon_name = 'FaHiking' WHERE tag_id = 128;
UPDATE accommodation_tag SET icon_name = 'FaMountain' WHERE tag_id = 129;