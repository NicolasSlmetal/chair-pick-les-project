package com.chairpick.ecommerce.services;

import com.chairpick.ecommerce.model.Coupon;
import com.chairpick.ecommerce.model.enums.CouponType;
import com.chairpick.ecommerce.repositories.CouponRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CouponService {

    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public Map<CouponType, List<Coupon>> findCouponsByCustomer(Long customerId) {
        return couponRepository.findCouponsByCustomer(customerId)
                .stream()
                .sorted(
                        Comparator.comparing(
                                coupon -> {
                                    if (coupon.getType() == CouponType.PROMOTIONAL) return 1;
                                    if (coupon.getType() == CouponType.SWAP) return 2;
                                    return 0;
                                }
                        )
                )
                .collect(Collectors.groupingBy(
                        Coupon::getType,
                        Collectors.toList()
                ));
    }
}
