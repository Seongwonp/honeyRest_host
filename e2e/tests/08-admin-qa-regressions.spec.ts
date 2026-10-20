import path from 'node:path';
import fs from 'node:fs';
import os from 'node:os';
import { test, expect, type Page } from '@playwright/test';
import { acceptDialogs, adminToast, isoDate, login } from './helpers';

// 관리자 화면 회귀 시나리오: 총관리자 메뉴 화면 렌더링·폼 동작, 업체 관리자 숙소 등록·취소 요청 승인/거부

const PNG = path.join(os.tmpdir(), 'honeyrest-e2e-1px.png');
fs.writeFileSync(PNG, Buffer.from(
  'iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mP8z8BQDwAEhQGAhKmMIQAAAABJRU5ErkJggg==', 'base64'));

/** 서버 오류 화면(500)·스크립트 오류 없이 열리는지 */
async function openOk(page: Page, url: string) {
  const errors: string[] = [];
  page.on('pageerror', (e) => errors.push(e.message));
  const res = await page.goto(url);
  expect(res?.status(), url).toBe(200);
  await expect(page.locator('.error-code')).toHaveCount(0);
  expect(errors, `${url} 스크립트 오류`).toEqual([]);
}

test.describe('총관리자 화면', () => {
  test.beforeEach(async ({ page }) => {
    acceptDialogs(page);
    await login(page, 'super');
  });

  test('사이드바의 모든 화면이 오류 없이 열린다', async ({ page }) => {
    // owner/fragments/sidebar.html 의 메뉴
    for (const url of [
      '/owner/dashboard', '/owner/company/list', '/owner/company/create', '/owner/company/inActive/list',
      '/owner/accommodation/list', '/owner/accommodation/create', '/owner/accommodation/inActive/list',
      '/owner/room/list', '/owner/room/create', '/owner/room/inActive/list',
      '/owner/reservation/list', '/owner/reservation/inActive/list', '/owner/calendar/list',
      '/owner/user/list', '/owner/review/list', '/owner/coupon/list',
      '/owner/sales/month', '/owner/sales/day', '/owner/system/errors',
    ]) {
      await openOk(page, url);
    }
  });

  test('숙소 등록: 메인 지역을 고르면 서브 지역이 채워지고 등록된다', async ({ page }) => {
    await openOk(page, '/owner/accommodation/create');
    await page.selectOption('select[name=companyId]', '1');
    await page.selectOption('#mainRegionId', { index: 1 });
    await expect(page.locator('#subRegionId option')).not.toHaveCount(1);
    await page.selectOption('#subRegionId', { index: 1 });
    await page.fill('input[name=name]', 'E2E 총관리자 숙소');
    await page.fill('#addressInput', '서울 중구 1');
    await page.fill('input[name=checkInTime]', '2025-01-01T15:00');
    await page.fill('input[name=checkOutTime]', '2025-01-01T11:00');
    await page.fill('input[name=minPrice]', '80000');
    await page.setInputFiles('input[name=file]', PNG);
    await page.selectOption('select[name=status]', 'INACTIVE');
    await page.locator('form button[type=submit]').last().click();
    await expect(page).toHaveURL(/\/owner\/accommodation\/inActive\/list/);
    await expect(page.locator('tr', { hasText: 'E2E 총관리자 숙소' })).toBeVisible();
  });

  test('객실 등록(이미지) → 판매 중지 → 다시 활성화', async ({ page }) => {
    await openOk(page, '/owner/room/create?accommodationId=1');
    await page.fill('#name', 'E2E 객실');
    await page.fill('#price', '99000');
    await page.fill('#standardOccupancy', '2');
    await page.fill('#maxOccupancy', '3');
    await page.fill('#totalRooms', '2');
    await page.fill('#bedInfo', '더블 침대 1개');
    await page.setInputFiles('input[name=file]', PNG);
    await page.locator('form button[type=submit]').last().click();
    const row = page.locator('tr', { hasText: 'E2E 객실' });
    await expect(row.locator('img.thumb')).toBeVisible();

    await row.getByRole('button', { name: '판매 중지' }).click();
    await expect(adminToast(page)).toHaveText(/판매 중지/);
    await page.goto('/owner/room/inActive/list');
    await page.locator('tr', { hasText: 'E2E 객실' }).getByRole('button', { name: '활성화' }).click();
    await expect(adminToast(page)).toHaveText(/판매 가능/);
  });

  test('리뷰 답글 저장', async ({ page }) => {
    await openOk(page, '/owner/review/list');
    await page.getByRole('link', { name: '답글' }).first().click();
    await page.fill('#reply', 'E2E 총관리자 답글');
    await page.getByRole('button', { name: '저장' }).click();
    await expect(adminToast(page)).toHaveText(/저장/);
    await expect(page.locator('#reply')).toHaveValue('E2E 총관리자 답글');
  });

  test('예약 등록: 회원·객실을 고르면 박 수만큼 금액이 계산된다', async ({ page }) => {
    await openOk(page, '/owner/reservation/create');
    await page.selectOption('#roomSelect', '1');
    await page.selectOption('#userSelect', { index: 1 });
    await page.fill('#checkInDate', isoDate(90));
    await page.fill('#checkOutDate', isoDate(92));
    await expect(page.locator('#originalPrice')).toHaveValue(/240,000원 \(2박\)/);
    await page.locator('form button[type=submit]').last().click();
    await expect(page).toHaveURL(/\/owner\/reservation\/list/);
    await expect(adminToast(page)).toHaveText(/예약을 등록했습니다/);
  });

  test('쿠폰 등록: 활성 체크가 반영되고 특정 숙소 대상으로 저장된다', async ({ page }) => {
    await openOk(page, '/owner/coupon/create');
    await page.fill('#name', 'E2E 쿠폰');
    await page.fill('#code', `E2E${Date.now() % 100000}`);
    await page.selectOption('#discountType', 'PERCENTAGE');
    await page.fill('#discountValue', '10');
    await page.fill('#startDate', `${isoDate(0)}T00:00`);
    await page.fill('#endDate', `${isoDate(30)}T23:59`);
    await page.selectOption('#targetType', 'ACCOMMODATION');
    await page.selectOption('#targetId', '1');
    await page.locator('form button[type=submit]').last().click();
    const row = page.locator('tr', { hasText: 'E2E 쿠폰' });
    await expect(row).toContainText('10%');
    await expect(row).toContainText('특정 숙소 #1');
    await expect(row).toContainText('활성');
  });

  test('에러 로그 해결 버튼이 동작한다', async ({ page }) => {
    await openOk(page, '/owner/system/errors?filter=unresolved');
    const before = await page.locator('.resolve-btn').count();
    expect(before).toBeGreaterThan(0);
    await page.locator('.resolve-btn').first().click();
    await expect(page.locator('.resolve-btn')).toHaveCount(before - 1);
  });
});

