package com.shopping.site.Service

import com.shopping.site.dataClass.Cart
import com.shopping.site.dataClass.User
import com.shopping.site.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository
) {
    @Transactional
    fun upBalance(email: String, amount: Double) {
        val user = userRepository.findByEmail(email)!!
        user.balance += amount
        userRepository.save(user)
    }

    @Transactional
    fun downBalance(email: String, amount: Double) {
        val user = userRepository.findByEmail(email)!!
        user.balance -= amount
        userRepository.save(user)
    }

    fun findUserByEmail(userEmail: String): User? {
        return userRepository.findByEmail(userEmail)
    }
}
