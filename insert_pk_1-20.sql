-- ============================================================
-- 실행 순서: insert.sql 완료 후 이 파일 실행
-- 포함 데이터: accommodation 1~20 (명시적 PK),
--             accommodation_image, accommodation_tag_map,
--             room 1~58, room_image, cancellation_policy
-- ※ insert.sql과 중복으로 실행하지 말 것
-- ============================================================

INSERT INTO accommodation
(accommodation_id, address, amenities, check_in_time, check_out_time, description, latitude, longitude, min_price, name, rating, status, thumbnail, category_id, company_id, main_region_id, sub_region_id)
VALUES
    (1,'경상북도 경주시 남산동 1156-461','["투숙객 외 다른 사람의 방문을 제한합니다","사전 문의 없이 추가 인원 발생 시 입실이 불가하며 당일 예약 취소와 동일하게 간주됩니다","애완동물과 동반 입실 시 입실이 불가하며 당일 예약 취소와 동일하게 간주됩니다","한옥 특성상 오소한옥 내 BBQ 조개 또는 어패류 튀김류 조리는 불가한 점 양해 부탁드립니다","전 객실의 실내는 금연 구역이며 화재 위험이 있는 촛불 및 폭죽 사용부탄가스 사용은 금지입니다","시설물 훼손 및 분실 시 책임은 고객님께 부과하오니 객실 사용에 주의 부탁드립니다","부주의로 인해 일어난 안전사고 귀중품(현금포함) 분실 및 파손에 대해서는 리조트에서 책임지지 않습니다","마지막 퇴실 시에는 실내에 있는 쓰레기는 분리수거해 버려 주시고 사용하신 식기도 세척해 주시기 바랍니다"]',
     '2025-09-04 15:00:00.000000','2025-09-04 11:00:00.000000',
     '오랜 꿈으로 빚어낸 오소한옥의 공간에는 전통 한옥 양식과 실용적인 서양식 인테리어를 결합하여 설계했습니다.\n\n여유롭게 풍류를 즐기던 선조들의 일상처럼 느리게, 천천히 쉬어갈 수 있는 공간입니다.',
     35.8393,129.2247,110000.00,'경주 오소한옥스테이',0.0,'ACTIVE','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F855e7e73-7b2d-4e94-a4d5-bcca80eaa438_1.jpg?alt=media&token=41131168-e1f4-4435-a8a5-5f6733710cc2',6,2,15,212),

    (2,'부산광역시 해운대구 우동 645-6','["위의 정보는 호텔의 사정에 따라 변경될 수 있습니다","미성년자는 보호자 동반 없이 이용이 불가합니다","이미지는 실제와 상이할 수 있습니다","체크인 시 배정 상품 또는 베드 타입이 미기재된 상품은 특정 객실과 베드 타입을 보장하지 않습니다","객실가는 세금","봉사료가 포함된 금액입니다","수영장 운영은 날씨 또는 호텔 상황에 따라 변동될 수 있습니다","가전/가구/비품 등의 분실 및 파손 시 추가 비용이 발생합니다","객실 사진은 대표적인 예시로 실제 객실의 구조나 인테리어는 상이할 수 있습니다."]',
     '2025-09-04 16:00:00.000000','2025-09-04 11:00:00.000000',
     '사계절을 품은 프라이빗 레지던스로 2024년 7월 신규 오픈한 호텔입니다.\n\n해운대의 주요 관광지를 도보로 이동할 수 있으며, 주방 시설을 보유하고 있어 편리한 여행을 즐기실 수 있습니다.',
     35.1581,129.1604,130300.00,'뉴시스 해운대 레지던스',0.0,'ACTIVE','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F7429b880-0372-4187-b895-daa68b112a78_5.jpg?alt=media&token=41217f42-419c-4ff4-bd02-b28ff59331cb',1,2,2,38),

    (3,'부산광역시 동구 초량동 1204-1','["최대 인원 초과 시 입실 불가합니다","미성년자는 보호자 동반 없이 이용이 불가합니다","위의 정보는 호텔의 사정에 따라 변경될 수 있습니다","해당 이미지는 실제와 상이할 수 있습니다","체크인 시 배정 또는 베드 타입 미기재 상품은 특정 객실과 베드 타입을 보장하지 않습니다","반려동물 동반 입실 불가합니다","해당 객실 가는 세금","봉사료가 포함된 금액","데코레이션 업체를 통한 객실 내 장식물 설치는 불가합니다"]',
     '2025-09-04 15:00:00.000000','2025-09-04 11:00:00.000000',
     '라마다 앙코르 바이 윈덤 부산역은 446객실을 보유한 4성급 호텔로, 부산역 광장에 위치하여 비즈니스와 관광에 최적의 장소입니다.',
     35.1165,129.0400,115000.00,'라마다 앙코르 바이 윈덤 부산역',0.0,'ACTIVE','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F085c3f13-c6e2-4cca-b20e-dcf52c694b2b_1.webp?alt=media&token=db34b462-440b-4b6f-899d-4922101c25fc',1,2,2,46),

    (4,'강원도 속초시 조양동 1493-7','["미성년자는 보호자 동반없이 이용이 불가합니다","전 객실 애완동물 출입이 불가합니다","위의 정보는 호텔의 사정에 따라 변경될 수 있습니다","해당 이미지는 실제와 상이할 수 있습니다","체크인 시 배정 또는 베드 타입 미기재 상품은 특정 객실과 베드 타입을 보장하지 않습니다","해당 객실가는 세금","봉사료가 포함된 금액"]',
     '2025-09-04 15:00:00.000000','2025-09-04 11:00:00.000000',
     '2021년 4월 오픈한 속초 호텔 더 블루테라는 속초해수욕장과 도보 5분 거리에 위치합니다.\n\n객실 테라스에서 아름다운 일출과 야경을 감상할 수 있습니다.',
     38.184965,128.601442,67900.00,'속초 호텔 더 블루테라',0.0,'ACTIVE','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F9a96adee-3403-4863-a321-c1aa2aa36b53_6.png?alt=media&token=d9950a65-5b46-496e-a217-db9fccfb8bf3',1,2,10,133),

    (5,'전라북도 전주시 완산구 풍남동2가 11-2','["위의 정보는 게스트하우스 사정에 따라 변경될 수 있습니다","해당 이미지는 실제와 상이할 수 있습니다","예약 확정 이후의 취소는 취소환불규정에 의거하여 적용됩니다"]',
     '2025-09-04 15:00:00.000000','2025-09-04 11:30:00.000000',
     '소소하게 행복이 머무는 공간입니다.\n\n전주 한옥마을 공용주차장에서 도보로 3~5분 소요됩니다.\n\n전 객실 소독과 침구 천연세제 사용을 원칙으로 합니다.',
     35.819859,127.152035,49000.00,'전주 소소한 한옥',0.0,'ACTIVE','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2Fee2138e3-069a-43e9-9602-79403f835cad_3.jpg?alt=media&token=f3f08e96-f837-4384-9264-e21c85d1c480',6,2,13,176),

    (6,'경기도 수원시 권선구 구운동 928-7','["주차시설을 보유한 숙소입니다.(차량 이용 시 숙소에 문의해 주세요.)","객실은 체크인 시 배정되며 사진과 다를 수 있습니다.","만 19세 미만 청소년 혼숙은 법적으로 불가하며","이에 대한 숙소의 입실 거부 시 취소/환불이 불가합니다."]',
     '2025-09-08 15:00:00.000000','2025-09-09 11:00:00.000000',
     '넓고 쾌적한 수원 최고의 시설.',
     37.281265,126.970797,41000.00,'수원 구운동 앤드 호텔',0.0,'ACTIVE','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F26edae18-54bc-4253-9caa-82f9cf96e620_1.webp?alt=media&token=9a329e8d-862c-42ef-b6d7-d2db638967e8',1,2,9,106),

    (7,'전라북도 전주시 완산구 경원동3가 72-2','["위의 정보는 호텔의 사정에 따라 변경될 수 있습니다","부모를 동반하지 않은 만 19세 미만 미성년자는 투숙할 수 없습니다","이미지는 실제와 상이할 수 있습니다"]',
     '2025-09-08 15:00:00.000000','2025-09-09 12:00:00.000000',
     '호텔신라의 프리미엄 비즈니스 호텔로 주요 관광지와 접근성이 편리합니다.',
     35.819839,127.148575,214225.00,'★당일특가★ 신라스테이 전주 한옥마을',0.0,'ACTIVE','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2Fd4c16a78-d373-4241-892f-30018d1fb0a8_687f3388789fa+%281%29.jpg?alt=media&token=cdb043fb-d2b0-4572-9593-fb0ebaa371bf',1,2,13,176),

    (8,'대전광역시 유성구 봉명동 469-2','["위의 정보 및 객실 정보는 변경될 수 있습니다.","미성년자 혼숙 예약으로 인한 입실 거부에 대해 취소 및 환불 불가"]',
     '2025-09-08 15:00:00.000000','2025-09-09 11:00:00.000000',
     '평범한 모텔에서 특별한 경험의 공간으로 탈바꿈한 아늑호텔앤스파 대전유성점입니다.',
     36.355929,127.338551,10400.00,'대전 아늑 호텔 앤 스파 대전유성점',0.0,'ACTIVE','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F68c50986-1e93-4b1b-8278-9f9ec423f8f9_1.jpg?alt=media&token=d961a0b2-69bf-47cb-8fbc-857323f4fee8',3,2,6,79),

    (9,'부산광역시 해운대구 중동 1392-100','["최대 인원 초과 시 입실 불가합니다","위의 정보는 호텔의 사정에 따라 변경될 수 있습니다","해당 이미지는 실제와 상이할 수 있습니다"]',
     '2025-09-08 15:00:00.000000','2025-09-09 12:00:00.000000',
     '낮과 밤, 모두 아름다운 해운대 바다가 눈앞에 펼쳐지는 최고의 전망을 자랑합니다.',
     35.160581,129.162237,92000.00,'★당일특가★ 코오롱 씨클라우드 호텔',0.0,'ACTIVE','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F888482d9-20e2-453c-a942-b8e7d127e6e9_15.jpg?alt=media&token=d7b489b8-fe51-4c02-8377-b8c53f7d1113',1,2,2,38),

    (10,'강원도 속초시 조양동 1411-150','["위의 정보는 호텔의 사정에 따라 변경될 수 있습니다","미성년자는 보호자 동반 없이 이용이 불가합니다","이미지는 실제와 상이할 수 있습니다"]',
     '2025-09-08 15:00:00.000000','2025-09-09 11:00:00.000000',
     '청량한 동해바다와 웅장한 설악산을 품은 반얀그룹 홈마리나속초입니다.',
     38.190436,128.595771,130200.00,'홈 마리나 속초 - Part of Banyan Group',0.0,'ACTIVE','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2Fdc139605-d4ae-40d2-9931-1fd0e7077536_10.jpg?alt=media&token=49485cec-cdc9-4727-838d-e626361e5d40',1,2,10,133),
    (11,'제주특별자치도 서귀포시 성산읍 오조리 990-1','["수영장 운영은 날씨 또는 호텔 상황에 따라 변동될 수 있습니다","미성년자는 보호자 동반없이 이용이 불가합니다"]',
     '2025-09-04 10:33:00.000000','2025-09-04 23:35:00.000000',
     '푸른 바다의 제주 성산에 위치한 코텔리아S 호텔은 일출이 아름다운 성산일출봉을 배경으로 어우러져 있습니다.\n\n넓은 로비와 로비 라운지, 편의점, 레스토랑, 연회장을 보유하며, 야외수영장에서 물놀이도 즐길 수 있습니다.',
     33.455053,126.910182,50000.00,'코델리아 호텔',0.0,'ACTIVE','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2Fca924043-79c0-4541-9415-4f5108b2dff2_1.jpg?alt=media&token=b369d4ad-a2d7-4f8c-896d-9bdb719be0f2',1,2,17,249),

    (12,'강원도 강릉시 강문동 223','["객실 요금 외 추가비용은 1박 기준으로 적용되며 자세한 사항은 펜션으로 문의바랍니다","최대 인원 초과시 입실이 불가 합니다"]',
     '2025-09-04 23:39:00.000000','2025-09-05 12:38:00.000000',
     '자연 속 힐링을 즐길 수 있는 조용한 펜션입니다.\n\n자쿠지에서 하루의 피로를 씻고, 소중한 사람들과 특별한 시간을 만끽할 수 있습니다.',
     37.796686,128.916969,50000.00,'강릉 더스테이 은파',0.0,'ACTIVE','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F2ba14a6f-5c96-4f25-bc40-7b17d8e8bc3b_6.jpg?alt=media&token=aa7e0f11-2660-4940-a9ed-7aba97bbfa29',2,2,10,132),

    (13,'대구광역시 동구 상매동 506','["프론트에서 여기어때 회원임을 알려주세요","각 객실의 입실 기준을 확인 부탁드립니다"]',
     '2025-09-04 15:00:00.000000','2025-09-05 11:00:00.000000',
     '아침형 고객을 위한 조식 제공.\n\n전 객실 시트교체 및 소독 100%.\n\n대형/중형/소형 주차장 완비.',
     35.889297,128.706124,40000.00,'대구 하운드 호텔-대구혁신점',0.0,'ACTIVE','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F94c01ba9-2abb-46ff-85fe-56a446fb9ca6_10.jpg?alt=media&token=4521caa7-f2f7-411a-9bec-f12fe1ff1ce1',3,2,3,56),

    (14,'대구광역시 동구 신천동 326-1','["체크인 시 Deposit(보증금) 요구 가능","퇴실 시 전액 환불"]',
     '2025-09-08 15:00:00.000000','2025-09-08 00:00:00.000000',
     '대중교통 이용이 편리하며 대구 주요 관광지와 접근성이 좋은 호텔입니다.\n\n깔끔한 외관과 항상 청결하게 관리되는 객실을 제공합니다.',
     35.875415,128.627703,231000.00,'대구 메리어트 호텔',0.0,'ACTIVE','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F9f202ea1-93af-4055-b2c2-ebfde680d31d_15.jpg?alt=media&token=a69d1889-2563-4dac-bd62-e5215ffc7add',1,2,3,56),

    (15,'부산광역시 남구 문현동 1227-2','["최대인원 초과시 입실 불가합니다","미성년자는 보호자 동반없이 이용이 불가합니다"]',
     '2025-09-08 15:00:00.000000','2025-09-08 11:00:00.000000',
     '모든 객실에 비품을 구비하여 편안한 여행이 가능하며,\n\n아름다운 외관과 훌륭한 객실을 자랑합니다.',
     35.148426,129.065470,481000.00,'아바니 센트럴 부산',0.0,'ACTIVE','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F2c8ea8af-36c5-4112-b756-9e15c96f89f5_20.jpg?alt=media&token=994a922f-3213-4990-9e6b-8c64e6143513',1,2,2,42),

    (16,'서울특별시 중구 을지로6가 58-5','["체크인 시 Deposit(보증금) 요구 가능","퇴실 시 전액 환불"]',
     '2025-09-08 15:00:00.000000','2025-09-09 00:00:00.000000',
     '흥인지문의 처마를 모티브로 모던하고 안락한 인테리어가 돋보이는 객실과 편의 주방시설이 있는 객실입니다.',
     37.566440,127.004274,355300.00,'노보텔 앰배서더 동대문 호텔&레지던스',0.0,'ACTIVE','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F826c2ecb-19e5-484b-a30e-318635b2bfe5_20.jpg?alt=media&token=ef05e48f-062b-4ce5-a573-e7af61324554',1,2,1,23),

    (17,'부산광역시 수영구 광안동 201-1','["위의 정보는 호텔의 사정에 따라 변경될 수 있습니다","미성년자는 보호자 동반 없이 이용이 불가합니다"]',
     '2025-09-08 15:00:00.000000','2025-09-09 11:00:00.000000',
     '부산의 아름다운 바다, 광안리가 함께하는 레지던스 호텔입니다.',
     35.150209,129.115274,104600.00,'효이스테이 더 광안',0.0,'ACTIVE','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F7f09ac39-f1ff-4555-ba58-512f63fc9378_26.jpg?alt=media&token=12c29e46-2f19-45a5-82ed-60a2dc635e7f',1,2,2,39),

    (18,'제주특별자치도 서귀포시 색달동 2101','["체크인 시 Deposit(보증금) 요구 가능","퇴실 시 전액 환불"]',
     '2025-09-08 03:00:00.000000','2025-09-08 11:00:00.000000',
     '조선호텔의 노하우를 집대성한 그랜드 조선 제주로,\n\n최적의 편안함과 최고의 즐거움을 제공합니다.',
     33.252802,126.408098,372900.00,'그랜드 조선 제주',0.0,'ACTIVE','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F6e281112-7afd-4f85-b537-59d2712d2915_30.jpg?alt=media&token=07fc935f-1cdc-44aa-bb30-d7683329121c',1,2,17,249),

    (19,'강원도 양양군 현남면 전포매리 47-4','["미니바 제공","버블 용품 제공","풀장 사용료 1일 55,000원"]',
     '2025-09-08 03:00:00.000000','2025-09-09 11:00:00.000000',
     '양양 최상급 온천이 샘솟는 더 앤 리조트 & 스파,\n\n자연 친화적 시설과 전 객실 천연 온천수 공급.',
     37.942095,128.620000,122000.00,'양양 더 앤 리조트 호텔&스파',0.0,'ACTIVE','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F4d7bbb9c-bf25-4945-b8f4-7437be3a4b9e_35.jpg?alt=media&token=bbf5aa6d-f0f4-4901-883e-357f784e7360',1,2,10,146),

    (20,'서울특별시 용산구 한강로3가 40-708','["미성년자는 보호자 동반 없이 이용이 불가합니다","체크인 시 배정 상품 또는 베드 타입 미기재 시 특정 객실과 베드 타입 보장 불가"]',
     '2025-09-08 15:00:00.000000','2025-09-09 11:00:00.000000',
     '나인트리 프리미어 로카우스 호텔 서울 용산은 용산역 바로 앞에 위치하여 뛰어난 입지를 자랑하며,\n\n다양한 부대시설을 제공합니다.',
     37.527791,126.964497,385400.00,'나인트리 프리미어 로카우스 호텔 서울 용산',0.0,'ACTIVE','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2Fe6a85cbf-2f7a-45a1-868a-600c830e554e_41.jpg?alt=media&token=35a1d895-f12b-4236-b7f0-b5454a076e3b',1,2,1,23);



