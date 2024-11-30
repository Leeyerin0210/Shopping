package com.shopping.site.dataClass
import jakarta.persistence.*


@Entity
@Table(name = "Users")
data class User(
    @Id
    @Column(name = "email", nullable = false, unique = true)
    val email: String = "",
    val username: String = "",
    val nickname: String = "",
    val password: String = "",
    val address : String = "",
    var balance: Double = 0.0,

) {
    constructor() : this(
        email = "",
        username = "",
        password = "",
        nickname = "",
        address = ""
    )
}
