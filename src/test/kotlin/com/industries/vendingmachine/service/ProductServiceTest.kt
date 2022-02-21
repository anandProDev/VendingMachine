package com.industries.vendingmachine.service

import com.industries.vendingmachine.dto.Product
import com.industries.vendingmachine.exception.ProductException
import com.industries.vendingmachine.exception.ProductUnavailableException
import com.industries.vendingmachine.model.ProductModel
import com.industries.vendingmachine.model.Role
import com.industries.vendingmachine.model.UserModel
import com.industries.vendingmachine.repo.ProductRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal
import java.util.*


@DisplayName("Product service tests")
@ExtendWith(MockitoExtension::class)
class ProductServiceTest {

    private val productRepository: ProductRepository = mockk()
    private val userService: UserService = mockk()

    val productService = ProductService(productRepository, userService)

    val product = Product(1, "snickers", 10, BigDecimal(10), 1)
    val productModel = ProductModel(1, "snickers", 10, BigDecimal(10), 1)

    private val userModel = UserModel(
        1,
        "myusername",
        "mypassword",
        BigDecimal(100),
        Role.SELLER
    )

    @DisplayName("Displays all products")
    @Test
    fun `returns list of all products successfully`() {
        every { productRepository.findProducts() } returns listOf(product)

        val products = productService.getProducts()

        assertEquals(1, products.size)
        val returnedProduct = products[0]
        assertEquals(product.id, returnedProduct.id)
        assertEquals(product.productname, returnedProduct.productname)
        assertEquals(product.cost, returnedProduct.cost)
        assertEquals(product.quantityavailable, returnedProduct.quantityavailable)
        assertEquals(product.sellerid, returnedProduct.sellerid)
    }

    @DisplayName("Error fetching data from database")
    @Test
    fun `ProductException_isThrownWhenThereIsErrorFetchingDataFromDB`() {
        every { productRepository.findProducts() } throws Exception("something went wrong")

        assertThrows<ProductException> { productService.getProducts() }
    }

    @DisplayName("Create product successful")
    @Test
    fun `createProductSuccessful`() {

        val copyProduct = product.copy(id = 0)
        every { productRepository.save(copyProduct) } returns product

        val createProduct = productService.createProduct(productModel)

        assertEquals(productModel.id, createProduct.id)
        assertEquals(productModel.productname, createProduct.productname)
        assertEquals(productModel.sellerid, createProduct.sellerid)
        assertEquals(productModel.quantityavailable, createProduct.quantityavailable)
    }

    @DisplayName("Unknown error when creating product")
    @Test
    fun `shouldThrowProductExceptionWhenSomethingGoesWrongWhenSavingProduct`() {
        val copyProduct = product.copy(id = 0)
        every { productRepository.save(copyProduct) } throws Exception("something went wrong")

        assertThrows<ProductException> { productService.createProduct(productModel) }
    }

    @DisplayName("Update product successful")
    @Test
    fun `update Product Successful`() {

        every { productRepository.save(product) } returns product

        val updatedProduct = productService.updateProduct(productModel)

        assertEquals(productModel.id, updatedProduct.id)
        assertEquals(productModel.productname, updatedProduct.productname)
        assertEquals(productModel.sellerid, updatedProduct.sellerid)
        assertEquals(productModel.quantityavailable, updatedProduct.quantityavailable)
    }

    @DisplayName("Exception is thrown when updating product")
    @Test
    fun `throws ProductException When Update Product Fails`() {

        val copyProduct = product.copy(id = 0)
        every { productRepository.save(copyProduct) } throws Exception("something went wrong")

        assertThrows<ProductException> { productService.updateProduct(productModel) }
    }

    @DisplayName("Gets one product based on id")
    @Test
    fun `returns Product Successfully`() {

        every { productRepository.findById(product.id) } returns Optional.of(product)

        val returnedProduct = productService.getProduct(product.id)

        assertEquals(productModel.id, returnedProduct.id)
        assertEquals(productModel.productname, returnedProduct.productname)
        assertEquals(productModel.sellerid, returnedProduct.sellerid)
        assertEquals(productModel.quantityavailable, returnedProduct.quantityavailable)
    }

    @DisplayName("product does not exist for given id")
    @Test
    fun `ProductUnavailableException Is Thrown When NoProductFound For Id`() {

        every { productRepository.findById(product.id) } returns Optional.empty()

        assertThrows<ProductUnavailableException> { productService.getProduct(product.id) }
    }

    @DisplayName("Product can be deleted based on id")
    @Test
    fun `delete productById is Successful`() {
        every { productRepository.deleteById(product.id) } returns Unit
        productService.deleteProduct(product.id)

        verify(exactly = 1) { productRepository.deleteById(product.id) }
    }

    @DisplayName("Seller is product owner and has seller role")
    @Test
    fun `Seller is authorised`() {
        every { productRepository.findById(product.id) } returns Optional.of(product)
        every { userService.getUser(product.sellerid) } returns userModel

        assertFalse(productService.isUnAuthorizeSeller(product.id))
    }

    @DisplayName("Buyer tries to access product and fails")
    @Test
    fun `Buyer is unauthorised`() {
        val copyUserModel = userModel.copy(role = Role.BUYER)
        every { productRepository.findById(product.id) } returns Optional.of(product)
        every { userService.getUser(product.sellerid) } returns copyUserModel

        assertTrue(productService.isUnAuthorizeSeller(product.id))
    }


}