package com.shopping.site.repository

import com.shopping.site.dataClass.Comment
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findAllByReview_Id(reviewId: Long): List<Comment>
}
