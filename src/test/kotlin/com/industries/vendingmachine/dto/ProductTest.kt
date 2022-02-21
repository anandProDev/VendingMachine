package com.industries.vendingmachine.dto

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal


@DisplayName("User tests")
@ExtendWith(MockitoExtension::class)
class ProductTest {

    val product = Product(1, "snickers", 10, BigDecimal(10), 1)

    @DisplayName("Converts usermodel to user")
    @Test
    fun `transform userModel to user`() {

        val toProductModel = product.toProductModel()

        assertEquals(product.id, toProductModel.id)
        assertEquals(product.productname, toProductModel.productname)
        assertEquals(product.cost, toProductModel.cost)
        assertEquals(product.quantityavailable, toProductModel.quantityavailable)
        assertEquals(product.sellerid, toProductModel.sellerid)
    }

}