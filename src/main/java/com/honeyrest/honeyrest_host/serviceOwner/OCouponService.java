package com.honeyrest.honeyrest_host.serviceOwner;

import com.amazonaws.services.kms.model.NotFoundException;
import com.honeyrest.honeyrest_host.dtoOwner.CouponDTO;
import com.honeyrest.honeyrest_host.entity.Coupon;
import com.honeyrest.honeyrest_host.repository.OCouponRepository;
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
                .orElseThrow(()-> new NotFoundException("해당하는 쿠폰이 존재하지 않습니다"));
        return modelMapper.map(coupon, CouponDTO.class);
    }

    public List<CouponDTO> getCoupons() {
        return couponRepository.findAll()
                .stream()
                .map(coupon -> modelMapper.map(coupon, CouponDTO.class))
                .toList();
    }

    public void registerCoupon(CouponDTO dto) {

        couponRepository.save(modelMapper.map(dto, Coupon.class));
    }

    public void modifyCoupon(CouponDTO dto) {
        couponRepository.save(modelMapper.map(dto, Coupon.class));
    }

    public void removeCoupon(Long id) {
        couponRepository.deleteById(id);
    }
}
