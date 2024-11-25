package com.shopping.site
import jakarta.persistence.*
import java.math.BigDecimal

@Entity
@Table(name = "PRODUCT")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    val name: String = "",

    @Column(nullable = false)
    val price: BigDecimal = BigDecimal.ZERO,

    @OneToMany(mappedBy = "product", cascade = [CascadeType.ALL], orphanRemoval = true)
    val images: List<ProductImage> = mutableListOf()
) {
    constructor() : this(
        id = 0,
        name = "",
        price = BigDecimal.ZERO,
        images = mutableListOf()
    )
}
