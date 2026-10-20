package com.honeyrest.honeyrest_host.serviceOwner;

import jakarta.persistence.EntityNotFoundException;
import com.honeyrest.honeyrest_host.dtoOwner.CouponDTO;
import com.honeyrest.domain.entity.Coupon;
import com.honeyrest.honeyrest_host.repositoryOwner.OCouponRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OCouponService {
    private final OCouponRepository couponRepository;
    private final ModelMapper modelMapper;

    public CouponDTO getCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("해당하는 쿠폰이 존재하지 않습니다"));
        return modelMapper.map(coupon, CouponDTO.class);
    }

    public List<CouponDTO> getCoupons() {
        return couponRepository.findAll()
                .stream()
                .map(coupon -> modelMapper.map(coupon, CouponDTO.class))
                .toList();
    }

    /** 사용자 API(Coupon.isApplicableToAccommodation)가 적용하는 대상 유형 */
    static final java.util.Set<String> TARGET_TYPES = java.util.Set.of("ALL", "ACCOMMODATION");

    public void registerCoupon(CouponDTO dto) {
        validate(dto);
        couponRepository.saveAndFlush(modelMapper.map(dto, Coupon.class));
    }

    /** 대상 유형(사용자 API 가 적용하는 ALL/ACCOMMODATION만)·기간·할인값을 검증한다. */
    void validate(CouponDTO dto) {
        if (dto.getTargetType() == null || !TARGET_TYPES.contains(dto.getTargetType())) {
            throw new IllegalArgumentException("적용 대상은 전체(ALL) 또는 특정 숙소(ACCOMMODATION)만 가능합니다.");
        }
        if ("ALL".equals(dto.getTargetType())) {
            dto.setTargetId(null);
        } else if (dto.getTargetId() == null) {
            throw new IllegalArgumentException("특정 숙소 쿠폰은 숙소를 선택해야 합니다.");
        }
        try {
            com.honeyrest.domain.type.CouponDiscountType.from(dto.getDiscountType());
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        if (dto.getDiscountValue() == null || dto.getDiscountValue().signum() <= 0) {
            throw new IllegalArgumentException("할인 값은 0보다 커야 합니다.");
        }
        if (dto.getStartDate() == null || dto.getEndDate() == null || !dto.getStartDate().isBefore(dto.getEndDate())) {
            throw new IllegalArgumentException("종료일은 시작일 이후여야 합니다.");
        }
        if (dto.getCode() != null && dto.getCode().isBlank()) {
            dto.setCode(null);
        }
    }

    public void modifyCoupon(CouponDTO dto) {
        couponRepository.save(modelMapper.map(dto, Coupon.class));
    }

    public void removeCoupon(Long id) {
        couponRepository.deleteById(id);
    }
}
