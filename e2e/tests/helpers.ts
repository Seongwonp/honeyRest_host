import { expect, type Page } from '@playwright/test';
import { requireEnv } from '../env';

export type Role = 'company' | 'super';

/** 역할별 로그인 정보. 값은 환경 변수/.env.e2e(.example)에서만 읽는다 (테스트 코드에 비밀번호를 두지 않는다). */
export function credentials(role: Role): { email: string; password: string } {
  return role === 'company'
    ? { email: requireEnv('E2E_ADMIN_EMAIL'), password: requireEnv('E2E_ADMIN_PW') }
    : { email: requireEnv('E2E_SUPER_EMAIL'), password: requireEnv('E2E_SUPER_PW') };
}

/** 공용 로그인 화면(/auth/login)으로 로그인하고 역할별 대시보드 도착까지 확인한다. */
export async function login(page: Page, role: Role): Promise<void> {
  const { email, password } = credentials(role);
  await page.goto('/auth/login');
  await page.locator('#email').fill(email);
  await page.locator('#password').fill(password);
  await page.getByRole('button', { name: '로그인' }).click();
  await expect(page).toHaveURL(role === 'company' ? /\/admin\/dashboard/ : /\/owner\/dashboard/);
}

/** confirm() 대화상자를 자동 수락한다 (삭제/승인/완료 버튼의 onsubmit confirm). */
export function acceptDialogs(page: Page): void {
  page.on('dialog', (dialog) => dialog.accept());
}

/** 관리자 레이아웃(admin/layout/base.html)의 flash 토스트 본문 */
export function adminToast(page: Page) {
  return page.locator('#pageToastBody');
}

/** yyyy-MM-dd (로컬 날짜 기준) */
export function isoDate(offsetDays: number): string {
  const d = new Date();
  d.setDate(d.getDate() + offsetDays);
  const pad = (n: number) => String(n).padStart(2, '0');
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}`;
}
