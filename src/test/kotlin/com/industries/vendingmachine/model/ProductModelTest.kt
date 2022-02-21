package com.industries.vendingmachine.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import java.math.BigDecimal

@DisplayName("Product model test")
class ProductModelTest {

    @DisplayName("Transform successful")
    @Test
    fun `transform successful from model to dto`() {

        val productModel = ProductModel(1, "snickers", 10, BigDecimal(10), 1)
        val toProduct = productModel.toProduct()

        assertEquals(productModel.id, toProduct.id)
        assertEquals(productModel.productname, toProduct.productname)
        assertEquals(productModel.sellerid, toProduct.sellerid)
        assertEquals(productModel.cost, toProduct.cost)
        assertEquals(productModel.quantityavailable, toProduct.quantityavailable)
    }

}