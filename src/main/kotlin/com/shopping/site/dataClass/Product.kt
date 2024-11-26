package com.shopping.site.dataClass

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

    @Column(nullable = false,name = "Main_Img")
    val mainImg: String = "",

    @Column(nullable = false, columnDefinition = "TEXT",name = "Detail_Imgs")
    val images: String = "[]"
) {
    constructor() : this(
        id = 0,
        name = "",
        price = BigDecimal.ZERO,
        images = "[]"
    )
}
