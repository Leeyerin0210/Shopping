package com.shopping.site.dataClass

import java.math.BigDecimal
import jakarta.persistence.*

@Entity
@Table(name = "orderitems")
data class OrderItem(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Order_Item_Id")
    val orderItemId: Long = 0,

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    val order: Order,

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product,

    val quantity: Int,
    val price: BigDecimal
)
 {
    // JPA를 위한 기본 생성자 (빈 생성자)
    protected constructor() : this(
        orderItemId = 0,
        order = Order(),
        product = Product(),
        quantity = 0,
        price = BigDecimal.ZERO
    )
}