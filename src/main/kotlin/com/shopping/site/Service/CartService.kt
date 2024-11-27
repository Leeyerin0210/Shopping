package com.shopping.site.Service

import com.shopping.site.dataClass.Cart
import com.shopping.site.dataClass.User
import com.shopping.site.repository.CartRepository
import com.shopping.site.repository.ProductRepository
import com.shopping.site.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CartService(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository
) {

    @Transactional
    fun addToCart(userEmail: String, productId: Long, quantity: Int) {
        val product = productRepository.findById(productId).orElseThrow {
            IllegalArgumentException("제품을 찾을 수 없습니다.")
        }

        // 중복 확인
        val existingCartItem = cartRepository.findByUser_EmailAndProduct_Id(userEmail, productId)
        if (existingCartItem != null) {
            // 수량 업데이트
            existingCartItem.quantity += quantity
            cartRepository.save(existingCartItem)
        } else {
            // 새로운 항목 추가
            val cart = Cart(
                user = User(email = userEmail),
                product = product,
                quantity = quantity
            )
            cartRepository.save(cart)
        }
    }

    fun getCartItems(userEmail: String): List<Cart> {
        return cartRepository.findAllByUser_Email(userEmail)
    }

    @Transactional
    fun removeFromCart(userEmail: String, productId: Long) {
        val deleted = cartRepository.deleteByUser_EmailAndProduct_Id(userEmail, productId)
        if (deleted == 0) throw IllegalArgumentException("삭제할 항목을 찾을 수 없습니다.")
    }
}
