package com.industries.vendingmachine.model

import java.math.BigDecimal

data class VendingMachineResponseModel(
    val costOfPurchase: BigDecimal,
    val amountRemaining: BigDecimal,
    val productName: String
)