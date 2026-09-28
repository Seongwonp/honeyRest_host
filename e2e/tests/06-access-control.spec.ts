import { test, expect } from '@playwright/test';
import { login } from './helpers';

// 시드: 숙소 4, 5 = 회사 2(씨사이드 호텔) 소유, 숙소 3 = 회사 1 소유 PENDING
const OTHER_COMPANY_ACCOMMODATION = 4;

test.describe('회사 관리자 접근 제어', () => {
  test.beforeEach(async ({ page }) => {
    await login(page, 'company');
  });

  // (f) 다른 회사의 숙소는 열 수 없다 (403)
  test('다른 회사 숙소의 상세/수정 화면은 403 이다', async ({ page }) => {
    for (const path of [
      `/admin/accommodations/detail/${OTHER_COMPANY_ACCOMMODATION}`,
      `/admin/accommodations/edit/${OTHER_COMPANY_ACCOMMODATION}`,
    ]) {
      const response = await page.goto(path);
      expect(response?.status(), path).toBe(403);
      await expect(page.locator('.error-code')).toHaveText('403');
    }
  });

  // 승인 우회 차단: 수정 폼에서 ACTIVE 를 고를 수 없다 (서버 측 가드는 단위 테스트로 검증)
  test('승인 대기 숙소의 수정 폼에는 ACTIVE 선택지가 없다', async ({ page }) => {
    await page.goto('/admin/accommodations/edit/3');
    const statusSelect = page.locator('select[name="status"]');
    await expect(statusSelect).toBeVisible();
    await expect(statusSelect.locator('option[value="ACTIVE"]')).toHaveCount(0);
    await expect(statusSelect.locator('option[value="PENDING"]')).toHaveCount(1);
    await expect(statusSelect.locator('option[value="INACTIVE"]')).toHaveCount(1);
  });
  test('운영 중 숙소의 수정 폼은 ACTIVE 를 "현재 상태 유지"로만 보여준다', async ({ page }) => {
    await page.goto('/admin/accommodations/edit/1');
    const statusSelect = page.locator('select[name="status"]');
    await expect(statusSelect).toHaveValue('ACTIVE');
    await expect(statusSelect.locator('option[value="ACTIVE"]')).toHaveText(/현재 상태 유지/);
  });
});
