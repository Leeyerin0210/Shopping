package com.shopping.site.Service

import com.shopping.site.dataClass.Coupon
import com.shopping.site.dto.CouponRequest
import com.shopping.site.repository.CouponRepository
import com.shopping.site.repository.UserRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CouponService(
    private val couponRepository: CouponRepository,
    private val userRepository: UserRepository
) {

    // 사용자별 쿠폰 목록 조회
    fun getCouponsByUser(email: String): List<Coupon> {
        return couponRepository.findByUserEmail(email)
    }

    // 전체 쿠폰 조회
    fun getAllCoupons(): List<Coupon> {
        return couponRepository.findAll()
    }

    // 쿠폰 생성
    fun createCoupon(request: CouponRequest): Coupon {
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("Invalid email address")

        if (request.discount <= 0 ) {
            throw IllegalArgumentException("Discount must be plus")
        }

        if (request.expiryDate.isBefore(LocalDate.now())) {
            throw IllegalArgumentException("Expiry date cannot be in the past")
        }

        val coupon = Coupon(
            name = request.name.ifEmpty { "Discount Coupon" },
            discount = request.discount,
            expiryDate = request.expiryDate,
            user = user
        )
        return couponRepository.save(coupon)
    }

    // 쿠폰 삭제
    fun deleteCoupon(id: Long) {
        val coupon = couponRepository.findById(id).orElseThrow {
            IllegalArgumentException("Coupon not found")
        }
        couponRepository.delete(coupon)
    }
}
