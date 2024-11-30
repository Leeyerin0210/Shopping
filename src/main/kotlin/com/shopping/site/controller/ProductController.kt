package com.shopping.site.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import com.shopping.site.repository.ProductRepository
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

@Controller
class ProductController(private val productRepository: ProductRepository) {

    // 기존 검색 엔드포인트
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

    // 기존 제품 상세 정보 엔드포인트
    @GetMapping("/product/{id}")
    fun getProduct(@PathVariable id: Long, model: Model): String {
        val images = getImagesForProduct(id, productRepository)
        val product = productRepository.searchById(id)
        model.addAttribute("product", product)
        model.addAttribute("images", images)
        return "product-details"
    }

    // 카테고리별 제품 조회 엔드포인트
    @GetMapping("/category/{category}")
    fun getProductsByCategory(
        @PathVariable category: String,
        @RequestParam(required = false, defaultValue = "default") sort: String,
        model: Model
    ): String {
        // 카테고리 이름의 첫 글자를 대문자로 변환
        val formattedCategory = category.replaceFirstChar { it.uppercaseChar() }

        // 카테고리별 정렬된 제품 목록 가져오기
        val products = when (sort) {
            "asc" -> productRepository.findByCategoryOrderByPriceAsc(category)
            "desc" -> productRepository.findByCategoryOrderByPriceDesc(category)
            else -> productRepository.findByCategory(category)
        }

        // 뷰로 데이터 전달
        model.addAttribute("category", formattedCategory) // 변환된 카테고리 이름
        model.addAttribute("products", products)
        return "category-products" // 템플릿 파일 이름 (확장자 생략)
    }


    fun getImagesForProduct(productId: Long, productRepository: ProductRepository): List<String> {
        val jsonImages = productRepository.findImagesByProductId(productId) ?: "[]"
        val objectMapper = jacksonObjectMapper()

        return try {
            // jackson-module-kotlin 사용
            objectMapper.readValue<List<String>>(jsonImages)
        } catch (e: Exception) {
            println("JSON 파싱 오류: ${e.message}")
            emptyList() // 파싱 실패 시 빈 리스트 반환
        }
    }
}

