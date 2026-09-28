import { test, expect } from '@playwright/test';
import { acceptDialogs, adminToast, login } from './helpers';

// (c) 예약 목록 → 상세 → COMPLETED 로 상태 변경
test('확정 예약을 상세 화면에서 체크아웃 완료(COMPLETED) 처리한다', async ({ page }) => {
  acceptDialogs(page);
  await login(page, 'company');

  await page.goto('/admin/reservations/my?status=CONFIRMED');
  const firstDetail = page.getByRole('link', { name: '상세' }).first();
  await expect(firstDetail).toBeVisible();
  await firstDetail.click();

  await expect(page).toHaveURL(/\/admin\/reservations\/\d+$/);
  const status = page.getByTestId('reservation-status');
  await expect(status).toHaveText('CONFIRMED');
  const detailUrl = page.url();

  await page.getByTestId('btn-complete').click();

  // 같은 상세 화면으로 돌아와 성공 토스트와 COMPLETED 상태를 보여준다
  await expect(page).toHaveURL(detailUrl);
  await expect(adminToast(page)).toHaveText('체크아웃 완료 처리');
  await expect(status).toHaveText('COMPLETED');
  await expect(page.getByTestId('btn-complete')).toHaveCount(0);
});
