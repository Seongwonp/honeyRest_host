package com.honeyrest.honeyrest_host.schema;

import jakarta.persistence.EntityManagerFactory;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * 호스트 엔티티 ↔ 사용자 API 저장소 Flyway 마이그레이션 스키마의 교차 검증.
 *
 * <p>호스트 앱은 마이그레이션 없이 운영에서 {@code ddl-auto=validate} 로 기동하고, 기본 test 태스크는 H2 + create-drop 이라
 * 두 저장소 사이의 스키마 차이(drift)는 지금까지 운영 기동 시점에야 드러났다. 이 테스트는
 * <ol>
 *   <li>Testcontainers 로 빈 MySQL 8.0 을 띄우고</li>
 *   <li>사용자 API 저장소의 {@code db/migration} (V1~최신) 을 Flyway 로 그대로 적용한 뒤
 *       (V3·V10 등 MySQL 전용 PREPARE/information_schema 구문도 실제 MySQL 이 처리)</li>
 *   <li>호스트 컨텍스트를 {@code ddl-auto=validate} 로 기동한다 — 누락 테이블/컬럼·타입 불일치가 있으면 기동이 실패한다.</li>
 * </ol>
 *
 * <p>마이그레이션 위치 결정 순서:
 * <ol>
 *   <li>시스템 프로퍼티 또는 환경변수 {@code HONEYREST_USER_MIGRATIONS} (명시적으로 지정하면 우선)</li>
 *   <li>형제 디렉터리 {@code ../honeyRest_user/src/main/resources/db/migration} (로컬에서 두 저장소를 나란히 클론한 경우)</li>
 * </ol>
 * 둘 다 없으면 로컬에서는 건너뛰고, {@code integration.requireDocker=true}(CI)에서는 실패한다.
 * Docker 가 없으면 클래스 전체를 건너뛴다. 실행: {@code ./gradlew integrationTest}
 */
@Tag("integration")
@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
@ActiveProfiles("test") // 더미 jwt.secret/로컬 저장소는 test 프로필을 재사용하고 DB 관련 설정만 아래에서 덮어쓴다.
class HostSchemaAgainstUserMigrationsIntegrationTest {

    static final String MIGRATIONS_KEY = "HONEYREST_USER_MIGRATIONS";
    static final Path SIBLING_MIGRATIONS = Path.of("..", "honeyRest_user", "src", "main", "resources", "db", "migration");

    @Container
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>(DockerImageName.parse("mysql:8.0"))
            .withDatabaseName("honeyrest_db")
            .withCommand("--character-set-server=utf8mb4", "--collation-server=utf8mb4_unicode_ci");

    /**
     * 컨테이너는 @Testcontainers 확장이 먼저 띄우고, 스프링 컨텍스트는 테스트 인스턴스 준비 시점에 로드되므로
     * 여기서 적용한 스키마에 대해 validate 가 수행된다.
     */
    @BeforeAll
    static void applyUserMigrations() {
        Path migrations = resolveUserMigrations().orElse(null);
        if (migrations == null) {
            String message = "사용자 API 마이그레이션 디렉터리를 찾지 못했다: " + MIGRATIONS_KEY
                    + " 를 지정하거나 " + SIBLING_MIGRATIONS.toAbsolutePath().normalize() + " 에 honeyRest_user 를 클론하라.";
            if (Boolean.getBoolean("integration.requireDocker")) {
                fail(message);
            }
            assumeTrue(false, message);
        }

        MigrateResult result = Flyway.configure()
                .dataSource(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword())
                .locations("filesystem:" + migrations.toAbsolutePath().normalize())
                .load()
                .migrate();

        assertThat(result.success).as("사용자 API 마이그레이션 적용 결과").isTrue();
        assertThat(result.migrationsExecuted).as("적용된 마이그레이션 수").isPositive();
    }

    @DynamicPropertySource
    static void mysqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        // test 프로필의 H2Dialect/create-drop 을 운영과 같은 조합으로 되돌린다.
        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MySQLDialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        // 스키마는 위 @BeforeAll 에서 사용자 API 마이그레이션으로만 만든다.
        registry.add("spring.flyway.enabled", () -> "false");
        registry.add("app.storage.type", () -> "local");
    }

    static Optional<Path> resolveUserMigrations() {
        String explicit = System.getProperty(MIGRATIONS_KEY, System.getenv(MIGRATIONS_KEY));
        if (explicit != null && !explicit.isBlank()) {
            Path path = Path.of(explicit);
            if (!Files.isDirectory(path)) {
                // 명시적으로 지정했는데 틀린 경로면 조용히 넘어가지 않는다.
                throw new IllegalStateException(MIGRATIONS_KEY + " 경로가 디렉터리가 아니다: " + path.toAbsolutePath());
            }
            return Optional.of(path);
        }
        return Files.isDirectory(SIBLING_MIGRATIONS) ? Optional.of(SIBLING_MIGRATIONS) : Optional.empty();
    }

    @Autowired
    EntityManagerFactory entityManagerFactory;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Test
    void 호스트_엔티티가_사용자_마이그레이션_스키마에_대해_validate_를_통과한다() {
        // validate 실패 시 컨텍스트 기동 단계에서 SchemaManagementException 으로 이미 실패한다.
        // 여기서는 실제로 MySQL + validate 조합으로 기동했는지(H2/create-drop 으로 우회되지 않았는지) 확인한다.
        assertThat(entityManagerFactory.getProperties())
                .containsEntry("hibernate.hbm2ddl.auto", "validate");
        String product = jdbcTemplate.queryForObject("SELECT VERSION()", String.class);
        assertThat(product).startsWith("8.0");
    }

    @Test
    void 호스트가_쓰는_공유_스키마_항목이_존재한다() {
        assertThat(tableCount("error_log")).as("ErrorLog 엔티티 테이블 (V11)").isEqualTo(1);
        assertThat(columnNullable("reservation", "accommodation_name")).as("V10").isEqualTo("NO");
        assertThat(columnNullable("accommodation_tag", "icon_name")).as("AccommodationTag.icon 매핑 컬럼").isNotNull();
        // 호스트 CancellationPolicyServiceImpl 은 policy_name/detail 만 INSERT 한다 → 구세대 NOT NULL 컬럼은 완화돼 있어야 한다 (V11)
        assertThat(columnNullable("cancellation_policy", "days_before")).isEqualTo("YES");
        assertThat(columnNullable("cancellation_policy", "refund_rate")).isEqualTo("YES");
    }

    private Integer tableCount(String table) {
        return jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.tables WHERE table_schema = DATABASE() AND table_name = ?",
                Integer.class, table);
    }

    private String columnNullable(String table, String column) {
        return jdbcTemplate.query(
                "SELECT is_nullable FROM information_schema.columns "
                        + "WHERE table_schema = DATABASE() AND table_name = ? AND column_name = ?",
                rs -> rs.next() ? rs.getString(1) : null, table, column);
    }
}
