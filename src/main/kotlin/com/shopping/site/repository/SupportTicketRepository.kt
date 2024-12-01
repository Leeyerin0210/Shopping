package com.shopping.site.repository

import com.shopping.site.dataClass.SupportTicket
import org.springframework.data.jpa.repository.JpaRepository

interface SupportTicketRepository : JpaRepository<SupportTicket, Long>
