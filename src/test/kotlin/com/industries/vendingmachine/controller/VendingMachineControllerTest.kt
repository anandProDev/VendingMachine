package com.industries.vendingmachine.controller

import com.industries.vendingmachine.exception.InvalidDenominationException
import com.industries.vendingmachine.exception.NotEnoughItemsInVendingMachineException
import com.industries.vendingmachine.exception.UserNotAllowedException
import com.industries.vendingmachine.model.*
import com.industries.vendingmachine.service.ProductService
import com.industries.vendingmachine.service.UserService
import com.industries.vendingmachine.service.VendingMachineService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import java.math.BigDecimal

@DisplayName("Vending machine tests")
@ExtendWith(MockitoExtension::class)
class VendingMachineControllerTest {
    private val userService: UserService = mockk()
    private val productService: ProductService = mockk()
    private val vendingMachineService: VendingMachineService = mockk()
    private val vendingMachineController = VendingMachineController(productService, userService, vendingMachineService)

    val depositorModel = DepositorModel(1, 100)
    val productModel = ProductModel(1, "a", 1, BigDecimal(10), 1)

    private val userModel = UserModel(
        1,
        "myusername",
        "mypassword",
        BigDecimal(100),
        Role.SELLER
    )

    val productBuyerModel = ProductBuyerModel(1, 1, 2)

    @DisplayName("Seller tries to deposit money")
    @Test
    fun `UserNotAllowedException thrown when seller tries to deposit money`() {

        every { userService.getUser(depositorModel.buyerId.toLong()) } returns userModel

        assertThrows<UserNotAllowedException> {
            vendingMachineController.deposit(depositorModel)
        }
    }

    @DisplayName("Seller tries to deposit money")
    @Test
    fun `InvalidDenominationException thrown invalid amount is deposited`() {

        val updatedModel = userModel.copy(
            role = Role.BUYER
        )
        val updatedDepositorModel = depositorModel.copy(
            amount = 11
        )

        every { userService.getUser(depositorModel.buyerId.toLong()) } returns updatedModel

        assertThrows<InvalidDenominationException> {
            vendingMachineController.deposit(updatedDepositorModel)
        }
    }

    @DisplayName("Buyer deposits money successfully")
    @Test
    fun `Buyer deposits money successfully`() {

        val updatedModel = userModel.copy(
            role = Role.BUYER
        )
        every { userService.getUser(depositorModel.buyerId.toLong()) } returns updatedModel
        val updatedDeposit = updatedModel.copy(deposit = BigDecimal(200))
        every { userService.updateUser(updatedDeposit)} returns updatedDeposit

        val deposit = vendingMachineController.deposit(depositorModel)

        assertEquals(HttpStatus.OK.value(), deposit.statusCode.value())
        assertEquals(updatedDeposit.deposit.toInt(), deposit.body?.deposit)
        assertEquals(depositorModel.buyerId, deposit.body?.buyerId)
    }

    @DisplayName("seller unsuccessful in buying product")
    @Test
    fun `UserNotAllowedException when seller tries to buy product`() {
        every { userService.getUser(productBuyerModel.buyerId.toLong()) } returns userModel

        assertThrows<UserNotAllowedException> {
            vendingMachineController.buy(productBuyerModel)
        }
    }

    @DisplayName("Vending machine out of products")
    @Test
    fun `NotEnoughItemsInVendingMachineException is thrown when product not available`() {
        every { userService.getUser(productBuyerModel.buyerId.toLong()) } returns userModel
        val updatedModel = userModel.copy(
            role = Role.BUYER
        )
        every { userService.getUser(depositorModel.buyerId.toLong()) } returns updatedModel
        val modelWithNewAmount = updatedModel.copy(deposit = BigDecimal(200))
        every { userService.updateUser(modelWithNewAmount) } returns modelWithNewAmount
        every { productService.getProduct(productBuyerModel.productId.toLong()) } returns productModel

        vendingMachineController.deposit(depositorModel)

        assertThrows<NotEnoughItemsInVendingMachineException> {
            vendingMachineController.buy(productBuyerModel)
        }
    }

    @DisplayName("Buyer does not have enough funds to purchase product")
    @Test
    fun `FundsUnavailableException is thrown when funds are insufficient`() {
        every { userService.getUser(productBuyerModel.buyerId.toLong()) } returns userModel
        val updatedModel = userModel.copy(
            role = Role.BUYER
        )
        every { userService.getUser(depositorModel.buyerId.toLong()) } returns updatedModel
        val updatedDepositModel = depositorModel.copy(amount = 5)
        every { productService.getProduct(productBuyerModel.productId.toLong()) } returns productModel
        val modelWithNewAmount = updatedModel.copy(deposit = BigDecimal(105))
        every { userService.updateUser(modelWithNewAmount) } returns modelWithNewAmount

        vendingMachineController.deposit(updatedDepositModel)

        assertThrows<NotEnoughItemsInVendingMachineException> {
            vendingMachineController.buy(productBuyerModel)
        }
    }

    @DisplayName("Buyer purchase of product successful")
    @Test
    fun `Buyer purchases product successfully`() {

        val updatedModel = userModel.copy(
            role = Role.BUYER
        )
        every { userService.getUser(productBuyerModel.buyerId.toLong()) } returns updatedModel

        val updatedProductModel = productModel.copy(quantityavailable = 5)
        every { productService.getProduct(productBuyerModel.productId.toLong()) } returns updatedProductModel
        every { productService.updateProduct(any()) } returns productModel

        val modelWithDeposit = updatedModel.copy(deposit = BigDecimal(80))
        every { userService.updateUser(modelWithDeposit) } returns modelWithDeposit

        every { vendingMachineService.calculateChange(any()) } returns arrayListOf(5)

        val response = vendingMachineController.buy(productBuyerModel)

        assertEquals(HttpStatus.OK.value(), response.statusCode.value())
        assertEquals(BigDecimal(20), response.body?.costOfPurchase)
        assertEquals(BigDecimal(80), response.body?.amountRemaining)
        assertEquals(AllowedDepositDenomination.FIVE.value, response.body?.denominationsReturned?.getOrNull(0))
        assertEquals(productModel.productname, response.body?.productName)
    }
}