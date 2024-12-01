package com.shopping.site.Service

import com.shopping.site.dataClass.Admin
import com.shopping.site.repository.AdminRepository
import org.springframework.stereotype.Service

@Service
class AdminService(private val adminRepository: AdminRepository) {
    fun login(email: String, password: String): Admin? {
        val admin = adminRepository.findByEmail(email)
        return if (admin != null && admin.password == password) {
            admin
        } else {
            null
        }
    }
}
