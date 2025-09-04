package com.honeyrest.honeyrest_host.repositoryAdmin.accommodation;

import com.honeyrest.honeyrest_host.entity.AccommodationTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AccommodationTagRepository extends JpaRepository<AccommodationTag, Long> {

    // [상세뷰] 숙소에 매핑된 태그 조회 (카테고리/이름 정렬)
    @Query("""
           select t
           from AccommodationTag t
           join AccommodationTagMap m on m.tag = t
           where m.accommodation.accommodationId = :accId
           order by t.category asc, t.name asc
           """)
    List<AccommodationTag> findByAccommodationIdOrderByCategoryAscNameAsc(@Param("accId") Long accId);

    // [수정폼] id 목록으로 태그 조회
    List<AccommodationTag> findByTagIdInOrderByCategoryAscNameAsc(List<Long> tagIds);
}
