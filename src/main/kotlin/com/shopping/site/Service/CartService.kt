package com.shopping.site.Service

import com.shopping.site.dataClass.Cart
import com.shopping.site.dataClass.User
import com.shopping.site.repository.CartRepository
import com.shopping.site.repository.ProductRepository
import com.shopping.site.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

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

        val existingCartItem = cartRepository.findByUser_EmailAndProduct_Id(userEmail, productId)
        if (existingCartItem != null) {
            existingCartItem.quantity += quantity
            cartRepository.save(existingCartItem)
        } else {
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

    fun calculateSubtotal(cartItems: List<Cart>): BigDecimal {
        return if (cartItems.isEmpty()) {
            BigDecimal.ZERO
        } else {
            cartItems.sumOf { it.product.price * it.quantity.toBigDecimal() }
        }
    }

    @Transactional
    fun updateCartItemQuantity(userEmail: String, productId: Long, quantity: Int) {
        val cartItem = cartRepository.findByUser_EmailAndProduct_Id(userEmail, productId)
            ?: throw IllegalArgumentException("Cart item not found")
        cartItem.quantity = quantity
        cartRepository.save(cartItem)
    }

    @Transactional
    fun removeFromCart(userEmail: String, productId: Long) {
        val deleted = cartRepository.deleteByUser_EmailAndProduct_Id(userEmail, productId)
        if (deleted == 0) throw IllegalArgumentException("삭제할 항목을 찾을 수 없습니다.")
    }
}
