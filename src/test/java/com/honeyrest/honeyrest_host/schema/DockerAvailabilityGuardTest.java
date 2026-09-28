package com.honeyrest.honeyrest_host.schema;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Testcontainers 테스트는 Docker 가 없으면 조용히 건너뛰므로, CI 에서 Docker 가 빠지면
 * "스키마 교차 검증이 하나도 안 돌았는데 초록불" 이 될 수 있다.
 * integrationTest 태스크가 INTEGRATION_REQUIRE_DOCKER=true 로 실행될 때만(=CI) Docker 존재를 강제한다.
 * 로컬에서는 이 테스트도 건너뛴다.
 */
@Tag("integration")
class DockerAvailabilityGuardTest {

    @Test
    void 필수_환경에서는_Docker_가_있어야_한다() {
        assumeTrue(Boolean.getBoolean("integration.requireDocker"),
                "integration.requireDocker=false: Docker 부재 시 통합 테스트 skip 허용 (로컬)");

        assertThat(DockerClientFactory.instance().isDockerAvailable())
                .as("INTEGRATION_REQUIRE_DOCKER=true 인데 Docker 를 사용할 수 없다")
                .isTrue();
    }
}
