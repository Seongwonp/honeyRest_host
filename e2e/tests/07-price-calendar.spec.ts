import { test, expect } from '@playwright/test';
import { adminToast, isoDate, login } from './helpers';

// (g) 가격 캘린더 1박 가격 수정 모달
//  - 모달 기본값은 그날 "설정된 1박 가격"이어야 한다 (과거: 체크인 매출 합계를 가격으로 채움)
//  - 저장 요청 파라미터 ym 이 컨트롤러와 맞아야 한다 (과거: 컨트롤러가 yearMonth 를 요구해 항상 400)
//  - 사용자 API 가 따르지 않는 "재고 0 = 판매중지" 입력은 없어야 한다
test('가격 캘린더에서 1박 가격을 수정하면 같은 달 캘린더에 반영된다', async ({ page }) => {
  await login(page, 'company');

  // 시드 요금 캘린더(오늘 -7 ~ +45일) 밖의 날짜 → 설정가 = 객실 기본가(디럭스 오션뷰 더블 120,000원)
  const date = isoDate(60);
  const ym = date.slice(0, 7);
  await page.goto(`/admin/price/page?companyId=1&ym=${ym}&accommodationId=1&roomId=1`);

  const editButton = page.locator(`button[data-date="${date}"]`);
  await expect(editButton).toBeVisible();
  await editButton.click();

  const priceInput = page.locator('#modalPrice');
  await expect(priceInput).toBeVisible();
  expect(Number(await priceInput.inputValue())).toBe(120000);
  await expect(page.locator('#cellEditModal input[name="available"]')).toHaveCount(0);

  await priceInput.fill('99000');
  await page.locator('#cellEditModal').getByRole('button', { name: '저장' }).click();

  await expect(page).toHaveURL(new RegExp(`/admin/price/page\\?companyId=\\d+&ym=${ym}&mode=calendar&accommodationId=1&roomId=1`));
  await expect(adminToast(page)).toHaveText(/생성 완료|수정 완료/);
  const saved = page.locator(`button[data-date="${date}"]`);
  expect(Number(await saved.getAttribute('data-price'))).toBe(99000);
});
