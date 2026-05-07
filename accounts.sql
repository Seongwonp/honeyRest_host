-- ============================================================
-- HoneyRest 테스트 계정 INSERT SQL
-- 실행 순서: insert.sql (company 데이터) 이후에 실행
-- ============================================================
-- 비밀번호
--   COMPANY_ADMIN : company1234
--   SUPER_ADMIN   : admin1234
-- ============================================================

INSERT INTO user (email, password_hash, name, phone, role, status, is_verified, marketing_agree, point)
VALUES
    -- 허니레스트컴퍼니 (company_id=1)
    ('contact@honeyrest.com',
     '$2b$10$mytdzJlAP8eU5.pVRblZbOL8ADUW4.UvgJ.lA0ELzFutdRVjxERtq',
     '박성원', '010-1234-5678', 'COMPANY_ADMIN', 'ACTIVE', true, false, 0),

    -- 바다사랑 (company_id=2)
    ('info@seasidehotel.com',
     '$2b$10$mytdzJlAP8eU5.pVRblZbOL8ADUW4.UvgJ.lA0ELzFutdRVjxERtq',
     '김바다', '010-2345-6789', 'COMPANY_ADMIN', 'ACTIVE', true, false, 0),

    -- 어반스테이 (company_id=3)
    ('info@urbanstay.com',
     '$2b$10$mytdzJlAP8eU5.pVRblZbOL8ADUW4.UvgJ.lA0ELzFutdRVjxERtq',
     '이도시', '010-3456-7890', 'COMPANY_ADMIN', 'ACTIVE', true, false, 0),

    -- 한옥호스피탈리티 (company_id=4)
    ('info@hanokhospitality.com',
     '$2b$10$mytdzJlAP8eU5.pVRblZbOL8ADUW4.UvgJ.lA0ELzFutdRVjxERtq',
     '박한옥', '010-4567-8901', 'COMPANY_ADMIN', 'ACTIVE', true, false, 0),

    -- 자연쉼터 (company_id=5)
    ('info@natureretreat.com',
     '$2b$10$mytdzJlAP8eU5.pVRblZbOL8ADUW4.UvgJ.lA0ELzFutdRVjxERtq',
     '최자연', '010-5678-9012', 'COMPANY_ADMIN', 'ACTIVE', true, false, 0),

    -- 경주스테이 (company_id=6)
    ('info@gyeongjustay.com',
     '$2b$10$mytdzJlAP8eU5.pVRblZbOL8ADUW4.UvgJ.lA0ELzFutdRVjxERtq',
     '김경주', '010-6789-0123', 'COMPANY_ADMIN', 'ACTIVE', true, false, 0),

    -- 총관리자
    ('admin@honeyrest.com',
     '$2b$10$7X8/kNIHAaxcszpnWo0qvO6wIrLl5oNu4sqW4MJ7hBz57xHUDCKwi',
     'HoneyRest관리자', '010-0000-0000', 'SUPER_ADMIN', 'ACTIVE', true, false, 0);