test.describe('업체 관리자 화면', () => {
  test.beforeEach(async ({ page }) => {
    acceptDialogs(page);
    await login(page, 'company');
  });

  test('숙소 등록은 회사 ID 를 입력하지 않아도 된다', async ({ page }) => {
    await openOk(page, '/admin/accommodations/add');
    const form = page.locator('#accForm');
    await expect(form.locator('input[name=companyId]')).toHaveValue('1');
    await form.locator('select[name=categoryId]').selectOption({ index: 1 });
    await page.locator('#mainRegion').selectOption({ index: 1 });
    await expect(page.locator('#subRegion option')).not.toHaveCount(1);
    await page.locator('#subRegion').selectOption({ index: 1 });
    await form.locator('input[name=name]').fill('E2E 업체 숙소');
    await form.locator('input[name=address]').fill('제주시 1');
    await form.locator('input[name=checkInTime]').fill('2025-01-01T15:00');
    await form.locator('input[name=checkOutTime]').fill('2025-01-01T11:00');
    await form.locator('input[name=minPrice]').fill('90000');
    await page.setInputFiles('#fileInput', PNG);
    await form.locator('button[type=submit]').click();
    await expect(page).toHaveURL(/\/admin\/accommodations\/list\?status=PENDING/);
    await expect(page.getByText('E2E 업체 숙소').first()).toBeVisible();
  });

  test('취소 요청 거부(사유 모달)와 승인(즉시)', async ({ page }) => {
    await openOk(page, '/admin/reservations/cancel-requests');
    const rows = page.locator('.js-approve');
    const before = await rows.count();
    expect(before).toBeGreaterThanOrEqual(2);

    await page.locator('[data-action=reject]').first().click();
    await page.locator('#reasonModal textarea[name=reason]').fill('E2E 거부');
    await page.locator('#reasonSubmitBtn').click();
    await expect(page).toHaveURL(/\/admin\/reservations\/cancel-requests/);
    await expect(page.locator('.js-approve')).toHaveCount(before - 1);

    await page.locator('.js-approve').first().click();
    await expect(page.locator('.js-approve')).toHaveCount(before - 2);
  });

  test('매출 리포트 단위 전환 시 표가 채워진다', async ({ page }) => {
    await openOk(page, '/admin/reports/sales');
    await page.click('[data-group="monthly"]');
    await expect(page.locator('#rowsTbody tr').first()).toBeVisible();
    await expect(page.locator('#rowsTbody')).not.toContainText('NaN');
  });
});
