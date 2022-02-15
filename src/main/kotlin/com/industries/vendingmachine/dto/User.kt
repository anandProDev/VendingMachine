package com.industries.vendingmachine.dto

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.math.BigDecimal
import javax.annotation.processing.Generated

@Table("USERS")
data class User(val username: String, val password: String, val deposit: BigDecimal, val role: String){
    @Id
    @Generated
    var id: Long = 0
}
