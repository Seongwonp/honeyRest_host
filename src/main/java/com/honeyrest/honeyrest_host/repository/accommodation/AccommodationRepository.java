package com.honeyrest.honeyrest_host.repository.accommodation;

import com.honeyrest.honeyrest_host.dto.accommodation.AccommodationListDTO;
import com.honeyrest.honeyrest_host.entity.Accommodation;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface AccommodationRepository extends JpaRepository<Accommodation, Long>, AccommodationQuery {
}

interface AccommodationQuery {
    Page<AccommodationListDTO> search(String q, Long categoryId, Long mainRegionId, Pageable pageable);
}

@Repository
@RequiredArgsConstructor
class AccommodationQueryImpl implements AccommodationQuery {
    private final EntityManager em;

    @Override
    public Page<AccommodationListDTO> search(String q, Long categoryId, Long mainRegionId, Pageable pageable) {
        String where = " where 1=1 ";
        if (q != null && !q.isBlank()) where += " and (a.name like :q or a.address like :q) ";
        if (categoryId != null) where += " and c.categoryId = :categoryId ";
        if (mainRegionId != null) where += " and mr.regionId = :mainRegionId ";

        String select = """
            select new com.honeyrest.honeyrest_host.dto.accommodation.AccommodationListDTO(
              a.accommodationId, a.name, c.name, mr.name, a.minPrice, a.status
            )
            from Accommodation a
            join a.category c
            join a.mainRegion mr
            """;
        String count = "select count(a) from Accommodation a join a.category c join a.mainRegion mr ";

        TypedQuery<AccommodationListDTO> dataQ = em.createQuery(select + where + " order by a.accommodationId desc", AccommodationListDTO.class);
        TypedQuery<Long> countQ = em.createQuery(count + where, Long.class);

        if (q != null && !q.isBlank()) {
            dataQ.setParameter("q", "%" + q + "%");
            countQ.setParameter("q", "%" + q + "%");
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