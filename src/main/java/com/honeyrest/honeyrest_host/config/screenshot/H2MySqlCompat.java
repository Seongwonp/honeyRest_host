package com.honeyrest.honeyrest_host.config.screenshot;

import org.hibernate.resource.jdbc.spi.StatementInspector;

import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * screenshot 프로필(H2, MySQL 모드) 전용. H2 가 파싱하지 못하는 MySQL 문법(DATE_ADD INTERVAL, DATE_FORMAT 등)을
 * 치환하고, 치환된 함수는 {@code db/screenshot-seed.sql} 의 CREATE ALIAS 로 이 클래스의 static 메서드에 연결한다.
 */
public class H2MySqlCompat implements StatementInspector {

    private static final Pattern INTERVAL_DAY = Pattern.compile("INTERVAL\\s+(.+?)\\s+DAY\\b");
    /** MySQL 은 VARCHAR 와 숫자 비교 시 암묵 변환하지만 H2 는 변환 오류를 낸다 (예: rm.status = 1) */
    private static final Pattern STATUS_NUM = Pattern.compile("(\\bstatus\\s*=\\s*)(\\d+)\\b");
    private static final Pattern FUNCS = Pattern.compile("\\b(DATE_ADD|DATE_SUB|DATEDIFF|DATE_FORMAT|WEEKDAY)\\(");

    @Override
    public String inspect(String sql) {
        if (sql == null) return null;
        String out = INTERVAL_DAY.matcher(sql).replaceAll("$1");
        out = STATUS_NUM.matcher(out).replaceAll("$1'$2'");
        return FUNCS.matcher(out).replaceAll("MYSQL_$1(");
    }

    // ---- CREATE ALIAS 대상 함수 (H2 가 호출) ----

    public static Timestamp dateAdd(Timestamp base, Integer days) {
        if (base == null || days == null) return null;
        return Timestamp.valueOf(base.toLocalDateTime().plusDays(days));
    }

    public static Timestamp dateSub(Timestamp base, Integer days) {
        if (base == null || days == null) return null;
        return Timestamp.valueOf(base.toLocalDateTime().minusDays(days));
    }

    public static Long dateDiff(Timestamp a, Timestamp b) {
        if (a == null || b == null) return null;
        return ChronoUnit.DAYS.between(b.toLocalDateTime().toLocalDate(), a.toLocalDateTime().toLocalDate());
    }

    /** MySQL WEEKDAY: 월=0 … 일=6 */
    public static Integer weekday(Timestamp ts) {
        if (ts == null) return null;
        return ts.toLocalDateTime().getDayOfWeek().getValue() - 1;
    }

    /** MySQL DATE_FORMAT 의 %Y %m %d %W 만 지원 (쿼리에서 쓰는 형식) */
    public static String dateFormat(Timestamp ts, String pattern) {
        if (ts == null || pattern == null) return null;
        LocalDateTime t = ts.toLocalDateTime();
        DayOfWeek dow = t.getDayOfWeek();
        return pattern
                .replace("%Y", String.format("%04d", t.getYear()))
                .replace("%m", String.format("%02d", t.getMonthValue()))
                .replace("%d", String.format("%02d", t.getDayOfMonth()))
                .replace("%W", dow.getDisplayName(TextStyle.FULL, Locale.ENGLISH));
    }
}
