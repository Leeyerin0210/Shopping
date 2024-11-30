package com.shopping.site.repository

import com.shopping.site.dataClass.Coupon
import org.springframework.data.jpa.repository.JpaRepository

interface CouponRepository : JpaRepository<Coupon, Long> {
    fun findByUserEmail(email: String): List<Coupon>
}