INSERT INTO accommodation_image
(image_id, created_at, updated_at, image_type, image_url, sort_order, accommodation_id)
VALUES
    (1,'2025-09-02 22:44:42.800868','2025-09-02 22:44:42.800868','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F6%2Fimages%2Fa19393f3-d6ea-4a7e-8ed7-920c82ef4612_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+10.27.08.png?alt=media&token=761857bd-7960-4119-b35a-a77933ccc8a3',1,6),
    (2,'2025-09-02 22:44:43.179902','2025-09-02 22:44:43.179902','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F6%2Fimages%2Fea30373a-114c-457c-808a-10b066597e4d_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+10.27.20.png?alt=media&token=be00bab3-3128-472d-90cf-f219c262de03',2,6),
    (3,'2025-09-02 22:44:43.559074','2025-09-02 22:44:43.559074','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F6%2Fimages%2F4eea2921-1328-4781-bf8c-428c7445012a_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+10.27.12.png?alt=media&token=c5e6b92b-ac66-4465-831a-89138a627cc3',3,6),
    (4,'2025-09-02 22:45:02.419930','2025-09-02 22:45:02.419930','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F5%2Fimages%2Fe3b1c03a-949f-4b46-8d98-38780e929343_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB+2.10.39.png?alt=media&token=15e4e199-a543-4753-9523-21cdcc754e23',1,5),
    (5,'2025-09-02 22:45:02.837930','2025-09-02 22:45:02.837930','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F5%2Fimages%2F707dcd1f-f69b-4a4b-b9d9-57029d02651b_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB+2.10.33.png?alt=media&token=4825c14e-0ac4-4f20-bb39-a9cc4f7c152c',2,5),
    (6,'2025-09-02 22:45:03.322635','2025-09-02 22:45:03.322635','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F5%2Fimages%2F99de375e-1760-4a26-aee2-369710adc360_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%8C%E1%85%A5%E1%86%AB+2.10.29.png?alt=media&token=3e578536-34ab-49c9-ad0e-e3bb48e7f090',3,5),
    (7,'2025-09-02 22:46:27.639079','2025-09-02 22:46:27.639079','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F2%2Fimages%2F663c8b48-948d-4965-89b8-ed175132f1e1_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+10.22.14.png?alt=media&token=02d991ca-7f86-46bd-9bfb-d5797feca9c5',1,2),
    (8,'2025-09-02 22:46:28.097505','2025-09-02 22:46:28.097505','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F2%2Fimages%2Fd686479f-e07c-48d8-899d-f36382bff1ae_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+10.22.17.png?alt=media&token=75948854-8dab-4ca7-ae7a-b15c3e34befb',2,2),
    (9,'2025-09-02 22:46:28.609613','2025-09-02 22:46:28.609613','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F2%2Fimages%2Ff9b078c7-cf62-4b69-b891-6c6a05061370_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+10.22.21.png?alt=media&token=2b0ecc55-030e-4f32-9c2a-a005b83c51bb',3,2),
    (10,'2025-09-05 13:40:02.707018','2025-09-05 13:40:02.707018','MAIN','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F26edae18-54bc-4253-9caa-82f9cf96e620_1.webp?alt=media&token=9a329e8d-862c-42ef-b6d7-d2db638967e8',0,6),
    (11,'2025-09-08 00:39:25.662602','2025-09-08 00:39:25.662602','MAIN','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F855e7e73-7b2d-4e94-a4d5-bcca80eaa438_1.jpg?alt=media&token=41131168-e1f4-4435-a8a5-5f6733710cc2',0,1),
    (12,'2025-09-08 00:39:26.017712','2025-09-08 00:39:26.017712','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F1%2Fimages%2F52eac953-f7bc-491f-95e8-0babba2e3e65_2.jpg?alt=media&token=0cb7c46e-993f-4689-afaf-a603450bd075',1,1),
    (13,'2025-09-08 00:39:26.444091','2025-09-08 00:39:26.444091','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F1%2Fimages%2F762c1e93-9486-4875-a7bf-b6c2a3399951_3.jpg?alt=media&token=e776beb9-488e-458a-a198-5bb2c7e257cc',2,1),
    (14,'2025-09-08 00:39:26.862938','2025-09-08 00:39:26.862938','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F1%2Fimages%2F024da806-a28d-471b-9e1d-1529913cf25d_4.jpg?alt=media&token=2be4a9d0-c7f8-4050-aafb-448e07e53770',3,1),
    (15,'2025-09-08 00:43:27.426251','2025-09-08 00:43:27.426251','MAIN','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F7429b880-0372-4187-b895-daa68b112a78_5.jpg?alt=media&token=41217f42-419c-4ff4-bd02-b28ff59331cb',0,2),
    (16,'2025-09-08 00:46:26.431386','2025-09-08 00:46:26.431386','MAIN','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F085c3f13-c6e2-4cca-b20e-dcf52c694b2b_1.webp?alt=media&token=db34b462-440b-4b6f-899d-4922101c25fc',0,3),
    (17,'2025-09-08 00:46:26.764732','2025-09-08 00:46:26.764732','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F3%2Fimages%2F33677994-c5c6-4b7e-a169-45ae21644210_2.jpg?alt=media&token=b64c8093-0846-4b90-b480-b433ce422ba1',1,3),
    (18,'2025-09-08 00:46:27.044740','2025-09-08 00:46:27.044740','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F3%2Fimages%2Fd3cc928c-261e-4fad-975d-c0c9bf477b12_3.jpg?alt=media&token=520471bd-f4fc-4d90-b3c5-abd41730b658',2,3),
    (19,'2025-09-08 00:46:27.336478','2025-09-08 00:46:27.336478','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F3%2Fimages%2Fb143c5fe-a374-4b6f-ba42-882c662c9c92_4.jpg?alt=media&token=1e3c8607-288e-42da-a841-d79c8c0cfae6',3,3),
    (20,'2025-09-08 00:51:50.297123','2025-09-08 00:51:50.297123','MAIN','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F9a96adee-3403-4863-a321-c1aa2aa36b53_6.png?alt=media&token=d9950a65-5b46-496e-a217-db9fccfb8bf3',0,4),
    (21,'2025-09-08 00:52:09.065674','2025-09-08 00:52:09.065674','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F4%2Fimages%2F453bf00b-49fb-45df-a32c-885570c8a741_7.jpg?alt=media&token=06ac687d-ac0e-444a-9bb6-17b1e3ffbc09',1,4),
    (22,'2025-09-08 00:52:09.240859','2025-09-08 00:52:09.240859','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F4%2Fimages%2F2d0d90ef-6125-4ac9-beb4-f2a9b34b6fa8_8.jpg?alt=media&token=8b2b8cde-c0c9-47de-bfcd-31d8bec1ad95',2,4),
    (23,'2025-09-08 00:56:33.088780','2025-09-08 00:56:33.088780','MAIN','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2Fee2138e3-069a-43e9-9602-79403f835cad_3.jpg?alt=media&token=f3f08e96-f837-4384-9264-e21c85d1c480',0,5),
    (24,'2025-09-08 01:10:51.233182','2025-09-08 01:10:51.233182','MAIN','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2Fca924043-79c0-4541-9415-4f5108b2dff2_1.jpg?alt=media&token=b369d4ad-a2d7-4f8c-896d-9bdb719be0f2',0,11),
    (25,'2025-09-08 01:10:51.456164','2025-09-08 01:10:51.456164','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F19%2Fimages%2F416be22d-d06e-45c5-a832-e5f697252d04_4.jpg?alt=media&token=841bd29b-8cce-4783-9344-2955291f8362',1,11),
    (26,'2025-09-08 01:10:51.652341','2025-09-08 01:10:51.652341','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F19%2Fimages%2F1a971b99-b82d-4d20-9678-c4eea343d829_3.jpg?alt=media&token=a70ca4d4-5d1b-4a72-9843-3890b9d10f10',2,11),
    (27,'2025-09-08 01:10:52.744893','2025-09-08 01:10:52.744893','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F19%2Fimages%2Ffdcb0085-d786-4c34-8e8c-c901a6cfdbf1_2.jpg?alt=media&token=b516b2e3-bf85-4309-a264-fd235099f9a8',3,11),
    (28,'2025-09-08 01:14:11.394297','2025-09-08 01:14:11.394297','MAIN','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F2ba14a6f-5c96-4f25-bc40-7b17d8e8bc3b_6.jpg?alt=media&token=aa7e0f11-2660-4940-a9ed-7aba97bbfa29',0,12),
    (29,'2025-09-08 01:14:11.535299','2025-09-08 01:14:11.535299','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F20%2Fimages%2F9d566779-e740-4d5a-8f78-18b493d04feb_7.jpg?alt=media&token=526eb584-fcfd-4e96-97ff-c6d34bcbd5f5',1,12),
    (30,'2025-09-08 01:14:11.689007','2025-09-08 01:14:11.689007','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F20%2Fimages%2Fc673c71e-cafe-423f-9c5a-279f2c3d7419_8.jpg?alt=media&token=178a4b04-1b4f-4c1b-a940-60b216829020',2,12),
    (31,'2025-09-08 01:14:11.855285','2025-09-08 01:14:11.855285','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F20%2Fimages%2F0b7c46c6-65de-47a9-9bb7-e48de635dcdf_9.jpg?alt=media&token=5b927a22-19e9-4754-80e9-ddcf2257b204',3,12),
    (32,'2025-09-08 01:34:43.968354','2025-09-08 01:34:43.968354','MAIN','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F94c01ba9-2abb-46ff-85fe-56a446fb9ca6_10.jpg?alt=media&token=4521caa7-f2f7-411a-9bec-f12fe1ff1ce1',0,13),
    (33,'2025-09-08 01:34:44.248573','2025-09-08 01:34:44.248573','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F21%2Fimages%2Fcfe9be15-08d5-4a5e-9743-1880eecf4ee9_11.jpeg?alt=media&token=f38d112b-4601-457c-99fa-234efeaac703',1,13),
    (34,'2025-09-08 01:34:44.539284','2025-09-08 01:34:44.539284','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F21%2Fimages%2Fad2024dc-e6b1-4007-a5dd-a3db011bcc2f_12.jpeg?alt=media&token=29a4a962-a5df-4f80-8021-b95073fd7db3',2,13),
    (35,'2025-09-08 01:34:44.776809','2025-09-08 01:34:44.776809','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F21%2Fimages%2F44b826d6-78b8-458e-8f53-86fd0c975185_13.jpg?alt=media&token=92959887-fcca-4640-9941-1120ab5da243',3,13),
    (36,'2025-09-08 01:40:58.415065','2025-09-08 01:40:58.415065','MAIN','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F9f202ea1-93af-4055-b2c2-ebfde680d31d_15.jpg?alt=media&token=a69d1889-2563-4dac-bd62-e5215ffc7add',0,14),
    (37,'2025-09-08 01:40:58.576434','2025-09-08 01:40:58.576434','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F22%2Fimages%2F7cc89eb5-5128-4d6e-8e1f-b91d547c32f5_16.jpg?alt=media&token=2d93df1d-5a93-40fa-8cd0-53198ccbf408',1,14),
    (38,'2025-09-08 01:40:58.988757','2025-09-08 01:40:58.988757','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F22%2Fimages%2F622b6d25-ec9d-4158-b939-4f35d8c560a3_17.jpg?alt=media&token=3c3ebe94-797b-4f67-940f-d2be6166dfdf',2,14),
    (39,'2025-09-08 01:40:59.177605','2025-09-08 01:40:59.177605','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F22%2Fimages%2F8a5b9831-ff46-4e21-a587-c23325063413_18.jpg?alt=media&token=0487de38-d28e-4aa0-bffa-06f033cc318f',3,14),
    (40,'2025-09-08 01:40:59.324017','2025-09-08 01:40:59.324017','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F22%2Fimages%2F504b2a77-7209-4b7d-9e29-167fe87e7ab6_19.jpg?alt=media&token=02b1e46f-b3a0-4707-82e9-b4359aa504f4',4,14),
    (41,'2025-09-08 01:49:37.783276','2025-09-08 01:49:37.783276','MAIN','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F2c8ea8af-36c5-4112-b756-9e15c96f89f5_20.jpg?alt=media&token=994a922f-3213-4990-9e6b-8c64e6143513',0,15),
    (42,'2025-09-08 01:49:37.930735','2025-09-08 01:49:37.930735','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F23%2Fimages%2Ff713ca6a-a976-4409-858c-d58823771760_21.jpg?alt=media&token=b6455fb8-8cae-443b-acc5-e613231f9994',1,15),
    (43,'2025-09-08 01:49:38.077007','2025-09-08 01:49:38.077007','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F23%2Fimages%2F57760a22-4c18-4f25-bf4f-e629fd544442_22.jpg?alt=media&token=372fc2b1-db8e-4300-aaa3-e1ade2662e2a',2,15),
    (44,'2025-09-08 01:49:38.255404','2025-09-08 01:49:38.255404','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F23%2Fimages%2F77f3d065-d722-4d5c-a338-00e24e45b2ec_23.jpg?alt=media&token=e92e86af-94e7-418e-85e3-7f1fe4bfd559',3,15),
    (45,'2025-09-08 01:49:38.415794','2025-09-08 01:49:38.415794','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F23%2Fimages%2F881777b5-29e3-46da-b5d5-24bbd0708b59_24.jpg?alt=media&token=a51d7f75-5f7c-46e1-b3ff-6587c288d034',4,15),
    (46,'2025-09-08 09:25:28.029923','2025-09-08 09:25:28.029923','MAIN','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2Fd4c16a78-d373-4241-892f-30018d1fb0a8_687f3388789fa+%281%29.jpg?alt=media&token=cdb043fb-d2b0-4572-9593-fb0ebaa371bf',0,7),
    (47,'2025-09-08 09:25:28.200617','2025-09-08 09:25:28.200617','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F10%2Fimages%2Fae085d55-47e0-4282-bb6b-7b6ec018d854_2.jpg?alt=media&token=e76fb3f6-22c2-412b-a70a-122d2274e11c',1,7),
    (48,'2025-09-08 09:25:28.340509','2025-09-08 09:25:28.340509','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F10%2Fimages%2F16e725a9-b5a3-497f-8b3e-ba5de13a4dd6_3.jpg?alt=media&token=9781e5d1-389b-45ac-8479-578661dba637',2,7),
    (49,'2025-09-08 09:25:28.524427','2025-09-08 09:25:28.524427','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F10%2Fimages%2Fd8c572b8-9dc6-4041-a8a4-caaca1562455_4.jpg?alt=media&token=4c02b196-5c1a-442c-a430-a9e5b7dc2576',3,7),
    (50,'2025-09-08 09:25:28.663786','2025-09-08 09:25:28.663786','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F10%2Fimages%2F5fd2db4e-9327-4add-bb44-74f5ff754194_5.jpg?alt=media&token=0c092923-f471-4df1-9af2-2ff70c8231bf',4,7),
    (51,'2025-09-08 09:45:20.191316','2025-09-08 09:45:20.191316','MAIN','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F68c50986-1e93-4b1b-8278-9f9ec423f8f9_1.jpg?alt=media&token=d961a0b2-69bf-47cb-8fbc-857323f4fee8',0,8),
    (52,'2025-09-08 09:45:20.345365','2025-09-08 09:45:20.345365','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F11%2Fimages%2F98b72d36-158c-4333-9b50-eab1917c150a_6.jpg?alt=media&token=e3353f59-4626-4516-abfe-b7b6e698e590',1,8),
    (53,'2025-09-08 09:45:20.500694','2025-09-08 09:45:20.500694','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F11%2Fimages%2Fc3877aa9-b2b1-4281-ae41-2e59797725a3_7.jpg?alt=media&token=5dd1fb8a-1f26-44e7-b123-795874e2a95f',2,8),
    (54,'2025-09-08 09:45:20.610377','2025-09-08 09:45:20.610377','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F11%2Fimages%2F03a033f5-72a9-4e8d-aa58-2f4f47bb02f2_8.jpg?alt=media&token=a0fab71b-a0d3-4708-aed9-c7ed0bdcb435',3,8),
    (55,'2025-09-08 09:45:20.746300','2025-09-08 09:45:20.746300','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F11%2Fimages%2Fdc672f00-ca1f-43d8-ae2a-29d103b81223_9.jpg?alt=media&token=494cbb70-4c70-4e2e-aa7f-19505802702d',4,8),
    (56,'2025-09-08 09:53:08.328042','2025-09-08 09:53:08.328042','MAIN','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2Fdc139605-d4ae-40d2-9931-1fd0e7077536_10.jpg?alt=media&token=49485cec-cdc9-4727-838d-e626361e5d40',0,10),
    (57,'2025-09-08 09:53:08.557511','2025-09-08 09:53:08.557511','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F18%2Fimages%2Faed26005-f4c9-4ec4-9837-a2c4577367e3_11.jpg?alt=media&token=6d11fdaa-6d62-456e-bb8e-e824d9d0bbb7',1,10),
    (58,'2025-09-08 09:53:08.818480','2025-09-08 09:53:08.818480','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F18%2Fimages%2F0aed024c-6276-4058-b684-6029a50e3782_12.jpg?alt=media&token=46397b00-818b-4813-9d64-d70feb444b5a',2,10),
    (59,'2025-09-08 09:53:09.219346','2025-09-08 09:53:09.219346','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F18%2Fimages%2Fafddfbea-3abe-49f8-b54b-12d5489956e6_13.jpg?alt=media&token=8da4f2ae-8f25-4843-a87e-f4387f598760',3,10),
    (60,'2025-09-08 09:53:09.609342','2025-09-08 09:53:09.609342','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F18%2Fimages%2Fa7ec8ad8-02eb-4dc6-a42f-d7c539f6bcb5_14.jpg?alt=media&token=1643a569-46df-43ed-85e8-ba01f7579a0d',4,10),
    (61,'2025-09-08 10:01:18.595901','2025-09-08 10:01:18.595901','MAIN','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F888482d9-20e2-453c-a942-b8e7d127e6e9_15.jpg?alt=media&token=d7b489b8-fe51-4c02-8377-b8c53f7d1113',0,9),
    (62,'2025-09-08 10:01:19.092719','2025-09-08 10:01:19.092719','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F12%2Fimages%2Fef60245e-5082-4992-a831-2bdf2a108ab8_16.jpg?alt=media&token=d32e6bdf-e370-4885-9985-7204dbd1c0c0',1,9),
    (63,'2025-09-08 10:01:19.622729','2025-09-08 10:01:19.622729','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F12%2Fimages%2F9ede4263-dcec-4551-8baa-f287a639d6cf_17.jpg?alt=media&token=6cf12336-1e40-4a00-8ca5-cfe802ea32fb',2,9),
    (64,'2025-09-08 10:01:20.103518','2025-09-08 10:01:20.103518','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F12%2Fimages%2F43635a53-e9b9-4bb7-b41a-a2ad19144f37_18.jpg?alt=media&token=5c07be5a-1b0b-42f6-b8ca-8d9862580077',3,9),
    (65,'2025-09-08 10:01:20.502360','2025-09-08 10:01:20.502360','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F12%2Fimages%2F9d7613ea-5f2c-470c-b5c0-f28b5e9c31bb_19.jpg?alt=media&token=dad80889-f9f0-4c98-ad4b-e57be05ed448',4,9),
    (66,'2025-09-08 10:07:00.317308','2025-09-08 10:07:00.317308','MAIN','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F826c2ecb-19e5-484b-a30e-318635b2bfe5_20.jpg?alt=media&token=ef05e48f-062b-4ce5-a573-e7af61324554',0,16),
    (67,'2025-09-08 10:07:00.631701','2025-09-08 10:07:00.631701','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F24%2Fimages%2F54d9f32e-9c8d-4fa7-a7d6-c6fe88969758_21.jpg?alt=media&token=80d0d55f-6caa-4a43-bae6-9365ce6325d2',1,16),
    (68,'2025-09-08 10:07:02.054088','2025-09-08 10:07:02.054088','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F24%2Fimages%2F75aa0857-2bef-41b3-8683-added8c572c4_22.png?alt=media&token=adf8f09e-7628-4b93-865f-77633bd44ad4',2,16),
    (69,'2025-09-08 10:07:02.287661','2025-09-08 10:07:02.287661','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F24%2Fimages%2F6971e349-f078-4425-8167-4a80badd0d87_23.jpg?alt=media&token=0345ecfe-03c1-455c-b6cc-33c4954e5d79',3,16),
    (70,'2025-09-08 10:07:03.549974','2025-09-08 10:07:03.549974','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F24%2Fimages%2F70c4ac39-3a53-4ec7-8395-dc6adeb0137c_24.jpg?alt=media&token=15951685-2e91-4fe7-b2ad-3d1e411bd9b5',4,16),
    (71,'2025-09-08 10:07:04.452288','2025-09-08 10:07:04.452288','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F24%2Fimages%2F5fcf1bc9-84f0-4b6d-9341-5d06c9856a97_25.png?alt=media&token=9182ed99-b734-495f-83d2-8b7a0b99ef00',5,16),
    (72,'2025-09-08 10:16:35.246300','2025-09-08 10:16:35.246300','MAIN','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F7f09ac39-f1ff-4555-ba58-512f63fc9378_26.jpg?alt=media&token=12c29e46-2f19-45a5-82ed-60a2dc635e7f',0,17),
    (73,'2025-09-08 10:16:35.572392','2025-09-08 10:16:35.572392','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F25%2Fimages%2F0a808416-7028-4fa2-8e49-328e708440a0_27.jpg?alt=media&token=73831cae-9ba6-4a75-8ede-dd6aab47666b',1,17),
    (74,'2025-09-08 10:16:35.911489','2025-09-08 10:16:35.911489','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F25%2Fimages%2F5d62bea9-d819-4be8-89bd-459231187554_28.jpg?alt=media&token=4caf7149-2e28-4894-87b9-7aa262ff9045',2,17),
    (75,'2025-09-08 10:16:36.071442','2025-09-08 10:16:36.071442','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F25%2Fimages%2Fab35a0c0-bfff-4b81-9d5e-b8406f36c82d_29.jpg?alt=media&token=c37ccdb6-0a73-4485-a05a-fe1a160f7d23',3,17),
    (76,'2025-09-08 10:21:42.958494','2025-09-08 10:21:42.958494','MAIN','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F6e281112-7afd-4f85-b537-59d2712d2915_30.jpg?alt=media&token=07fc935f-1cdc-44aa-bb30-d7683329121c',0,18),
    (77,'2025-09-08 10:21:53.833938','2025-09-08 10:21:53.833938','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F26%2Fimages%2F081de921-e11e-4b11-b553-b0ab180c5e98_31.jpg?alt=media&token=020c296a-6e6c-40a8-ba24-1c357c1763b5',1,18),
    (78,'2025-09-08 10:21:54.070820','2025-09-08 10:21:54.070820','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F26%2Fimages%2Fd9b50d51-8caf-4a3c-b125-a98ec67f3112_32.jpg?alt=media&token=9bbbe651-9dc9-4662-8a5f-65738e793a13',2,18),
    (79,'2025-09-08 10:21:54.518977','2025-09-08 10:21:54.518977','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F26%2Fimages%2F45303941-eedf-495f-8c6a-02790b77ca1d_33.jpg?alt=media&token=4f09bfbd-06f9-45f3-946a-c06b388e42b5',3,18),
    (80,'2025-09-08 10:21:54.999743','2025-09-08 10:21:54.999743','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F26%2Fimages%2F888dedf2-f7a7-4590-94e3-32495f9c85e3_34.jpg?alt=media&token=1e57a93c-1d5d-42d7-8025-b8d741269d0c',4,18),
    (81,'2025-09-08 11:06:54.819014','2025-09-08 11:06:54.819014','MAIN','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F4d7bbb9c-bf25-4945-b8f4-7437be3a4b9e_35.jpg?alt=media&token=bbf5aa6d-f0f4-4901-883e-357f784e7360',0,19),
    (82,'2025-09-08 11:06:54.989846','2025-09-08 11:06:54.989846','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F19%2Fimages%2F706b0cf7-300d-4d97-86c2-8d6a9938517f_36.jpg?alt=media&token=c7f3c588-d2b7-4509-ab64-332580fad95c',1,19),
    (83,'2025-09-08 11:06:55.182894','2025-09-08 11:06:55.182894','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F19%2Fimages%2Fe9b56ec4-d8b4-4d79-bc78-89d57c1f140a_38.jpg?alt=media&token=481b89c7-a64a-4c82-a9cc-9acda4679043',2,19),
    (84,'2025-09-08 11:06:55.312848','2025-09-08 11:06:55.312848','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F19%2Fimages%2F7337898f-5f66-455f-acea-384f3fb1ee14_39.jpg?alt=media&token=3895e041-3164-4e39-a8e7-d7b9357499e0',3,19),
    (85,'2025-09-08 11:06:55.459499','2025-09-08 11:06:55.459499','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F19%2Fimages%2F750fca81-4c4d-4ef9-acdb-84087cd430e0_40.jpg?alt=media&token=1f721a58-5316-43da-8ba4-8eaed9280503',4,19),
    (86,'2025-09-08 11:13:19.936230','2025-09-08 11:13:19.936230','MAIN','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2Fe6a85cbf-2f7a-45a1-868a-600c830e554e_41.jpg?alt=media&token=35a1d895-f12b-4236-b7f0-b5454a076e3b',0,20),
    (87,'2025-09-08 11:13:20.117418','2025-09-08 11:13:20.117418','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F20%2Fimages%2F5cb64c80-4720-4f12-89ca-424347d83855_42.jpg?alt=media&token=1888c6e6-b60f-4f51-933b-e98847ff46cc',1,20),
    (88,'2025-09-08 11:13:20.345767','2025-09-08 11:13:20.345767','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F20%2Fimages%2Fcd20e01a-92c7-4373-9ada-b3c21dc9d1a7_43.jpg?alt=media&token=6ae56ef3-72b5-46a5-b1a6-1d032085649f',2,20),
    (89,'2025-09-08 11:13:20.551730','2025-09-08 11:13:20.551730','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F20%2Fimages%2F2ccb33cd-ffdc-4bee-a9d2-e3398ca253ca_44.jpg?alt=media&token=c5a45b5a-56ec-432d-8b3d-765180660dc6',3,20),
    (90,'2025-09-08 11:13:20.756640','2025-09-08 11:13:20.756640','SUB','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/accommodations%2F20%2Fimages%2F6f24ba05-5406-4d72-bda1-36191cf7529d_45.jpg?alt=media&token=01ee6e0b-47ce-418b-8d13-b859079fb6a3',4,20);


-- 1. 경주 오소한옥스테이
INSERT INTO accommodation_tag_map (accommodation_id, tag_id) VALUES
                                                                 (1, 23), -- 한옥스타일
                                                                 (1, 110), -- 한옥
                                                                 (1, 1), -- 감성숙소
                                                                 (1, 3), -- 가족여행숙소
                                                                 (1, 83), -- 무료주차
                                                                 (1, 49), -- 금연객실
                                                                 (1, 119); -- 관광지근처

-- 2. 뉴시스 해운대 레지던스
INSERT INTO accommodation_tag_map (accommodation_id, tag_id) VALUES
                                                                 (2, 107), -- 호텔
                                                                 (2, 42), -- 취사도구
                                                                 (2, 41), -- 전자레인지
                                                                 (2, 40), -- 냉장고
                                                                 (2, 16), -- 오션뷰
                                                                 (2, 50), -- 수영장
                                                                 (2, 112), -- 해변근처
                                                                 (2, 119); -- 관광지근처

-- 3. 라마다 앙코르 바이 윈덤 부산역
INSERT INTO accommodation_tag_map (accommodation_id, tag_id) VALUES
                                                                 (3, 102), -- 4성급
                                                                 (3, 107), -- 호텔
                                                                 (3, 115), -- 역세권
                                                                 (3, 114), -- 도심
                                                                 (3, 49), -- 금연객실
                                                                 (3, 3); -- 가족여행숙소

-- 4. 속초 호텔 더 블루테라
INSERT INTO accommodation_tag_map (accommodation_id, tag_id) VALUES
                                                                 (4, 107), -- 호텔
                                                                 (4, 16), -- 오션뷰
                                                                 (4, 13), -- 해돋이명소
                                                                 (4, 14), -- 야경좋은
                                                                 (4, 112), -- 해변근처
                                                                 (4, 119); -- 관광지근처

-- 5. 전주 소소한 한옥
INSERT INTO accommodation_tag_map (accommodation_id, tag_id) VALUES
                                                                 (5, 23), -- 한옥스타일
                                                                 (5, 110), -- 한옥
                                                                 (5, 1), -- 감성숙소
                                                                 (5, 3), -- 가족여행숙소
                                                                 (5, 106), -- 게스트하우스
                                                                 (5, 119); -- 관광지근처

-- 6. 수원 구운동 앤드 호텔
INSERT INTO accommodation_tag_map (accommodation_id, tag_id) VALUES
                                                                 (6, 108), -- 모텔
                                                                 (6, 83), -- 무료주차
                                                                 (6, 87), -- 당일예약 가능
                                                                 (6, 96); -- 실시간예약

-- 7. 신라스테이 전주 한옥마을
INSERT INTO accommodation_tag_map (accommodation_id, tag_id) VALUES
                                                                 (7, 102), -- 4성급
                                                                 (7, 107), -- 호텔
                                                                 (7, 119), -- 관광지근처
                                                                 (7, 1), -- 감성숙소
                                                                 (7, 83); -- 무료주차

-- 8. 대전 아늑 호텔 앤 스파
INSERT INTO accommodation_tag_map (accommodation_id, tag_id) VALUES
                                                                 (8, 108), -- 모텔
                                                                 (8, 27), -- 스파/월풀
                                                                 (8, 28), -- 객실스파
                                                                 (8, 30), -- 무선인터넷
                                                                 (8, 83); -- 무료주차

-- 9. 코오롱 씨클라우드 호텔
INSERT INTO accommodation_tag_map (accommodation_id, tag_id) VALUES
                                                                 (9, 107), -- 호텔
                                                                 (9, 16), -- 오션뷰
                                                                 (9, 112), -- 해변근처
                                                                 (9, 13), -- 해돋이명소
                                                                 (9, 14); -- 야경좋은

-- 10. 홈 마리나 속초
INSERT INTO accommodation_tag_map (accommodation_id, tag_id) VALUES
                                                                 (10, 107), -- 호텔
                                                                 (10, 16), -- 오션뷰
                                                                 (10, 13), -- 해돋이명소
                                                                 (10, 14), -- 야경좋은
                                                                 (10, 112); -- 해변근처

-- 11. 코델리아 호텔
INSERT INTO accommodation_tag_map (accommodation_id, tag_id) VALUES
                                                                 (11, 107), -- 호텔
                                                                 (11, 50), -- 수영장
                                                                 (11, 82), -- 조식제공
                                                                 (11, 112), -- 해변근처
                                                                 (11, 119); -- 관광지근처

-- 12. 강릉 더스테이 은파
INSERT INTO accommodation_tag_map (accommodation_id, tag_id) VALUES
                                                                 (12, 103), -- 풀빌라
                                                                 (12, 27), -- 스파/월풀
                                                                 (12, 28), -- 객실스파
                                                                 (12, 26), -- 자연친화적
                                                                 (12, 82); -- 조식제공

-- 13. 대구 하운드 호텔 혁신점
INSERT INTO accommodation_tag_map (accommodation_id, tag_id) VALUES
                                                                 (13, 107), -- 호텔
                                                                 (13, 82), -- 조식제공
                                                                 (13, 99), -- 청결우수
                                                                 (13, 83), -- 무료주차
                                                                 (13, 114); -- 도심

-- 14. 대구 메리어트 호텔
INSERT INTO accommodation_tag_map (accommodation_id, tag_id) VALUES
                                                                 (14, 101), -- 5성급
                                                                 (14, 107), -- 호텔
                                                                 (14, 55), -- 피트니스
                                                                 (14, 56), -- 레스토랑
                                                                 (14, 114); -- 도심

