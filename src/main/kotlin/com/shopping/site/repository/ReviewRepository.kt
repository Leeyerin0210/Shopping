package com.shopping.site.repository

import com.shopping.site.dataClass.ReviewData
import org.springframework.data.jpa.repository.JpaRepository

interface ReviewRepository : JpaRepository<ReviewData, Long> {
    fun findAllByProduct_Id(productId: Long): List<ReviewData>
}
