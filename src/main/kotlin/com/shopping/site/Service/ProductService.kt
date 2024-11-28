package com.shopping.site.Service

import com.shopping.site.repository.ProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ProductService(
    private val productRepository: ProductRepository
) {

    @Transactional
    fun reduceStock(productId: Long, quantity: Int) {
        val updatedRows = productRepository.reduceStock(productId, quantity)
        if (updatedRows == 0) {
            throw IllegalStateException("Insufficient stock for product ID $productId")
        }
    }

    @Transactional
    fun increaseStock(productId: Long, quantity: Int) {
        productRepository.increaseStock(productId, quantity)
    }
}
