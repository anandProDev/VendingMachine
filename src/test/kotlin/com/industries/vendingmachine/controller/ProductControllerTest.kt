package com.industries.vendingmachine.controller

import com.industries.vendingmachine.model.ProductModel
import com.industries.vendingmachine.service.ProductService
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import java.math.BigDecimal

@DisplayName("Product controller tests")
@ExtendWith(MockKExtension::class)
class ProductControllerTest{

    @MockK
    lateinit var productService : ProductService
    @InjectMocks
    lateinit var productController : ProductController

    @Test
    fun `returnsProductInformationSuccessfully`(){

        every { productService.getProducts() } returns listOf(ProductModel(1, "a", 1, BigDecimal(10), 1))

        val items = productController.getProducts()

        verify(exactly = 1) { productService.getProducts() }
        assertEquals(1, items.body?.size)
    }
}