import { test, expect, type Page } from '@playwright/test';
import { adminToast, isoDate, login } from './helpers';

// 시드: room 3 = '오션 스위트'(숙소 1, 회사 1), total_rooms = 2
const ROOM_ID = '3';
const TOTAL_ROOMS = 2;

async function submitReservation(page: Page, checkIn: string, checkOut: string, guest: string) {
  await page.goto('/admin/reservations/new');
  await page.locator('input[name="userId"]').fill('1');
  await page.locator('input[name="roomId"]').fill(ROOM_ID);
  await page.locator('select[name="status"]').selectOption('CONFIRMED');
  await page.locator('input[name="checkInDate"]').fill(checkIn);
  await page.locator('input[name="checkOutDate"]').fill(checkOut);
  await page.locator('input[name="guestCount"]').fill('2');
  await page.locator('input[name="price"]').fill('280000');
  await page.locator('input[name="guestName"]').fill(guest);
  await page.locator('input[name="guestPhone"]').fill('010-0000-0000');
  await page.getByRole('button', { name: '등록' }).click();
}

// (d) 겹치는 예약으로 객실을 채우면 다음 예약은 재고 부족(409 의미) flash 로 거절된다
test('객실이 가득 찬 기간의 추가 예약은 재고 부족 메시지로 거절된다', async ({ page }) => {
  await login(page, 'company');

  // 시드/다른 시나리오와 겹치지 않도록 먼 미래의 임의 기간을 쓴다 (서버 재사용 시에도 충돌 방지)
  const offset = 200 + Math.floor(Math.random() * 1500);
  const checkIn = isoDate(offset);
  const checkOut = isoDate(offset + 2);

  for (let i = 1; i <= TOTAL_ROOMS; i++) {
    await submitReservation(page, checkIn, checkOut, `E2E 재고${i}`);
    await expect(page).toHaveURL(/\/admin\/reservations\/my\?q=/);
    await expect(adminToast(page)).toContainText('예약이 등록되었습니다.');
  }

  // 겹치는 기간(하루 걸침)의 추가 예약
  await submitReservation(page, isoDate(offset + 1), isoDate(offset + 3), 'E2E 초과');
  await expect(page).toHaveURL(/\/admin\/reservations\/new$/);
  await expect(adminToast(page)).toContainText('남은 객실이 없습니다');
  await expect(adminToast(page)).toContainText(`총 ${TOTAL_ROOMS}실`);
});
