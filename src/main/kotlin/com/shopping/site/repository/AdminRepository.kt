package com.shopping.site.repository

import com.shopping.site.dataClass.Admin
import org.springframework.data.jpa.repository.JpaRepository

interface AdminRepository : JpaRepository<Admin, Long> {
    fun findByEmail(email: String): Admin?
}
