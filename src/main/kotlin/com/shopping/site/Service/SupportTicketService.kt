package com.shopping.site.Service

import com.shopping.site.dataClass.SupportTicket
import com.shopping.site.repository.SupportTicketRepository
import com.shopping.site.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class SupportTicketService(
    private val ticketRepository: SupportTicketRepository,
    private val userRepository: UserRepository
) {
    // 모든 티켓 조회
    fun getAllTickets(): List<SupportTicket> {
        return ticketRepository.findAll()
    }
    fun getTicketById(id: Long): SupportTicket {
        return ticketRepository.findById(id).orElseThrow {
            IllegalArgumentException("Ticket with ID $id not found")
        }
    }
    fun updateTicket(
        id: Long,
        title: String,
        category: String,
        message: String,
        isSecret: Boolean,
        userEmail: String
    ) {
        val ticket = ticketRepository.findById(id).orElseThrow {
            IllegalArgumentException("Ticket with ID $id not found")
        }

        // 작성자 확인
        if (ticket.user.email != userEmail) {
            throw IllegalArgumentException("Unauthorized to update this ticket")
        }

        ticket.title = title
        ticket.category = category
        ticket.message = message
        ticket.isSecret = isSecret

        ticketRepository.save(ticket) // 변경 내용 저장
    }
    fun addReply(ticketId: Long, replyContent: String) {
        val ticket = ticketRepository.findById(ticketId).orElseThrow {
            IllegalArgumentException("문의가 존재하지 않습니다.")
        }

        ticket.reply = replyContent
        ticket.hasReply = true
        ticketRepository.save(ticket)
    }

    // 티켓 생성
    fun createTicket(email: String, title: String, message: String, isSecret: Boolean, category: String) {
        val user = userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("User not found")
        val ticket = SupportTicket(
            title = title,
            message = message,
            isSecret = isSecret,
            category = category,
            user = user
        )
        ticketRepository.save(ticket)
    }
}
