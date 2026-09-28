import { defineConfig, devices } from '@playwright/test';
import { loadE2eEnv, requireEnv } from './env';

loadE2eEnv();

const baseURL = requireEnv('E2E_BASE_URL');
const reuseServer = process.env.E2E_REUSE_SERVER === '1';
const chromiumPath = process.env.E2E_CHROMIUM_PATH || undefined;

/**
 * 호스트 관리자 E2E 설정.
 * - 대상: screenshot 프로필(H2 인메모리 + db/screenshot-seed.sql) + local-demo(DataInitializer 데모 계정)
 * - 시나리오들이 같은 인메모리 DB 를 공유·변경하므로 workers=1, 파일 순서대로 직렬 실행한다.
 */
export default defineConfig({
  testDir: './tests',
  fullyParallel: false,
  workers: 1,
  forbidOnly: !!process.env.CI,
  retries: process.env.CI ? 1 : 0,
  timeout: 60_000,
  expect: { timeout: 10_000 },
  reporter: process.env.CI
    ? [['list'], ['html', { open: 'never' }], ['github']]
    : [['list'], ['html', { open: 'never' }]],
  use: {
    baseURL,
    locale: 'ko-KR',
    timezoneId: 'Asia/Seoul',
    trace: 'retain-on-failure',
    screenshot: 'only-on-failure',
    launchOptions: chromiumPath ? { executablePath: chromiumPath } : {},
  },
  projects: [
    { name: 'chromium', use: { ...devices['Desktop Chrome'] } },
  ],
  webServer: {
    command: "./gradlew bootRun --no-daemon --args='--spring.profiles.active=screenshot,local-demo'",
    cwd: '..',
    url: `${baseURL}/auth/login`,
    timeout: 300_000,
    reuseExistingServer: reuseServer,
    stdout: 'ignore',
    stderr: 'pipe',
    env: {
      // DataInitializer(@Value demo.*.password)가 테스트와 같은 비밀번호로 데모 계정을 만들도록 전달
      DEMO_COMPANYADMIN_PASSWORD: requireEnv('E2E_ADMIN_PW'),
      DEMO_SUPERADMIN_PASSWORD: requireEnv('E2E_SUPER_PW'),
    },
  },
});