-- 15. 아바니 센트럴 부산
INSERT INTO accommodation_tag_map (accommodation_id, tag_id) VALUES
                                                                 (15, 101), -- 5성급
                                                                 (15, 107), -- 호텔
                                                                 (15, 16), -- 오션뷰
                                                                 (15, 55), -- 피트니스
                                                                 (15, 56); -- 레스토랑

-- 16. 노보텔 앰배서더 동대문
INSERT INTO accommodation_tag_map (accommodation_id, tag_id) VALUES
                                                                 (16, 101), -- 5성급
                                                                 (16, 107), -- 호텔
                                                                 (16, 56), -- 레스토랑
                                                                 (16, 55), -- 피트니스
                                                                 (16, 114); -- 도심

-- 17. 효이스테이 더 광안
INSERT INTO accommodation_tag_map (accommodation_id, tag_id) VALUES
                                                                 (17, 107), -- 호텔
                                                                 (17, 16), -- 오션뷰
                                                                 (17, 112), -- 해변근처
                                                                 (17, 125); -- 바닷가 산책로

-- 18. 그랜드 조선 제주
INSERT INTO accommodation_tag_map (accommodation_id, tag_id) VALUES
                                                                 (18, 101), -- 5성급
                                                                 (18, 107), -- 호텔
                                                                 (18, 50), -- 수영장
                                                                 (18, 55), -- 피트니스
                                                                 (18, 82); -- 조식제공

-- 19. 양양 더 앤 리조트 호텔&스파
INSERT INTO accommodation_tag_map (accommodation_id, tag_id) VALUES
                                                                 (19, 109), -- 리조트
                                                                 (19, 27), -- 스파/월풀
                                                                 (19, 50), -- 수영장
                                                                 (19, 26), -- 자연친화적
                                                                 (19, 82); -- 조식제공

-- 20. 나인트리 프리미어 로카우스 서울 용산
INSERT INTO accommodation_tag_map (accommodation_id, tag_id) VALUES
                                                                 (20, 102), -- 4성급
                                                                 (20, 107), -- 호텔
                                                                 (20, 115), -- 역세권
                                                                 (20, 114), -- 도심
                                                                 (20, 82); -- 조식제공







INSERT INTO room (
    room_id, created_at, updated_at, amenities, bed_info,
    description, extra_person_fee, max_occupancy, name, price,
    standard_occupancy, status, total_rooms, type, accommodation_id
) VALUES
-- 1
(1,'2025-08-28 01:09:09.395665','2025-09-02 23:15:54.309458',
 JSON_ARRAY('- 객실','로비','프런트 이용안내 -\r\n\r\n*객실 내 1','2인 욕조','드라이기/고데기','75인치/65인치 TV','노하드 컴퓨터','넷플릭스 시청생수3 / 샴푸','바디워시','린스','비누 / 일회용 남여 스킨','로션 비치\r\n\r\n*일회용품(칫솔','치약','면도기 등 )은 프런트에서 구매가능합니다. (1','000원)\r\n\r\n*디럭스2type','프리미엄 3type','스위트 2type','스위트 트윈 2type 객실사진과 다를수 있습니다.type내에서 랜덤 배정입니다.'),
 JSON_ARRAY('퀸침대 1개'),
 '**전객실 에어컨 청소완료**\n쿠폰 수량이 남아있을 경우 예약페이지에서 사용이 가능합니다.\n\n즉시할인쿠폰은 1일 1회만 사용 가능합니다.\n\n쿠폰 생성 수량에 따라 조기 마감될 수 있습니다.\n\n본 혜택은 제휴점 및 여기어때 사정에 의해 변경/중지될 수 있습니다.\n\n방문 결제 및 비회원 예약은 대상에서 제외됩니다.',
 10000.00,2,'럭셔리커플PC75TV최고사양커플PC고급PC방의자2인욕조스타일러',61000.00,2,'ACTIVE',4,'Standard',6),

-- 2
(2,'2025-08-28 10:09:44.553373','2025-09-02 23:19:15.943681',
 JSON_ARRAY('객실','로비','프런트 이용안내 -\r\n\r\n*객실 내 1','2인 욕조','드라이기/고데기','75인치/65인치 TV','노하드 컴퓨터','넷플릭스 시청생수3 / 샴푸','바디워시','린스','비누 / 일회용 남여 스킨','로션 비치\r\n\r\n*일회용품(칫솔','치약','면도기 등 )은 프런트에서 구매가능합니다. (1','000원)\r\n\r\n*디럭스2type','프리미엄 3type','스위트 2type','스위트 트윈 2type 객실사진과 다를수 있습니다.type내에서 랜덤 배정입니다.'),
 JSON_ARRAY('퀸침대1개, 2인욕조'),
 '쿠폰 수량이 남아있을 경우 예약페이지에서 사용이 가능합니다.\n\n즉시할인쿠폰은 1일 1회만 사용 가능합니다.\n\n쿠폰 생성 수량에 따라 조기 마감될 수 있습니다.\n\n본 혜택은 제휴점 및 여기어때 사정에 의해 변경/중지될 수 있습니다.\n\n방문 결제 및 비회원 예약은 대상에서 제외됩니다.',
 10000.00,3,'럭셔리 사우나 빠른 입실 (75TV, 사우나, 2인욕조, 스타일러)',121000.00,2,'ACTIVE',3,'Deluxe',6),

-- 3
(3,'2025-09-02 00:55:19.415288','2025-09-02 23:52:37.512565',
 JSON_ARRAY('TV','소파','금고','업무용 책상','에어컨','미니 냉장고','무료 생수','커피','티','전기포트','욕실용품','샤워가운','헤어드라이기','비데'),
 JSON_ARRAY('더블침대 1개','객실+욕실 / 6.52평'),
 '22시 이후 체크인 시 호텔 프론트 문의\n비대면 키오스크 (Kiosk) 체크인 가능\n무료 Wi-Fi\n전 객실 금연\n일회용품 무료 제공 불가(칫솔, 치약, 면도기 등)\n주차 공간 협소, 선착순 주차 가능 (만차 시 근처 공영주차장 유료 이용)',
 10000.00,3,'프리미엄 더블',115000.00,2,'ACTIVE',10,'Standard',3),

-- 4
(4,'2025-09-02 01:04:04.647458','2025-09-02 23:52:25.113049',
 JSON_ARRAY('TV','소파','금고','업무용 책상','에어컨','미니 냉장고','무료 생수','커피','티','전기포트','욕실용품','샤워가운','헤어드라이기','비데','욕조'),
 JSON_ARRAY('싱글침대 3개','객실+욕실 / 8.5평'),
 '22시 이후 체크인 시 호텔 프론트 문의,\n비대면 키오스크 (Kiosk) 체크인 가능,\n무료 Wi-Fi,\n전 객실 금연,\n일회용품 무료 제공 불가(칫솔, 치약, 면도기 등),\n주차 공간 협소, 선착순 주차 가능 (만차 시 근처 공영주차장 유료 이용),',
 10000.00,3,'스튜디오 트리플',203000.00,2,'ACTIVE',2,'Suite',3),

-- 5
(5,'2025-09-02 01:44:43.875309','2025-09-02 23:49:33.007838',
 JSON_ARRAY('TV','에어컨','세탁기','테라스','옷장','냉장고','헤어드라이기','욕실용품','비데','샤워가운','일회용슬리퍼','생수2개'),
 JSON_ARRAY('싱글침대 1개','더블침대 1개  객실+욕실 / 7평'),
 '이른 체크인과 늦은 체크아웃의 경우 시간당 15,000원의 추가비용이 발생/얼리체크인은 입실 가능한 객실이 있을 경우 가능(최대 2시간)\n체크인 / 체크아웃 : L층 로비 프론트\n프런트 24시간 운영\n무료 Wi-Fi\n전 객실 금연\nBath Amenity (치약, 칫솔 무료)\n수건, 타월, 생수 등 추가 요금 발생\n주차 가능 (객실당 1대 무료) / 내부 주차장 만차 시 건물 바로 옆 야외 주차장 이용가능',
 10000.00,3,'[시티뷰]스탠다드 트윈',78500.00,2,'ACTIVE',6,'Standard',4),

-- 6
(6,'2025-09-02 02:03:04.790746','2025-09-02 23:49:26.990539',
 JSON_ARRAY('TV','에어컨','세탁기','옷장','냉장고','화장대','좌식접이식 테이블','헤어드라이기','욕실용품','비데','일회용슬리퍼','생수4개'),
 JSON_ARRAY('인원 추가시 비용이 발생되며 현장에서 결제 바랍니다.  더블침대 1개','침구세트 2개  객실+욕실+테라스 / 8평'),
 '이른 체크인과 늦은 체크아웃의 경우 시간당 15,000원의 추가비용이 발생/얼리체크인은 입실 가능한 객실이 있을 경우 가능(최대 2시간)\n\n체크인 / 체크아웃 : L층 로비 프론트\n\n프런트 24시간 운영\n\n무료 Wi-Fi\n\n전 객실 금연\n\nBath Amenity (치약, 칫솔 무료)\n수건, 타월, 생수 등 추가 요금 발생\n주차 가능 (객실당 1대 무료) / 내부 주차장 만차 시 건물 바로 옆 야외 주차장 이용가능',
 10000.00,5,'[시티뷰] 디럭스 하프 온돌',102000.00,4,'ACTIVE',6,'Deluxe',4),

-- 7
(7,'2025-09-02 02:05:55.121145','2025-09-02 23:49:20.439155',
 JSON_ARRAY('TV','에어컨','세탁기','테라스','옷장','냉장고','아일랜드식탁','소파','헤어드라이기','욕실용품','비데','샤워가운','일회용슬리퍼','생수4개'),
 JSON_ARRAY('더블침대 2개,객실+욕실 / 8평'),
 '이른 체크인과 늦은 체크아웃의 경우 시간당 15,000원의 추가비용이 발생/얼리체크인은 입실 가능한 객실이 있을 경우 가능(최대 2시간)\n\n체크인 / 체크아웃 : L층 로비 프론트\n\n프런트 24시간 운영\n\n무료 Wi-Fi\n\n전 객실 금연\n\nBath Amenity (치약, 칫솔 무료)\n\n수건, 타월, 생수 등 추가 요금 발생\n\n주차 가능 (객실당 1대 무료) / 내부 주차장 만차 시 건물 바로 옆 야외 주차장 이용가능',
 10000.00,6,'[오션뷰] 그랜드 오션 스위트',188100.00,4,'ACTIVE',6,'Suite',4),

-- 8
(8,'2025-09-02 13:53:43.439491','2025-09-02 23:49:05.524091',
 JSON_ARRAY('TV','에어컨','티테이블','옷장','냉장고','인덕션','전자레인지','정수기','커피포트','커피머신','커피캡슐','식기','조리도구','드라이기','욕실용품'),
 JSON_ARRAY('퀸침대 1개  객실+주방+욕실 / 8평'),
 '객실 정비 3박 당 1회 무료 제공 / 별도 요청 시 비용 발생 (하루 전 신청 필수)\n\n쓰레기 배출 안내 : 음식물 쓰레기 (지상 1층 후문) / 재활용품 분리수거 (지하 1층) / 일반쓰레기 (종량제 봉투 구입 후 지하 1층)\n\n종량제 봉투는 프론트에서 구입 가능합니다 ( 10L : 450원 / 20L : 890원)',
 10000.00,3,'#허니레스트단독# 디럭스 더블 시티',120300.00,2,'ACTIVE',10,'Deluxe',2),

-- 9
(9,'2025-09-02 13:56:25.557085','2025-09-02 23:48:57.308830',
 JSON_ARRAY('TV','에어컨','티테이블','옷장','냉장고','인덕션','전자레인지','정수기','커피포트','커피머신','커피캡슐','식기','조리도구','드라이기','욕실용품'),
 JSON_ARRAY('퀸침대 1개  객실+주방+욕실 / 8평'),
 '인피니티풀 수영장 2인\n커뮤니티 시설 (수영장, 피트니스, 사우나) 이용 가능',
 10000.00,2,'[인피니티풀 수영장 2인] 디럭스 더블 시티',221700.00,2,'ACTIVE',8,'Suite',2),

-- 10
(10,'2025-09-02 13:59:17.388289','2025-09-02 23:48:45.453703',
 JSON_ARRAY('TV','에어컨','티테이블','옷장','냉장고','인덕션','전자레인지','정수기','커피포트','커피머신','커피캡슐','식기','조리도구','드라이기','욕실용품'),
 JSON_ARRAY('싱글침대 1개','퀸침대 1개  객실+주방+욕실 / 10평'),
 '3인 기준 최대 3인\n객실 정비 3박 당 1회 무료 제공 / 별도 요청 시 비용 발생 (하루 전 신청 필수)\n\n쓰레기 배출 안내 : 음식물 쓰레기 (지상 1층 후문) / 재활용품 분리수거 (지하 1층) / 일반쓰레기 (종량제 봉투 구입 후 지하 1층)\n\n종량제 봉투는 프론트에서 구입 가능합니다 ( 10L : 450원 / 20L : 890원)',
 10000.00,3,'[단독특가] 디럭스 패밀리 트윈 시티',172600.00,3,'ACTIVE',10,'Deluxe',2),

-- 11
(11,'2025-09-02 23:24:00.157324','2025-09-02 23:48:36.730699',
 JSON_ARRAY('TV','쇼파','간이테이블','커피포트','탁상시계','블루투스스피커','옷걸이','빨래건조대','간이소화기','에어컨','냉장고','드라이기','욕실용품','샤워가운','비데'),
 JSON_ARRAY('인원 추가시 비용이 발생되며','현장에서 결제 바랍니다.  퀸침대 1개  객실+욕실 / 16평'),
 '야외 수영장 / 야외 / 12:00~21:00 (7월~8월 말 운영) / 숙박 시 수영장 입장료 무료, 바디 타올 및 가운 유료 제공, 구명조끼 유료 대여, 음료 및 다과 별도 판매\n\n자쿠지 요금 별도 (사전문의) / 실내 (객실 분리) / 15:00~21:30 / 바디 타올 및 가운, 블루투스 스피커 무료 대여\n\n스탠다드 자쿠지 5만원, 스페셜 자쿠지 6만원 추가 요금 발생\n\n오픈 주방 / 지상 1층 / 15:00~21:30 / 4인 전용룸 2개, 6인 전용룸 4개 구비 / 개수대, 싱크대, 냉장고, 조리도구, 인덕션, 식탁 개별 이용 / 자이글 대여 10,000원 / 선착순 제공\n\n카페 / 로비층 / 15:00~22:00 / 커피 및 음료 판매\n\n건식사우나 / 지상 1층 / 15:00~24:00 / 5,000원 / 미온방 1개, 저온방 1개, 고온방 1개, 냉방 1개',
 10000.00,4,'별',240000.00,2,'ACTIVE',5,'Standard',1),

-- 12
(12,'2025-09-02 23:26:24.709147','2025-09-02 23:48:27.006739',
 JSON_ARRAY('TV','쇼파','간이테이블','커피포트','탁상시계','블루투스스피커','옷걸이','빨래건조대','간이소화기','에어컨','냉장고','드라이기','욕실용품','샤워가운','비데'),
 JSON_ARRAY('인원 추가시 비용이 발생되며','현장에서 결제 바랍니다.  퀸침대 1개  객실+욕실 / 16평'),
 '오픈 주방 무료 이용 가능\n\n야외 수영장 입장 무료\n\n조식, 웰컴티 무료 제공\n\n자전거 및 전동 킥보드 무료 대여\n\n짐 보관 가능\n\n전 객실 넷플릭스 이용 가능 (개인 계정)',
 15000.00,4,'봄',240000.00,2,'ACTIVE',5,'Deluxe',1),

-- 13
(13,'2025-09-02 23:29:12.385058','2025-09-02 23:48:21.529265',
 JSON_ARRAY('TV','쇼파','간이테이블','커피포트','탁상시계','블루투스스피커','옷걸이','빨래건조대','간이소화기','에어컨','냉장고','드라이기','욕실용품','비데'),
 JSON_ARRAY('인원 추가시 비용이 발생되며','현장에서 결제 바랍니다.  퀸침대 1개','킹침대 2개  객실2+거실+욕실 / 27평'),
 '오픈 주방 무료 이용 가능\n\n야외 수영장 입장 무료\n\n조식, 웰컴티 무료 제공\n\n자전거 및 전동 킥보드 무료 대여\n\n짐 보관 가능\n\n전 객실 넷플릭스 이용 가능 (개인 계정)\n\n부대시설 정보\n야외 수영장 / 야외 / 12:00~21:00 (7월~8월 말 운영) / 숙박 시 수영장 입장료 무료, 바디 타올 및 가운 유료 제공, 구명조끼 유료 대여, 음료 및 다과 별도 판매\n\n자쿠지 요금 별도 (사전문의) / 실내 (객실 분리) / 15:00~21:30 / 바디 타올 및 가운, 블루투스 스피커 무료 대여\n\n스탠다드 자쿠지 5만원, 스페셜 자쿠지 6만원 추가 요금 발생\n\n오픈 주방 / 지상 1층 / 15:00~21:30 / 4인 전용룸 2개, 6인 전용룸 4개 구비 / 개수대, 싱크대, 냉장고, 조리도구, 인덕션, 식탁 개별 이용 / 자이글 대여 10,000원 / 선착순 제공\n\n카페 / 로비층 / 15:00~22:00 / 커피 및 음료 판매\n\n건식사우나 / 지상 1층 / 15:00~24:00 / 5,000원 / 미온방 1개, 저온방 1개, 고온방 1개, 냉방 1개',
 15000.00,6,'하늘',440000.00,4,'ACTIVE',5,'Suite',1),

-- 14
(14,'2025-09-08 11:27:01.340466','2025-09-08 11:27:56.641501',
 JSON_ARRAY('50인치 TV','개인금고','전기포트','옷걸이','에어컨','냉장고','드라이기','욕실용품','슬리퍼','비데','소화기'),
 JSON_ARRAY('더블침대 1개  객실+욕실 / 8.5평'),
 '<추가패키지>\n조식 2인 제공\n피트니스 룸, 비즈니스 룸 무료 이용 가능 (출력 장당 200원 / 흑백만 가능)\n\n짐 보관 가능',
 15000.00,2,'[조식 2인 패키지] 스탠다드 더블(한강뷰)',290600.00,2,'ACTIVE',10,'Standard',20),

-- 15
(15,'2025-09-08 11:30:48.656165','2025-09-08 11:30:48.656165',
 JSON_ARRAY('50인치 및 55인치 TV','스타일러','개인금고','알람시계(블루투스 스피커)','캡슐 커피머신','전기포트','옷걸이','에어컨','냉장고','드라이기','욕실용품','슬리퍼','비데','소화기'),
 JSON_ARRAY('킹침대 1개  객실+거실+욕실 / 17.6평'),
 '조식 / 4층 아페티 / 주중 07:00~10:00 (라스트입장 09:30) / 주말 1부 07:00~08:30, 주말 2부 08:50~10:20 (라스트입장 09:50)\n\n티 브레이크 / 10층 / 14:00~16:30\n\n해피아워 / 10층 / 17:30~20:30\n\n티 브레이크와 해피아워는 체크인 당일에만 이용 가능합니다\n\n미성년자(자녀 동반) 입장이 가능합니다',
 15000.00,2,'레지던스 스위트',428000.00,2,'ACTIVE',10,'Suite',20),

-- 16
(16,'2025-09-08 11:33:15.620641','2025-09-08 11:33:15.620641',
 JSON_ARRAY('50인치 및 55인치 TV','스타일러','개인금고','알람시계(블루투스 스피커)','캡슐 커피머신','전기포트','옷걸이','에어컨','냉장고','드라이기','욕실용품','슬리퍼','비데','소화기'),
 JSON_ARRAY('싱글침대 2개','킹침대 1개  객실2+욕실 / 17.6평'),
 '피트니스 룸, 비즈니스 룸 무료 이용 가능 (출력 장당 200원 / 흑백만 가능)\n\n짐 보관 가능\n\nEXECUTIVE FLOOR LOUNGE 이용안내 (EFL 패키지 예약 고객 한정)\n조식 / 4층 아페티 / 주중 07:00~10:00 (라스트입장 09:30) / 주말 1부 07:00~08:30, 주말 2부 08:50~10:20 (라스트입장 09:50)\n\n티 브레이크 / 10층 / 14:00~16:30\n\n해피아워 / 10층 / 17:30~20:30\n\n티 브레이크와 해피아워는 체크인 당일에만 이용 가능합니다\n\n미성년자(자녀 동반) 입장이 가능합니다',
 15000.00,4,'레지던스 스위트 쿼드룸',478000.00,4,'ACTIVE',10,'Suite',20),

-- 17
(17,'2025-09-08 11:39:11.089282','2025-09-08 11:39:11.089282',
 JSON_ARRAY('TV','에어컨','의자','티테이블','옷장','냉장고','커피포트','드라이기','욕실용품','샤워가운','슬리퍼'),
 JSON_ARRAY('퀸침대 1개  객실+욕실 / 8평'),
 '스탠다드 더블 시네마, 스탠다드 더블 모션 시네마 객실 : 창문이 없는 시네마 특화 객실 입니다\n<인원 추가 정보>\n전 객실 인원추가 불가\n\n침구 및 침대 추가 불가\n\n영유아 인원수 포함 / 최대 인원 초과 불가\n\n현장 결제\n<혜택>\n짐 보관 가능\n\n피트니스센터 무료 (생수, 수건 무료 제공)',
 10000.00,2,'#굿초이스# 스탠다드 더블',113200.00,2,'ACTIVE',10,'Standard',7),

-- 18
(18,'2025-09-08 11:42:11.157441','2025-09-08 11:42:11.157441',
 JSON_ARRAY('토퍼 매트리스1','TV','에어컨','의자','티테이블','옷장','냉장고','커피포트','드라이기','욕실용품','샤워가운','슬리퍼'),
 JSON_ARRAY('싱글침대 1개','퀸침대 1개  객실+욕실 / 8평'),
 '<조식 정보>\n레스토랑 뷔페 / 1층 / 07:00~10:00\n\n1인 30,000원 (14세 이상~성인), 20,000원 (37개월 이상~13세 이하), 37개월 미만 무료\n\n현장 결제\n\n<취사 시설>\n전 객실 취사 불가',
 10000.00,3,'[룸온리] 스탠다드 패밀리 트윈',167200.00,2,'ACTIVE',10,'Standard',7),

-- 19
(19,'2025-09-08 11:43:53.775005','2025-09-08 11:44:28.719063',
 JSON_ARRAY('토퍼 매트리스1','TV','에어컨','의자','티테이블','옷장','냉장고','커피포트','드라이기','욕실용품','샤워가운','슬리퍼'),
 JSON_ARRAY('퀸침대 1개','침구세트 1개  객실+욕실 / 8평'),
 '<인원 추가>\n전 객실 인원추가 불가\n\n침구 및 침대 추가 불가\n\n영유아 인원수 포함 / 최대 인원 초과 불가\n\n현장 결제\n\n<투숙객 혜택>\n짐 보관 가능\n\n피트니스센터 무료 (생수, 수건 무료 제공)\n\n<부대시설 정보>\n레스토랑 / 1층\n\n카페 / 1층 / 07:00~10:00\n\n루프탑 / 11층 / 16:00~24:00 (라스트 오더 23:30)\n\n비즈니스 코너 / 1층 / 00:00~24:00 / 노트북 2대, 프린터 1대 (프린트 유료)\n\n피트니스 센터 / 2층 / 06:00~23:00 / 운동화, 운동복 개인 지참\n\n코인 세탁실 / 2층 / 00:00~24:00 / 세탁세제, 섬유유연제 별도 구매',
 10000.00,3,'[룸온리] 스탠다드 온돌',167200.00,2,'ACTIVE',10,'Standard',7),

-- 20
(20,'2025-09-08 11:46:52.570997','2025-09-08 11:46:52.570997',
 JSON_ARRAY('침구류','TV','에어컨','식탁','냉장고','드라이기','세면도구','욕실용품'),
 JSON_ARRAY('2인 기준 최대 2인  객실+욕실 / 10평'),
 '입실 : 15:00 | 퇴실 : 11:00\n\n22시 이후 입실 시 사전문의 (필수)\n\n무료 Wi-Fi\n\n전 객실 금연\n\n주차 가능 / 무료\n\n픽업 불가\n\n<객실 정보>\n스파 : 무료 / 202, 203, 204, 302, 303, 305 객실 이용 가능 / 입실 시~21:00\n\n자쿠지 욕조 : 무료 / 207, 301 객실 이용 가능 / 입실 시~21:00\n\n취사 가능 객실 : 307, 401\n\n전 객실 : OTT 이용 가능',
 15000.00,2,'The 206',113800.00,2,'ACTIVE',6,'Standard',12),

