package com.shopping.site

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
    private val userRepository: UserRepository
) {

    @Transactional
    fun register(username: String, email: String, password: String) {
        // 중복 이메일 또는 사용자명 체크
        if (userRepository.findByEmail(email) != null) {
            throw IllegalArgumentException("Email already exists.")
        }

        if (userRepository.findByUsername(username) != null) {
            throw IllegalArgumentException("Username already exists.")
        }

        // 비밀번호 해싱 (예시: 실제로는 BCrypt 사용 권장)
        val hashedPassword = password // 실제로는 해시 처리가 필
        println("Received signup data - Username: $username, Email: $email, Password: $password")
        val user = User(username = username, email = email, password = hashedPassword)
        userRepository.save(user)
    }

    @Transactional(readOnly = true)
    fun authenticateUser(email: String, password: String): Boolean {
        val user = userRepository.findByEmail(email)
        return user?.password == password // 실제로는 해시된 비밀번호 비교 필요
    }
}
