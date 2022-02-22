package com.industries.vendingmachine.service

import com.industries.vendingmachine.controller.AllowedDepositDenomination
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@DisplayName("Return change in denominations")
class VendingMachineServiceTest{

    val vendingMachineService = VendingMachineService()
    @Test
    @DisplayName("Return denomination")
    fun `return change in denominations successful`(){
        var calculateChange = vendingMachineService.calculateChange(100)
        assertEquals(AllowedDepositDenomination.HUNDRED.value, calculateChange[0])

        calculateChange = vendingMachineService.calculateChange(50)
        assertEquals(AllowedDepositDenomination.FIFTY.value, calculateChange[0])

        calculateChange = vendingMachineService.calculateChange(20)
        assertEquals(AllowedDepositDenomination.TWENTY.value, calculateChange[0])

        calculateChange = vendingMachineService.calculateChange(10)
        assertEquals(AllowedDepositDenomination.TEN.value, calculateChange[0])

        calculateChange = vendingMachineService.calculateChange(5)
        assertEquals(AllowedDepositDenomination.FIVE.value, calculateChange[0])

        calculateChange = vendingMachineService.calculateChange(70)
        assertEquals(AllowedDepositDenomination.FIFTY.value, calculateChange[0])
        assertEquals(AllowedDepositDenomination.TWENTY.value, calculateChange[1])

        calculateChange = vendingMachineService.calculateChange(65)
        assertEquals(AllowedDepositDenomination.FIFTY.value, calculateChange[0])
        assertEquals(AllowedDepositDenomination.TEN.value, calculateChange[1])
        assertEquals(AllowedDepositDenomination.FIVE.value, calculateChange[2])

        calculateChange = vendingMachineService.calculateChange(35)
        assertEquals(AllowedDepositDenomination.TWENTY.value, calculateChange[0])
        assertEquals(AllowedDepositDenomination.TEN.value, calculateChange[1])
        assertEquals(AllowedDepositDenomination.FIVE.value, calculateChange[2])

    }

}