-- 21
(21,'2025-09-08 12:39:52.942655','2025-09-08 12:40:49.369660',
 JSON_ARRAY('침구류','TV','에어컨','식탁','냉장고','전자레인지','취사도구','드라이기','세면도구','욕실용품'),
 JSON_ARRAY('2인 기준 최대 2인  객실+욕실 / 10평'),
 '가스버너+고기 구이판 : 무료\n\n이용시간 : 17:00~22:00\n\n공용 바비큐장 구비 / 바비큐 가능\n\n우천, 동절기 이용 가능\n\n사전문의 및 예약 필수\n\n현장 결제\n====\n스파 : 무료 / 202, 203, 204, 302, 303, 305 객실 이용 가능 / 입실 시~21:00\n\n자쿠지 욕조 : 무료 / 207, 301 객실 이용 가능 / 입실 시~21:00\n\n취사 가능 객실 : 307, 401\n\n전 객실 : OTT 이용 가능',
 10000.00,2,'The 201 (Half Ocean View)',133400.00,2,'ACTIVE',10,'Standard',12);

INSERT INTO room (
    room_id, created_at, updated_at, amenities, bed_info, description,
    extra_person_fee, max_occupancy, name, price, standard_occupancy,
    status, total_rooms, type, accommodation_id
) VALUES
      (22,'2025-09-08 12:54:56.653915','2025-09-08 12:55:49.546757',
       '["침구류","TV","에어컨","식탁","냉장고","핫플레이트","전자레인지","취사도구","드라이기","세면도구","욕실용품"]',
       '["인원 추가시 비용이 발생되며","현장에서 결제 바랍니다.  객실+욕실 / 20평"]',
       '스파 : 무료 / 202, 203, 204, 302, 303, 305 객실 이용 가능 / 입실 시~21:00

       자쿠지 욕조 : 무료 / 207, 301 객실 이용 가능 / 입실 시~21:00

       취사 가능 객실 : 307, 401

       전 객실 : OTT 이용 가능

       <인원 추가 정보>
       1인 20,000원 (13세 이하), 30,000원 (14세 이상)

       영유아 인원수 포함 / 최대인원 초과불가

       현장 결제',
       15000.00,4,'The 401 (Ocean View)',238600.00,2,'ACTIVE',8,'Suite',12),

      (23,'2025-09-08 12:59:40.700942','2025-09-08 13:12:22.375383',
       '["TV","에어컨","냉장고","생수","커피포트","드라이기","가운","욕실용품","샤워시설","욕조"]',
       '["킹침대 1개  객실+욕실 / 14.1평"]',
       '힐 스튜디오 스위트 가든뷰 객실: 1~2F, 오션뷰는 3~6F 배정 / 배정에 따라 전망의 차이가 있음

       키즈 디럭스 패밀리 트윈 가든뷰, 키즈 디럭스 패밀리 트윈 풀사이드뷰 : 해당 객실 예약 시 그랑 조이 유료 키즈 프로그램 1인 1회가 박당 제공됩니다 (사전 예약 필수)

       투숙 혜택
       사우나,수영장,체련장(피트니스),조선키즈클럽 입장 무료(단 객실별 기준인원 한정)

       식음업장 10%할인(주류, IRD제외)',
       15000.00,3,'디럭스 가든뷰 킹',372900.00,2,'ACTIVE',10,'Deluxe',18),

      (24,'2025-09-08 13:15:19.600841','2025-09-08 13:16:35.502762',
       '["TV","에어컨","냉장고","생수","커피포트","드라이기","가운","욕실용품","샤워시설","욕조"]',
       '["싱글침대 1개","더블침대 1개  객실+욕실 / 14.1평"]',
       '<추가정보>
       최대 3인 투숙 가능 (성인 2인+소인 1인 기준) , 키즈 프로그램 1회 포함 (박당 제공)
       ARIA/본관 1층

       LOUNGE&BAR/본관 1층/ 08:00~23:00

       EAT2O/본관 1층

       JOSUN DELI/본관 1층

       HEAVENLY LOUNGE/힐스위트관 6층/힐 스위트 타입 이상 투숙객 전용

       사우나, 피트니스, GX ROOM, 조선주니어 키즈클럽: 입실 전에는 오후 12시 이후부터, 퇴실 당일에는 오후 15시 이전까지 이용가능

       피트니스/본관 1층/운동복, 운동화 착용 필수/음주상태에서 입장 불가/17세 이상 이용가능

       GX ROOM/힐스위트관 3층/힐 스위트 타입 이상 투숙객 전용/운동복, 운동화 착용필수/음주상태에서 입장불가/17세 이상 이용가능

       PEAK POINT POOL(아웃도어 풀)/본관 루프탑/20세 이상 성인만 이용가능/2025년 1월 1일~3월 31일 미운영

       조선주니어 키즈클럽/본관 2층/안전요원이 상주하지 않습니다/부모님 또는 보호자(만 19세 이상) 1인 이상 동반입장 필수

       본관 사우나/본관 1층/17세 이상 이용가능

       힐스위트관 사우나/힐 스위트 타입 이상 투숙객 전용/17세 이상 이용 가능

       가든풀(인도어, 아웃도어 풀)/본관 1층

       헤븐리풀(인도어, 아웃도어 풀)/힐스위트관 6층/힐 스위트 타입 이상 투숙객 전용/20세 이상 성인만 이용가능

       [휴관일 안내]

       본관 가든풀 : 25년 11월 18일~11월 19일

       본관 사우나 : 25년 11월 18일

       피크포인트/풀 : 25년 11월 1일~26년 4월 30일

       힐스위트관 헤븐리풀 : 25년 10월 28일

       힐스위트관 사우나 : 매월 두번째 화요일 정기휴무

       부대시설 이용 관련 자세한 사항의 경우, 공식 홈페이지 참고 부탁 드립니다',
       15000.00,3,'키즈 디럭스 패밀리 트윈 가든뷰',393600.00,2,'ACTIVE',8,'Deluxe',18),

      (25,'2025-09-08 13:18:15.284812','2025-09-08 13:18:15.284812',
       '["TV","에어컨","냉장고","생수","커피포트","드라이기","가운","욕실용품","샤워시설","욕조"]',
       '["인원 추가시 비용이 발생되며","현장에서 결제 바랍니다.  킹침대 1개  객실+욕실 / 14.1평"]',
       '<추가패키지>
       조식 2인
       <조식 정보>
       ARIA / 본관 1층 / 07:00~10:30

       정상가: 성인 70,000원(13세 이상~성인) 소인 38,000원(37개월 이상~12세 이하), 36개월 이하 무료

       투숙객 할인가: 성인 63,000원(13세 이상~성인) 소인 34,200원(37개월 이상~12세 이하), 36개월 이하 무료

       (특정일 12/24~25, 12/30~31 및 이벤트 일에는 금액이 변동될 수 있음)

       현장 결제',
       15000.00,3,'[조식 2인] 디럭스 풀사이드뷰 킹',498800.00,2,'ACTIVE',10,'Deluxe',18),

      (26,'2025-09-08 14:08:04.835738','2025-09-08 14:08:04.835738',
       '["TV","옷장","금고","쇼파","에어컨","냉장고","미니바","커피포트","욕실용품","샤워가운","슬리퍼","헤어드라이기"]',
       '["더블침대 1개  객실+욕실 / 8.65평"]',
       '<숙소 이벤트>
       [일~목요일 투숙 고객 한정 특전]
       [부대시설]

       해당 업장: 인발란스(실내/실외 루프탑 수영장, 피트니스 센터) 및 키즈존 / 매월 첫 번째 화요일에 휴관(호텔 상황에 따라 부대시설의 운영 및 휴무일은 사전 고지없이 변경 가능)

       실내 수영장 / 07:00~21:30

       피트니스 센터 / 06:00~22:00 / 만 16세 이상 입장 가능

       키즈존 / 08:00~20:00

       실외 루프탑 수영장 / 만 19세 이상 입장 가능 / 기상 또는 호텔 상황에 따라 운영 여부 및 시간이 변경될 수 있습니다(동절기 미운영)

       -2025년 9월 월~목요일은 17시 50분까지 운영, 금~일요일 & 공휴일은 21시 30분까지 운영 / 9월 12일~9월 14일은 루프탑 와인 페어로 15시까지 운영

       -2025년 10월 1일은 12~14시는 대관 행사로 실외 수영장 이용이 불가합니다

       탈의실이 협소하오니, 탈의 및 샤워는 객실 이용을 권장 드립니다

       실내/실외 루프탑 수영장, 키즈존은 사전 예약제로 운영되며 1일 1회만 입장 가능합니다(실내 또는 실외 수영장 중 택 1 이용)

       수영장 이용 시 수영모 또는 캡모자 착용 필수

       수영장 내 튜브 및 물놀이 기구 반입 금지(구명 조끼,암튜브,넥튜브에 한하여 허용)

       36개월 미만 유아 방수 기저귀 필수 착용

       [레스토랑]

       더 델리(카페/1층),푸드 익스체인지(레스토랑/20층),고메바(캐주얼 다이닝 바/20층)',
       15000.00,2,'[Room only 특가] 슈페리어 더블',270200.00,2,'ACTIVE',10,'Deluxe',16),

      (27,'2025-09-08 14:18:39.147080','2025-09-08 14:18:39.147080',
       '["침대","소파","테이블","TV","냉장고"]',
       '["킹침대 1개  객실+욕실 / 13.37평"]',
       '<추가패키지>
       이그제큐티브 라운지 2인 제공

       위치: 19층

       조식 07:30~10:00

       티타임 12:00~16:00 중 한 시간

       해피아워 17:30~19:30

       운영은 호텔 상황에 따라 사전 고지없이 변경될 수 있습니다(시간, 2-3부제 운영, 장소 등)

       최적의 서비스 제공을 위해 조식과 티타임은 만 13세 미만, 해피아워는 만 19세 미만 고객의 출입을 제한하고 있습니다

       이그제큐티브 라운지 미포함 상품 이용 시 추가 비용이 발생되며, 호텔로 사전 문의 바랍니다
       <취사 시절>
       객실명: 레지던스 스튜디오 더블, 레지던스 디럭스 더블, 레지던스 디럭스 트리플, 레지던스 프리미어 1베드룸 (이 외 객실 취사 불가)

       객실 내 취사도구 및 식기류 및 세탁 시설 구비(인덕션, 밥솥, 전자레인지, 정수기, 취사도구, 세탁기 등)',
       15000.00,2,'[라운지 2인 패키지] 프리미어 스위트',514400.00,2,'ACTIVE',10,'Suite',16),

      (28,'2025-09-08 14:22:38.692406','2025-09-08 14:22:38.692406',
       '["TV","옷장","금고","에어컨","냉장고","미니바","커피포트","욕실용품","샤워가운","슬리퍼","헤어드라이기"]',
       '["싱글침대 1개","더블침대 1개  객실+욕실 / 8.65평"]',
       '<추가패키지>
       푸드익스체인지 조식 3인 제공

       16세 미만 소인 2인 무료 조식 제공
       <이그제큐티브 라운지 이용 안내>
       위치: 19층

       조식 07:30~10:00

       티타임 12:00~16:00 중 한 시간

       해피아워 17:30~19:30

       운영은 호텔 상황에 따라 사전 고지없이 변경될 수 있습니다(시간, 2-3부제 운영, 장소 등)

       최적의 서비스 제공을 위해 조식과 티타임은 만 13세 미만, 해피아워는 만 19세 미만 고객의 출입을 제한하고 있습니다

       이그제큐티브 라운지 미포함 상품 이용 시 추가 비용이 발생되며, 호텔로 사전 문의 바랍니다',
       15000.00,3,'[조식 3인 패키지] 슈페리어 패밀리 트윈',474400.00,3,'ACTIVE',10,'Suite',16),

      (29,'2025-09-08 14:25:14.509673','2025-09-08 14:25:14.509673',
       '["생수","TWG 티백","네스프레소 머신 (캡슐 커피 포함)","드라이기","욕실용품 (샴푸","린스","바디워시","바디로션","핸드소프)","커피포트","TV","코튼가운","슬리퍼"]',
       '["인원 추가시 비용이 발생되며","현장에서 결제 바랍니다.  킹침대 1개  객실+욕실 / 10평"]',
       '음료 제공/2층 라운지/24시간 운영/Grab & Go 커피, H2O Station 레몬&허브를 넣은 얼음물 2종류

       짐 보관 가능

       프린터 이용 가능

       전 객실 OTT 서비스 이용 가능 (개인 계정 필요)

       다리미 대여 가능

       전 객실 메트리스 시몬스사의 최고급 라인 뷰티레스트 사용

       부대시설 정보
       피트니스센터 / 지하 1층 / 07:00~22:00 / 무료 / 개인 운동화 및 운동복 지참 필수

       [인피니티풀 사계절 온수 수영장&자쿠지]

       2025년 운영 시간 : 매일 08:00~23:00

       홈 마리나 속초의 인피니티 풀 입장은 수영장 포함 패키지 구매 시에만 사용 가능합니다 (루프탑 바는 모든 투숙객 이용 가능)

       수영장 이용 시 체크아웃 시간 전까지 무제한 입장이 가능합니다

       수영장 이용 시 수영복 및 래시가드 착용 필수입니다

       16세 이하(중학생)는 20시 이후에는 입장이 제한됩니다

       안전상의 이유로 인피니티풀 가장자리에 앉는 행위 및 비치볼 & 튜브 사용이 제한됩니다

       제한 품목: 모든 유형의 튜브(보트, 베드), 핀(오리발), 스노클, 드론 등 (구명조끼 및 암튜브는 사용 가능)

       무료 대여 가능 품목 : 야외선베드, 바디타올, 구명조끼, 암링, 해먹튜브, 유아 물놀이 용품

       36개월 미만 유아는 방수 기저귀를 착용 후 이용 가능합니다

       [SKY 21 루프탑 라운지&펍]

       2025년 운영 시간 : 매일 08:00~23:00, 라스트 오더 22:30

       푸드 메뉴: 버팔로윙&감자튀김, 페퍼로니 또띠아 피자, 비프버거&감자튀김, 감자튀김, 치즈&미트 소스 나초, 황금먹태, 씨즐링 그릴 핫도그, 스카이21 해물라면, 핫바, 비엔나 소시지 등

       음료 메뉴: 커피, 맥주, 칵테일, 와인, 위스키 등

       [2025 DJ Party]

       20:00~23:00 / 전 연령 입장 가능 / 단, 20시 이후 Sky 21 인피니티 풀 입장은 17세 이상만 가능합니다

       9월 : 6일, 13일, 20일, 27일

       10월 : 4일~8일

       [Late - Night DJ Party]

       20:00~24:00 / 미성년자 입장 불가 / 미성년자는 해당 시간 내 21층 모든 구역 출입이 제한됩니다

       [Seorak Bistro 설악 비스트로]

       중식 및 석식 운영 시간 : 11:00~22:00 (라스트 오더 21:30) / 단품 (A la carte)

       [룸서비스]

       2025년 운영 시간 : 11:00~22:00 (라스트 오더 21:30)',
       15000.00,3,'[숙박페스타][룸온리 특가] 디럭스 킹베드 (전망없음)',130200.00,2,'ACTIVE',10,'Deluxe',10),

      (30,'2025-09-08 14:27:47.514734','2025-09-08 14:27:47.514734',
       '["생수","TWG 티백","네스프레소 머신 (캡슐 커피 포함)","드라이기","욕실용품 (샴푸","린스","바디워시","바디로션","핸드소프)","커피포트","TV","코튼가운","슬리퍼"]',
       '["인원 추가시 비용이 발생되며","현장에서 결제 바랍니다.  킹침대 1개  객실+욕실 / 10.6평"]',
       '기준: 성인 (12세 이상~성인), 소인 (37개월 이상~13세 이하), 36개월 이하 무료

       룸온리 객실 기준인원 외 추가 시, 1인 1박당 22,000원 (37개월 이상~성인)

       조식+수영장 패키지 추가 요금 : 1인 성인 70,000원, 소인 53,000원

       조식+수영장 패키지 객실 기준인원 외 추가 시, 1인 성인 88,000원, 소인 71,500원

       인원 추가 시 수건 및 어메니티만 추가 제공

       입실 시 현장 추가 및 결제

       <투숙객 혜택>
       음료 제공/2층 라운지/24시간 운영/Grab & Go 커피, H2O Station 레몬&허브를 넣은 얼음물 2종류

       짐 보관 가능

       프린터 이용 가능

       전 객실 OTT 서비스 이용 가능 (개인 계정 필요)

       다리미 대여 가능

       전 객실 메트리스 시몬스사의 최고급 라인 뷰티레스트 사용',
       15000.00,3,'[룸온리/수영장 미포함] 그랜드 디럭스 바다+호수 전망 킹베드',199200.00,2,'ACTIVE',10,'Deluxe',10),

      (31,'2025-09-08 14:29:59.616889','2025-09-08 14:29:59.616889',
       '["생수","TWG 티백","네스프레소 머신 (캡슐 커피 포함)","드라이기","욕실용품 (샴푸","린스","바디워시","바디로션","핸드소프)","커피포트","TV","코튼가운","슬리퍼"]',
       '["싱글침대 1개","퀸침대 1개  객실+욕실 / 10.6평"]',
       '<숙소 이벤트>
       [키즈 패밀리 퀸 & 싱글] 객실 이용 안내
       <추가패키지>
       조식 2인',
       15000.00,3,'조식 2인] 키즈 패밀리 퀸 & 싱글 (전망 없음)',223200.00,3,'ACTIVE',8,'Deluxe',10),

      (32,'2025-09-08 14:34:32.697800','2025-09-08 14:34:32.697800',
       '["TV","쇼파","커피포트","아이스버킷","티테이블","옷장","에어컨","냉장고","미니바","헤어드라이기","욕실용품","샤워가운","슬리퍼","다리미"]',
       '["더블침대 2개  객실+욕실 / 11평"]',
       '<추가정보>
       욕조 객실 추가 요금발생 및 호텔 유선확인필수
       <객실정보>
       [디럭스 더블, 디럭스 킹 객실 이용 안내]

       욕조가 있는 객실을 원하시면 호텔 대표번호 (T.053-327-7000)로 문의하시기 바랍니다

       욕조 객실 체크인 시 1박 기준 22,000원이 부과 됩니다',
       15000.00,33,'디럭스 더블',264000.00,2,'ACTIVE',10,'Deluxe',14),

      (33,'2025-09-08 14:36:28.667884','2025-09-08 14:36:28.667884',
       '["TV","쇼파","커피포트","아이스버킷","티테이블","옷장","에어컨","냉장고","미니바","헤어드라이기","욕실용품","샤워가운","슬리퍼","다리미"]',
       '["킹침대 1개  객실+욕실 / 11평"]',
       '[디럭스 더블, 디럭스 킹 객실 이용 안내]

       욕조가 있는 객실을 원하시면 호텔 대표번호 (T.053-327-7000)로 문의하시기 바랍니다

       욕조 객실 체크인 시 1박 기준 22,000원이 부과 됩니다
       <투숙객 혜택>
       비즈니스 센터, 피트니스 센터 무료 이용

       ',
       15000.00,3,'[2인 조식 패키지] 디럭스 킹',319000.00,2,'ACTIVE',10,'Deluxe',14),

      (34,'2025-09-08 14:39:26.928028','2025-09-08 14:39:26.928028',
       '["TV","쇼파","커피포트","아이스버킷","티테이블","옷장","에어컨","냉장고","미니바","헤어드라이기","욕실용품","샤워가운","슬리퍼","다리미"]',
       '["킹침대 1개  객실+욕실 / 11평"]',
       '<숙소 이벤트>
       예술과 자연, 역사와 미식이 어우러진 힐링 여행
       <추가패키지>
       조식 2인

       사유원 2인 관람권 + 몽몽다방 한식 (체크인 당일)

       간송 미술관 2인 티켓 + 도록 1권 (체크아웃 일 사용)

       묘운카페 맡김차림 & 음료 2인 & 달항아리 2개

       ',
       15000.00,3,'대구 아트 & 헤리티지 K여행] 디럭스 킹',649000.00,2,'ACTIVE',10,'Deluxe',14);


