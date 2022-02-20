package com.industries.vendingmachine.controller

import com.industries.vendingmachine.exception.FundsUnavailableException
import com.industries.vendingmachine.exception.InvalidDenominationException
import com.industries.vendingmachine.exception.NotEnoughItemsInVendingMachineException
import com.industries.vendingmachine.exception.UserNotAllowedException
import com.industries.vendingmachine.model.*
import com.industries.vendingmachine.service.ProductService
import com.industries.vendingmachine.service.UserService
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

@RestController("vendingmachine")
class VendingMachineController(val productService: ProductService, val userService: UserService){

    var depositHolder = HashMap<Int, Int>()

    companion object {
        private val klogger = KotlinLogging.logger { }
        const val JSON = "application/json"
    }

    @PostMapping("/deposit", consumes = [JSON], produces = [JSON])
    fun deposit(@RequestBody depositorModel: DepositorModel): ResponseEntity<DepositAvailableModel> {
        val (buyerId, amount) = depositorModel

        if(isSeller(buyerId)){
            klogger.info { "Seller with id $buyerId attempted to deposit money" }
            throw UserNotAllowedException("Only buyer allowed to deposit money")
        }

        AllowedDepositDenomination.fromInt(amount)
            ?: throw InvalidDenominationException("Please enter a valid amount. Allowed amounts ${AllowedDepositDenomination.getValues()}")

        depositHolder[buyerId] = depositHolder[buyerId] ?: (0 + depositorModel.amount)

        val depositAvailableModel = DepositAvailableModel(
            buyerId = buyerId,
            deposit = depositHolder[buyerId] ?: 0
        )
        return ResponseEntity<DepositAvailableModel>(depositAvailableModel, HttpStatus.OK)
    }

    @PostMapping("/buy", consumes = [JSON], produces = [JSON])
    fun buy(@RequestBody productBuyerModel: ProductBuyerModel): ResponseEntity<VendingMachineResponseModel> {
        val (buyerId, productId, quantity) = productBuyerModel

        if(isSeller(buyerId)) {
            klogger.info { "Seller with id $buyerId attempted to buy product" }
            throw UserNotAllowedException("Only buyer allowed to purchase product")
        }

        val availableFunds = BigDecimal(depositHolder[buyerId] ?: 0)
        val product = productService.getProduct(productId.toLong())
        val productCost = product.cost * BigDecimal(quantity)
        if(quantity > product.quantityavailable)
            throw NotEnoughItemsInVendingMachineException("Quantity requested unavailable")

        if(productCost > availableFunds)
            throw FundsUnavailableException("Sorry, not enough funds to complete purchase")

        val fundsCurrentlyAvailable = availableFunds - productCost
        depositHolder[buyerId] = fundsCurrentlyAvailable.toInt()

        val vendingMachineResponseModel =
            VendingMachineResponseModel(productCost, fundsCurrentlyAvailable, product.productname)
        return ResponseEntity<VendingMachineResponseModel>(vendingMachineResponseModel, HttpStatus.OK)
    }

    private fun isSeller(buyerId: Int) = userService.getUser(buyerId.toLong()).role != Role.BUYER
}

enum class AllowedDepositDenomination(val value: Int){
    FIVE(5), TEN(10), TWENTY(20), FIFTY(50), HUNDRED(100);
    companion object {
        fun fromInt(value: Int) = values().find { it.value == value }
        fun getValues() = values().map { it.value }
    }
}
