import { test, expect } from '@playwright/test';
import { acceptDialogs, login } from './helpers';

// (e) 총관리자 로그인 → PENDING 숙소 승인 → ACTIVE
test('총관리자가 승인 대기 숙소를 승인하면 ACTIVE 가 된다', async ({ page }) => {
  acceptDialogs(page);
  await login(page, 'super');

  await page.goto('/owner/accommodation/inActive/list');
  // 승인 버튼이 있는(= PENDING) 첫 번째 행
  const row = page.locator('tbody tr').filter({ has: page.locator('[data-testid^="approve-"]') }).first();
  await expect(row).toBeVisible();
  await expect(row.locator('.badge-chip')).toHaveText('PENDING');
  const approveButton = row.locator('[data-testid^="approve-"]');
  const name = (await row.locator('.fw-semibold').first().innerText()).trim();

  await approveButton.click();

  // 승인 후 운영(ACTIVE) 목록으로 이동하고, 해당 숙소가 ACTIVE 로 보인다
  await expect(page).toHaveURL(/\/owner\/accommodation\/list/);
  const approvedRow = page.locator('tr', { has: page.locator('.fw-semibold', { hasText: name }) });
  await expect(approvedRow.locator('.badge-chip')).toHaveText('ACTIVE');

  // 비활성/대기 목록에서는 사라진다
  await page.goto('/owner/accommodation/inActive/list');
  await expect(page.locator('tr', { has: page.locator('.fw-semibold', { hasText: name }) })).toHaveCount(0);
});
