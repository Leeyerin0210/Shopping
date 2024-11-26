package com.shopping.site.repository

import com.shopping.site.dataClass.Cart
import com.shopping.site.dataClass.Product
import com.shopping.site.dataClass.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CartRepository : JpaRepository<Cart, Long> {
    fun findByUserAndProduct(user: User, product: Product): Cart?
}
