package com.shopping.site.controller

import com.shopping.site.dataClass.Comment
import com.shopping.site.dataClass.ReviewData
import com.shopping.site.dataClass.User
import com.shopping.site.repository.ReviewRepository
import com.shopping.site.repository.ProductRepository
import com.shopping.site.repository.CommentRepository
import com.shopping.site.repository.UserRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*

@Controller
@RequestMapping("/reviews")
class ReviewController(
    private val reviewRepository: ReviewRepository,
    private val productRepository: ProductRepository,
    private val commentRepository: CommentRepository,
    private val userRepository: UserRepository
) {

    @GetMapping("/{productId}")
    fun getReviewsForProduct(@PathVariable productId: Long, model: Model): String {
        val product = productRepository.findById(productId)
            .orElseThrow { IllegalArgumentException("Product not found") }
        val reviews = reviewRepository.findAllByProduct_Id(productId)

        model.addAttribute("product", product)
        model.addAttribute("reviews", reviews)
        return "product-reviews"
    }

    @GetMapping("/{productId}/write")
    fun showReviewForm(@PathVariable productId: Long, model: Model): String {
        val product = productRepository.findById(productId)
            .orElseThrow { IllegalArgumentException("Product not found") }
        model.addAttribute("product", product)
        return "write-review"
    }

    @PostMapping("/{productId}/write")
    fun submitReview(
        @PathVariable productId: Long,
        @RequestParam review: String,
        @RequestParam rating: Int,
        request: HttpServletRequest
    ): String {
        val product = productRepository.findById(productId)
            .orElseThrow { IllegalArgumentException("Product not found") }

        val userEmail = request.session.getAttribute("userEmail") as? String
            ?: return "redirect:/login" // 로그인 안 된 경우 로그인 페이지로 리다이렉트

        val user = userRepository.findByEmail(userEmail)
            ?: throw IllegalArgumentException("User not found.")

        val newReview = ReviewData(
            review = review,
            rating = rating,
            product = product,
            user = user
        )
        reviewRepository.save(newReview)

        return "redirect:/reviews/$productId"
    }

    @GetMapping("/{reviewId}/details")
    fun getReviewDetails(@PathVariable reviewId: Long, model: Model): String {
        val review = reviewRepository.findById(reviewId)
            .orElseThrow { IllegalArgumentException("Review not found") }

        val comments = commentRepository.findAllByReview_Id(reviewId)

        model.addAttribute("review", review)
        model.addAttribute("comments", comments)
        return "review-details"
    }

    @PostMapping("/{reviewId}/comments")
    fun addComment(
        @PathVariable reviewId: Long,
        @RequestParam content: String,
        request: HttpServletRequest
    ): String {
        val review = reviewRepository.findById(reviewId)
            .orElseThrow { IllegalArgumentException("Review not found") }

        val userEmail = request.session.getAttribute("userEmail") as? String
            ?: throw IllegalArgumentException("User must be logged in to write a comment.")

        val user = userRepository.findByEmail(userEmail)
            ?: throw IllegalArgumentException("User not found.")

        val comment = Comment(
            content = content,
            review = review,
            user = user
        )
        commentRepository.save(comment)

        return "redirect:/reviews/${review.product?.id}"
    }
}