INSERT INTO room (
    room_id, created_at, updated_at, amenities, bed_info, description,
    extra_person_fee, max_occupancy, name, price, standard_occupancy,
    status, total_rooms, type, accommodation_id
) VALUES
      (35,'2025-09-08 14:49:00.296455','2025-09-08 14:49:00.296455',
       '["주차시설을 보유한 숙소입니다.(차량 이용 시 숙소에 문의해 주세요.)  객실은 체크인 시 배정되며 사진과 다를 수 있습니다.  만 19세 미만 청소년 혼숙은 법적으로 불가하며","이에 대한 숙소의 입실 거부 시 취소/환불이 불가합니다.  만 19세 미만 청소년 예약에 대한 숙소의 입실 거부 시 취소/환불이 불가하오니","예약 전 반드시 숙소에 확인하시기 바랍니다."]',
       '["킹베드1"]',
       '<즉시할인 쿠폰 안내>\n쿠폰 수량이 남아있을 경우 예약페이지에서 사용이 가능합니다.\n\n즉시할인쿠폰은 1일 1회만 사용 가능합니다.\n\n쿠폰 생성 수량에 따라 조기 마감될 수 있습니다.\n\n본 혜택은 제휴점 및 여기어때 사정에 의해 변경/중지될 수 있습니다.\n\n방문 결제 및 비회원 예약은 대상에서 제외됩니다.',
       15000.00,2,'디럭스',75000.00,2,'ACTIVE',10,'Deluxe',13),

      (36,'2025-09-08 14:50:36.647836','2025-09-08 14:50:36.647836',
       '["대구 혁신도시 하운드 조식 안내❣️  시간 | 07:30 ~ 10:00AM(월요일 조식 제공X)  메뉴 | 단품형 정찬조식","샐러드바  1객실 1인 제공으로 인원 추가 시","추가 요금 발생(현장 문의)  ❣️대구 혁신도시 하운드 헬스장 이용 안내❣️  시간 | 07:00 ~ 21:00PM  ✅24시간 캐리어 보관 가능"]',
       '["킹베드1","더블베드1"]',
       '객실 내부 시설\n✅아침형고객을 위한 조식제공\n\n✅선명한 화질 고화질 TV\n\n✅전객실 시트교체(필)/소독100%\n\n✅대형/중형/소형 주차장 완비\n\n✅전객실 와이파이 가능\n\n✅5성급 특급호텔 침구류 구비\n\n✅전 객실 스타일러/공기청정기/비데\n\n✅공조시스템 설치',
       15000.00,4,'로얄 스위트',113000.00,2,'ACTIVE',10,'Suite',13),

      (37,'2025-09-08 14:52:00.606762','2025-09-08 14:52:00.606762',
       '["추가 안내 사항 프론트에서 여기어때 회원임을 알려주세요.  각 객실의 입실 기준을 확인 부탁드립니다.  파티룸 등 특수객실의 경우 직접 입실 인원 확인이 필요합니다.  추가 인원 숙박 희망 시 업소로 문의 부탁드립니다.  성수기 및 특정일","공휴일에는 요금 변동이 있을 수 있습니다.  객실 호수 및 디자인은 업체 운영 상황에 따라 랜덤 배정됩니다.  입실 시에 신분증 확인하오니 양해 부탁드립니다.  미성년자는 입실이 제한됩니다.  (신분증을 꼭 지참해주시기 바랍니다.)  추가 인원 요금은 15","000원 입니다."]',
       '["더블베드2"]',
       '<즉시할인 쿠폰 안내>\n쿠폰 수량이 남아있을 경우 예약페이지에서 사용이 가능합니다.\n\n즉시할인쿠폰은 1일 1회만 사용 가능합니다.\n\n쿠폰 생성 수량에 따라 조기 마감될 수 있습니다.\n\n본 혜택은 제휴점 및 여기어때 사정에 의해 변경/중지될 수 있습니다.\n\n방문 결제 및 비회원 예약은 대상에서 제외됩니다.',
       15000.00,3,'프리미엄 트윈',80000.00,2,'ACTIVE',10,'Suite',13),

      (38,'2025-09-08 14:54:46.005159','2025-09-08 14:54:46.005159',
       '["공용 로비 라운지  1층 라면바  운영시간 : 16시~23시 / 오전시간 별도 운영하지 않음  1층 세탁시설"]',
       '["침구 및 침대 추가 불가"]',
       '<즉시할인 쿠폰 안내>\n쿠폰 수량이 남아있을 경우 예약페이지에서 사용이 가능합니다.\n\n즉시할인쿠폰은 1일 1회만 사용 가능합니다.\n\n쿠폰 생성 수량에 따라 조기 마감될 수 있습니다.\n\n본 혜택은 제휴점 및 여기어때 사정에 의해 변경/중지될 수 있습니다.\n\n방문 결제 및 비회원 예약은 대상에서 제외됩니다.',
       15000.00,2,'STANDARD',75000.00,2,'ACTIVE',10,'Standard',8),

      (39,'2025-09-08 14:56:57.244759','2025-09-08 14:56:57.244759',
       '["공용 로비 라운지  1층 라면바  운영시간 : 16시~23시 / 오전시간 별도 운영하지 않음  1층 세탁시설","짐 보관 가능  전 객실 OTT(넷플릭스,유튜브) 시청가능(개인계정사용)"]',
       '["더블베드1"]',
       '숙소 이용안내\n주차시설을 보유한 숙소입니다.(차량 이용 시 숙소에 문의해 주세요.)\n\n객실은 체크인 시 배정되며 사진과 다를 수 있습니다.\n\n만 19세 미만 청소년 혼숙은 법적으로 불가하며, 이에 대한 숙소의 입실 거부 시 취소/환불이 불가합니다.\n\n만 19세 미만 청소년 예약에 대한 숙소의 입실 거부 시 취소/환불이 불가하오니, 예약 전 반드시 숙소에 확인하시기 바랍니다.',
       15000.00,4,'SIGNATURE (SPA)',26000.00,2,'ACTIVE',10,'Deluxe',8),

      (40,'2025-09-08 14:56:57.299409','2025-09-08 14:58:03.249185',
       '["공용 로비 라운지  1층 라면바  운영시간 : 16시~23시 / 오전시간 별도 운영하지 않음  1층 세탁시설","짐 보관 가능  전 객실 OTT(넷플릭스,유튜브) 시청가능(개인계정사용)"]',
       '["더블베드1"]',
       '숙소 이용안내\n주차시설을 보유한 숙소입니다.(차량 이용 시 숙소에 문의해 주세요.)\n\n객실은 체크인 시 배정되며 사진과 다를 수 있습니다.\n\n만 19세 미만 청소년 혼숙은 법적으로 불가하며, 이에 대한 숙소의 입실 거부 시 취소/환불이 불가합니다.\n\n만 19세 미만 청소년 예약에 대한 숙소의 입실 거부 시 취소/환불이 불가하오니, 예약 전 반드시 숙소에 확인하시기 바랍니다.',
       15000.00,2,'BUSINESS',85000.00,2,'ACTIVE',10,'Suite',8),

      (41,'2025-09-08 15:00:04.133839','2025-09-08 15:00:04.133839',
       '["TV","금고","에어컨","냉장고","커피포트","욕실용품","샤워가운","슬리퍼","헤어드라이기"]',
       '["싱글침대 2개  객실+욕실 / 8평"]',
       '생수 2병 무료\n\n베이비 가드 선착순 대여 가능 (유료 / 한정적 수량 운영으로 유선 사용 예약 요청 이후 체크인 당일 사용 여부 확정 / 이용 시 안전사고로부터 아이를 지키기 위해 부모의 각별한 주의가 필요 하오니 유의하여 주시기 바랍니다)\n\n피트니스 무료 이용 (운동화 대여 불포함)',
       15000.00,2,'아바니 디럭스 트윈',169400.00,2,'ACTIVE',10,'Deluxe',15),

      (42,'2025-09-08 15:01:13.317610','2025-09-08 15:01:13.317610',
       '["TV","금고","쇼파","다리미","에어컨","냉장고","커피포트","욕실용품","샤워가운","슬리퍼","헤어드라이기","욕조"]',
       '["더블침대 1개  객실+욕실 / 10평"]',
       '1) 객실 슬리퍼 이용 시 출입 불가능 합니다\n\n2) 수건 및 운동복, 개인 운동화를 지참하여 이용 바랍니다\n\n3) 무단 이용 또는 슬리퍼 착용 후 시설 이용 시 생기는 사고 등에 대하여 호텔은 책임이 없음을 안내드립니다\n\nAVANI Theraphy(마사지 샵) / 19층 / 10:00~24:00 (라스트 오더 23:00) / 이용 시 사전 예약 필수 / 예약 및 문의 : 051-791-5819',
       15000.00,2,'아바니 프리미어',242000.00,2,'ACTIVE',10,'Deluxe',15),

      (43,'2025-09-08 15:03:23.273542','2025-09-08 15:03:23.273542',
       '["TV","쇼파","다리미","에어컨","냉장고","커피포트","욕실용품","샤워가운","슬리퍼","헤어드라이기"]',
       '["인원 추가시 비용이 발생되며","현장에서 결제 바랍니다.  더블침대 1개  객실+거실+욕실 / 12평"]',
       '<추가패키지>\n조식 2인\nThe Cube(레스토랑) / 4층 / 07:00~10:00 (마지막 입장 09:30)\n\n룸서비스 / 07:00~10:00 (라스트 오더 09:30)\n\n\n\nhe Cube(레스토랑) / 4층 / 중식 11:30~14:00 (월~금만 운영) / 석식 임시 중단\n\nThe SKY(스카이 라운지) / 36층 / 10:00~23:00 (라스트 오더 22:00) / 대관과 관련하여 업장 사용이 불가할 수 있음 (상세 일정 호텔 문의: 051-791-5800)\n\nRoom Service(룸 서비스) / 올데이 다이닝 11:30~22:00 (라스트 오더 21:30)\n\nAVANI Fit(피트니스) / 6층 / 06:00~21:00 / 필라테스 룸 및 사우나 골프 시설 유지보수 작업으로 인해 사용 불가\n',
       15000.00,3,'아바니 주니어 스위트',290400.00,2,'ACTIVE',10,'Suite',15),

      (44,'2025-09-08 15:05:54.200206','2025-09-08 15:09:29.600867',
       '["침대","TV","에어컨","티테이블","개인금고","전기포트","헤어드라이기","인덕션","싱크대"]',
       '["더블침대 1개  객실+욕실 / 11평"]',
       '<추가정보>\n본 상품 예약 후 현장에서 주차 추가 시 20,000원/대 지불\n===\n디어스 (레스토랑) / 4층\n\n피트니스 센터\n\n야외 수영장 / 운영기간: 25년 6월 6일~9월 6일 / 운영시간: 6월&9월 10:00~19:00, 7월&8월 08:00~19:00 / 1인당 20,000원\n\n기상 악화에 따른 수영장 미운영 및 호텔 사정에 따라 운영정보가 변동될 수 있으며, 사전 안내 불가할 수 있습니다\n',
       10000.00,2,'[주차불가] 디럭스 더블 (노뷰)',92000.00,2,'ACTIVE',10,'Deluxe',9),

      (45,'2025-09-08 15:07:53.894477','2025-09-08 15:07:53.894477',
       '["TV","티 테이블","에어컨","금고","전기포트","헤어드라이기","인덕션","싱크대"]',
       '["더블침대 2개  객실(더블베드1)+거실(더블베드1)+욕실 / 22평"]',
       '<조식 정보>\n디어스 / 4층 / 07:00~10:00 (라스트오더 09:30)\n\n1인 28,000원 (14세 이상~성인), 20,000원 (48개월 이상~13세 이하), 48개월 미만 무료\n\n현장결제',
       15000.00,4,'11+ [특가! 조식 2인] 이그제큐티브 스위트 (오션뷰)',283300.00,2,'ACTIVE',10,'Suite',9),

      (46,'2025-09-08 15:12:27.329579','2025-09-08 15:12:27.329579',
       '["TV","티 테이블","에어컨","금고","전기포트","헤어드라이기","인덕션","싱크대"]',
       '["더블침대 2개  복층형 객실 (1층 더블베드1&2층 더블베드1)+욕실 / 20평"]',
       '피트니스 센터 무료\n\n보드게임 대여 / 현장 선착순\n\n부대시설 정보\n디어스 (레스토랑) / 4층\n\n피트니스 센터\n\n야외 수영장 / 운영기간: 25년 6월 6일~9월 6일 / 운영시간: 6월&9월 10:00~19:00, 7월&8월 08:00~19:00 / 1인당 20,000원\n\n기상 악화에 따른 수영장 미운영 및 호텔 사정에 따라 운영정보가 변동될 수 있으며, 사전 안내 불가할 수 있습니다\n',
       15000.00,4,'팔러스위트 복층 (시티뷰)',232700.00,2,'ACTIVE',10,'Suite',9),

      (47,'2025-09-08 15:17:00.381741','2025-09-08 15:17:00.381741',
       '["TV","에어컨","냉장고","커피포트","드라이기","고데기","욕실용품","슬리퍼","전자레인지","커피머신","주방용품","세탁기"]',
       '["퀸침대 1개  객실+욕실+테라스 / 6.74평"]',
       '<투숙객 혜택>\n짐 보관 가능\n\n전 객실 넷플릭스 이용 가능 (개인 계정)\n\n<취사 시설>\n전 객실 간단 취사 가능\n\n객실 내 육류, 튀김류, 생선류 조리 금지',
       15000.00,2,'테라스 파노라마 오션뷰 A',171800.00,2,'ACTIVE',10,'Standard',17),

      (48,'2025-09-08 15:18:33.021439','2025-09-08 15:18:33.021439',
       '["TV","에어컨","냉장고","커피포트","드라이기","고데기","욕실용품","슬리퍼","전자레인지","커피머신","세탁기","주방용품"]',
       '["더블침대 1개  객실+욕실+테라스 / 5.09평"]',
       '<주차안내>\n주차관리실 운영은 효이스테이 더 광안와 별개로 운영됩니다\n\n[2025년 7월 , 8월 성수기 주차 안내]\n\n1박 차량 : 차량 외출 불가능\n\n연박 차량 : 다음날 외출 가능 (첫입차 날에는 외출불가)\n\n주차 자리 부족 및 입구 협소로 인하여 출차 제한을 시행하오니 많은 양해 부탁드립니다\n\n입차 전 차량등록 필수\n\n주말, 공휴일 혹은 행사가 있는 경우 주차관리실 자체적인 판단에 의해 ''등록 후 입차한 차량에 대한 출차 제한'' 조치를 시행하고 있다고 하오니 주차관리실에서 정확한 내용을 전달 받으시길 바랍니다\n\n주차등록 위치 : 건물 뒷편 주차관리실 (타워주차장 앞)\n\n1일 기준 10,000원 (카드수수료 1,000원 별도)\n\n입차 전 차량 등록 필수이며, 반드시 건물 뒷편 타워주차장 앞 주차관리실에 차량 등록하셔야 합니다 (지하로 입차 절대 불가)\n\n미등록 시, 1시간당 3,000원 부과\n\n주차는 호텔과 별도로 운영되며, 만차일 경우 책임을 지지 않습니다\n\n차량 미등록에 대한 주차비 발생 책임은 게스트님께 있으니 주의 부탁드립니다',
       15000.00,2,'테라스 하프오션뷰 B',104600.00,2,'ACTIVE',10,'Deluxe',17),

      (49,'2025-09-08 15:20:04.610641','2025-09-08 15:20:04.610641',
       '["TV","에어컨","냉장고","커피포트","드라이기","고데기","욕실용품","슬리퍼","전자레인지","커피머신","주방용품","세탁기"]',
       '["퀸침대 1개  객실+욕실+테라스 / 6.69평"]',
       '<인원 추가 정보>\n기준인원 외 추가 시, 1인 20,000원 (만 1세 이상~성인)만 1세 미만 무료\n\n침구 추가 : 1채당 20,000원\n\n침대 추가 불가\n\n영유아 인원수 포함 / 최대 인원 초과 불가\n\n<투숙객 혜택>\n짐 보관 가능\n\n전 객실 넷플릭스 이용 가능 (개인 계정)\n\n<취사 시설>\n전 객실 간단 취사 가능\n\n객실 내 육류, 튀김류, 생선류 조리 금지',
       15000.00,2,'테라스 오션뷰 A',143000.00,2,'ACTIVE',10,'Deluxe',17),

      (50,'2025-09-08 15:22:31.347730','2025-09-08 15:22:31.347730',
       '["비치 물품  한실이불","수건","비누","치약","칫솔","가그린","샴푸","린스","바디워시","헤어드라이기","냉장고","에어컨","티비","개별화장실    : 조식  토스트","수제청","커피2"]',
       '["개인실  기준 2인 최대 2인"]',
       '입실시간: 3pm 퇴실시간: 10시30분am 이예요\n\n영.유아도 인원수에 포함해주시고 최대인원수를 초과하시면 안됩니다\n\n전 객실 금연 (한옥마을은 목조건물로 이루어져 있어 금연구역으로 지정되어 있습니다)\n\n전 객실 취사금지\n\n시설물을 파손하거나 침구류 훼손 및 오염(세탁 불가능) 시 전액 배상하셔야 되세요\n\n타인에게 피해나 불쾌감을 주는 행위 또는 게스트하우스 이용 규정을 지키지 않을 경우 강제 퇴실 조치\n\n보호자 미동반 미성년자 입실 불가 (업체 문의 필수)\n\n반려 동물은 입장 불가 하세요\n\n숙박양도 사기사건에 대해 책임없으며 양도시 미환불강제퇴실합니다',
       10000.00,2,'6월의 방',75000.00,2,'ACTIVE',5,'Standard',5),

      (51,'2025-09-08 15:27:17.677028','2025-09-08 15:27:46.772566',
       '["한실이불","수건","비누","치약","칫솔","가그린","샴푸","린스","바디워시","헤어드라이기","냉장고","에어컨","티비","개별화장실"]',
       '["기준 3인 최대 4인  기준인원 외 인원 추가 시 비용이 발생할 수 있으며","현장 결제하시기 바랍니다."]',
       '패밀리 룸으로 3~4인 가족,친구,연인이 쉬어가는 공간 입니다.\n\n넓은 창문으로 마당뷰를 볼수 있습니다.\n<조식>\n토스트,수제청,커피',
       20000.00,4,'12월의 방',110000.00,3,'ACTIVE',6,'Deluxe',5);

        INSERT INTO room (
          room_id, created_at, updated_at, amenities, bed_info, description,
          extra_person_fee, max_occupancy, name, price, standard_occupancy,
          status, total_rooms, type, accommodation_id
        ) VALUES
        (52,'2025-09-08 15:29:38.071826','2025-09-08 15:29:38.071826',
         '["한실이불","수건","비누","치약","칫솔","가그린","샴푸","린스","바디워시","헤어드라이기","냉장고","에어컨","티비","개별화장실"]',
         '["개인실"]',
         '별채로 되어 있어 프라이빗한 공간이예요:)\n\n: 연인,친구분들께 추천 드려요\n\n: 방은 편백나무로 이루어져 있으며 편백의 향이 느껴지는 공간 이고 마루에 미니 다과상과 방석이 준비 되어 있어요\n\n소소한 이야기를 나누시면서 조명뷰,마당뷰를 느껴 보세요',
         NULL,2,'2월의 방(별채)',90000.00,2,'ACTIVE',4,'Deluxe',5),

        (53,'2025-09-08 15:38:16.970647','2025-09-08 15:38:16.970647',
         '["TV","옷장","개별냉난방조절기","냉장고","드라이기","욕실용품"]',
         '["더블침대 2개  객실+욕실 / 15평"]',
         '<추가패키지>\n레이트 체크아웃 1시 제공',
         15000.00,4,'레이트 체크아웃 13시] 디럭스 트윈 일출봉뷰',61000.00,2,'ACTIVE',10,'Deluxe',11),

        (54,'2025-09-08 15:39:20.156318','2025-09-08 15:39:20.156318',
         '["TV","옷장","개별냉난방조절기","냉장고","드라이기","욕실용품"]',
         '["더블침대 2개  객실+욕실 / 15평"]',
         '레스토랑 / 1층 / 07:00~09:30 (09:00 입장 마감)\n\n로비 라운지 / 1층 / 10:00~22:00\n\n편의점 (세븐일레븐) / 1층 / 07:00~24:00 (11:00~15:00 미운영)\n\n오락실 / 1층 / 07:00~24:00 (11:00~15:00 미운영)\n\n코인 세탁실 / 1층 / 00:00~24:00\n\n야외 수영장(냉수) / 2층 / 15:00~22:00 / 투숙객 무료 / 매년 10월~4월 미운영\n\n2025년 5월 26일~5월 31일 수영장 내부 보수 공사로 인한 휴장',
         15000.00,4,'디럭스 트윈',56000.00,2,'ACTIVE',10,'Deluxe',11),

        (55,'2025-09-08 15:40:30.246872','2025-09-08 15:40:30.246872',
         '["TV","옷장","개별냉난방조절기","냉장고","드라이기","욕실용품"]',
         '["싱글침대 1개","더블침대 1개  객실+욕실 / 12평"]',
         '레스토랑 / 1층 / 07:00~09:30 (09:00 입장 마감)\n\n로비 라운지 / 1층 / 10:00~22:00\n\n편의점 (세븐일레븐) / 1층 / 07:00~24:00 (11:00~15:00 미운영)\n\n오락실 / 1층 / 07:00~24:00 (11:00~15:00 미운영)\n\n코인 세탁실 / 1층 / 00:00~24:00\n\n야외 수영장(냉수) / 2층 / 15:00~22:00 / 투숙객 무료 / 매년 10월~4월 미운영\n\n2025년 5월 26일~5월 31일 수영장 내부 보수 공사로 인한 휴장',
         15000.00,3,'스탠다드 트윈',51000.00,2,'ACTIVE',10,'Standard',11),

        (56,'2025-09-08 15:43:26.559610','2025-09-08 15:43:26.559610',
         '["TV","옷장","에어컨","미니바","샤워가운","헤어드라이기"]',
         '["침구세트 2개  객실2+욕실 / 15평"]',
         '<추가정보>\n한실침구 2채 / 최대인원 초과 불가능 / 침구추가현장결제\n\n레스토랑/본관 2층\n\n베이커리 카페/본관 1층\n\n스파 온(溫) /본관 1층',
         20000.00,5,'디럭스 온돌',122800.00,2,'ACTIVE',8,'Deluxe',19),

        (57,'2025-09-08 15:44:56.369455','2025-09-08 15:45:57.542941',
         '["TV","티테이블","옷장","에어컨","냉장고or미니바","샤워가운","헤어드라이기"]',
         '["더블침대 2개  객실2+욕실 / 12.9평"]',
         '<숙소 이벤트>\n[펫룸 투숙 안내]\n우리 댕댕이와 함께 떠나요 #애견 비품 구비\n\n풀빌라 객실 이용안내\n미니바(와인 1병, 치즈 1팩, 생수) 제공\n\n버블 용품 제공\n\n풀장 사용료(1일 사용) : 55,000원 (이용일 4시간 전 예약 필수)\n\n바비큐장 : 가스 그릴 사용료 50,000원 (바비큐 세트 메뉴 : 2인 세트 12만원, 4인 세트 17만원)\n\n가스 그릴 사용은 당일 예약 가능 / 바비큐 세트 메뉴는 이용일 하루 전 18시까지만 가능\n\n숙박 시간 연장 : 1시간당 50,000원 (최대 14시까지 가능, 이후는 1박 비용 발생)',
         20000.00,4,'[더앤펫츠-베이직] 디럭스 패밀리 트윈',169400.00,4,'ACTIVE',8,'Deluxe',19),

        (58,'2025-09-08 15:47:37.313124','2025-09-08 15:48:53.021630',
         '["TV","티테이블","옷장","에어컨","미니바","슬리퍼","샤워가운","헤어드라이기"]',
         '["더블침대 2개  객실2+욕실2 / 30평"]',
         '배드 : 퀸2 / 최대인원 초과 불가능\n=====\n운영 정보는 현장 상황에 따라 변동될 수 있습니다\n\n상세 운영 정보는 리조트 홈페이지를 참고해 주시기 바랍니다\n\n[SPA ON(실내사우나)]\n\n투숙객 1인 22,000원(성인), 14,000원(소인)\n\n~2025년 10월 31일: 매일 07:00~22:00(입장 마감 21:00)\n\n[GARDEN ON(야외수영장+야외온천)]\n\n유료 / 리조트 홈페이지 참고\n\n2025년 7월 18일~8월 31일: 주간권 10:00~15:00(입장 마감 14:00), 오후권 15:30~21:00(입장 마감 20:00)',
         20000.00,6,'패밀리 스위트',298800.00,4,'ACTIVE',10,'Suite',19);





