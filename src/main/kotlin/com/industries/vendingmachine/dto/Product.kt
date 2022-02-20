package com.industries.vendingmachine.dto

import com.industries.vendingmachine.model.ProductModel
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType


@Table("PRODUCTS")
data class Product(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val productname: String,
    val quantityavailable: Long,
    val cost: BigDecimal,
    val sellerid: Long
) {
    fun toProductModel() =
        ProductModel(
            id = id,
            productname = productname,
            quantityavailable = quantityavailable,
            cost = cost,
            sellerid = sellerid
        )
}
