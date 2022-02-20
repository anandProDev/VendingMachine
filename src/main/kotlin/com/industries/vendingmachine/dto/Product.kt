package com.industries.vendingmachine.dto

import com.industries.vendingmachine.model.Role
import com.industries.vendingmachine.model.UserModel
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType

@Table("USERS")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val username: String,
    val password: String,
    val deposit: BigDecimal,
    val role: String
) {
    fun toUserModel() =
        UserModel(
            id = id,
            username = username,
            password = "*******",
            deposit = deposit,
            role = Role.valueOf(role)
        )
}