INSERT INTO room_image (
    image_id, created_at, updated_at, image_url, sort_order, room_id
) VALUES
      (1,'2025-09-02 23:03:46.068647','2025-09-02 23:03:46.068647','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F29050a68-b41c-4a1f-ad67-0dec4f17b9ca_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+10.59.51.png?alt=media&token=5180af6a-bd6b-4c0a-b731-55c19916d9c2',0,4),
      (2,'2025-09-02 23:03:46.070753','2025-09-02 23:03:46.070753','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fceb8e238-fdde-45e9-ab61-b4121f358aa4_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+10.59.58.png?alt=media&token=723437d6-7bf7-4267-9033-216c88bc8a5e',1,4),
      (3,'2025-09-02 23:03:46.072244','2025-09-02 23:03:46.072244','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F5fbbcea0-cebe-45b8-8861-ec2bc6ad292a_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.00.05.png?alt=media&token=387d93e2-3a44-4235-b3fb-1c247be9824b',2,4),
      (4,'2025-09-02 23:04:53.667796','2025-09-02 23:04:53.667796','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fb2a768ff-9f5c-4925-a141-48cbbd66793f_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.04.12.png?alt=media&token=b17142fc-3375-401c-a9b7-4bd7752bc760',0,3),
      (5,'2025-09-02 23:04:53.669717','2025-09-02 23:04:53.669717','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fce2531fc-0617-47e0-8298-478199685124_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.04.23.png?alt=media&token=5876d32c-b92b-4f54-a59e-f9c14ff0678a',1,3),
      (6,'2025-09-02 23:04:53.670805','2025-09-02 23:04:53.670805','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fecffc4d1-d5ea-48b1-b84a-9d113dee91de_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.04.30.png?alt=media&token=4525e4b7-b91d-43c6-a619-b737ae4c1218',2,3),
      (7,'2025-09-02 23:06:10.782986','2025-09-02 23:06:10.782986','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F9c1834bf-bec0-4054-a65d-294dbdefe604_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.05.36.png?alt=media&token=5399d1bb-f870-4f31-b577-d82db1956e0f',0,7),
      (8,'2025-09-02 23:06:10.783733','2025-09-02 23:06:10.783733','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fd90bc679-dc5f-47a6-b650-f44b1d08a919_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.05.41.png?alt=media&token=d7e68526-b658-4df8-86e1-e924b8faa2f7',1,7),
      (9,'2025-09-02 23:06:10.784138','2025-09-02 23:06:10.784138','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F85cfde0c-a40e-446b-ac83-aacd62e00178_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.05.51.png?alt=media&token=6043066b-0731-4214-9826-0718897554f2',2,7),
      (10,'2025-09-02 23:06:10.785875','2025-09-02 23:06:10.785875','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F0c83f5d9-5349-429e-ba0d-7c42f7091751_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.05.46.png?alt=media&token=2e02d0d2-b416-436f-9fde-c8a0ab0d695b',3,7),
      (11,'2025-09-02 23:06:56.615835','2025-09-02 23:06:56.615835','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fdfa7b421-9433-43a6-aabb-080a36bbe9e4_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.06.32.png?alt=media&token=ae2c9429-82a2-47a9-aa41-43b760c34388',0,6),
      (12,'2025-09-02 23:06:56.616427','2025-09-02 23:06:56.616427','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F0b96a374-54fa-4874-b4bc-6e79c0a6ed4a_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.06.36.png?alt=media&token=b1e5efc7-9ede-4933-94b3-7261231e6857',1,6),
      (13,'2025-09-02 23:06:56.617376','2025-09-02 23:06:56.617376','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F4496c278-1491-4935-814c-16a534921dcb_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.06.40.png?alt=media&token=7641fd8b-a16a-462b-a8c5-1ca224343cc0',2,6),
      (14,'2025-09-02 23:08:07.789952','2025-09-02 23:08:07.789952','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Ffa878100-8ab2-4567-a29f-40a1f2dcc9a4_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.07.39.png?alt=media&token=6acdb3f5-bc78-4907-baf5-65dbfd0b7b34',0,5),
      (15,'2025-09-02 23:08:07.790780','2025-09-02 23:08:07.790780','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F7c315b94-81a9-4157-bbe2-54b76b2c56b8_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.07.45.png?alt=media&token=6915e16f-0ade-4327-bbf6-dfe251a1a7e9',1,5),
      (16,'2025-09-02 23:08:07.791909','2025-09-02 23:08:07.791909','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F67505f73-40ec-40a7-af92-2a36bc36c7b5_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.07.50.png?alt=media&token=f450d6d3-a780-484c-9ac9-841d0f6b9a2f',2,5),
      (17,'2025-09-02 23:09:02.984529','2025-09-02 23:09:02.984529','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F2eb20460-e022-454e-a7cb-4b6f4297a5c8_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.08.29.png?alt=media&token=dcf416af-1cc5-4375-ac0c-a61421e86b0e',0,10),
      (18,'2025-09-02 23:09:02.986876','2025-09-02 23:09:02.986876','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F98932c4e-6262-4905-bc23-8599cc9442fc_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.08.35.png?alt=media&token=8bde15aa-5faa-45df-95f1-c3983744436d',1,10),
      (19,'2025-09-02 23:09:02.988320','2025-09-02 23:09:02.988320','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fab5ad3f9-9695-4ce3-b47c-82eeafac2f2a_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.08.43.png?alt=media&token=e790bcae-4aea-4b01-811a-c36eb7fbdf3e',2,10),
      (20,'2025-09-02 23:09:02.989135','2025-09-02 23:09:02.989135','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F62b1fef3-0a60-466f-becf-10e0c16d5aa2_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.08.47.png?alt=media&token=b83f13c9-7de6-4229-88bb-4d5c5d8e892a',3,10),
      (21,'2025-09-02 23:09:59.034984','2025-09-02 23:09:59.034984','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fe175399d-aad3-4fec-a9a8-552320f4fe29_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.09.26.png?alt=media&token=1ebe6e0d-c77f-435f-be25-e998b6603e3e',0,9),
      (22,'2025-09-02 23:09:59.036279','2025-09-02 23:09:59.036279','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F3af65c3f-ffe8-4d24-b8a9-44f1ff45d808_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.09.30.png?alt=media&token=cc9bea05-7188-4043-9c96-7fcd6bbfd261',1,9),
      (23,'2025-09-02 23:09:59.037068','2025-09-02 23:09:59.037068','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Ff6bfc96e-b90d-4756-a62b-b97b55927955_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.09.35.png?alt=media&token=fbec74c3-6395-48de-b71a-e9550846efc5',2,9),
      (24,'2025-09-02 23:09:59.037606','2025-09-02 23:09:59.037606','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F2c7e3e74-ccae-489e-9291-5ded8203f84e_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.09.42.png?alt=media&token=4b0ab14b-1322-4a96-bbaf-356b6c36c72d',3,9),
      (25,'2025-09-02 23:10:56.622725','2025-09-02 23:10:56.622725','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F42100dba-9fc0-4e81-88c1-14a861861fbe_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.10.21.png?alt=media&token=04b450ca-b619-418d-817d-87ce94f8f598',0,8),
      (26,'2025-09-02 23:10:56.624723','2025-09-02 23:10:56.624723','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F42306eeb-5027-4b90-bd31-60474033188a_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.10.27.png?alt=media&token=9e7285a6-84fe-420b-9d6a-b89026e0fa17',1,8),
      (27,'2025-09-02 23:10:56.625993','2025-09-02 23:10:56.625993','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F73a2684e-8bd1-4e8f-8630-e6683599939a_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.09.42.png?alt=media&token=79a7a1f0-3eba-4c8f-bd90-2852c28e69ed',2,8),
      (28,'2025-09-02 23:15:55.261060','2025-09-02 23:15:55.261060','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fd5474f89-2f1d-4c8a-b423-a4d9bdf97a5c_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.15.22.png?alt=media&token=1dfcadae-2d18-430d-8d77-05698f70b9d1',0,1),
      (29,'2025-09-02 23:15:55.263177','2025-09-02 23:15:55.263177','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Ffaf14192-f7a7-45cd-82bc-1542eacfb931_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.15.32.png?alt=media&token=d0d1094f-a345-48be-9b7b-94f454c76028',1,1),
      (30,'2025-09-02 23:15:55.264035','2025-09-02 23:15:55.264035','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F38825a80-3dbe-44c5-be2f-a5810ad92690_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.15.41.png?alt=media&token=05ed5850-32ab-429c-8c81-297d1878b78e',2,1),
      (31,'2025-09-02 23:19:18.239628','2025-09-02 23:19:18.239628','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fb0f31638-d808-46c1-867d-01b5b41b89a5_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.18.08.png?alt=media&token=60e9b2e9-b9c3-4567-aa95-65acd21940c6',0,2),
      (32,'2025-09-02 23:19:18.242358','2025-09-02 23:19:18.242358','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fce4682dd-3a86-4890-99ca-26434cb2cb0c_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.15.22.png?alt=media&token=afd68e1e-335b-43a2-9565-03ddb1e550bf',1,2),
      (33,'2025-09-02 23:19:18.243497','2025-09-02 23:19:18.243497','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F1374be2a-d736-4668-92ef-1cf586a3fa62_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.15.32.png?alt=media&token=3441a2aa-c475-4cfb-a68b-f28a340a4739',2,2),
      (34,'2025-09-02 23:19:18.244332','2025-09-02 23:19:18.244332','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F37549d65-fd8c-4d2a-b4d3-419596ff2030_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.15.41.png?alt=media&token=07ed38d9-a6a9-489a-be09-a4778149beb3',3,2),
      (35,'2025-09-02 23:19:18.245443','2025-09-02 23:19:18.245443','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Ffe65eb10-302e-46f7-8d91-77d5733dae74_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.18.12.png?alt=media&token=b8da0c1f-a170-4d37-b901-1c638b1f711d',4,2),
      (36,'2025-09-02 23:19:18.246929','2025-09-02 23:19:18.246929','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fe5016a64-4615-45af-8360-0187f23d7a50_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.18.30.png?alt=media&token=d88d841a-7acd-4a3f-8f13-5b7fd922c0bc',5,2),
      (37,'2025-09-02 23:19:18.247623','2025-09-02 23:19:18.247623','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F6e0cd447-5fb6-4a85-b6fa-199265163efc_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.18.41.png?alt=media&token=95fe5174-026e-46ac-9c3e-e5e49f3c8cab',6,2),
      (38,'2025-09-02 23:19:18.248289','2025-09-02 23:19:18.248289','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fc9abb443-41df-4089-88e2-acbe43d5d633_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.18.46.png?alt=media&token=b3bc00e0-1b80-456f-a0fb-2a69ac022c1e',7,2),
      (39,'2025-09-02 23:24:01.826029','2025-09-02 23:24:01.826029','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fcc9dc857-439f-4e21-91d3-28ce55296177_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.22.27.png?alt=media&token=b7d7d3b2-7015-480d-b8da-67c178579935',0,11),
      (40,'2025-09-02 23:24:01.828307','2025-09-02 23:24:01.828307','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F98f43b94-10f2-41e0-b55e-0593a7b8a8ec_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.22.42.png?alt=media&token=e73215bb-5663-4786-b5a4-59df10b50fb5',1,11),
      (41,'2025-09-02 23:24:01.830434','2025-09-02 23:24:01.830434','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fff13d027-eafc-43e3-bd6c-e196a787dd82_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.22.37.png?alt=media&token=db96ccfb-52ee-47d9-9739-569a9c8e64dc',2,11),
      (42,'2025-09-02 23:24:01.831268','2025-09-02 23:24:01.831268','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F4db8c3ad-32e1-4cb8-a481-c4527c02e055_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.22.32.png?alt=media&token=a5eb9a0a-4b34-40b0-9827-33782fe894b8',3,11),
      (43,'2025-09-02 23:26:26.749342','2025-09-02 23:26:26.749342','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fe9c1d27a-1fe1-449b-bf0d-0657279a173a_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.24.28.png?alt=media&token=ad5dba5a-7b7c-458e-99cf-696969dd9887',0,12),
      (44,'2025-09-02 23:26:26.750722','2025-09-02 23:26:26.750722','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fa22fc616-540a-473b-9e26-0748cd27df92_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.24.33.png?alt=media&token=17ab00e6-818f-4f00-a92e-805b5ede96a9',1,12),
      (45,'2025-09-02 23:26:26.751870','2025-09-02 23:26:26.751870','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Ff0b06d93-31bc-4dc8-a5a7-9c356c2ed3fc_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.24.59.png?alt=media&token=7d27e5c1-bfe7-462f-b93a-b4fa3f2fb701',2,12),
      (46,'2025-09-02 23:26:26.752881','2025-09-02 23:26:26.752881','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fb09ed596-0799-48c0-bdb9-deb3b2128ff0_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.24.50.png?alt=media&token=1befb491-8cbd-4c6d-bdcf-ceffed2dee04',3,12),
      (47,'2025-09-02 23:26:26.754263','2025-09-02 23:26:26.754263','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fafbb5981-d44c-40a6-96db-fd820a6bd17e_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.24.45.png?alt=media&token=87629a48-9388-4e7f-8b17-5a693d892033',4,12),
      (48,'2025-09-02 23:29:13.949338','2025-09-02 23:29:13.949338','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fac584978-1207-46f5-96df-185177f0993d_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.26.38.png?alt=media&token=3a12e8e9-c3cf-41e7-9e6a-b040bda0dfe9',0,13),
      (49,'2025-09-02 23:29:13.951263','2025-09-02 23:29:13.951263','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F9dd617c9-4523-4faf-ac67-8ce58b99f7bc_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.26.43.png?alt=media&token=0710aafd-0373-4f15-bd66-d76f6d00c3e9',1,13),
      (50,'2025-09-02 23:29:13.952639','2025-09-02 23:29:13.952639','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F663de1b1-abfc-4d5c-ac17-8b8eadd23ccd_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.27.02.png?alt=media&token=0e284d6e-1fe7-48c5-a445-e155f89045e7',2,13),
      (51,'2025-09-02 23:29:13.953671','2025-09-02 23:29:13.953671','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F37d96711-518e-46f0-a255-2b9be48ded0b_%E1%84%89%E1%85%B3%E1%84%8F%E1%85%B3%E1%84%85%E1%85%B5%E1%86%AB%E1%84%89%E1%85%A3%E1%86%BA+2025-09-02+%E1%84%8B%E1%85%A9%E1%84%92%E1%85%AE+11.27.06.png?alt=media&token=4bf07b25-237f-4318-8a21-f39f1a2399fd',3,13),
      (52,'2025-09-08 11:27:58.100240','2025-09-08 11:27:58.100240','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fb1665984-d955-47b1-bf83-b02e98c6baaf_46.jpg?alt=media&token=922df95f-a52a-4a8a-83c8-f2a09dd7c980',0,14),
      (53,'2025-09-08 11:27:58.102015','2025-09-08 11:27:58.102015','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F8dad6306-8fd8-4c31-9ffa-4cafdbc3245f_47.jpg?alt=media&token=cfff6a38-0583-4f44-86ec-def4f9a8d4a6',1,14),
      (54,'2025-09-08 11:27:58.103202','2025-09-08 11:27:58.103202','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F48c8ebb5-31b8-4be3-8187-808b8968bd85_48.jpg?alt=media&token=dc4b587b-88d9-4ff1-9d1a-72e2b8f440f7',2,14),
      (55,'2025-09-08 11:27:58.104096','2025-09-08 11:27:58.104096','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Faac0cd25-95b5-464d-8927-935d78533895_49.jpg?alt=media&token=ccffa33e-c12c-43b0-b163-7a719a079dde',3,14),
      (60,'2025-09-08 11:30:51.295485','2025-09-08 11:30:51.295485','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F960402d5-74ec-4ca9-be95-221bd6d88c26_50.jpg?alt=media&token=9ad46753-1625-43e8-8284-d17ce537a0f7',0,15),
      (61,'2025-09-08 11:30:51.296339','2025-09-08 11:30:51.296339','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F4ffe3e23-5c16-4d04-b065-dc8f79a0b1b9_51.jpg?alt=media&token=04ccfac9-7bd8-4de5-8c56-25e7bcd7eb6a',1,15),
      (62,'2025-09-08 11:30:51.296766','2025-09-08 11:30:51.296766','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F20bf4c05-a26a-4917-8465-95f7280f5fea_52.jpg?alt=media&token=13dd0e1f-4adf-4875-9feb-e686b06c1e3e',2,15),
      (63,'2025-09-08 11:30:51.297143','2025-09-08 11:30:51.297143','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F6db451ca-c5ea-4258-a03e-66892d46233b_53.jpg?alt=media&token=f0bd5264-f730-413b-9606-6fe4284b5039',3,15),
      (64,'2025-09-08 11:30:51.297566','2025-09-08 11:30:51.297566','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F2448b8f0-e4f0-407f-ab2c-f62566fbc760_54.jpg?alt=media&token=c1662611-370f-4052-9a99-09db08cd71e2',4,15),
      (65,'2025-09-08 11:33:18.500979','2025-09-08 11:33:18.500979','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fa191d66b-bcdc-407d-878e-e19661c36d29_55.png?alt=media&token=b094c445-cfc0-4154-9809-9d69d136176f',0,16),
      (66,'2025-09-08 11:33:18.502707','2025-09-08 11:33:18.502707','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fefcade73-876b-44fe-a068-64f0f0ef64c4_56.png?alt=media&token=a933d581-a53c-4219-96cb-a8d3b51b8f07',1,16),
      (67,'2025-09-08 11:33:18.503561','2025-09-08 11:33:18.503561','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fc887a9a5-3183-45f0-8d4f-3d1e913f0846_57.png?alt=media&token=bf6c670d-d3fe-4c40-a8cd-c4996ad5765a',2,16),
      (68,'2025-09-08 11:33:18.505565','2025-09-08 11:33:18.505565','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F58368413-60b2-43e2-86bc-99be8cfaa9ef_58.png?alt=media&token=cbe80017-823d-4c4d-b683-35da345adb5a',3,16),
      (69,'2025-09-08 11:39:11.341977','2025-09-08 11:39:11.341977','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fb0312517-a384-45f9-aa55-fc2d34259891_59.jpg?alt=media&token=01cdb4fa-5e54-4b32-9453-5ef8490154d3',0,17),
      (70,'2025-09-08 11:42:11.632852','2025-09-08 11:42:11.632852','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Ff8691bdc-0924-41ea-88b6-2f5aff49e42b_60.jpg?alt=media&token=9c1dbad5-f073-4f4a-91b6-52eb1669edc0',0,18),
      (71,'2025-09-08 11:42:11.634473','2025-09-08 11:42:11.634473','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F468a626c-b330-460d-be24-fff8f427b3e7_61.jpg?alt=media&token=a29813ee-92d8-41b0-a6a4-bcd8659e5795',1,18),
      (72,'2025-09-08 11:44:29.113142','2025-09-08 11:44:29.113142','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fdf15f245-1bb7-4e5b-9c6e-7268d550573e_62.jpg?alt=media&token=822f9cd8-a7b7-41ee-b380-c3f5a0d6eb35',0,19),
      (73,'2025-09-08 11:44:29.113726','2025-09-08 11:44:29.113726','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fb01db181-eb25-422d-901b-47d41d7dee15_63.jpg?alt=media&token=90a19052-c5c7-4083-a34f-4cd998a02476',1,19),
      (74,'2025-09-08 11:46:53.293160','2025-09-08 11:46:53.293160','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fb25853f0-50d6-4885-9776-2a13fe8cf4eb_64.jpg?alt=media&token=e2605dc5-5c56-4c09-91f8-5b195e08d576',0,20),
      (75,'2025-09-08 11:46:53.294493','2025-09-08 11:46:53.294493','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fdca0c947-3188-4a48-ac21-7aebd439c5dd_65.jpg?alt=media&token=032d3ff7-b03e-4841-9a8b-b59e56c53312',1,20),
      (76,'2025-09-08 11:46:53.295430','2025-09-08 11:46:53.295430','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fd1a08d96-1469-4b28-ad2a-abbfb88672f8_66.jpg?alt=media&token=0ca1404b-64ff-4f02-9fc7-fbb42eac2945',2,20),
      (77,'2025-09-08 11:46:53.296086','2025-09-08 11:46:53.296086','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F34f502e9-e567-4911-8df3-3b6812ac1444_67.jpg?alt=media&token=1cfbb0c3-f72e-449c-a03d-62e5f70e9990',3,20),
      (78,'2025-09-08 12:40:52.542053','2025-09-08 12:40:52.542053','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F4e1e5800-e890-4278-a626-8b372a72409c_68.jpg?alt=media&token=ae3a59dc-2492-4471-992b-4b2cc09a82b5',0,21),
      (79,'2025-09-08 12:40:52.542926','2025-09-08 12:40:52.542926','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F2c9e3d6a-4b2f-445f-949e-18253fec45c0_69.jpg?alt=media&token=6fcdccd4-83df-4266-92ad-fa50affed259',1,21),
      (80,'2025-09-08 12:40:52.543288','2025-09-08 12:40:52.543288','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fd3142775-c334-4f04-afa8-3fc746ed46f2_70.jpg?alt=media&token=52b644ab-9208-41cc-ac5f-b3e348b4c470',2,21),
      (81,'2025-09-08 12:40:52.543672','2025-09-08 12:40:52.543672','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F57a54726-b6c4-4236-98fe-c76aaf0f956b_71.jpg?alt=media&token=ac6a6710-c972-47a9-8c5a-e5a1b05b0317',3,21);


INSERT INTO room_image (
    image_id, created_at, updated_at, image_url, sort_order, room_id
) VALUES
      (82,'2025-09-08 12:55:50.972639','2025-09-08 12:55:50.972639','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F0514180b-27af-4e82-b1b3-a9c31dd917e6_76.jpg?alt=media&token=8b5f5f5e-72ce-4ec1-b0be-025544050645',0,22),
      (83,'2025-09-08 12:55:50.975197','2025-09-08 12:55:50.975197','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F2180a4cb-4ddc-4218-9a15-aad1dcadc554_80.jpg?alt=media&token=04c629fb-78fe-4992-a54c-b3ba96d3ed12',1,22),
      (84,'2025-09-08 12:55:50.976353','2025-09-08 12:55:50.976353','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F14530aa5-99d8-4611-8ccf-76da9d617dc0_79.jpg?alt=media&token=c3efa161-e1ca-4fe9-829e-4c5bb57f8161',2,22),
      (85,'2025-09-08 12:55:50.977181','2025-09-08 12:55:50.977181','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F9e3ddb42-7875-41e0-9448-7ade48fcc1ed_78.jpg?alt=media&token=b0b5e14c-4b68-45aa-91e8-c22940c74b1c',3,22),
      (86,'2025-09-08 12:55:50.977893','2025-09-08 12:55:50.977893','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F3c92eed7-0035-4cc4-bf81-c86ac54b49ec_77.jpg?alt=media&token=83c907ef-697a-4852-b1da-876c48699e97',4,22),
      (87,'2025-09-08 13:12:23.752225','2025-09-08 13:12:23.752225','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F5039f5eb-bd35-42cf-8a0d-e6b9e8290bc1_81.jpg?alt=media&token=486eba06-70d4-4f8e-86eb-5aea415852a4',0,23),
      (88,'2025-09-08 13:12:23.754219','2025-09-08 13:12:23.754219','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fe97e3108-9835-4306-be9c-d233ff34936f_82.jpg?alt=media&token=61fb02d4-e07a-4368-82e8-7c1a7a869e23',1,23),
      (89,'2025-09-08 13:16:36.861698','2025-09-08 13:16:36.861698','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fa0b57aa2-73a3-47d1-998b-9bd359df3e75_83.jpg?alt=media&token=f1c5c0d2-04cc-45d0-95a9-ba9e2f8d139c',0,24),
      (90,'2025-09-08 13:16:36.864163','2025-09-08 13:16:36.864163','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fce234a33-cdf2-4110-b4fd-5bb253c26ddb_84.jpg?alt=media&token=8e0e67a6-b94f-46c7-828e-51bdf95472a9',1,24),
      (91,'2025-09-08 13:18:17.199600','2025-09-08 13:18:17.199600','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fac69c079-00fd-403f-9bfb-6425bc5a3cea_85.png?alt=media&token=a702740f-1699-4c4f-9a4d-80cc09e2ba9e',0,25),
      (92,'2025-09-08 13:18:17.201426','2025-09-08 13:18:17.201426','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F76acefc8-364c-45cb-be4f-7bf7f1442fe4_86.jpg?alt=media&token=a8d8cf83-5409-4bf1-8226-580493ad36a0',1,25),
      (93,'2025-09-08 13:18:17.203459','2025-09-08 13:18:17.203459','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F16589e0e-f832-47bc-aecb-443dff763d53_87.jpg?alt=media&token=6ea3f363-0565-47af-ba88-ec2ea94f6407',2,25),
      (94,'2025-09-08 14:08:06.895738','2025-09-08 14:08:06.895738','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F2e095bbe-3b41-41ab-943d-7690ca180d21_5b91cc47e642d.jpg?alt=media&token=87f06375-1409-4a86-8287-5130982c1d31',0,26),
      (95,'2025-09-08 14:08:06.897903','2025-09-08 14:08:06.897903','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F16c6a551-c473-459a-aa3f-a973cb3361e8_689d932e5b865.jpg?alt=media&token=046a4222-1f21-4636-b702-b5a800472f6e',1,26),
      (96,'2025-09-08 14:08:06.898964','2025-09-08 14:08:06.898964','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fc724d5ba-e31b-4251-8d85-7db8c542cd83_686788cbc90cc.jpg?alt=media&token=5fe466e6-c046-4f51-a47c-0e06305f4528',2,26),
      (97,'2025-09-08 14:18:40.494192','2025-09-08 14:18:40.494192','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Ff489e9a8-4891-47eb-b416-90fd18cfeb2a_6142c06a3dfd9.jpg?alt=media&token=3110948d-96ec-4927-962c-fb486f03583e',0,27),
      (98,'2025-09-08 14:18:40.496178','2025-09-08 14:18:40.496178','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F6e26c6c6-397f-47d8-a5eb-0fae9f061eb2_6142c0691c044.jpg?alt=media&token=172ca6b6-ad4d-4848-bda1-ec8834a3ca2a',1,27),
      (99,'2025-09-08 14:18:40.497392','2025-09-08 14:18:40.497392','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fb4fa72d7-dbdb-423b-a0c1-de45aa35b3ca_6867843a9f47d.jpg?alt=media&token=c2cae0c3-c054-40d2-a4a4-08769b94da51',2,27),
      (100,'2025-09-08 14:18:40.498262','2025-09-08 14:18:40.498262','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F9ff0ab52-f163-4b6a-9f1e-5ac5cefe1a7b_6867843986d84.jpg?alt=media&token=cb1139ff-f700-49b5-bb22-8be3940be586',3,27),
      (101,'2025-09-08 14:22:39.890848','2025-09-08 14:22:39.890848','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F046ecb4f-c20c-4d80-aa01-1b314cfeb29b_5b91cba2b07ad.jpg?alt=media&token=bf66f84e-5978-44c0-924b-9c8d62d85f89',0,28),
      (102,'2025-09-08 14:22:39.892714','2025-09-08 14:22:39.892714','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F9f2a47b9-ec41-4ed3-8152-9d2272421c51_5b91cba13ad2f.jpg?alt=media&token=a70d4521-eb9a-4f02-a82e-41e54c023012',1,28),
      (103,'2025-09-08 14:22:39.893835','2025-09-08 14:22:39.893835','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F4c34b656-c20f-4562-8efe-552e57c07720_68678798973fe.jpg?alt=media&token=4dca198f-eba7-4695-bfe8-93a8133d6a9e',2,28),
      (104,'2025-09-08 14:25:16.312853','2025-09-08 14:25:16.312853','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F204bfe5f-bb51-4cdb-af63-42c206ace239_668620c9dfcb5.jpg?alt=media&token=6d547ab7-c48c-4e86-be76-8f12e8b148fd',0,29),
      (105,'2025-09-08 14:25:16.315580','2025-09-08 14:25:16.315580','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fa98cc910-401b-468c-b814-d480f3b07f9c_668620caaaaca.jpg?alt=media&token=69437173-d781-43d1-8d91-c49789d38f9b',1,29),
      (106,'2025-09-08 14:25:16.316963','2025-09-08 14:25:16.316963','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F381ddcca-83d2-48cb-a688-e026b6e698f0_668620cbc8210.jpg?alt=media&token=0de418d9-7768-4ef5-982b-527f67aba875',2,29),
      (107,'2025-09-08 14:25:16.317767','2025-09-08 14:25:16.317767','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F52546e0b-3ca2-4793-962e-0306d7fe02bd_668620cceba8f.jpg?alt=media&token=18dea02d-1e6a-4755-a04d-e501531be05b',3,29),
      (108,'2025-09-08 14:27:49.087196','2025-09-08 14:27:49.087196','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fb032cd5e-3675-428d-9748-0e1e46a1560b_668620e5e4155.jpg?alt=media&token=1b80b36f-aa6d-4f77-9db1-cf5673207da6',0,30),
      (109,'2025-09-08 14:27:49.089237','2025-09-08 14:27:49.089237','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F20d87d9c-dc76-4f87-8eb6-2c8979c72191_668620e6e407d.jpg?alt=media&token=1055c81b-d290-410c-aa04-13b4baf46781',1,30),
      (110,'2025-09-08 14:27:49.090287','2025-09-08 14:27:49.090287','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F524afe35-e8b3-4351-894f-c891ef2c9884_668620e810cdf.jpg?alt=media&token=8aeb2ef3-10b3-42c7-96be-f696c91baf73',2,30),
      (111,'2025-09-08 14:27:49.091178','2025-09-08 14:27:49.091178','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fa102606b-5b0a-4f32-bed4-dbd42e8362a7_668620e924d77.jpg?alt=media&token=faf0b6db-791b-4df4-bef5-d26f101788d5',3,30),
      (112,'2025-09-08 14:30:00.172160','2025-09-08 14:30:00.172160','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F0ff67083-80ad-4cf4-a8d4-c296de965f4a_88.jpg?alt=media&token=b589209e-52ad-4dab-a2a4-410c6339cb9b',0,31),
      (113,'2025-09-08 14:30:00.173061','2025-09-08 14:30:00.173061','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F7fa322f1-142d-4680-9b57-6ec733994f09_89.jpg?alt=media&token=a3494ee5-b78e-46f8-933d-a8f7f4819096',1,31),
      (114,'2025-09-08 14:30:00.173645','2025-09-08 14:30:00.173645','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F34aebb1e-e56d-4ea2-9b99-583430e6e012_90.jpg?alt=media&token=053af4be-8022-4bc3-90c7-c81fca6e0f08',2,31),
      (115,'2025-09-08 14:34:33.742301','2025-09-08 14:34:33.742301','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F0f22fed9-6fcd-4152-9ea6-4c696d2a1cab_6074ee7c61821.jpg?alt=media&token=fec95896-73aa-4e7b-9fb1-f3f5a039447d',0,32),
      (116,'2025-09-08 14:34:33.744481','2025-09-08 14:34:33.744481','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fb7c70623-8ce1-4576-bdc1-fa5d45af32e2_6074ee8081af7.jpg?alt=media&token=bb400492-c024-4db8-a0d7-62286fb332e4',1,32),
      (117,'2025-09-08 14:36:29.749236','2025-09-08 14:36:29.749236','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F02923cdf-e4da-47e4-88ec-29e5c90d6b16_6074ee4ca34ff.jpg?alt=media&token=0a294332-5ff6-4aef-8321-494bdd6476f0',0,33),
      (118,'2025-09-08 14:36:29.751278','2025-09-08 14:36:29.751278','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F7337f8a3-7386-4e9d-a64c-8ef20cf4548f_6074ee711261a.jpg?alt=media&token=a738d011-e1f6-46f0-8577-ace15de72523',1,33),
      (119,'2025-09-08 14:39:28.373547','2025-09-08 14:39:28.373547','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fcf3ee471-49b2-4210-9bd7-fa064aab67e3_6299b957d2c0f.jpg?alt=media&token=bb33502a-d39a-436e-b1e9-dc193c693199',0,34),
      (120,'2025-09-08 14:39:28.375831','2025-09-08 14:39:28.375831','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Ffddbcd40-a424-46de-8647-24a1369408d7_94.jpg?alt=media&token=e961d1eb-dddb-41ee-ad07-68d64b5f6521',1,34),
      (121,'2025-09-08 14:39:28.377420','2025-09-08 14:39:28.377420','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Faab99f98-df6a-4667-8b15-bae6a16a055b_92.jpg?alt=media&token=dc410d55-3a2f-473d-90da-93065c301ae4',2,34),
      (122,'2025-09-08 14:39:28.378555','2025-09-08 14:39:28.378555','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F7d050225-3b93-4188-9afd-205aecf6e52c_91.jpg?alt=media&token=cf6bde36-48f1-47fb-ba58-ac2b8f614317',3,34);

