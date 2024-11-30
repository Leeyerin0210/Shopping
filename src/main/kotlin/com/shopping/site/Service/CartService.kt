package com.shopping.site.Service

import com.shopping.site.dataClass.Cart
import com.shopping.site.dataClass.User
import com.shopping.site.repository.CartRepository
import com.shopping.site.repository.CouponRepository
import com.shopping.site.repository.ProductRepository
import com.shopping.site.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDate

@Service
class CartService(
    private val cartRepository: CartRepository,
    private val productRepository: ProductRepository,
    private val userRepository: UserRepository,
    private val couponRepository: CouponRepository
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
    @Transactional
    fun calculateDiscountedTotal(userEmail: String, points: Int, couponId: Long?): BigDecimal {
        // 사용자의 장바구니 아이템 가져오기
        val cartItems = cartRepository.findAllByUser_Email(userEmail)
        if (cartItems.isEmpty()) {
            throw IllegalArgumentException("Cart is empty")
        }

        // 총액 계산
        val subtotal = calculateSubtotal(cartItems)
        val shipping = BigDecimal("4000") // 고정 배송비
        var total = subtotal + shipping

        // 사용자의 적립금 확인
        val user = userRepository.findByEmail(userEmail)
            ?: throw IllegalArgumentException("User not found")

        if (points > user.balance) {
            throw IllegalArgumentException("Insufficient points")
        }

        // 적립금 차감
        if (points > 0) {
            total -= points.toBigDecimal()
            if (total < BigDecimal.ZERO) {
                total = BigDecimal.ZERO // 총액은 0 이하가 될 수 없음
            }
        }

        // 쿠폰 적용
        if (couponId != null) {
            val coupon = couponRepository.findById(couponId)
                .orElseThrow { IllegalArgumentException("Invalid coupon") }

            if (coupon.user?.email != userEmail) {
                throw IllegalArgumentException("Coupon does not belong to the user")
            }

            if (coupon.expiryDate.isBefore(LocalDate.now())) {
                throw IllegalArgumentException("Coupon has expired")
            }

            val discountAmount = subtotal * (coupon.discount.toBigDecimal() / BigDecimal("100"))
            total -= discountAmount
            if (total < BigDecimal.ZERO) {
                total = BigDecimal.ZERO // 총액은 0 이하가 될 수 없음
            }
        }

        return total
    }

    @Transactional
    fun finalizeOrder(userEmail: String, totalAmount: BigDecimal) {
        // 사용자의 정보 가져오기
        val user = userRepository.findByEmail(userEmail)
            ?: throw IllegalArgumentException("User not found")

        // 적립금 계산 (10%)
        val rewardPoints = totalAmount * BigDecimal("0.1")

        // 사용자 적립금 업데이트
        user.balance += rewardPoints.toInt() // 적립금을 정수로 변환하여 추가
        userRepository.save(user)

        // 장바구니 비우기
        cartRepository.deleteAllByUser_Email(userEmail)
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
