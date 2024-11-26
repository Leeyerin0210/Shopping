package com.shopping.site

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CartRepository : JpaRepository<Cart, Long> {
    fun findByUserAndProduct(user: User, product: Product): Cart?
}
