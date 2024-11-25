package com.shopping.site

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
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
        val images = productRepository.findByIdWithImageUrl(id)
        val product = productRepository.searchById(id)
        model.addAttribute("product", product)
        model.addAttribute("images", images)
        return "product-details"
    }
}
