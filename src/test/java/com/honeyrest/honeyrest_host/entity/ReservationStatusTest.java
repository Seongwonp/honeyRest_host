package com.honeyrest.honeyrest_host.entity;

import com.honeyrest.domain.type.ReservationStatus;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 호스트가 기대하는 예약 상태 값을 고정한다.
 * ReservationStatus 는 이제 공유 도메인 모듈(com.honeyrest.domain.type)의 단일 클래스라 두 앱 사이 불일치는 생길 수 없지만,
 * 서브모듈을 재고정(re-pin)했을 때 호스트 화면/쿼리가 가정하는 값이 바뀌었는지 여기서 드러나게 한다.
 */
class ReservationStatusTest {

    @Test
    void 상태_상수는_사용자_저장소와_같다() {
        assertThat(ReservationStatus.PENDING).isEqualTo("PENDING");
        assertThat(ReservationStatus.CONFIRMED).isEqualTo("CONFIRMED");
        assertThat(ReservationStatus.CANCEL_REQUEST).isEqualTo("CANCEL_REQUEST");
        assertThat(ReservationStatus.COMPLETED).isEqualTo("COMPLETED");
        assertThat(ReservationStatus.NO_SHOW).isEqualTo("NO_SHOW");
        assertThat(ReservationStatus.CANCELLED).isEqualTo("CANCELLED");
    }

    @Test
    void 점유_상태_목록은_사용자_저장소와_같다() {
        assertThat(ReservationStatus.OCCUPYING).containsExactlyInAnyOrderElementsOf(
                Set.of("PENDING", "CONFIRMED", "CANCEL_REQUEST", "COMPLETED", "NO_SHOW"));
        assertThat(ReservationStatus.OCCUPYING).doesNotContain("CANCELLED", "CANCELED");
    }

    @Test
    void 정규화는_대소문자를_무시하고_오타는_거부한다() {
        assertThat(ReservationStatus.normalize("confirmed", "PENDING")).isEqualTo("CONFIRMED");
        assertThat(ReservationStatus.normalize(null, "PENDING")).isEqualTo("PENDING");
        assertThatThrownBy(() -> ReservationStatus.normalize("CANCELED", "PENDING"))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
