-- ============================================================
-- 실행 순서: insert_pk_21-30.sql 완료 후 이 파일 실행
-- 포함 데이터: accommodation 31~33 (명시적 PK),
--             accommodation_image, accommodation_tag_map,
--             room 91~101 (자동 증가), room_image 91~101,
--             cancellation_policy
-- ============================================================

-- accommodations
-- 31
INSERT INTO accommodation (
    accommodation_id,
    name,
    address,
    amenities,
    check_in_time,
    check_out_time,
    description,
    latitude,
    longitude,
    min_price,
    rating,
    status,
    thumbnail,
    category_id,
    company_id,
    main_region_id,
    sub_region_id
) VALUES (
             31,
             '전주한옥마을 연우당',
             '전북특별자치도 전주시 완산구 풍남동3가 54-1',
             '["위의 정보는 게스트하우스 사정에 따라 변경될 수 있습니다","해당 이미지는 실제와 상이 할 수 있습니다","예약 확정 이후의 취소는 취소환불규정에 의거하여 적용됩니다"]',
             '2025-08-20 15:00:00.000000',
             '2025-08-21 10:00:00.000000',
             '연우당은 전주한옥마을안에 위치한 전통 한옥 숙소 입니다. 전주한옥마을내 경기전, 전동성당, 오목대, 풍남문, 자만벽화마을, 유명먹거리 등 중심지에 위치합니다. 오목대 도보 4분, 완산칠봉꽃동산 차량 5분, 전주고속버스터미널 차량 11분.',
             35.8172,
             127.1478,
             59000.00,
             0.0,
             'ACTIVE',
             'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Fthumbnail%2Fthumnail.jpg?alt=media&token=cf8db352-ee70-463e-a43d-4b69a038fdd1',
             6,    -- 카테고리 id (한옥)
             4,   -- company_id 예시
             13,   -- main_region_id (전북특별자치도)
             176   -- sub_region_id (전주시)
         );

-- 32번 숙소: 경주 한옥 신달하우스
INSERT INTO accommodation (
    accommodation_id,
    name,
    address,
    amenities,
    check_in_time,
    check_out_time,
    description,
    latitude,
    longitude,
    min_price,
    rating,
    status,
    thumbnail,
    category_id,
    company_id,
    main_region_id,
    sub_region_id
) VALUES (
             32,
             '경주 한옥 신달하우스',
             '경상북도 경주시 사정동 482-11',
             '["무선인터넷","조식제공","라운지","개인콘센트","주방/식당","욕실용품","에어컨","냉장고","짐보관가능","샤워실","드라이기","카페"]',
             '2025-08-20 16:00:00.000000',
             '2025-08-21 11:00:00.000000',
             '경주 고양이 한옥 신달 게스트하우스는 교통, 관광 중심지에 위치하여 가성비와 감성힐링 여행자의 공간입니다. 영아(5세 미만), 과음·고성 술파티, 어르신 분들은 외곽 호텔 또는 팬션 이용 권장. 건축가 한옥과 고양이를 좋아하시는 분 환영. 신달집사 추천!',
             35.8503,
             129.2248,
             99000.00,
             0.0,
             'ACTIVE',
             'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F32%2Fthumbnail%2Fthumbnail.jpeg?alt=media&token=f93807f4-0813-4cc5-99ac-50979495748d',
             4,    -- 카테고리 id (한옥)
             6,   -- company_id 예시 (신달하우스 회사)
             15,   -- main_region_id (경상북도)
             212   -- sub_region_id (경주시)
         );

-- 33번 숙소: 양양 게스트하우스 바다정원
INSERT INTO accommodation (
    accommodation_id,
    name,
    address,
    amenities,
    check_in_time,
    check_out_time,
    description,
    latitude,
    longitude,
    min_price,
    rating,
    status,
    thumbnail,
    category_id,
    company_id,
    main_region_id,
    sub_region_id
) VALUES (
             33,
             '양양 게스트하우스 바다정원',
             '강원도 양양군 손양면 동호리 101-5',
             '["무선인터넷","조식제공","개인사물함","개인콘센트","주방/식당","욕실용품","에어컨","탈수기","냉장고","샤워실","주차장","무료주차","드라이기","바베큐","카페","TV","전자레인지","취사가능"]',
             '2025-09-08 15:00:00.000000',
             '2025-09-09 11:00:00.000000',
             '강원도에서 해안선이 가장 예쁜 동호해변에 위치한 여성전용 게스트하우스입니다. 여행객이 안전하고 편안하게 머물 수 있도록 주변 보안업체 등록 및 친절한 스텝 상주. 객실청소 및 고객응대는 전화상담보다 문자/네이버톡톡 이용 권장.',
             38.0713,
             128.6405,
             45000.00,
             0.0,
             'ACTIVE',
             'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F33%2Fthumbnail%2F1a6c6ac64a2d3fabd4db2f82f7602dfc.jpg?alt=media&token=4e45d475-00d5-4ec3-912d-5c6c87570846',
             4,    -- 카테고리 id (게스트하우스)
             6,   -- company_id (예시)
             10,   -- main_region_id (강원도)
             146   -- sub_region_id (양양군)
         );

-- accommodation_image

