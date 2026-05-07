package com.honeyrest.honeyrest_host.repositoryAdmin.accommodation;


import com.honeyrest.honeyrest_host.dtoAdmin.accommodation.AccommodationListDTO;
import com.honeyrest.honeyrest_host.entity.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long>, AccommodationQuery {


    Page<Accommodation> findByCompany_CompanyIdAndStatus(Integer companyId, String status, Pageable pageable);

    Long countByCompany_CompanyId(Integer companyId);

    List<Accommodation> findAllByCompany_CompanyId(Integer companyId);

    @Query("select a.company.companyId from Accommodation a where a.accommodationId = :id")
    Integer findCompanyIdByAccommodationId(@Param("id") Long accommodationId);

    // --- 연관관계: 개별 업데이트 (COALESCE/CASE 없이 대입만) ---
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Accommodation a set a.company = :company where a.accommodationId = :id")
    int updateCompany(@Param("id") Long id, @Param("company") Company company);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Accommodation a set a.category = :category where a.accommodationId = :id")
    int updateCategory(@Param("id") Long id, @Param("category") AccommodationCategory category);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Accommodation a set a.mainRegion = :mainRegion where a.accommodationId = :id")
    int updateMainRegion(@Param("id") Long id, @Param("mainRegion") Region mainRegion);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("update Accommodation a set a.subRegion = :subRegion where a.accommodationId = :id")
    int updateSubRegion(@Param("id") Long id, @Param("subRegion") Region subRegion);

    @Modifying
    @Query("update Accommodation a set a.thumbnail = :thumbnail where a.accommodationId = :id")
    int updateThumbnail(@Param("id") Long id, @Param("thumbnail") String thumbnail);


    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
            "update Accommodation a set " +
            " a.name         = CASE WHEN :name         IS NULL THEN a.name         ELSE :name         END," +
            " a.address      = CASE WHEN :address      IS NULL THEN a.address      ELSE :address      END," +
            " a.latitude     = CASE WHEN :latitude     IS NULL THEN a.latitude     ELSE :latitude     END," +
            " a.longitude    = CASE WHEN :longitude    IS NULL THEN a.longitude    ELSE :longitude    END," +
            // ↓ 엔티티 실제 필드명과 100% 일치: thumbnail 또는 thumbnailUrl
            " a.thumbnail    = CASE WHEN :thumbnail    IS NULL THEN a.thumbnail    ELSE :thumbnail    END," +
            " a.description  = CASE WHEN :description  IS NULL THEN a.description  ELSE :description  END," +
            " a.amenities    = CASE WHEN :amenities    IS NULL THEN a.amenities    ELSE :amenities    END," +
            " a.checkInTime  = CASE WHEN :checkInTime  IS NULL THEN a.checkInTime  ELSE :checkInTime  END," +
            " a.checkOutTime = CASE WHEN :checkOutTime IS NULL THEN a.checkOutTime ELSE :checkOutTime END," +
            " a.status       = CASE WHEN :status       IS NULL THEN a.status       ELSE :status       END," +
            " a.minPrice     = CASE WHEN :minPrice     IS NULL THEN a.minPrice     ELSE :minPrice     END " +
            "where a.accommodationId = :id"
    )
    int patchUpdateScalars(
            @Param("id") Long id,
            @Param("name") String name,
            @Param("address") String address,
            @Param("latitude") BigDecimal latitude,
            @Param("longitude") BigDecimal longitude,
            @Param("thumbnail") String thumbnail,
            @Param("description") String description,
            @Param("amenities") String amenities,
            @Param("checkInTime") LocalDateTime checkInTime,
            @Param("checkOutTime") LocalDateTime checkOutTime,
            @Param("status") String status,
            @Param("minPrice") BigDecimal minPrice
    );

    @Query(value = """
            select new com.honeyrest.honeyrest_host.dtoAdmin.accommodation.AccommodationListDTO(
                a.accommodationId,
                a.name,
                c.name,
                r.name,
                a.minPrice,
                a.status,
                null
            )
            from Accommodation a
            left join a.category c
            left join a.mainRegion r
            where a.company.companyId = :companyId
            """,
            countQuery = """
                    select count(a)
                                from Accommodation a
                                            where a.company.companyId = :companyId
                    """)
    Page<AccommodationListDTO> findListByCompanyId(@Param("companyId") Integer companyId, Pageable pageable);

    @Query("select a.name from Accommodation a where a.accommodationId = :id")
    Optional<String> findNameById(@Param("id") Long accommodationId);

    // Accommodation → Company (연관관계 매핑 가정: a.company)
    @Query("""
        SELECT a.accommodationId
        FROM Accommodation a
        JOIN a.company c
        WHERE c.email = :email
    """)
    List<Long> findAccommodationIdsByAdminEmail(@Param("email") String email);
}



interface AccommodationQuery {
    Page<AccommodationListDTO> search(String q, Integer categoryId, Integer mainRegionId, Pageable pageable);
}

@Repository
@RequiredArgsConstructor
class AccommodationQueryImpl implements AccommodationQuery {
    private final EntityManager em;

    @Override
    public Page<AccommodationListDTO> search(String q, Integer categoryId, Integer mainRegionId, Pageable pageable) {
        String where = " where 1=1 ";
        if (q != null && !q.isBlank()) where += " and (a.name like :q or a.address like :q) ";
        if (categoryId != null) where += " and c.categoryId = :categoryId ";
        if (mainRegionId != null) where += " and mr.regionId = :mainRegionId ";

        String select = """
                select new com.honeyrest.honeyrest_host.dtoAdmin.accommodation.AccommodationListDTO(
                a.accommodationId,
                a.name,
                c.name,
                mr.name,
                a.minPrice,
                a.status,
                null
                )
                from Accommodation a
                join a.category c
                join a.mainRegion mr
                """;
        String count = """
                select count(a) 
                from Accommodation a
                 join a.category c
                 join a.mainRegion mr
                """;

        TypedQuery<AccommodationListDTO> dataQ = em.createQuery(select + where + " order by a.accommodationId desc", AccommodationListDTO.class);
        TypedQuery<Long> countQ = em.createQuery(count + where, Long.class);

        if (q != null && !q.isBlank()) {
            String like = "%" + q + "%";
            dataQ.setParameter("q", like);
            countQ.setParameter("q", like);
        }
        if (categoryId != null) {
            dataQ.setParameter("categoryId", categoryId);
            countQ.setParameter("categoryId", categoryId);
        }
        if (mainRegionId != null) {
            dataQ.setParameter("mainRegionId", mainRegionId);
            countQ.setParameter("mainRegionId", mainRegionId);
        }

        dataQ.setFirstResult((int) pageable.getOffset());
        dataQ.setMaxResults(pageable.getPageSize());

        List<AccommodationListDTO> content = dataQ.getResultList();
        long total = countQ.getSingleResult();

        return new PageImpl<>(content, pageable, total);




    }

}