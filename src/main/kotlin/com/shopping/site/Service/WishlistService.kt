package com.shopping.site

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WishlistService(
    private val wishlistRepository: WishlistRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun addToWishlist(userEmail: String, productId: Long): String {
        // 사용자 확인
        val user = userRepository.findByEmail(userEmail)
            ?: throw IllegalArgumentException("User with email $userEmail not found.")

        // 제품 확인
        val product = productRepository.findById(productId)
            .orElseThrow { IllegalArgumentException("Product with id $productId not found.") }

        // 중복 확인
        val existingWishlistItem = wishlistRepository.findByUserAndProduct(user, product)
        if (existingWishlistItem != null) {
            return "This product is already in your wishlist."
        }

        // 위시리스트에 추가
        val wishlistItem = Wishlist(user = user, product = product)
        wishlistRepository.save(wishlistItem)

        return "Product added to wishlist successfully."
    }
}
