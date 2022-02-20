package com.industries.vendingmachine.repo

import com.industries.vendingmachine.dto.Product
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository

interface ProductRepository : CrudRepository<Product, Long>{

    @Query("select * from products")
    fun findProducts(): List<Product>

    @Query("DELETE FROM products WHERE id = #{id}")
    override fun deleteById(id: Long)

    @Query(
        "INSERT INTO products(id, productName, amountAvailable,deposit, sellerid) " +
                " VALUES (#{id}, #{productName}, #{amountAvailable}, #{deposit}, #{sellerid})"
    )
    fun insert(products: Product): Long
}
