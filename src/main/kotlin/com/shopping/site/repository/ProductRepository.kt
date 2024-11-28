package com.shopping.site.repository

import com.shopping.site.dataClass.Product
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
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

    // JSON으로 저장된 이미지 URL 목록 반환
    @Query("SELECT p.images FROM Product p WHERE p.id = :id")
    fun findImagesByProductId(@Param("id") id: Long): String

    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - :quantity WHERE p.id = :productId AND p.stock >= :quantity")
    fun reduceStock(@Param("productId") productId: Long, @Param("quantity") quantity: Int): Int

    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock + :quantity WHERE p.id = :productId")
    fun increaseStock(@Param("productId") productId: Long, @Param("quantity") quantity: Int): Int
}

