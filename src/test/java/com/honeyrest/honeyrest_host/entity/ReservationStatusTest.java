package com.honeyrest.honeyrest_host.entity;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * 호스트 예약 상태 상수가 사용자 저장소(honeyRest_user)의 entity.ReservationStatus 와 같은지 고정한다.
 * 두 저장소는 같은 reservation.status 컬럼을 쓰므로 값이 어긋나면 재고 계산이 달라진다.
 * 사용자 저장소 값이 바뀌면 이 기대값과 호스트 상수를 함께 수정해야 한다.
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
