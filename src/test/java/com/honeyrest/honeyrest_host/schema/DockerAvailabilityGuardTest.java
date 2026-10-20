package com.honeyrest.honeyrest_host.schema;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.testcontainers.DockerClientFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Testcontainers 테스트는 Docker 가 없으면 건너뛰므로, CI(INTEGRATION_REQUIRE_DOCKER=true)에서는 Docker 존재를 강제한다.
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
