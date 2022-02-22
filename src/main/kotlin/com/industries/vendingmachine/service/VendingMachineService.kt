package com.industries.vendingmachine.service

import com.industries.vendingmachine.controller.AllowedDepositDenomination
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service

@Service
class VendingMachineService {

    fun calculateChange(changeToReturn: Int): ArrayList<Int> {

        var change = arrayListOf<Int>()
        var remainingChange = changeToReturn

        AllowedDepositDenomination.values().map { denomination ->
            val quotientAndRemainder = findQuotientAndRemainder(remainingChange, denomination)

            repeat(quotientAndRemainder.first) {
                change.add(denomination.value)
            }
            remainingChange = quotientAndRemainder.second
        }

        return change
    }

    private fun findQuotientAndRemainder(amount: Int, denomination: AllowedDepositDenomination): Pair<Int, Int>{
        val quotient = amount / denomination.value
        val remainder = amount % denomination.value
        return Pair(quotient, remainder)
    }
}