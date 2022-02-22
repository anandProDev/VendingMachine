package com.industries.vendingmachine.controller

import com.industries.vendingmachine.exception.FundsUnavailableException
import com.industries.vendingmachine.exception.InvalidDenominationException
import com.industries.vendingmachine.exception.NotEnoughItemsInVendingMachineException
import com.industries.vendingmachine.exception.UserNotAllowedException
import com.industries.vendingmachine.model.*
import com.industries.vendingmachine.service.ProductService
import com.industries.vendingmachine.service.UserService
import com.industries.vendingmachine.service.VendingMachineService
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController
@RequestMapping("vendingmachine")
class VendingMachineController(val productService: ProductService, val userService: UserService, val vendingMachineService: VendingMachineService) {

    companion object {
        private val klogger = KotlinLogging.logger { }
        const val APPLICATION_JSON = "application/json"
    }

    @PostMapping("/deposit", consumes = [APPLICATION_JSON], produces = [APPLICATION_JSON])
    fun deposit(@RequestBody depositorModel: DepositorModel): ResponseEntity<DepositAvailableModel> {
        val (depositerId, amount) = depositorModel
        val user = userService.getUser(depositerId.toLong())

        if (user.role == Role.SELLER) {
            klogger.info { "Seller with id $depositerId attempted to deposit money" }
            throw UserNotAllowedException("Only buyer allowed to deposit money $depositerId is   a seller ")
        }

        AllowedDepositDenomination.fromInt(amount)
            ?: throw InvalidDenominationException("Please enter a valid amount. Allowed amounts ${AllowedDepositDenomination.getValues()}")

        val newDeposit = user.deposit.add(BigDecimal(amount))
        val copy = user.copy(
            deposit = newDeposit
        )
        userService.updateUser(copy)

        val depositAvailableModel = DepositAvailableModel(
            buyerId = depositerId,
            deposit = copy.deposit.toInt()
        )
        return ResponseEntity<DepositAvailableModel>(depositAvailableModel, HttpStatus.OK)
    }

    @PostMapping("/buy", consumes = [APPLICATION_JSON], produces = [APPLICATION_JSON])
    fun buy(@RequestBody productBuyerModel: ProductBuyerModel): ResponseEntity<VendingMachineResponseModel> {
        val (buyerId, productId, quantity) = productBuyerModel

        val user = userService.getUser(buyerId.toLong())

        if (user.role == Role.SELLER) {
            klogger.info { "Seller with id $buyerId attempted to buy product" }
            throw UserNotAllowedException("Only buyer allowed to purchase product")
        }

        val availableFunds = user.deposit
        val product = productService.getProduct(productId.toLong())
        val productCost = product.cost * BigDecimal(quantity)
        if (quantity > product.quantityavailable)
            throw NotEnoughItemsInVendingMachineException("Quantity requested unavailable")

        if (productCost > availableFunds)
            throw FundsUnavailableException("Sorry, not enough funds to complete purchase")

        val fundsCurrentlyAvailable = availableFunds - productCost
        val copy = user.copy(
            deposit = fundsCurrentlyAvailable
        )
        userService.updateUser(copy)

        val productWithAvailableQuantity = product.copy(
            quantityavailable = (product.quantityavailable - quantity)
        )
        productService.updateProduct(productWithAvailableQuantity)

        val calculateChange = vendingMachineService.calculateChange(fundsCurrentlyAvailable.toInt())
        val vendingMachineResponseModel =
            VendingMachineResponseModel(productCost, fundsCurrentlyAvailable, calculateChange, product.productname)
        return ResponseEntity<VendingMachineResponseModel>(vendingMachineResponseModel, HttpStatus.OK)
    }
}

enum class AllowedDepositDenomination(val value: Int) {
    HUNDRED(100), FIFTY(50), TWENTY(20), TEN(10),FIVE(5) ;

    companion object {
        fun fromInt(value: Int) = values().find { it.value == value }
        fun getValues() = values().map { it.value }
    }
}
