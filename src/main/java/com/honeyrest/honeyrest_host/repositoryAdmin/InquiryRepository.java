package com.honeyrest.honeyrest_host.repositoryAdmin;

import com.honeyrest.honeyrest_host.entity.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    @Query("""
    select i
    from Inquiry i
      join i.accommodation a
      join a.company c
    where c.companyId = :companyId
      and (:q is null or :q = '' 
           or i.title like concat('%', :q, '%') 
           or i.content like concat('%', :q, '%') 
           or i.user.name like concat('%', :q, '%'))
      and (:replied is null or i.isReplied = :replied)
    """)
    Page<Inquiry> findByCompany(@Param("companyId") Long companyId,
                                @Param("q") String q,
                                @Param("replied") Boolean replied,
                                Pageable pageable);

    /** 숙소 기준(단일 숙소 관리자용) */
    @Query("""
    select i
    from Inquiry i
    where i.accommodation.accommodationId = :accId
    """)
    Page<Inquiry> findByAccommodation(@Param("accId") Long accommodationId, Pageable pageable);

    /** 회사 기준(여러 숙소 관리, 검색/답변여부 필터) */
    @EntityGraph(attributePaths = {"user", "accommodation"})
    @Query("""
        select i
          from Inquiry i
          join i.accommodation a
          join a.company c
         where c.companyId = :companyId
           and (:replied is null or i.isReplied = :replied)
           and (
                :q is null
             or lower(i.title) like lower(concat('%', :q, '%'))
             or lower(i.content) like lower(concat('%', :q, '%'))
             or lower(i.reply) like lower(concat('%', :q, '%'))
           )
         order by i.createdAt desc
    """)
    Page<Inquiry> searchByCompany(Long companyId, String q, Boolean replied, Pageable pageable);

    // 회수 소속 숙소 문의만 가져오기.
    @Query("""
    select i
    from Inquiry i
      join i.accommodation a
      join a.company c
    where c.companyId = :companyId
      and (:accId is null or a.accommodationId = :accId)
      and (:q is null or :q = '' 
           or i.title like concat('%', :q, '%') 
           or i.content like concat('%', :q, '%') 
           or i.user.name like concat('%', :q, '%'))
      and (:replied is null or i.isReplied = :replied)
    order by i.createdAt desc
    """)
    Page<Inquiry> findByCompanyWithFilters(@Param("companyId") Long companyId,
                                           @Param("accId") Long accommodationId,   // nullable
                                           @Param("q") String q,                   // nullable
                                           @Param("replied") Boolean replied,      // nullable
                                           Pageable pageable);
}
