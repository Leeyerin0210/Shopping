package com.shopping.site.dto

import java.time.LocalDate

data class CouponRequest(
    val email: String,
    val name: String = "",
    val discount: Double,
    val expiryDate: LocalDate
)