-- 31
INSERT INTO accommodation_image (created_at, updated_at, image_type, image_url, sort_order, accommodation_id) VALUES
    ('2025-09-04 23:00:00.000000', '2025-09-04 23:00:00.000000', 'MAIN', 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Fimages%2Fimg1.jpg?alt=media&token=06634b3f-a818-4e57-9eec-ae245b82fb61', 1, 31),
    ('2025-09-04 23:00:00.000000', '2025-09-04 23:00:00.000000', 'SUB', 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Fimages%2Fimg2.jpg?alt=media&token=20567142-9569-4459-8353-2ad840f9af38', 2, 31),
    ('2025-09-04 23:00:00.000000', '2025-09-04 23:00:00.000000', 'SUB', 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Fimages%2Fimg3.jpg?alt=media&token=83c64758-0355-4828-8cc0-0f47413329b2', 3, 31),
    ('2025-09-04 23:00:00.000000', '2025-09-04 23:00:00.000000', 'SUB', 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Fimages%2Fimg4.jpg?alt=media&token=feb039f2-56d2-4ef8-a9f0-5e5d9f417288', 4, 31),
    ('2025-09-04 23:00:00.000000', '2025-09-04 23:00:00.000000', 'SUB', 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Fimages%2Fimg5.jpg?alt=media&token=542c30e3-f41c-42cb-bd67-318d65d50d6e', 5, 31),
    ('2025-09-04 23:00:00.000000', '2025-09-04 23:00:00.000000', 'SUB', 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Fimages%2Fimg6.jpg?alt=media&token=2e4b380c-5b40-4e20-8982-3309146ea320', 6, 31),
    ('2025-09-04 23:00:00.000000', '2025-09-04 23:00:00.000000', 'SUB', 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Fimages%2Fimg7.jpeg?alt=media&token=0c34450c-e03f-4f49-b87e-c36f58afd9ac', 7, 31);

-- 32번 숙소 이미지
INSERT INTO accommodation_image (created_at, updated_at, image_url, sort_order, accommodation_id) VALUES
                                                                                                      ('2025-09-08 15:30:00.000000', '2025-09-08 15:30:00.000000', 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F32%2Fimages%2F%E1%84%89%E1%85%B5%E1%86%AB%E1%84%83%E1%85%A1%E1%86%AF1.jpeg?alt=media&token=e07eb58f-1544-4a51-8444-d6d975c31e18', 1, 32),
                                                                                                      ('2025-09-08 15:30:00.000000', '2025-09-08 15:30:00.000000', 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F32%2Fimages%2F%E1%84%89%E1%85%B5%E1%86%AB%E1%84%83%E1%85%A1%E1%86%AF1.jpeg?alt=media&token=e07eb58f-1544-4a51-8444-d6d975c31e18', 2, 32),
                                                                                                      ('2025-09-08 15:30:00.000000', '2025-09-08 15:30:00.000000', 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F32%2Fimages%2F%E1%84%89%E1%85%B5%E1%86%AB%E1%84%83%E1%85%A1%E1%86%AF3.jpeg?alt=media&token=f0b2206a-b423-4c36-8e68-25b0ada5b71e', 3, 32),
                                                                                                      ('2025-09-08 15:30:00.000000', '2025-09-08 15:30:00.000000', 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F32%2Fimages%2F%E1%84%89%E1%85%B5%E1%86%AB%E1%84%83%E1%85%A1%E1%86%AF4.jpeg?alt=media&token=52448885-0676-4c72-b234-d9a551e92e8c', 4, 32),
                                                                                                      ('2025-09-08 15:30:00.000000', '2025-09-08 15:30:00.000000', 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F32%2Fimages%2F%E1%84%89%E1%85%B5%E1%86%AB%E1%84%83%E1%85%A1%E1%86%AF5.jpeg?alt=media&token=bc77c77d-ffa7-4210-af0f-bebaaf589ad1', 5, 32),
                                                                                                      ('2025-09-08 15:30:00.000000', '2025-09-08 15:30:00.000000', 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F32%2Fimages%2F%E1%84%89%E1%85%B5%E1%86%AB%E1%84%83%E1%85%A1%E1%86%AF6.jpeg?alt=media&token=a4db7098-5385-4c85-b543-28c870fd5574', 6, 32),
                                                                                                      ('2025-09-08 15:30:00.000000', '2025-09-08 15:30:00.000000', 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F32%2Fimages%2F%E1%84%89%E1%85%B5%E1%86%AB%E1%84%83%E1%85%A1%E1%86%AF7.jpg?alt=media&token=fd3130ba-e7f9-4fb6-9d18-a9489a3da72c', 7, 32),
                                                                                                      ('2025-09-08 15:30:00.000000', '2025-09-08 15:30:00.000000', 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F32%2Fimages%2F%E1%84%89%E1%85%B5%E1%86%AB%E1%84%83%E1%85%A1%E1%86%AF8.jpg?alt=media&token=62fd914a-d3d3-4cc0-a0d2-a62662ac7fef', 8, 32),
                                                                                                      ('2025-09-08 15:30:00.000000', '2025-09-08 15:30:00.000000', 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F32%2Fimages%2F%E1%84%89%E1%85%B5%E1%86%AB%E1%84%83%E1%85%A1%E1%86%AF9.jpg?alt=media&token=df36efb8-2687-4731-8906-fbecaa95a10d', 9, 32),
                                                                                                      ('2025-09-08 15:30:00.000000', '2025-09-08 15:30:00.000000', 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F32%2Fimages%2F%E1%84%89%E1%85%B5%E1%86%AB%E1%84%83%E1%85%A1%E1%86%AF10.jpeg?alt=media&token=a66c66f6-c42f-4554-8793-9fe957dfaed0', 10, 32),
                                                                                                      ('2025-09-08 15:30:00.000000', '2025-09-08 15:30:00.000000', 'https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F32%2Fimages%2F%E1%84%89%E1%85%B5%E1%86%AB%E1%84%83%E1%85%A1%E1%86%AF11.jpeg?alt=media&token=7ca85618-1ae0-44d6-a88f-fa7256a576f3', 11, 32);

-- 33번 숙소 이미지
INSERT INTO accommodation_image (created_at, updated_at, image_url, sort_order, accommodation_id) VALUES
                                                                                                      ('2025-09-08 17:30:00.000000','2025-09-08 17:30:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F33%2Fimages%2F1a6c6ac64a2d3fabd4db2f82f7602dfc.jpg?alt=media&token=7ad0b53a-c3d8-4859-8a69-f1c36633f1fe',1,33),
                                                                                                      ('2025-09-08 17:30:00.000000','2025-09-08 17:30:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F33%2Fimages%2F3152c94da6f71e04f4595f2dce4fa181.jpg?alt=media&token=1f890e79-6b21-4492-9297-cf2da914b177',2,33),
                                                                                                      ('2025-09-08 17:30:00.000000','2025-09-08 17:30:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F33%2Fimages%2F229999adb9f2226ba179998571e53b94.jpg?alt=media&token=351120e2-9909-40ec-8d5d-01d01fca79f5',3,33),
                                                                                                      ('2025-09-08 17:30:00.000000','2025-09-08 17:30:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F33%2Fimages%2Fac3c455dd734c7e97837454d05d7c253.jpg?alt=media&token=4227798f-bdf0-4456-bf69-5cab716282e3',4,33),
                                                                                                      ('2025-09-08 17:30:00.000000','2025-09-08 17:30:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F33%2Fimages%2F01f84bc4d492022767c6fbf74c560cdd.jpg?alt=media&token=0e459e62-94a2-4a04-9363-f4da52ac87f6',5,33),
                                                                                                      ('2025-09-08 17:30:00.000000','2025-09-08 17:30:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F33%2Fimages%2F51dd484767044e53d3bae8ea38fba90a.jpg?alt=media&token=d5f08ad9-011a-4c80-b4de-04f639bbd076',6,33),
                                                                                                      ('2025-09-08 17:30:00.000000','2025-09-08 17:30:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F33%2Fimages%2F53b5b5a157b8c667d05659e88829c6ef.jpg?alt=media&token=ad45afe2-ac89-40fe-b0e7-0f4723c8dc0b',7,33),
                                                                                                      ('2025-09-08 17:30:00.000000','2025-09-08 17:30:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F33%2Fimages%2Fa0f5db75c6e2359e37103ba64dc8ea47.jpg?alt=media&token=0dcb08af-5db0-43c3-b5b3-9d71bb8e0269',8,33),
                                                                                                      ('2025-09-08 17:30:00.000000','2025-09-08 17:30:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F33%2Fimages%2F7d3b238c085f8069e434acd2e681f5cd.jpg?alt=media&token=05bbde80-5e6c-4095-b6c1-9bfbec475037',9,33);

-- accommodation_tag_map


-- 31
INSERT INTO accommodation_tag_map (accommodation_id, tag_id) VALUES
                                                                         (31, 1),   -- 감성숙소
                                                                         (31, 4),   -- 친구끼리 (선택)
                                                                         (31, 23),  -- 한옥스타일
                                                                         (31, 8);   -- 조용한

-- 32번 숙소 태그 매핑
INSERT INTO accommodation_tag_map (accommodation_id, tag_id) VALUES
                                                                 (32, 1),   -- 감성숙소
                                                                 (32, 4),   -- 친구끼리
                                                                 (32, 23),  -- 한옥스타일
                                                                 (32, 8),   -- 조용한
                                                                 (32, 12);  -- 가족용
-- 33번 숙소 핵심 태그 매핑
INSERT INTO accommodation_tag_map (accommodation_id, tag_id) VALUES
                                                                 (33, 1),   -- 감성숙소
                                                                 (33, 3),   -- 가족여행숙소
                                                                 (33, 4),   -- 친구끼리
                                                                 (33, 30),  -- 무선인터넷
                                                                 (33, 31),  -- 에어컨
                                                                 (33, 32),  -- 욕실용품
                                                                 (33, 33),  -- 샤워실
                                                                 (33, 35),  -- TV
                                                                 (33, 40),  -- 냉장고
                                                                 (33, 41),  -- 전자레인지
                                                                 (33, 42),  -- 취사도구
                                                                 (33, 52),  -- 바베큐장
                                                                 (33, 57),  -- 카페
                                                                 (33, 62),  -- 공용세탁기
                                                                 (33, 64),  -- 탈수기
                                                                 (33, 71),  -- 주차장
                                                                 (33, 82);  -- 조식제공


-- room
-- 31
INSERT INTO room (
    created_at,
    updated_at,
    amenities,
    bed_info,
    description,
    extra_person_fee,
    max_occupancy,
    name,
    price,
    standard_occupancy,
    status,
    total_rooms,
    type,
    accommodation_id
) VALUES
      (
          '2025-09-04 23:13:00.000000',
          '2025-09-04 23:13:00.000000',
          JSON_ARRAY('에어컨', '냉장고', '욕실', '헤어드라이기', 'TV', '공용 전자렌지', '공용 전기보트', '무료 Wi-Fi', '일회용 슬리퍼', '화장대', '객실 금연'),
          JSON_ARRAY('온돌룸 1개', '객실+전용 화장실', '객실 냉장고 1개'),
          '2인 기준 최대 2인까지 투숙 가능. 2인용 침구세트 제공. 객실 내 전용 화장실 이용. 사랑방은 아담하고 예쁜 마당이 한눈에 보이는 룸이며, 다른 객실과 떨어져 있어 조용함.',
          0,
          2,
          '사랑방',
          59000.00,
          2,
          'ACTIVE',
          3,
          '개인실',
          31
      ),
      (
          '2025-09-04 23:20:00.000000',
          '2025-09-04 23:20:00.000000',
          JSON_ARRAY('에어컨', '냉장고', '욕실', '헤어드라이기', 'TV', '공용 전자렌지', '공용 전기보트', '무료 Wi-Fi', '일회용 슬리퍼', '화장대', '객실 금연'),
          JSON_ARRAY('온돌룸 1개', '객실+전용 화장실', '객실 냉장고 1개'),
          '2인 기준 최대 2인까지 투숙 가능. 2인용 침구세트 제공. 객실 내 전용 화장실 이용. 지혜방은 다른 객실과 떨어져 있어 조용하며 거실을 지나 안쪽에 위치한 룸.',
          0,
          2,
          '지혜방',
          59000.00,
          2,
          'ACTIVE',
          3,
          '개인실',
          31
      ),
      (
          '2025-09-04 23:21:00.000000',
          '2025-09-04 23:21:00.000000',
          JSON_ARRAY('에어컨', '냉장고', '욕실', '헤어드라이기', 'TV', '공용 전자렌지', '공용 전기보트', '무료 Wi-Fi', '일회용 슬리퍼', '화장대', '객실 금연'),
          JSON_ARRAY('온돌룸 1개', '다락룸 1개', '객실 냉장고 1개', '객실+전용 화장실'),
          '2인 기준 최대 4인까지 투숙 가능. 기준인원 외 추가 인원 1인당 15,000원 추가. 앞·뒤로 문이 있으며 앞마당이 보이는 복층형 룸.',
          15000.00,
          4,
          '순수방',
          79000.00,
          2,
          'ACTIVE',
          2,
          '개인실',
          31
      ),
      (
          '2025-09-04 23:22:00.000000',
          '2025-09-04 23:22:00.000000',
          JSON_ARRAY('에어컨', '냉장고', '욕실', '헤어드라이기', 'TV', '공용 전자렌지', '공용 전기보트', '무료 Wi-Fi', '일회용 슬리퍼', '화장대', '객실 금연'),
          JSON_ARRAY('온돌룸 1개', '다락룸 1개', '객실 냉장고 1개', '객실+전용 화장실'),
          '2인 기준 최대 4인까지 투숙 가능. 기준인원 외 추가 인원 1인당 15,000원 추가. 앞·뒤로 문이 있으며 앞마당이 보이는 복층형 룸.',
          15000.00,
          4,
          '해돋이방',
          79000.00,
          2,
          'ACTIVE',
          2,
          '개인실',
          31
      ),
      (
          '2025-09-04 23:23:00.000000',
          '2025-09-04 23:23:00.000000',
          JSON_ARRAY('에어컨', '냉장고', '욕실', '헤어드라이기', 'TV', '공용 전자렌지', '공용 전기보트', '무료 Wi-Fi', '일회용 슬리퍼', '화장대', '객실 금연'),
          JSON_ARRAY('온돌룸 1개', '다락룸 1개', '객실+전용 화장실', '객실 냉장고 1개'),
          '2인 기준 최대 4인까지 투숙 가능. 기준인원 외 추가 인원 1인당 15,000원 추가. 연우당에서 가장 큰 룸이며 앞·뒤로 문이 있고 예쁜 마당이 보이는 복층형 룸.',
          15000.00,
          4,
          '오방방',
          89000.00,
          2,
          'ACTIVE',
          2,
          '개인실',
          31
      );

-- 32번 숙소
INSERT INTO room (
    created_at,
    updated_at,
    amenities,
    bed_info,
    description,
    extra_person_fee,
    max_occupancy,
    name,
    price,
    standard_occupancy,
    status,
    total_rooms,
    type,
    accommodation_id
) VALUES
      (
          '2025-09-08 16:00:00.000000',
          '2025-09-08 16:00:00.000000',
          JSON_ARRAY(
                  '드라이기',
                  '타올',
                  '욕실용품',
                  '공용 신달빠(야간 식음담소, 셀프 조식)',
                  '고양이카페 TV(고양이 3냥이)',
                  '옥상테라스(식음, 흡연 가능)'
          ),
          JSON_ARRAY(
                  '더블침대 1개',
                  '객실+전용 화장실',
                  '객실 냉난방 개별제어'
          ),
          '기준 2인 최대 3인 투숙 가능. 기준인원 외 추가 인원 1인당 15,000원 현장 결제. 추가 1인 최대 3인 이용 시 가벼운 짐 여행객 추천. 전 객실 화장실(욕실, 제습기, 개별 냉난방) 완비. 타올, 샴푸, 린스, 바디, 드라이 제공(칫솔세트 별도 지참). 편의시설이 중요한 어르신 및 대리예약 불가. 영아(5세미만, 기저귀), 음주·야간 소음 예약 불가.',
          15000.00,
          3,
          '더블룸',
          99000.00,
          2,
          'ACTIVE',
          4,
          '개인실',
          32
      ),
      (
          '2025-09-08 16:30:00.000000',
          '2025-09-08 16:30:00.000000',
          JSON_ARRAY(
                  '공용 신달빠(다용도실, 야간 식음, 배달/포장 음식, 셀프 조식)',
                  '고양이카페 TV(고양이)',
                  '옥상테라스(식음, 흡연 가능)'
          ),
          JSON_ARRAY(
                  '침대 4~6인용',
                  '객실+전용 화장실',
                  '객실 냉난방 개별제어'
          ),
          '기준인원 4인 외 추가 인원은 1인당 15,000원 현장 결제. 전 객실 화장실(욕실, 제습기, 개별 냉난방) 완비. 타올, 샴푸, 린스, 바디, 드라이 제공(칫솔세트 지참). 가족룸은 독립객실로 가족, 모임객에게 권장. 한옥 감성보다 TV 등 편의시설에 예민한 어르신, 대리 예약 불가. 영아(5세 미만, 기저귀), 술모임, 단체 모임 예약 불가. 고즈넉 고양이&한옥 게하와 맞지 않는 분 예약 자제.',
          15000.00,
          6,
          '가족룸',
          159000.00,
          4,
          'ACTIVE',
          3,
          '개인실',
          32
      );


-- 33번 숙소: 양양 게스트하우스 바다정원 룸
INSERT INTO room (
    created_at,
    updated_at,
    amenities,
    bed_info,
    description,
    extra_person_fee,
    max_occupancy,
    name,
    price,
    standard_occupancy,
    status,
    total_rooms,
    type,
    accommodation_id
) VALUES
-- 4인 여자 도미토리
('2025-09-08 17:00:00.000000','2025-09-08 17:00:00.000000',
 JSON_ARRAY('개인 캐비닛','콘센트','드라이기','빗','수건','옷걸이'),
 JSON_ARRAY('이층침대 2개'),
 '도미토리는 1인 1침대를 사용하며 객실을 타인과 함께 사용합니다. 기준 1인, 최대 4인. 추가인원 발생 시 15,000원 현장 결제.',
 15000.00,
 4,
 '4인 여자 도미토리',
 45000.00,
 1,
 'ACTIVE',
 4,
 '도미토리(여자)',
 33
),
-- 6인 여자 도미토리
('2025-09-08 17:05:00.000000','2025-09-08 17:05:00.000000',
 JSON_ARRAY('개인 캐비닛','콘센트','드라이기','빗','수건','옷걸이','주방조리도구','기본양념'),
 JSON_ARRAY('이층침대 3개'),
 '도미토리는 1인 1침대를 사용하며 객실을 타인과 함께 사용합니다. 기준 1인, 최대 6인. 추가인원 발생 시 현장 결제.',
 15000.00,
 6,
 '6인 여자 도미토리',
 45000.00,
 1,
 'ACTIVE',
 3,
 '도미토리(여자)',
 33
),
-- 온돌객실 개인실
('2025-09-08 17:10:00.000000','2025-09-08 17:10:00.000000',
 JSON_ARRAY('취사시설 구비','에어컨','드라이기','빗','화장대','TV','침구세트'),
 JSON_ARRAY('온돌방'),
 '온돌방으로 객실 내 화장실과 주방이 따로 준비되어 있습니다. 기준 2인, 최대 4인. 구이류 조리 금지, 모든 객실 금연. 추가인원 발생 시 현장 결제.',
 15000.00,
 4,
 '온돌객실 개인실',
 99000.00,
 2,
 'ACTIVE',
 6,
 '개인실',
 33
),
-- 침대 개인실
('2025-09-08 17:15:00.000000','2025-09-08 17:15:00.000000',
 JSON_ARRAY('취사시설 구비','에어컨','드라이기','빗','화장대','TV','침구세트'),
 JSON_ARRAY('침대 2개'),
 '침대방으로 객실 내 화장실과 주방이 따로 준비되어 있습니다. 기준 2인, 최대 4인. 구이류 조리 금지, 모든 객실 금연. 추가인원 발생 시 현장 결제.',
 15000.00,
 4,
 '침대 개인실',
 99000.00,
 2,
 'ACTIVE',
 6,
 '개인실',
 33
);



-- room_image

-- 31 숙소 (room_id: 91~95)
INSERT INTO room_image (created_at, updated_at, image_url, sort_order, room_id) VALUES
-- 사랑방 (room_id = 91)
('2025-09-08 14:30:00.000000','2025-09-08 14:30:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Froom%2Froom1%2F%E1%84%89%E1%85%A1%E1%84%85%E1%85%A1%E1%86%BC1.jpg?alt=media&token=2d96889a-a8bc-42ca-b1e8-16996767d33f',1,91),
('2025-09-08 14:30:00.000000','2025-09-08 14:30:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Froom%2Froom1%2F%E1%84%89%E1%85%A1%E1%84%85%E1%85%A1%E1%86%BC2.jpeg?alt=media&token=fc544408-b8b8-4d05-beef-fb85524b8dfa',2,91),
('2025-09-08 14:30:00.000000','2025-09-08 14:30:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Froom%2Froom1%2F%E1%84%89%E1%85%A1%E1%84%85%E1%85%A1%E1%86%BC3.jpg?alt=media&token=c67efa2b-8948-48d4-8eb4-822f9741d4f0',3,91),
('2025-09-08 14:30:00.000000','2025-09-08 14:30:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Froom%2Froom1%2F%E1%84%89%E1%85%A1%E1%84%85%E1%85%A1%E1%86%BC4.jpeg?alt=media&token=093f2984-5fdd-4d87-89bf-999cd1485a10',4,91),

-- 지혜방 (room_id = 92)
('2025-09-08 14:30:00.000000','2025-09-08 14:30:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Froom%2Froom2%2F%E1%84%8C%E1%85%B5%E1%84%92%E1%85%A81.jpeg?alt=media&token=1dc0dbb0-5aec-432d-a174-914214f8577c',1,92),
('2025-09-08 14:30:00.000000','2025-09-08 14:30:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Froom%2Froom2%2F%E1%84%8C%E1%85%B5%E1%84%92%E1%85%A82.jpeg?alt=media&token=439ab36d-bb49-4f08-b645-980223d21e24',2,92),
('2025-09-08 14:30:00.000000','2025-09-08 14:30:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Froom%2Froom2%2F%E1%84%8C%E1%85%B5%E1%84%92%E1%85%A83.jpg?alt=media&token=5c4df88f-8784-431b-9399-adad3b7d66e6',3,92),
('2025-09-08 14:30:00.000000','2025-09-08 14:30:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Froom%2Froom2%2F%E1%84%8C%E1%85%B5%E1%84%92%E1%85%A84.jpeg?alt=media&token=8eaf48e8-5ab6-4894-9c4b-105cca100432',4,92),

-- 순수방 (room_id = 93)
('2025-09-08 14:40:00.000000','2025-09-08 14:40:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Froom%2Froom3%2F%E1%84%89%E1%85%AE%E1%86%AB%E1%84%89%E1%85%AE1.jpg?alt=media&token=f3fc8fbb-8032-43f8-9ba9-8dce01d7d7c8',1,93),
('2025-09-08 14:40:00.000000','2025-09-08 14:40:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Froom%2Froom3%2F%E1%84%89%E1%85%AE%E1%86%AB%E1%84%89%E1%85%AE2.jpeg?alt=media&token=296bfb80-4da5-40ac-bead-f7b254688c1f',2,93),
('2025-09-08 14:40:00.000000','2025-09-08 14:40:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Froom%2Froom3%2F%E1%84%89%E1%85%AE%E1%86%AB%E1%84%89%E1%85%AE3.jpg?alt=media&token=84584d2e-c91e-4ebe-aba4-e3c4fe007532',3,93),
('2025-09-08 14:40:00.000000','2025-09-08 14:40:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Froom%2Froom3%2F%E1%84%89%E1%85%AE%E1%86%AB%E1%84%89%E1%85%AE5.jpeg?alt=media&token=46bf3c5d-5222-4f48-90b1-61e3dfbb8532',4,93),

-- 해돋이방 (room_id = 94)
('2025-09-08 14:41:00.000000','2025-09-08 14:41:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Froom%2Froom4%2F%E1%84%92%E1%85%A2%E1%84%83%E1%85%A9%E1%86%AE%E1%84%8B%E1%85%B51.jpeg?alt=media&token=44e321b6-7a74-4ef1-bf61-9e3ad1299190',1,94),
('2025-09-08 14:41:00.000000','2025-09-08 14:41:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Froom%2Froom4%2F%E1%84%92%E1%85%A2%E1%84%83%E1%85%A9%E1%86%AE%E1%84%8B%E1%85%B52.jpg?alt=media&token=62e6ec00-fc17-4906-8352-c596d2ab90f6',2,94),
('2025-09-08 14:41:00.000000','2025-09-08 14:41:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Froom%2Froom4%2F%E1%84%92%E1%85%A2%E1%84%83%E1%85%A9%E1%86%AE%E1%84%8B%E1%85%B53.jpeg?alt=media&token=6d7070b4-8477-4096-a507-b0ca9dea9e61',3,94),
('2025-09-08 14:41:00.000000','2025-09-08 14:41:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Froom%2Froom4%2F%E1%84%92%E1%85%A2%E1%84%83%E1%85%A9%E1%86%AE%E1%84%8B%E1%85%B54.jpeg?alt=media&token=63b0cd87-3b38-4422-99c3-c931ed576c31',4,94),

-- 오방방 (room_id = 95)
('2025-09-08 14:42:00.000000','2025-09-08 14:42:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Froom%2Froom5%2F%E1%84%8B%E1%85%A9%E1%84%87%E1%85%A1%E1%86%BC%E1%84%87%E1%85%A1%E1%86%BC1.jpg?alt=media&token=56b8a2b3-ab78-4d42-8e0d-7e321da82295',1,95),
('2025-09-08 14:42:00.000000','2025-09-08 14:42:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Froom%2Froom5%2F%E1%84%8B%E1%85%A9%E1%84%87%E1%85%A1%E1%86%BC%E1%84%87%E1%85%A1%E1%86%BC2.jpeg?alt=media&token=19ce6549-4460-4d53-80cc-90669ae0d493',2,95),
('2025-09-08 14:42:00.000000','2025-09-08 14:42:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Froom%2Froom5%2F%E1%84%8B%E1%85%A9%E1%84%87%E1%85%A1%E1%86%BC%E1%84%87%E1%85%A1%E1%86%BC3.jpg?alt=media&token=2d27db46-930e-4ca3-b95c-e7e0d39b604d',3,95),
('2025-09-08 14:42:00.000000','2025-09-08 14:42:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F31%2Froom%2Froom5%2F%E1%84%8B%E1%85%A9%E1%84%87%E1%85%A1%E1%86%BC%E1%84%87%E1%85%A1%E1%86%BC5.jpeg?alt=media&token=5e95309c-ba51-4319-9426-0c5bd3d32908',4,95);

-- 32 숙소 (더블룸=96, 가족룸=97)
INSERT INTO room_image (created_at, updated_at, image_url, sort_order, room_id) VALUES
-- 더블룸 (room_id=96)
('2025-09-08 16:40:00.000000','2025-09-08 16:40:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F32%2Froom%2Froom1%2F9e21ecc6d0816b1716f8ffe94fcbc0a6.jpg?alt=media&token=b6736a06-289d-4de5-90f7-af78fe42404e',1,96),
('2025-09-08 16:40:00.000000','2025-09-08 16:40:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F32%2Froom%2Froom1%2Fa5fe7d3f359917060e5a67fcc1c479d9.jpg?alt=media&token=c5a4a0c0-7972-4824-8246-6da83c6a978f',2,96),
('2025-09-08 16:40:00.000000','2025-09-08 16:40:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F32%2Froom%2Froom1%2Faff57f4c2f31638b72f4fecc6a3c4cb5.jpg?alt=media&token=b81f7a45-d803-473a-b208-678ee6788ac3',3,96),
('2025-09-08 16:40:00.000000','2025-09-08 16:40:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F32%2Froom%2Froom1%2F30c206e9639948831dbe6554139d9ab5.jpeg?alt=media&token=757a4ece-58be-4391-bcf1-015e8dc971e1',4,96),
('2025-09-08 16:40:00.000000','2025-09-08 16:40:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F32%2Froom%2Froom1%2F7f4b903a6c858a6b7b037a862232a34c.jpg?alt=media&token=c356a08c-77de-47a6-9c94-b66b41c335ef',5,96),
('2025-09-08 16:40:00.000000','2025-09-08 16:40:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F32%2Froom%2Froom1%2F005f77a8a1f9d0b89e4560926e374863.jpeg?alt=media&token=692f4f21-8471-49ce-a16d-2dbc43cc3f49',6,96),

-- 가족룸 (room_id=97)
('2025-09-08 16:45:00.000000','2025-09-08 16:45:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F32%2Froom%2Froom2%2Ffc76f547678c31dfbe5a82c0958233fd.jpg?alt=media&token=ba0c4adf-3a18-4acc-98a8-a33aa1f187f4',1,97),
('2025-09-08 16:45:00.000000','2025-09-08 16:45:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F32%2Froom%2Froom2%2Fa7d5e61c81858043b916ca11940d544a.jpg?alt=media&token=b0b4b552-587c-474e-b92b-f20fe59e2e79',2,97),
('2025-09-08 16:45:00.000000','2025-09-08 16:45:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F32%2Froom%2Froom2%2F5826cb7e4d77dcd17c2ba1ee93b87068.jpg?alt=media&token=631230cc-7281-4aa1-8c7a-e2517c09e032',3,97),
('2025-09-08 16:45:00.000000','2025-09-08 16:45:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F32%2Froom%2Froom2%2F77cd95df66ad32acb3bec18ed6a193fa.jpg?alt=media&token=7803232f-ca65-4433-bb05-dcecffc3df30',4,97),
('2025-09-08 16:45:00.000000','2025-09-08 16:45:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F32%2Froom%2Froom2%2Fb554c2d0f6ae05a876f589ab425f72a1.jpg?alt=media&token=333a0193-5ee1-4316-97f2-b5a8e2282569',5,97),
('2025-09-08 16:45:00.000000','2025-09-08 16:45:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F32%2Froom%2Froom2%2F99c8e4158f4d2e963d444e98e55552a1.jpeg?alt=media&token=52dfe298-1731-4f1f-995a-cbbd22aedcd7',6,97);

-- 33 숙소 (도미토리, 개인실) → room_id 98~101
INSERT INTO room_image (created_at, updated_at, image_url, sort_order, room_id) VALUES
-- 4인 여자 도미토리 (room_id=98)
('2025-09-08 17:20:00.000000','2025-09-08 17:20:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F33%2Froom%2Froom1%2Fac3c455dd734c7e97837454d05d7c253.jpg?alt=media&token=cbeebb69-30d4-46f6-acf7-e2042e942c76',1,98),

-- 6인 여자 도미토리 (room_id=99)
('2025-09-08 17:25:00.000000','2025-09-08 17:25:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F33%2Froom%2Froom2%2F21289b5e9481c1d7b938aac37d0c2f67.jpg?alt=media&token=3cf671f0-a94b-454b-baa0-c27899c09c66',1,99),

-- 온돌 개인실 (room_id=100)
('2025-09-08 17:30:00.000000','2025-09-08 17:30:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F33%2Froom%2Froom3%2F229999adb9f2226ba179998571e53b94.jpg?alt=media&token=5a6a1338-796e-4572-9155-e87ed30f657d',1,100),

-- 침대 개인실 (room_id=101)
('2025-09-08 17:35:00.000000','2025-09-08 17:35:00.000000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F33%2Froom%2Froom4%2F3152c94da6f71e04f4595f2dce4fa181.jpg?alt=media&token=b122cc4c-e728-418a-b9bb-85e7b67816e0',1,101);
-- cancellation_policy
-- 31
INSERT INTO cancellation_policy (
    created_at,
    updated_at,
    detail,
    policy_name,
    days_before,
    refund_rate,
    accommodation_id
) VALUES
-- 1. 체크인 7일 전까지 100% 환불
('2025-09-08 14:00:00.000000', '2025-09-08 14:00:00.000000',
 '체크인일 기준 7일 전까지 취소 시: 전액 환불',
 '[기본] 체크인 7일 전까지 100% 환불', 7, 100.00, 31),

-- 2. 체크인 6~4일 전까지 50% 환불
('2025-09-08 14:00:00.000000', '2025-09-08 14:00:00.000000',
 '체크인일 기준 6 ~ 4일 전까지 취소 시: 50% 환불',
 '[성수기] 체크인 6~4일 전까지 50% 환불', 4, 50.00, 31),

-- 3. 체크인 3일 전 ~ 당일 및 No-Show: 환불 불가
('2025-09-08 14:00:00.000000', '2025-09-08 14:00:00.000000',
 '체크인일 기준 3일 전 ~ 당일 및 No-Show 취소 시: 환불 불가',
 '[비수기] 체크인 3일 전 ~ 당일 및 No-Show', 3, 0.00, 31),

-- 4. 예약 후 10분 내 취소/10분 경과 시 규정 적용
('2025-09-08 14:00:00.000000', '2025-09-08 14:00:00.000000',
 '예약 후 10분 내 취소 시 수수료 없음. 10분 경과 시 상기 숙소 취소 및 환불 규정 적용',
 '[당일예약] 예약 후 10분 내 취소 규정', 0, 100.00, 31);

-- 32
-- cancellation_policy
INSERT INTO cancellation_policy (
    created_at,
    updated_at,
    detail,
    policy_name,
    days_before,
    refund_rate,
    accommodation_id
) VALUES
      ('2025-09-08 15:20:00.000000','2025-09-08 15:20:00.000000','체크인일 기준 7일 전까지 취소 시: 전액 환불','[기본] 체크인 7일 전까지 100% 환불',7,100.00,32),
      ('2025-09-08 15:20:00.000000','2025-09-08 15:20:00.000000','체크인일 기준 6 ~ 4일 전까지 취소 시: 50% 환불','[성수기] 체크인 6~4일 전까지 50% 환불',4,50.00,32),
      ('2025-09-08 15:20:00.000000','2025-09-08 15:20:00.000000','체크인일 기준 3일 전 ~ 당일 및 No-Show 취소 시: 환불 불가','[비수기] 체크인 3일 전 ~ 당일 및 No-Show',3,0.00,32),
      ('2025-09-08 15:20:00.000000','2025-09-08 15:20:00.000000','예약 후 10분 내 취소 시 수수료 없음. 10분 경과 시 상기 숙소 취소 및 환불 규정 적용','[당일예약] 예약 후 10분 내 취소 규정',0,100.00,32);


-- 33
-- cancellation_policy
INSERT INTO cancellation_policy (
    created_at,
    updated_at,
    detail,
    policy_name,
    days_before,
    refund_rate,
    accommodation_id
) VALUES
      ('2025-09-08 15:20:00.000000','2025-09-08 15:20:00.000000','체크인일 기준 7일 전까지 취소 시: 전액 환불','[기본] 체크인 7일 전까지 100% 환불',7,100.00,33),
      ('2025-09-08 15:20:00.000000','2025-09-08 15:20:00.000000','체크인일 기준 6 ~ 4일 전까지 취소 시: 50% 환불','[성수기] 체크인 6~4일 전까지 50% 환불',4,50.00,33),
      ('2025-09-08 15:20:00.000000','2025-09-08 15:20:00.000000','체크인일 기준 3일 전 ~ 당일 및 No-Show 취소 시: 환불 불가','[비수기] 체크인 3일 전 ~ 당일 및 No-Show',3,0.00,33),
      ('2025-09-08 15:20:00.000000','2025-09-08 15:20:00.000000','예약 후 10분 내 취소 시 수수료 없음. 10분 경과 시 상기 숙소 취소 및 환불 규정 적용','[당일예약] 예약 후 10분 내 취소 규정',0,100.00,33);
