package com.shopping.site.repository

import com.shopping.site.dataClass.Product
import com.shopping.site.dataClass.User
import com.shopping.site.dataClass.Wishlist
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WishlistRepository : JpaRepository<Wishlist, Long> {
    fun findByUserAndProduct(user: User, product: Product): Wishlist?
}
