package com.shopping.site.repository

import com.shopping.site.dataClass.Wishlist
import com.shopping.site.dataClass.User
import com.shopping.site.dataClass.Product
import org.springframework.data.jpa.repository.JpaRepository

interface WishlistRepository : JpaRepository<Wishlist, Long> {
    // 특정 사용자와 제품으로 위시리스트 항목 검색
    fun findByUserAndProduct(user: User, product: Product): Wishlist?

    // 특정 사용자 이메일의 모든 위시리스트 항목 검색
    fun findAllByUser_Email(email: String): List<Wishlist>

    // 특정 사용자 이메일과 제품 ID로 위시리스트 항목 삭제
    fun deleteByUser_EmailAndProduct_Id(userEmail: String, productId: Long): Int
}