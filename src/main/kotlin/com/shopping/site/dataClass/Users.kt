package com.shopping.site.dataClass
import jakarta.persistence.*


@Entity
@Table(name = "Users")
data class User(
    @Id
    @Column(name = "email", nullable = false, unique = true)
    val email: String = "",
    val name: String = "",
    val password: String = "",

) {
    constructor() : this(
        email = "",
        name = "",
        password = "",
    )
}