INSERT INTO room_image (
    image_id, created_at, updated_at, image_url, sort_order, room_id
) VALUES
      (123,'2025-09-08 14:49:01.511621','2025-09-08 14:49:01.511621','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Ff3fb9d01-07ee-4735-a491-37955d7ad57d_eda757d386c75a84d1a54b86e7c949b7.jpg?alt=media&token=c7b023c2-ff4c-48e5-80d7-e85c68e88858',0,35),
      (124,'2025-09-08 14:49:01.513695','2025-09-08 14:49:01.513695','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fd934068c-d7a4-47ed-a416-b885e74253e9_12be2b0a515fd623dcfa6cc9b2e3c5f7.jpg?alt=media&token=d9f46fe9-1c31-4f2c-a4ff-f9368663a348',1,35),
      (125,'2025-09-08 14:49:01.514950','2025-09-08 14:49:01.514950','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F8169ee2f-ec0a-4a9f-880b-bc53378f1557_16b4fba6e91d78b7a8d3ad8114f87c91.jpg?alt=media&token=0d1d7bd8-16a6-4302-8673-eb0de3a01a83',2,35),
      (126,'2025-09-08 14:49:01.515723','2025-09-08 14:49:01.515723','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F0a41a789-d3dd-4669-9dc4-7911c84995f9_832152f8fd748ddc2c7c72f54d39ec67.jpg?alt=media&token=f7461a18-fc53-4402-9853-389f035c911f',3,35),
      (127,'2025-09-08 14:49:01.516461','2025-09-08 14:49:01.516461','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F3e696ccd-0d1c-45ea-b12b-890feebb7a4f_74879511cfe96430a46bfbf08e2a3335.jpg?alt=media&token=6e1e979a-562a-4442-baaf-830eb928cd33',4,35),
      (128,'2025-09-08 14:50:38.058466','2025-09-08 14:50:38.058466','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F177b3a3c-bbd9-49c7-bda9-0cec3c98c244_817184eec123070a81bf8839addcec6d.jpg?alt=media&token=1963adec-58f7-40f0-8c9a-e4d6deb2e28f',0,36),
      (129,'2025-09-08 14:50:38.060359','2025-09-08 14:50:38.060359','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fabc3e78c-9781-4ead-9f2a-63346d245175_1fcf90adeacb0ed9c5ee20819b9c0bfd.jpg?alt=media&token=c51d6a71-8593-4e00-a8e3-d55d15d6ac5a',1,36),
      (130,'2025-09-08 14:50:38.061460','2025-09-08 14:50:38.061460','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F4600cc54-082e-441d-af7c-3f0bf0261a14_5f64a5887ea44b32f2291c57d38f1a34.jpg?alt=media&token=5da6f41d-5a0f-4f0b-8b4b-510b3d063192',2,36),
      (131,'2025-09-08 14:50:38.062343','2025-09-08 14:50:38.062343','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F4d7f153b-733e-463a-99cd-14e6ebf6c580_78a2edf7adbb335f7d48d28fca073c07.jpg?alt=media&token=785525fd-ad20-4f7c-9b1d-58de0090af25',3,36),
      (132,'2025-09-08 14:50:38.063811','2025-09-08 14:50:38.063811','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F6d310941-0514-45a4-ba64-7152b7fa9984_388c73e7d15b340d88e53f71283cf479.jpg?alt=media&token=fbee49f6-6019-48e8-a4d0-566f80ad3293',4,36),
      (133,'2025-09-08 14:50:38.064567','2025-09-08 14:50:38.064567','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F5530c4ee-5782-40f7-a2bb-ddec0c166b1d_2676fe27a066e3d873c421e356c665b9.jpg?alt=media&token=2f2d5655-4eb6-437a-8d5f-60ca3c7dbe6a',5,36),
      (134,'2025-09-08 14:52:03.123055','2025-09-08 14:52:03.123055','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F0d34e482-22a6-4168-90ae-ef7fee1788a3_d774a3948a2815be5db7e5a08c1f622f.jpg?alt=media&token=155ea84a-8316-458f-b792-3f85a2bd9924',0,37),
      (135,'2025-09-08 14:52:03.124839','2025-09-08 14:52:03.124839','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fb31b40c0-f889-4e77-9a58-860ff7bbe034_2ba1516e62a003450bc57f720e10f7df.jpg?alt=media&token=aede3047-a7f6-424d-b8fa-b48b9d98b24e',1,37),
      (136,'2025-09-08 14:52:03.125645','2025-09-08 14:52:03.125645','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F2cb4857d-4429-433e-892f-499e47e131bc_19cd07928d22be165e801d78d84a41a5.jpg?alt=media&token=b69a88dd-ee03-45d6-b67b-7ea45168632f',2,37),
      (137,'2025-09-08 14:52:03.126217','2025-09-08 14:52:03.126217','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fd078d802-d0c6-4914-95c1-159a20a9b089_754936d22b3a853de18288e8f5228e26.jpg?alt=media&token=2887ffa7-de44-4224-9760-91c2761ececb',3,37),
      (138,'2025-09-08 14:52:03.127000','2025-09-08 14:52:03.127000','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fa31a8123-a43c-4d1a-bd8b-e5d8a8064099_cb46a44c4bb665e2472b8f39d158997f.jpg?alt=media&token=3c7fedbf-adbe-4649-915f-5c366a209e8b',4,37),
      (139,'2025-09-08 14:54:46.596826','2025-09-08 14:54:46.596826','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F5a263c6c-1b00-4104-a62c-58e629e2a4a0_0d41538d26a7b0dc402eb0baa5904885.jpg?alt=media&token=214185f6-d1bc-4435-bd15-089d746ef828',0,38),
      (140,'2025-09-08 14:54:46.599480','2025-09-08 14:54:46.599480','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F70bf6379-2913-4ed1-86dd-2f6271f00297_3f5d566532493515e9939b732cd2d385.jpg?alt=media&token=751fd9c7-92d6-4c13-959e-db2c97d415ce',1,38),
      (141,'2025-09-08 14:54:46.601019','2025-09-08 14:54:46.601019','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F8147819e-ce95-4759-aabd-f4b26d7ab36a_4b84c777e8df90e8f0812cbd460c2df0.jpg?alt=media&token=c90c0cd7-dc63-4ec5-8126-6e75b10cec86',2,38),
      (142,'2025-09-08 14:54:46.602195','2025-09-08 14:54:46.602195','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F62924afb-8937-4a76-aa75-17f2ee059ad8_a72ef3dad4c17e12929bfee61f25dad0.jpg?alt=media&token=cacb6dab-f369-4491-adeb-32ade0198d2c',3,38),
      (143,'2025-09-08 14:56:57.970401','2025-09-08 14:56:57.970401','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F911034f8-df2f-436d-9bca-0076b5110b1b_6b08df71fe7670cbccc01ce76bff4869.jpg?alt=media&token=6ff74033-a4d0-4a45-8445-7db36261d9a1',0,39),
      (144,'2025-09-08 14:56:57.972197','2025-09-08 14:56:57.972197','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F370d31ab-3a38-4aa3-989c-22d72ff106cf_8624fc2afeb1568ae4bb1c661235e7a7.jpg?alt=media&token=fa83ef95-6da6-4fed-ae32-23b726df2769',1,39),
      (145,'2025-09-08 14:56:57.973184','2025-09-08 14:56:57.973184','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F451270f6-26f0-496b-8626-96edf4c93737_208291359b01137ee3b2bd4fd14f0000.jpg?alt=media&token=61fbd5ed-9b0d-4efe-8ff6-2a17c473f40e',2,39),
      (146,'2025-09-08 14:56:57.973869','2025-09-08 14:56:57.973869','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F83c7fe6d-737b-4149-830b-99530bcd48a3_aa41aeb11c2eb4626590df0eb5799f29.jpg?alt=media&token=13f3fc72-fa22-44e5-b02b-817d857c974c',3,39),
      (147,'2025-09-08 14:56:57.974902','2025-09-08 14:56:57.974902','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F44d509d7-01f8-4761-bedb-e7926fb928a1_c446cd46112f2b0b1242eefd63062cdb.jpg?alt=media&token=74cb781e-f45b-4817-97b2-dd4d2a83ce84',4,39),
      (153,'2025-09-08 14:58:04.319474','2025-09-08 14:58:04.319474','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F5451e591-4d14-4b13-81bb-3cc6c44ae54d_58a08a238e1d3f1cab708167975ebf71.jpg?alt=media&token=4660a47c-4c2a-450a-a110-699ed7a4741f',0,40),
      (154,'2025-09-08 14:58:04.321059','2025-09-08 14:58:04.321059','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fa8aa17fc-d351-4181-9afa-37618ab8a107_9b3f28561241e9094e62afb83fc3fbfe.jpg?alt=media&token=22e383e7-c14e-4c08-b05b-a11f1e81d685',1,40),
      (155,'2025-09-08 14:58:04.321961','2025-09-08 14:58:04.321961','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fefafb60c-1910-46b1-99ce-e1d78e38e8b5_9b33074ea68b5b87cdb87d854340a156.jpg?alt=media&token=593fe09e-bcb9-49fa-9b26-a6bfc9475cef',2,40),
      (156,'2025-09-08 14:58:04.322591','2025-09-08 14:58:04.322591','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F84711c6c-5858-4703-a676-acea28a7c814_afbc8242c253a463275c6d5d669d9377.jpg?alt=media&token=b83e9072-e595-4910-a847-690f7f95d09a',3,40),
      (157,'2025-09-08 14:58:04.323096','2025-09-08 14:58:04.323096','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F7e9a2c65-9f73-4ba2-a2b8-fa432113efd1_f29b2643915413c4cc14d6b50777aa9a.jpg?alt=media&token=9221b1f1-a6f8-479a-ba8b-b3a9814c4083',4,40),
      (158,'2025-09-08 15:00:05.642263','2025-09-08 15:00:05.642263','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F4aba767a-bdf4-4adc-8694-304bf3dc587a_5cec876e91dd2.jpg?alt=media&token=8d6b2bae-f589-4b88-8241-de6a19c0d449',0,41),
      (159,'2025-09-08 15:00:05.644173','2025-09-08 15:00:05.644173','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F6a3a7a9c-3c59-42e4-a2e7-01bb8a70839f_5ec482b1e258e.jpg?alt=media&token=be8b523a-2900-4243-adb5-20df54c900a2',1,41),
      (160,'2025-09-08 15:00:05.645045','2025-09-08 15:00:05.645045','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fd44504bc-d10d-44d5-8ac4-ce764747e331_5ec482b30bf04.jpg?alt=media&token=58cd151e-811f-4993-b351-43a165206f52',2,41),
      (161,'2025-09-08 15:00:05.645780','2025-09-08 15:00:05.645780','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fbbd65b74-a4ef-4507-9a88-fa3a678c0ba8_5ec482b4309b2.jpg?alt=media&token=f153ca5e-bcc9-4769-822a-26237b69b3b6',3,41),
      (162,'2025-09-08 15:00:05.646468','2025-09-08 15:00:05.646468','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fb4971201-5636-4a36-b6a0-2963dd118376_5ec482b5633ad.jpg?alt=media&token=592440f4-2095-4172-b636-b1e4be9da199',4,41),
      (163,'2025-09-08 15:01:15.237286','2025-09-08 15:01:15.237286','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F13f2d0e1-0778-4753-835e-bff07d0f0852_5cec838d0ba05.jpg?alt=media&token=c271d5b8-b9db-49c2-899f-ce34c0b00048',0,42),
      (164,'2025-09-08 15:01:15.239100','2025-09-08 15:01:15.239100','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fafb006b1-aeb6-4bf6-b819-ab702f938c8f_5ec4a1d2e463d.jpg?alt=media&token=03d0fd7e-6d61-4802-a60a-5e0bbd5a94d2',1,42),
      (165,'2025-09-08 15:01:15.240092','2025-09-08 15:01:15.240092','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fa483f0d8-78a1-4439-86c9-7dcd00914b0a_5ec4a1d3a846a.jpg?alt=media&token=10d3164d-bbeb-4753-b670-952223b54ceb',2,42),
      (166,'2025-09-08 15:01:15.240947','2025-09-08 15:01:15.240947','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F1286ac43-0af7-4f33-95ee-434a903f4b9d_5ec4a1d5e1c75.jpg?alt=media&token=b154cdf0-bd28-4ed7-bc91-7f88fa132d07',3,42),
      (167,'2025-09-08 15:01:15.241555','2025-09-08 15:01:15.241555','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F5cac0783-7e76-4f77-ad6e-e78482371e5d_5ec4a1d46dc43.jpg?alt=media&token=12879baf-8b50-4f3b-b00c-67afea5ab2c5',4,42),
      (168,'2025-09-08 15:03:25.654864','2025-09-08 15:03:25.654864','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fb1157201-8937-46db-a666-d9ed3424fe0f_5ec4a0d9726bd.jpg?alt=media&token=33123c0b-3d11-4074-8087-0e0dff54c031',0,43),
      (169,'2025-09-08 15:03:25.656705','2025-09-08 15:03:25.656705','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F8e704eb2-4a5e-4140-99e8-f6a2e4e990ce_5cec838587e68.jpg?alt=media&token=d79bf75e-0999-4f78-b127-b98f53ed4b47',1,43),
      (170,'2025-09-08 15:03:25.657604','2025-09-08 15:03:25.657604','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fc7ba6ee7-30e2-4896-89ce-58531bc585c8_5ec4a0d7ac77f.jpg?alt=media&token=8caf22a3-4656-4240-87b3-e2b8761be7d9',2,43),
      (171,'2025-09-08 15:03:25.658281','2025-09-08 15:03:25.658281','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F162894bd-7b5b-4a58-925d-093a425f4a5a_5ec4a0d84e19c.jpg?alt=media&token=358c6cd7-9937-4fcb-a15e-99c916e3949f',3,43),
      (172,'2025-09-08 15:03:25.658814','2025-09-08 15:03:25.658814','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F7f621902-a588-425a-b3dc-0dd8e8d98bd7_5ec4a0d713c35.jpg?alt=media&token=1313ed67-0774-4269-b451-d8fe58333a97',4,43),
      (178,'2025-09-08 15:07:54.821077','2025-09-08 15:07:54.821077','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F714f4c9c-2493-4f61-a30a-c8ecbbdb697d_6479b9b3cfc33.jpg?alt=media&token=f8d37266-df45-482b-9e61-333e7fe717fa',0,45),
      (179,'2025-09-08 15:07:54.823299','2025-09-08 15:07:54.823299','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F64115bc2-0bb8-4f42-bb2d-9e265b98ab93_6479b9b5d88eb.jpg?alt=media&token=726d1285-ccf0-4ebc-a167-ca24b86a12dd',1,45),
      (180,'2025-09-08 15:07:54.824333','2025-09-08 15:07:54.824333','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fb8db4f59-0f17-47e9-894c-23c84a1ae530_6479b9b81c30e.jpg?alt=media&token=b37b39e3-fc1f-451e-9195-3ece861d1258',2,45),
      (181,'2025-09-08 15:07:54.825138','2025-09-08 15:07:54.825138','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F1140539f-75bd-4ec2-8db4-9fe577becfd2_6479b9b505cf1.jpg?alt=media&token=5886a11a-c0d4-4396-977f-484bfdf8b9dc',3,45),
      (182,'2025-09-08 15:07:54.825981','2025-09-08 15:07:54.825981','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F732232b9-2563-479f-94bd-9da83e6fb9fb_6479b9b7079d5.jpg?alt=media&token=74dc7c4e-d940-4ecd-a764-911197af9a6d',4,45),
      (183,'2025-09-08 15:09:30.400153','2025-09-08 15:09:30.400153','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F2657a64e-5408-4c48-a6c3-1cbb41e5871b_6479bc5d97b37.jpg?alt=media&token=d978e073-ca40-49a3-9e34-603102267cb9',0,44),
      (184,'2025-09-08 15:09:30.400647','2025-09-08 15:09:30.400647','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fc67f68c6-5b46-4e3e-a275-e451ccc440c6_6479bc5fcb64d.jpg?alt=media&token=9113b9ca-fd16-4a0b-8659-310ad2886a3f',1,44),
      (185,'2025-09-08 15:09:30.400897','2025-09-08 15:09:30.400897','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F821f2b74-9e5a-4281-ad5c-d665205193ee_6479bc6b21d14.jpg?alt=media&token=82942391-fbb5-486c-9b10-79c1cc9c3685',2,44),
      (186,'2025-09-08 15:09:30.401102','2025-09-08 15:09:30.401102','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fff0a97f5-b1f8-49aa-b192-ef59492caa7a_6479bc6d25896.jpg?alt=media&token=b830658a-28e4-4913-b172-7602c4fc6afe',3,44),
      (187,'2025-09-08 15:09:30.401333','2025-09-08 15:09:30.401333','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F6b59f176-d3b7-42ff-bc6c-88e612c16a70_6479bc6901a14.jpg?alt=media&token=ab3097b8-ea3f-470c-a5fc-beb588844ba6',4,44),
      (188,'2025-09-08 15:12:27.914182','2025-09-08 15:12:27.914182','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fa41898b9-f2b7-4a06-a72a-86b859ba7138_6479bab13a468.jpg?alt=media&token=98e8a420-6047-4b92-86bb-3f65ad9f86e1',0,46),
      (189,'2025-09-08 15:12:27.916009','2025-09-08 15:12:27.916009','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F0bff91c8-9527-44f9-ac21-1c9b747e2966_6479baaf25744.jpg?alt=media&token=a6cb1d57-8dff-4659-9381-f125a9d72fff',1,46),
      (190,'2025-09-08 15:12:27.916835','2025-09-08 15:12:27.916835','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F03191dd3-bce6-4e8a-a157-861b185f0909_6479bab24f534.jpg?alt=media&token=8f08689e-6cb0-4c98-b34b-ffedba965046',2,46),
      (191,'2025-09-08 15:17:02.465681','2025-09-08 15:17:02.465681','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fb9b9d0ba-84b6-45e9-baf4-4ad33f153c86_65ae0352e7b5c.jpg?alt=media&token=8dcc8cc6-3536-426a-a4b0-5861c29d0bac',0,47),
      (192,'2025-09-08 15:17:02.466071','2025-09-08 15:17:02.466071','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F113b9208-1460-4071-96bf-c2fa9408d3d6_657655ccdc15a.jpg?alt=media&token=20cfc9c5-873a-4e61-a2e6-6294f65af871',1,47),
      (193,'2025-09-08 15:17:02.466298','2025-09-08 15:17:02.466298','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fcb6a1820-027c-4fff-b1b8-ddb334b363c2_657655d322081.jpg?alt=media&token=dcadbb4f-6f58-4f6a-9256-b8ef682e6a8c',2,47),
      (194,'2025-09-08 15:17:02.466475','2025-09-08 15:17:02.466475','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Ffffbb898-b9b0-4798-a198-d456b2dd987b_657655d530091.jpg?alt=media&token=8ad0cee9-2eab-46ef-b1f8-c8ed16bc1778',3,47),
      (195,'2025-09-08 15:17:02.466650','2025-09-08 15:17:02.466650','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Facf55319-a198-4051-841e-d6d2445bb10f_657655dc47856.jpg?alt=media&token=01959bc8-c7ff-4193-b82d-7b9963fbf90e',4,47),
      (196,'2025-09-08 15:18:36.980806','2025-09-08 15:18:36.980806','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F566d02cb-38ba-4482-88dd-6b583166734c_65ae0193aa6b6.jpg?alt=media&token=70241604-0f42-4198-9f48-811e010b323c',0,48),
      (197,'2025-09-08 15:18:36.982632','2025-09-08 15:18:36.982632','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F232073cb-1f89-42c3-9755-2a74367b8aac_657652f1bae81.jpg?alt=media&token=29497b51-8e5a-49ca-94fd-ae8571ca13e8',1,48),
      (198,'2025-09-08 15:18:36.983939','2025-09-08 15:18:36.983939','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fffb8a912-dc0e-4eec-b9fd-a079b518eb5d_657652f2b1df1.jpg?alt=media&token=e53fb1b5-8503-443e-aa6d-de226b6389de',2,48),
      (199,'2025-09-08 15:18:36.985371','2025-09-08 15:18:36.985371','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fcf73ba4a-2427-4315-b412-7be5207e2956_657652f3b1451.jpg?alt=media&token=3d2ac849-466a-4d0c-90d1-5a56adc4cb19',3,48),
      (200,'2025-09-08 15:18:36.986316','2025-09-08 15:18:36.986316','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fca68a865-2904-46fb-9083-27d5ef0b0077_657652f5c850b.jpg?alt=media&token=82f040c5-ea68-4e43-aeef-84a965e1932f',4,48),
      (201,'2025-09-08 15:20:05.579639','2025-09-08 15:20:05.579639','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F596b4544-b476-4c59-ae23-0c31b591baab_6576544b9077f.jpg?alt=media&token=f22e4dae-1fdd-4dcf-a40f-1924487f6c35',0,49),
      (202,'2025-09-08 15:20:05.581111','2025-09-08 15:20:05.581111','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F570fdba5-1a25-4e92-ae31-29074d79d790_6576545a57f7a.jpg?alt=media&token=db442bdf-5bd5-422f-ab9b-2f98a792d8dd',1,49),
      (203,'2025-09-08 15:20:05.581935','2025-09-08 15:20:05.581935','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F0c5b2a48-2cc3-4a4b-9ae3-c9b02781bc07_6576545f75a9b.jpg?alt=media&token=14d34670-dca6-43ef-82a1-da2407243efd',2,49),
      (204,'2025-09-08 15:20:05.582669','2025-09-08 15:20:05.582669','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F3d1f8f6f-cd0a-419d-ae5e-b979ab6e9b2a_65765476dbb83.jpg?alt=media&token=352461d3-043c-4d11-a3a2-7263e94e571e',3,49),
      (205,'2025-09-08 15:20:05.583397','2025-09-08 15:20:05.583397','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F5ed6c864-ee0a-43a9-bd14-065e59227e53_6576545835a14.jpg?alt=media&token=58f0cbce-c3c2-449f-a419-8e2b9a825190',4,49),
      (206,'2025-09-08 15:22:31.888238','2025-09-08 15:22:31.888238','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Ff27f24c0-87ca-475d-bde6-7dfa4ed17d86_5c25e25a8189cdcf93a998ee96cd0f33.jpg?alt=media&token=40f0645f-60df-445a-b38f-626dd32909e3',0,50),
      (207,'2025-09-08 15:22:31.889996','2025-09-08 15:22:31.889996','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F1617787c-3a05-4e7c-9021-5d0a85ddbb0e_6b3503ae888158c5d36898c556ee0f27.jpg?alt=media&token=4f6b4836-d28a-4bd7-93a8-c227cab73b5f',1,50),
      (208,'2025-09-08 15:22:31.891103','2025-09-08 15:22:31.891103','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F26bd629c-5407-4ab8-bf19-4647fdea0bb2_19554dc945a5a9b914f1ff1ba32d0750.jpg?alt=media&token=ff142939-15a7-47af-bda6-1d430a1d48d9',2,50),
      (209,'2025-09-08 15:22:31.891915','2025-09-08 15:22:31.891915','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F05a8584d-04c0-4f52-a139-691d163d8817_d44e4a5ac8314c74e2a8b98e6ab470db.jpg?alt=media&token=edc8c107-3677-461b-8841-9d3b11462fee',3,50),
      (210,'2025-09-08 15:27:49.599948','2025-09-08 15:27:49.599948','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fd058a3de-d7a3-4880-ac2b-bcdc808d95bc_04e4f9ed3fb253438c4b905da694b43e.jpg?alt=media&token=f61382ae-f0a9-4b04-b49b-0420b3c2af57',0,51),
      (211,'2025-09-08 15:27:49.601706','2025-09-08 15:27:49.601706','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fc80851d4-d177-4c17-9813-252cb68e16ef_7b16efba034fd776d95a95a7c54af4bd.jpeg?alt=media&token=4822f6a9-6eb5-400e-b42d-5e214728a772',1,51),
      (212,'2025-09-08 15:27:49.603375','2025-09-08 15:27:49.603375','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F6a073b7c-dd45-4764-8c2a-b27ee829a10f_7572e0c3ed9e6e938388f618de2a0812.jpeg?alt=media&token=89e4ed16-79d9-43fb-897c-655f4aade50b',2,51),
      (213,'2025-09-08 15:29:40.438124','2025-09-08 15:29:40.438124','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fbc8894fc-4a86-4f06-882d-b85bbb5981b0_1a4f12374b24f0aa44b0f44df262620f.jpg?alt=media&token=dd794044-4180-4f84-8b63-abda03742044',0,52),
      (214,'2025-09-08 15:29:40.439937','2025-09-08 15:29:40.439937','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F5d47ba3f-ce94-488a-b239-d69fd09c8e57_7af117318af3cabd24f72f1cf1ce2c55.jpeg?alt=media&token=bb0a54a5-c5ce-4163-bfa6-dfe325f6b28f',1,52),
      (215,'2025-09-08 15:29:40.441146','2025-09-08 15:29:40.441146','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F39e38132-4be1-4ee5-aa2a-372da0435f28_457b898a23b90f52b327909f7358e35c.jpg?alt=media&token=1866906e-999e-49a0-86eb-b43e0e51c3a0',2,52),
      (216,'2025-09-08 15:29:40.441999','2025-09-08 15:29:40.441999','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F08eebc92-c37f-473a-8b70-1ec007eb81cb_4185112558d152ddab037fc5c1f13f17.jpeg?alt=media&token=6be601e6-cd79-45f1-a420-98a089db5916',3,52);

