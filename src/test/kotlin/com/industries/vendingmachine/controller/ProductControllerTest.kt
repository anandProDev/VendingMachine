package com.industries.vendingmachine.controller

import com.industries.vendingmachine.exception.ProductException
import com.industries.vendingmachine.exception.SellerNotAllowedToPerformOperationException
import com.industries.vendingmachine.exception.UserNotAllowedException
import com.industries.vendingmachine.model.ProductModel
import com.industries.vendingmachine.model.Role
import com.industries.vendingmachine.model.UserModel
import com.industries.vendingmachine.service.ProductService
import com.industries.vendingmachine.service.UserService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import java.math.BigDecimal

@DisplayName("Product tests")
@ExtendWith(MockitoExtension::class)
class ProductControllerTest {

    private val productService: ProductService = mockk()
    private val userService: UserService = mockk()
    private val productController = ProductController(productService, userService)

    val productModel = ProductModel(1, "a", 1, BigDecimal(10), 1)

    private val userModel = UserModel(
        1,
        "myusername",
        "mypassword",
        BigDecimal(100),
        Role.BUYER
    )

    @Test
    @DisplayName("No products found")
    fun `returnsEmptyList_WhenNoProductsAreFound`() {
        every { productService.getProducts() } returns listOf()
        val items = productController.getProducts()

        verify(exactly = 1) { productService.getProducts() }
        assertTrue { items.statusCode.is2xxSuccessful }
        items.body?.isEmpty()?.let { assertTrue(it) }
    }

    @Test
    @DisplayName("Returns products")
    fun `returns Products Successfully`() {
        every { productService.getProducts() } returns listOf(productModel)

        val items = productController.getProducts()

        verify(exactly = 1) { productService.getProducts() }
        assertTrue { items.statusCode.is2xxSuccessful }
        assertEquals(1, items.body?.size)
        val body = items.body?.get(0)
        assertEquals(productModel, body)
    }

    @DisplayName("Create product successful")
    @Test
    fun `createProductSuccessfully`() {

        val sellerUserModel = userModel.copy(role = Role.SELLER)
        every { userService.getUser(productModel.sellerid) } returns sellerUserModel
        every { productService.createProduct(productModel) } returns productModel

        val response = productController.createProduct(productModel)

        verify(exactly = 1) { productService.createProduct(productModel) }
        assertEquals(HttpStatus.CREATED.value(), response.statusCode.value())
        val body = response.body
        assertEquals(productModel, body)
    }

    @DisplayName("Buyer fails to create product")
    @Test
    fun `createProduct fails throwing UserNotAllowedException`() {

        val buyerUserModel = userModel.copy(role = Role.BUYER)
        every { userService.getUser(productModel.sellerid) } returns buyerUserModel
        every { productService.createProduct(productModel) } returns productModel

        assertThrows<UserNotAllowedException> { productController.createProduct(productModel) }

        verify(exactly = 1) { userService.getUser(productModel.id) }
    }

    @Nested
    inner class `given a product to update`() {
        @Nested
        inner class `when an unauthrorized seller is trying to update the product`() {

            @Test
            fun `SellerNotAllowedToPerformOperationException is thrown`() {

                every { productService.isUnAuthorizeSeller(productModel.id) } throws SellerNotAllowedToPerformOperationException(
                    "seller unauthorized"
                )

                assertThrows<SellerNotAllowedToPerformOperationException> {
                    productController.updateProduct(productModel)
                }
            }
        }

        @Nested
        inner class `when authorised user is trying to update product`() {

            @Nested
            inner class `cost is not in multiples of 5`() {

                @Test
                fun `then ProductException is thrown`() {

                    val updatedModel = productModel.copy(cost = BigDecimal(12))
                    every { productService.isUnAuthorizeSeller(updatedModel.id) } returns false

                    assertThrows<ProductException> {
                        productController.updateProduct(updatedModel)
                    }
                }
            }

            @Test
            fun `Product is updated successfully`() {

                val updatedModel = productModel.copy(cost = BigDecimal(15))
                every { productService.isUnAuthorizeSeller(updatedModel.id) } returns false
                every { productService.getProduct(productModel.id) } returns productModel
                every { productService.updateProduct(updatedModel) } returns updatedModel

                val response = productController.updateProduct(updatedModel)

                assertEquals(HttpStatus.OK.value(), response.statusCode.value())
                assertEquals(updatedModel, response.body)
            }
        }


        @Nested
        inner class `when delete product is called` {

            @Nested
            inner class `when seller is not authorised to delete product` {

                @Test
                fun `SellerNotAllowedToPerformOperationException is thrown`() {

                    every { productService.isUnAuthorizeSeller(productModel.id) } throws SellerNotAllowedToPerformOperationException(
                        "seller unauthorized"
                    )

                    assertThrows<SellerNotAllowedToPerformOperationException> {
                        productController.deleteProduct(productModel.id)
                    }
                }
            }

            @Test
            fun `product is deleted`() {

                every { productService.deleteProduct(productModel.id) } returns Unit
                every { productService.isUnAuthorizeSeller(productModel.id) } returns false

                val response = productController.deleteProduct(productModel.id)

                verify(exactly = 1) { productService.deleteProduct(productModel.id) }
                assertEquals(HttpStatus.NO_CONTENT.value(), response.statusCode.value())
            }
        }
    }


}