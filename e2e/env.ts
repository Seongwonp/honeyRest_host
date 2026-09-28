import fs from 'node:fs';
import path from 'node:path';

/**
 * .env.e2e(로컬 재정의) → .env.e2e.example(기본값) 순으로 읽어 process.env 에 채운다.
 * 이미 설정된 환경 변수는 덮어쓰지 않는다. dotenv 의존성 없이 KEY=VALUE 형식만 지원한다.
 */
export function loadE2eEnv(): void {
  for (const file of ['.env.e2e', '.env.e2e.example']) {
    const full = path.join(__dirname, file);
    if (!fs.existsSync(full)) continue;
    for (const raw of fs.readFileSync(full, 'utf-8').split(/\r?\n/)) {
      const line = raw.trim();
      if (!line || line.startsWith('#')) continue;
      const eq = line.indexOf('=');
      if (eq < 0) continue;
      const key = line.slice(0, eq).trim();
      const value = line.slice(eq + 1).trim().replace(/^['"]|['"]$/g, '');
      if (process.env[key] === undefined) process.env[key] = value;
    }
  }
}

export function requireEnv(key: string): string {
  loadE2eEnv();
  const value = process.env[key];
  if (!value) throw new Error(`E2E 환경 변수 ${key} 가 비어 있습니다 (.env.e2e.example 참고)`);
  return value;
}
