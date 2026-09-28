import { test, expect } from '@playwright/test';
import { adminToast, login } from './helpers';

// (b) 객실 등록 → 목록에 표시
test('객실을 등록하면 객실 목록에 나타난다', async ({ page }) => {
  const roomName = `E2E 테스트룸 ${Date.now()}`;
  await login(page, 'company');

  await page.goto('/admin/rooms/add');
  // 로그인 회사(1) 소유 숙소 [1] 허니레스트 제주 오션뷰
  await page.locator('select[name="accommodationId"]').selectOption('1');
  await page.locator('input[name="roomName"]').fill(roomName);
  await page.locator('select[name="type"]').selectOption('Deluxe');
  await page.locator('input[name="price"]').fill('99000');
  await page.locator('input[name="totalRooms"]').fill('3');
  await page.locator('input[name="standardOccupancy"]').fill('2');
  await page.locator('input[name="maxOccupancy"]').fill('3');
  await page.getByRole('button', { name: '저장' }).click();

  await expect(page).toHaveURL(/\/admin\/rooms\/list/);
  await expect(adminToast(page)).toHaveText('객실이 등록되었습니다.');
  await expect(page.locator('.card-title', { hasText: roomName })).toBeVisible();
});
