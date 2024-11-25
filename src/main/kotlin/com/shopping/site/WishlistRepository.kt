package com.shopping.site

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface WishlistRepository : JpaRepository<Wishlist, Long> {
    fun findByUserAndProduct(user: User, product: Product): Wishlist?
}
