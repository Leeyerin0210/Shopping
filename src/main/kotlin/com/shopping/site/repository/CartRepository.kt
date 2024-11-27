package com.shopping.site.repository

import com.shopping.site.dataClass.Cart
import org.springframework.data.jpa.repository.JpaRepository

interface CartRepository : JpaRepository<Cart, Long> {
    // 특정 사용자의 장바구니 항목 조회
    fun findAllByUser_Email(userEmail: String): List<Cart>

    // 특정 사용자의 특정 제품 장바구니 항목 조회
    fun findByUser_EmailAndProduct_Id(userEmail: String, productId: Long): Cart?

    // 특정 사용자의 특정 제품 장바구니 항목 삭제
    fun deleteByUser_EmailAndProduct_Id(userEmail: String, productId: Long): Int

    // 특정 사용자의 모든 장바구니 항목 삭제
    fun deleteAllByUser_Email(userEmail: String): Int
}
