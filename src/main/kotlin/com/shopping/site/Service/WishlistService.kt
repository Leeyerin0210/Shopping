package com.shopping.site.Service

import com.shopping.site.dataClass.Product
import com.shopping.site.dataClass.User
import com.shopping.site.dataClass.Wishlist
import com.shopping.site.repository.ProductRepository
import com.shopping.site.repository.UserRepository
import com.shopping.site.repository.WishlistRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class WishlistService(
    private val wishlistRepository: WishlistRepository,
    private val userRepository: UserRepository,
    private val productRepository: ProductRepository
) {

    // 위시리스트 조회
    fun getWishlist(userEmail: String): List<Wishlist> {
        return wishlistRepository.findAllByUser_Email(userEmail)
    }

    // 위시리스트에서 제품 삭제
    @Transactional
    fun removeFromWishlist(userEmail: String, productId: Long): Boolean {
        val deleted = wishlistRepository.deleteByUser_EmailAndProduct_Id(userEmail, productId)
        return deleted > 0
    }

    // 위시리스트에 제품 추가
    @Transactional
    fun addToWishlist(userEmail: String, productId: Long) {
        val user = userRepository.findByEmail(userEmail)
            ?: throw IllegalArgumentException("사용자를 찾을 수 없습니다.")

        val product = productRepository.findById(productId).orElseThrow {
            IllegalArgumentException("제품을 찾을 수 없습니다.")
        }

        // 중복 확인
        if (wishlistRepository.findAllByUser_Email(userEmail).any { it.product.id == productId }) {
            throw IllegalArgumentException("이미 위시리스트에 추가된 제품입니다.")
        }

        // 위시리스트에 추가
        val wishlist = Wishlist(user = user, product = product)
        wishlistRepository.save(wishlist)
    }
}
