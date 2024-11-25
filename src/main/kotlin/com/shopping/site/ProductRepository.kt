package com.shopping.site

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
@Repository
interface ProductRepository : JpaRepository<Product, Long> {

    // 검색 쿼리
    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword%")
    fun searchByKeyword(@Param("keyword") keyword: String): List<Product>

    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% ORDER BY p.price ASC")
    fun searchByKeywordOrderByPriceAsc(@Param("keyword") keyword: String): List<Product>

    @Query("SELECT p FROM Product p WHERE p.name LIKE %:keyword% ORDER BY p.price DESC")
    fun searchByKeywordOrderByPriceDesc(@Param("keyword") keyword: String): List<Product>

    // 제품 ID로 검색
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    fun searchById(@Param("id") id: Long): Product?

    // 이미지 URL 조회
    @Query("SELECT pi.imageUrl FROM ProductImage pi WHERE pi.product.id = :id")
    fun findByIdWithImageUrl(@Param("id") id: Long): List<String>
}
