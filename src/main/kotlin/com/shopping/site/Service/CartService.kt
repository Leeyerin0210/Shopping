package com.shopping.site.Service

import com.shopping.site.dataClass.Cart
import com.shopping.site.repository.CartRepository
import com.shopping.site.repository.ProductRepository
import com.shopping.site.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CartService(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository
) {

    @Transactional
    fun addToCart(userEmail: String, productId: Long, quantity: Int): String {
        // 사용자 확인
        val user = userRepository.findByEmail(userEmail)
            ?: throw IllegalArgumentException("User with email $userEmail not found.")

        // 제품 확인
        val product = productRepository.findById(productId)
            .orElseThrow { IllegalArgumentException("Product with id $productId not found.") }

        // 장바구니에 이미 있는지 확인
        val existingCartItem = cartRepository.findByUserAndProduct(user, product)
        if (existingCartItem != null) {
            existingCartItem.quantity += quantity
            cartRepository.save(existingCartItem)
            return "Updated product quantity in cart."
        }

        // 장바구니에 새로 추가
        val cartItem = Cart(user = user, product = product, quantity = quantity)
        cartRepository.save(cartItem)

        return "Product added to cart successfully."
    }
}
