package com.shopping.site.controller

import com.shopping.site.dataClass.Comment
import com.shopping.site.repository.CommentRepository
import com.shopping.site.repository.ReviewRepository
import com.shopping.site.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/comments")
class CommentController(
    private val commentRepository: CommentRepository,
    private val reviewRepository: ReviewRepository,
    private val userRepository: UserRepository
) {


    @PostMapping("/{reviewId}/add")
    fun addComment(
        @PathVariable reviewId: Long,
        @RequestParam content: String,
        request: HttpServletRequest
    ): String {
        // 로그인 확인
        val userEmail = request.session.getAttribute("userEmail") as? String
            ?: run {
                // 세션에 현재 URL 저장
                request.session.setAttribute("redirectAfterLogin", "/reviews/$reviewId/details")
                return "redirect:/login"
            }

        // 이메일로 User 엔터티 조회
        val user = userRepository.findByEmail(userEmail)
            ?: throw IllegalArgumentException("User not found.")

        // 리뷰 가져오기
        val review = reviewRepository.findById(reviewId)
            .orElseThrow { IllegalArgumentException("Review not found") }

        // 댓글 저장
        val comment = Comment(
            content = content,
            user = user, // User 엔터티 참조
            review = review
        )
        commentRepository.save(comment)

        return "redirect:/reviews/$reviewId/details" // 댓글 저장 후 리뷰 상세 페이지로 리다이렉트
    }

}
