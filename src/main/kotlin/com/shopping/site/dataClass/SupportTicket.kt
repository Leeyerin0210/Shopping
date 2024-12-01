package com.shopping.site.dataClass

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "SUPPORT_TICKET")
data class SupportTicket(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false)
    var title: String,

    @Column(nullable = false, columnDefinition = "TEXT")
    var message: String,

    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "is_secret", nullable = false)
    var isSecret: Boolean = false,

    @Column(nullable = false)
    var category: String = "기타", // 기본값 설정

    @Column(columnDefinition = "TEXT")
    var reply: String? = null, // 답글을 저장 (nullable)

    @Column(name = "has_reply", nullable = false)
    var hasReply: Boolean = false, // 답변 상태 (기본값 false)

    @ManyToOne
    @JoinColumn(name = "USER_EMAIL", nullable = false)
    val user: User
)
{
    val maskedUsername: String
        get() = if (user.email.contains("@")) {
            val parts = user.email.split("@")
            parts[0].replaceRange(1, parts[0].length - 1, "*".repeat(parts[0].length - 2)) + "@" + parts[1]
        } else {
            "익명 사용자"
        }
}