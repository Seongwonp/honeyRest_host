package com.honeyrest.honeyrest_host.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomCancellationPolicyDTO {

    private Long roomId;           // 화면 편의용
    private Long accommodationId;  // 실제 조회 키
    private String policyName;     // 예: "기본 환불 정책"

    // 상세 노출용
    private List<String> detailItems;  // 화면용 리스트, ["숙박일 기준 6일전 : 100% 환불", …]

    // 폼 편의(선택)
    private String detailJsonRaw;      // '["…","…"]'원본 JSON 문자열(선택)
    private String detailMultiline;    // "…\n…\n…" 개행 문자열(선택, 폼 편집 시)

}
