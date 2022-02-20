package com.industries.vendingmachine.model

import com.industries.vendingmachine.dto.Product
import java.math.BigDecimal

data class ProductModel(
    val id: Long,
    val productname: String,
    val quantityavailable: Long,
    val cost: BigDecimal,
    val sellerid: Long
) {
    fun toProduct() = Product(
        id = id,
        productname = productname,
        quantityavailable = quantityavailable,
        cost = cost,
        sellerid = sellerid
    )
}

