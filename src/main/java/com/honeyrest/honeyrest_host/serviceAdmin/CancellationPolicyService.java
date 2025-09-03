package com.honeyrest.honeyrest_host.serviceAdmin;

public interface CancellationPolicyService {

    /* 숙소 ID로 멀티라인 문자열("... \n ...")을 돌려준다. 없으면 null */
    String getMultilineByAccommodationId(Long accommodationId);

    void saveOrUpdate(Long accommodationId, String multiline);
}
