package com.industries.vendingmachine.model

import com.industries.vendingmachine.dto.User
import java.math.BigDecimal

data class UserModel(
    val id: Long,
    val username: String,
    val password: String,
    val deposit: BigDecimal,
    val role: Role
) {
    fun toUser() = User(
        id = id,
        username = username,
        password = password,
        deposit = deposit,
        role = role.name
    )
}

enum class Role {
    BUYER,
    SELLER
}