INSERT INTO room_image (
  image_id, created_at, updated_at, image_url, sort_order, room_id
) VALUES
(217,'2025-09-08 15:38:18.626506','2025-09-08 15:38:18.626506','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Faf952af4-883c-4166-9554-fb4a7790b37d_66d12381d3efe.jpg?alt=media&token=96800a90-3631-42b3-8cd1-b630bdf5f61c',0,53),
(218,'2025-09-08 15:38:18.627853','2025-09-08 15:38:18.627853','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fd58993e8-7255-4b7f-a06d-a7f383820176_63c6091a36526.jpg?alt=media&token=b14f0b70-abae-4a4d-b3eb-f50b38ef45ed',1,53),
(219,'2025-09-08 15:38:18.628474','2025-09-08 15:38:18.628474','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F68ec06a6-0235-4296-a559-b67521451962_63c6091f1d7ea.jpg?alt=media&token=5458ef8f-1658-4a04-9fba-81fea1b16916',2,53),
(220,'2025-09-08 15:38:18.628853','2025-09-08 15:38:18.628853','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fdbf82909-63a6-4e8a-93f4-976832f8c603_66d1237df0dbc.jpg?alt=media&token=59c5d7ad-f8c5-4d87-ac63-0a1340cb064a',3,53),
(221,'2025-09-08 15:39:22.025134','2025-09-08 15:39:22.025134','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fc43b2cab-a18d-4e14-85a6-b16a5074c7b0_66d122b0e5bde.jpg?alt=media&token=5fb835d3-5a57-4c42-a989-8b01d491dc0c',0,54),
(222,'2025-09-08 15:39:22.026949','2025-09-08 15:39:22.026949','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F590e2f92-d92f-4a0a-b65e-05499342a68f_63c6091a36526.jpg?alt=media&token=9c0bd876-6156-4d6d-804c-9e4a6afff9cf',1,54),
(223,'2025-09-08 15:39:22.028060','2025-09-08 15:39:22.028060','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F060d01eb-2ceb-4ab5-b857-26551fddebd4_63c6091f1d7ea.jpg?alt=media&token=009d3b84-8581-483d-b6de-929f478ccc42',2,54),
(224,'2025-09-08 15:39:22.028991','2025-09-08 15:39:22.028991','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F17dcfd7b-03c5-4e90-bf37-18c14892aeee_66d122af0ac03.jpg?alt=media&token=92dff0e6-f84c-4039-9fed-d51ae1fcb6d7',3,54),
(225,'2025-09-08 15:39:22.029674','2025-09-08 15:39:22.029674','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fcb695e3d-3004-4bd3-a476-49070c87133d_66d122b2dd330.jpg?alt=media&token=53d776e5-bf59-47fa-aa91-f7b34745a7fc',4,54),
(226,'2025-09-08 15:40:31.077631','2025-09-08 15:40:31.077631','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F07961ec3-97ae-4a46-b7e8-9d663a98d9b2_63bfac2a4bd25.jpg?alt=media&token=cc1d5985-8158-4086-9909-743d64859c1e',0,55),
(227,'2025-09-08 15:40:31.079028','2025-09-08 15:40:31.079028','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Ffdfecb16-4d8d-4b68-83ea-fa7ee3e58b2c_63bfac262daa6.jpg?alt=media&token=014f721d-b11c-40ce-a4d3-333d774ed329',1,55),
(228,'2025-09-08 15:40:31.079811','2025-09-08 15:40:31.079811','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F1041997a-257e-4e48-9e9e-fde459e3ecfb_63bfac272f28b.jpg?alt=media&token=16b6bfca-f136-4e6e-8405-249c9050bfb5',2,55),
(229,'2025-09-08 15:40:31.080383','2025-09-08 15:40:31.080383','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F520ca53f-9894-44ef-86f2-7ced81d28ad4_63bfac293d727.jpg?alt=media&token=f86e5007-dc0b-4871-a490-5e52a6855208',3,55),
(230,'2025-09-08 15:43:28.596930','2025-09-08 15:43:28.596930','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fc4b41a57-fac8-4b0c-9825-6e9eccb778a0_95.jpg?alt=media&token=c4471f3f-dd58-4404-9308-5c13601c406d',0,56),
(231,'2025-09-08 15:43:28.598754','2025-09-08 15:43:28.598754','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F226d9495-a6f6-43a4-b5b7-16ada85ef7a6_96.jpg?alt=media&token=ea499653-ebed-4be6-aea5-51fc083c48ac',1,56),
(232,'2025-09-08 15:43:28.599787','2025-09-08 15:43:28.599787','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F1189d40c-b37e-46d2-a42b-80fc7c581cdd_97.jpg?alt=media&token=abb9ae37-abe9-41ac-9330-391ff4af1160',2,56),
(233,'2025-09-08 15:43:28.600540','2025-09-08 15:43:28.600540','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Ff2ee94ea-cbd6-4459-9909-15db9fb7d1a9_98.jpg?alt=media&token=49d11cb7-78a5-4699-9932-20abf9db0689',3,56),
(234,'2025-09-08 15:45:58.423728','2025-09-08 15:45:58.423728','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F2aabaa89-5410-4c2a-8e61-b1d8d5d6380c_99.jpg?alt=media&token=f9fd815a-c8ba-45c1-9e94-85bfcc24ab13',0,57),
(235,'2025-09-08 15:45:58.424757','2025-09-08 15:45:58.424757','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fa1cf48f4-dcaf-4200-9987-114fb07aa36d_100.jpg?alt=media&token=dc980c21-314c-43a8-b34b-a77d986d43d6',1,57),
(236,'2025-09-08 15:45:58.425397','2025-09-08 15:45:58.425397','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F104436c6-0440-4bbf-a6ec-25ea7e369d68_101.jpg?alt=media&token=1914fbac-9c0c-4d24-a102-ab67480e6111',2,57),
(237,'2025-09-08 15:45:58.426022','2025-09-08 15:45:58.426022','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fafe38481-fcd1-4da6-a7d2-541b377d82c3_102.jpg?alt=media&token=303d8f46-c002-4577-9f10-0df7dca7df78',3,57),
(238,'2025-09-08 15:45:58.426736','2025-09-08 15:45:58.426736','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Ff8cf07f7-c509-470b-8762-f01779ddd4fd_103.jpg?alt=media&token=198cfc57-e5c9-4ce6-941c-eca39a0bab01',4,57),
(239,'2025-09-08 15:48:56.584266','2025-09-08 15:48:56.584266','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fbfbf92ff-3539-4fe2-970d-8b2359024dc1_104.jpg?alt=media&token=ab0b2cc4-2e50-46eb-9978-010e2fa0dd4b',0,58),
(240,'2025-09-08 15:48:56.584641','2025-09-08 15:48:56.584641','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Ff09347fa-9b35-4f3e-9808-289af13a626c_105.jpg?alt=media&token=448333ed-7619-42b3-a7f1-e3e65ac91ac0',1,58),
(241,'2025-09-08 15:48:56.584869','2025-09-08 15:48:56.584869','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F8ca3476e-c027-4911-b73f-99f24c037961_106.jpg?alt=media&token=f1c5fe76-a010-4752-acd9-23da9488ae25',2,58),
(242,'2025-09-08 15:48:56.585065','2025-09-08 15:48:56.585065','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F2374df75-ed95-476d-99bb-083846da18fc_107.jpg?alt=media&token=9ea98fd9-b794-4718-9cde-d8b4399ca1c2',3,58),
(243,'2025-09-08 15:48:56.585241','2025-09-08 15:48:56.585241','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2Fffd8de80-71e6-4c49-8656-cc0681c61d10_108.jpg?alt=media&token=d0129621-e681-4d2d-b05a-883cd21ba4e1',4,58),
(244,'2025-09-08 15:48:56.585409','2025-09-08 15:48:56.585409','https://firebasestorage.googleapis.com/v0/b/honeyrest-7fb60.firebasestorage.app/o/room%2F6cc5d829-46d7-4fda-815b-ddf0b6437270_109.jpg?alt=media&token=1bf22180-ac64-4b32-a780-3b9a93effdb8',5,58);



-- 1. 경주 오소한옥스테이
INSERT INTO cancellation_policy (created_at, updated_at, policy_name, detail, days_before, refund_rate, accommodation_id) VALUES
                                                                                                    (NOW(), NOW(), '체크인 7일 전까지 무료 취소', '체크인 예정일 기준 7일 전까지는 위약금 없이 전액 환불 가능합니다.', 7, 100.00, 1),
                                                                                                    (NOW(), NOW(), '체크인 3일 전까지 취소', '체크인 3일 전까지 취소 시 결제 금액의 50% 환불이 가능합니다.', 3, 50.00, 1),
                                                                                                    (NOW(), NOW(), '체크인 당일 취소 및 No-show', '체크인 당일 취소 또는 No-show의 경우 환불이 불가합니다.', 0, 0.00, 1);

-- 2. 뉴시스 해운대 레지던스
INSERT INTO cancellation_policy (created_at, updated_at, policy_name, detail, days_before, refund_rate, accommodation_id) VALUES
                                                                                                    (NOW(), NOW(), '체크인 5일 전까지 무료 취소', '체크인 5일 전까지는 결제금액 전액 환불이 가능합니다.', 5, 100.00, 2),
                                                                                                    (NOW(), NOW(), '체크인 2일 전까지 취소', '체크인 2일 전까지 취소 시 결제 금액의 70% 환불이 가능합니다.', 2, 70.00, 2),
                                                                                                    (NOW(), NOW(), '당일 취소 불가', '체크인 당일 취소 및 No-show 시 환불 불가합니다.', 0, 0.00, 2);

-- 3. 라마다 앙코르 바이 윈덤 부산역
INSERT INTO cancellation_policy (created_at, updated_at, policy_name, detail, days_before, refund_rate, accommodation_id) VALUES
                                                                                                    (NOW(), NOW(), '체크인 7일 전까지 무료 취소', '체크인 7일 전까지는 위약금 없이 전액 환불이 가능합니다.', 7, 100.00, 3),
                                                                                                    (NOW(), NOW(), '체크인 1일 전까지 취소', '체크인 하루 전까지 취소 시 결제 금액의 50% 환불 가능합니다.', 1, 50.00, 3),
                                                                                                    (NOW(), NOW(), '당일 취소 불가', '체크인 당일 이후 취소는 환불이 불가합니다.', 0, 0.00, 3);

-- 4. 속초 호텔 더 블루테라
INSERT INTO cancellation_policy (created_at, updated_at, policy_name, detail, days_before, refund_rate, accommodation_id) VALUES
                                                                                                    (NOW(), NOW(), '체크인 5일 전까지 무료 취소', '체크인 5일 전까지 취소 시 전액 환불 가능합니다.', 5, 100.00, 4),
                                                                                                    (NOW(), NOW(), '체크인 2일 전까지 취소', '체크인 2일 전까지 취소 시 50% 환불됩니다.', 2, 50.00, 4),
                                                                                                    (NOW(), NOW(), '당일 취소 불가', '체크인 당일 취소 또는 No-show 시 환불이 불가합니다.', 0, 0.00, 4);

-- 5. 전주 소소한 한옥
INSERT INTO cancellation_policy (created_at, updated_at, policy_name, detail, days_before, refund_rate, accommodation_id) VALUES
                                                                                                    (NOW(), NOW(), '체크인 7일 전까지 무료 취소', '체크인 7일 전까지는 위약금 없이 전액 환불 가능합니다.', 7, 100.00, 5),
                                                                                                    (NOW(), NOW(), '체크인 3일 전까지 취소', '체크인 3일 전까지는 50% 환불됩니다.', 3, 50.00, 5),
                                                                                                    (NOW(), NOW(), '당일 취소 불가', '체크인 당일 이후 취소 시 환불 불가합니다.', 0, 0.00, 5);

-- 6. 수원 구운동 앤드 호텔
INSERT INTO cancellation_policy (created_at, updated_at, policy_name, detail, days_before, refund_rate, accommodation_id) VALUES
                                                                                                    (NOW(), NOW(), '체크인 5일 전까지 무료 취소', '체크인 5일 전까지 취소 시 전액 환불 가능합니다.', 5, 100.00, 6),
                                                                                                    (NOW(), NOW(), '체크인 1일 전까지 취소', '체크인 하루 전까지 취소 시 70% 환불됩니다.', 1, 70.00, 6),
                                                                                                    (NOW(), NOW(), '당일 취소 불가', '체크인 당일 이후 취소 및 No-show는 환불 불가합니다.', 0, 0.00, 6);

-- 7. 신라스테이 전주 한옥마을
INSERT INTO cancellation_policy (created_at, updated_at, policy_name, detail, days_before, refund_rate, accommodation_id) VALUES
                                                                                                    (NOW(), NOW(), '체크인 7일 전까지 무료 취소', '체크인 7일 전까지는 전액 환불 가능합니다.', 7, 100.00, 7),
                                                                                                    (NOW(), NOW(), '체크인 2일 전까지 취소', '체크인 2일 전까지는 결제 금액의 50% 환불됩니다.', 2, 50.00, 7),
                                                                                                    (NOW(), NOW(), '당일 취소 불가', '체크인 당일 이후 취소 시 환불 불가합니다.', 0, 0.00, 7);

-- 8. 대전 아늑 호텔 앤 스파 대전유성점
INSERT INTO cancellation_policy (created_at, updated_at, policy_name, detail, days_before, refund_rate, accommodation_id) VALUES
                                                                                                    (NOW(), NOW(), '체크인 3일 전까지 무료 취소', '체크인 3일 전까지 취소 시 전액 환불 가능합니다.', 3, 100.00, 8),
                                                                                                    (NOW(), NOW(), '체크인 1일 전까지 취소', '체크인 하루 전까지는 50% 환불됩니다.', 1, 50.00, 8),
                                                                                                    (NOW(), NOW(), '당일 취소 불가', '체크인 당일 이후 취소 및 No-show는 환불 불가합니다.', 0, 0.00, 8);

-- 9. 코오롱 씨클라우드 호텔
INSERT INTO cancellation_policy (created_at, updated_at, policy_name, detail, days_before, refund_rate, accommodation_id) VALUES
                                                                                                    (NOW(), NOW(), '체크인 5일 전까지 무료 취소', '체크인 5일 전까지는 위약금 없이 전액 환불 가능합니다.', 5, 100.00, 9),
                                                                                                    (NOW(), NOW(), '체크인 2일 전까지 취소', '체크인 2일 전까지는 70% 환불됩니다.', 2, 70.00, 9),
                                                                                                    (NOW(), NOW(), '당일 취소 불가', '체크인 당일 이후에는 환불이 불가합니다.', 0, 0.00, 9);

-- 10. 홈 마리나 속초
INSERT INTO cancellation_policy (created_at, updated_at, policy_name, detail, days_before, refund_rate, accommodation_id) VALUES
                                                                                                    (NOW(), NOW(), '체크인 7일 전까지 무료 취소', '체크인 7일 전까지 취소 시 결제금액 전액 환불이 가능합니다.', 7, 100.00, 10),
                                                                                                    (NOW(), NOW(), '체크인 3일 전까지 취소', '체크인 3일 전까지는 결제금액의 50% 환불 가능합니다.', 3, 50.00, 10),
                                                                                                    (NOW(), NOW(), '당일 취소 불가', '체크인 당일 및 No-show 시 환불 불가합니다.', 0, 0.00, 10);

-- 11. 코델리아 호텔 (제주)
INSERT INTO cancellation_policy (created_at, updated_at, policy_name, detail, days_before, refund_rate, accommodation_id) VALUES
                                                                                                    (NOW(), NOW(), '체크인 5일 전까지 무료 취소', '체크인 5일 전까지는 전액 환불 가능합니다.', 5, 100.00, 11),
                                                                                                    (NOW(), NOW(), '체크인 2일 전까지 취소', '체크인 2일 전까지는 결제금액의 70% 환불이 가능합니다.', 2, 70.00, 11),
                                                                                                    (NOW(), NOW(), '당일 취소 불가', '체크인 당일 이후 취소 및 No-show 시 환불 불가합니다.', 0, 0.00, 11);

-- 12. 강릉 더스테이 은파
INSERT INTO cancellation_policy (created_at, updated_at, policy_name, detail, days_before, refund_rate, accommodation_id) VALUES
                                                                                                    (NOW(), NOW(), '체크인 7일 전까지 무료 취소', '체크인 7일 전까지는 위약금 없이 전액 환불 가능합니다.', 7, 100.00, 12),
                                                                                                    (NOW(), NOW(), '체크인 3일 전까지 취소', '체크인 3일 전까지 취소 시 50% 환불됩니다.', 3, 50.00, 12),
                                                                                                    (NOW(), NOW(), '당일 취소 불가', '체크인 당일 이후 취소 및 No-show 시 환불 불가합니다.', 0, 0.00, 12);

-- 13. 대구 하운드 호텔
INSERT INTO cancellation_policy (created_at, updated_at, policy_name, detail, days_before, refund_rate, accommodation_id) VALUES
                                                                                                    (NOW(), NOW(), '체크인 5일 전까지 무료 취소', '체크인 5일 전까지는 전액 환불 가능합니다.', 5, 100.00, 13),
                                                                                                    (NOW(), NOW(), '체크인 2일 전까지 취소', '체크인 2일 전까지는 70% 환불됩니다.', 2, 70.00, 13),
                                                                                                    (NOW(), NOW(), '당일 취소 불가', '체크인 당일 이후에는 환불이 불가합니다.', 0, 0.00, 13);

-- 14. 대구 메리어트 호텔
INSERT INTO cancellation_policy (created_at, updated_at, policy_name, detail, days_before, refund_rate, accommodation_id) VALUES
                                                                                                    (NOW(), NOW(), '체크인 7일 전까지 무료 취소', '체크인 7일 전까지 취소 시 전액 환불 가능합니다.', 7, 100.00, 14),
                                                                                                    (NOW(), NOW(), '체크인 1일 전까지 취소', '체크인 하루 전까지는 결제 금액의 50% 환불 가능합니다.', 1, 50.00, 14),
                                                                                                    (NOW(), NOW(), '당일 취소 불가', '체크인 당일 이후 환불 불가합니다.', 0, 0.00, 14);

-- 15. 아바니 센트럴 부산
INSERT INTO cancellation_policy (created_at, updated_at, policy_name, detail, days_before, refund_rate, accommodation_id) VALUES
                                                                                                    (NOW(), NOW(), '체크인 5일 전까지 무료 취소', '체크인 5일 전까지는 전액 환불 가능합니다.', 5, 100.00, 15),
                                                                                                    (NOW(), NOW(), '체크인 2일 전까지 취소', '체크인 2일 전까지는 결제 금액의 70% 환불됩니다.', 2, 70.00, 15),
                                                                                                    (NOW(), NOW(), '당일 취소 불가', '체크인 당일 이후 환불 불가합니다.', 0, 0.00, 15);

-- 16. 노보텔 앰배서더 동대문
INSERT INTO cancellation_policy (created_at, updated_at, policy_name, detail, days_before, refund_rate, accommodation_id) VALUES
                                                                                                    (NOW(), NOW(), '체크인 7일 전까지 무료 취소', '체크인 7일 전까지는 위약금 없이 전액 환불 가능합니다.', 7, 100.00, 16),
                                                                                                    (NOW(), NOW(), '체크인 3일 전까지 취소', '체크인 3일 전까지는 결제 금액의 50% 환불 가능합니다.', 3, 50.00, 16),
                                                                                                    (NOW(), NOW(), '당일 취소 불가', '체크인 당일 이후 환불 불가합니다.', 0, 0.00, 16);

-- 17. 효이스테이 더 광안
INSERT INTO cancellation_policy (created_at, updated_at, policy_name, detail, days_before, refund_rate, accommodation_id) VALUES
                                                                                                    (NOW(), NOW(), '체크인 5일 전까지 무료 취소', '체크인 5일 전까지는 전액 환불 가능합니다.', 5, 100.00, 17),
                                                                                                    (NOW(), NOW(), '체크인 2일 전까지 취소', '체크인 2일 전까지는 결제 금액의 70% 환불됩니다.', 2, 70.00, 17),
                                                                                                    (NOW(), NOW(), '당일 취소 불가', '체크인 당일 이후 취소 및 No-show는 환불 불가합니다.', 0, 0.00, 17);

-- 18. 그랜드 조선 제주
INSERT INTO cancellation_policy (created_at, updated_at, policy_name, detail, days_before, refund_rate, accommodation_id) VALUES
                                                                                                    (NOW(), NOW(), '체크인 7일 전까지 무료 취소', '체크인 7일 전까지 취소 시 전액 환불 가능합니다.', 7, 100.00, 18),
                                                                                                    (NOW(), NOW(), '체크인 3일 전까지 취소', '체크인 3일 전까지는 50% 환불됩니다.', 3, 50.00, 18),
                                                                                                    (NOW(), NOW(), '당일 취소 불가', '체크인 당일 이후 환불 불가합니다.', 0, 0.00, 18);

-- 19. 양양 더 앤 리조트 호텔&스파
INSERT INTO cancellation_policy (created_at, updated_at, policy_name, detail, days_before, refund_rate, accommodation_id) VALUES
                                                                                                    (NOW(), NOW(), '체크인 5일 전까지 무료 취소', '체크인 5일 전까지는 전액 환불 가능합니다.', 5, 100.00, 19),
                                                                                                    (NOW(), NOW(), '체크인 2일 전까지 취소', '체크인 2일 전까지는 결제 금액의 70% 환불됩니다.', 2, 70.00, 19),
                                                                                                    (NOW(), NOW(), '당일 취소 불가', '체크인 당일 이후 취소 및 No-show 시 환불 불가합니다.', 0, 0.00, 19);

-- 20. 나인트리 프리미어 로카우스 호텔 서울 용산
INSERT INTO cancellation_policy (created_at, updated_at, policy_name, detail, days_before, refund_rate, accommodation_id) VALUES
                                                                                                    (NOW(), NOW(), '체크인 7일 전까지 무료 취소', '체크인 7일 전까지는 전액 환불 가능합니다.', 7, 100.00, 20),
                                                                                                    (NOW(), NOW(), '체크인 3일 전까지 취소', '체크인 3일 전까지 취소 시 50% 환불됩니다.', 3, 50.00, 20),
                                                                                                    (NOW(), NOW(), '당일 취소 불가', '체크인 당일 이후 환불 불가합니다.', 0, 0.00, 20);