package com.shopping.site.Controller

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.shopping.site.repository.ProductRepository

@Controller
class ProductController(private val productRepository: ProductRepository) {

    @GetMapping("/search")
    fun search(
        @RequestParam query: String,
        @RequestParam(required = false, defaultValue = "default") sort: String,
        model: Model
    ): String {
        val products = when (sort) {
            "asc" -> productRepository.searchByKeywordOrderByPriceAsc(query)
            "desc" -> productRepository.searchByKeywordOrderByPriceDesc(query)
            else -> productRepository.searchByKeyword(query)
        }
        model.addAttribute("products", products)
        model.addAttribute("keyword", query)
        model.addAttribute("sort", sort)
        return "search-results"
    }

    @GetMapping("/product/{id}")
    fun getProduct(@PathVariable id: Long, model: Model): String {
        val images = getImagesForProduct(id,productRepository)
        val product = productRepository.searchById(id)
        model.addAttribute("product", product)
        model.addAttribute("images", images)
        return "product-details"
    }

    fun getImagesForProduct(productId: Long, productRepository: ProductRepository): List<String> {
        val jsonImages = productRepository.findImagesByProductId(productId)
        val objectMapper = jacksonObjectMapper()

        return try {
            objectMapper.readValue<List<String>>(jsonImages) // JSON 문자열 -> List<String>
        } catch (e: Exception) {
            println("JSON 파싱 오류: ${e.message}")
            emptyList() // 파싱 실패 시 빈 리스트 반환
        }
    }

}

