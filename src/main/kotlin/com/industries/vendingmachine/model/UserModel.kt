package com.industries.vendingmachine.model

import java.math.BigDecimal

data class UserModel(val username: String, val password: String, val deposit: BigDecimal, val role: Role)

enum class Role {
    BUYER,
    SELLER
}
