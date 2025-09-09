package com.honeyrest.honeyrest_host.serviceAdmin;

public interface CancellationPolicyService {

    /* 숙소 ID로 멀티라인 문자열("... \n ...")을 돌려준다. 없으면 null */
    // 화면 줄바꿈 반환
    String getMultilineByAccommodationId(Long accommodationId);

    // 멀티라인 입력을 json 으로 변환해서 upsert
    void saveOrUpdate(Long accommodationId, String multiline);

    void deleteIfExists(Long accommodationId);
}
