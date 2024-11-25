package com.shopping.site




import jakarta.persistence.*

@Entity
@Table(name = "PRODUCT_IMAGES")
data class ProductImage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "image_url", nullable = false)
    val imageUrl: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    val product: Product
) {
    constructor() : this(
        id = 0,
        imageUrl = "",
        product = Product()
    )
}
