import { test, expect } from '@playwright/test';
import { login } from './helpers';

// (a) 회사 관리자 로그인 → 대시보드 KPI 노출
test('회사 관리자 로그인 후 대시보드 KPI 가 보인다', async ({ page }) => {
  await login(page, 'company');

  const kpis = page.locator('.kpi');
  await expect(kpis).toHaveCount(3);
  for (const title of ['등록 숙소 수', '예약 건수', '총 객실 수']) {
    const card = kpis.filter({ has: page.locator('.kpi-title', { hasText: title }) });
    await expect(card).toBeVisible();
    // 시드 데이터 기준 모두 1 이상의 숫자가 표시돼야 한다
    await expect(card.locator('.kpi-value')).toHaveText(/^\s*[1-9]\d*\s*$/);
  }
});

// 알림 배지 모델 값(_notifyCancelCount)이 리다이렉트 쿼리스트링으로 새지 않는다
test('가격 캘린더 진입 리다이렉트 URL 에 내부 모델 값이 붙지 않는다', async ({ page }) => {
  await login(page, 'company');
  await page.goto('/admin/price/page');
  await expect(page).toHaveURL(/\/admin\/price\/page\?companyId=\d+&ym=\d{4}-\d{2}$/);
  expect(page.url()).not.toContain('_notifyCancelCount');
});
