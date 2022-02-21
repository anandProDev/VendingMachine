package com.industries.vendingmachine.dto

import com.industries.vendingmachine.model.Role
import com.industries.vendingmachine.model.UserModel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import java.math.BigDecimal

@DisplayName("User tests")
@ExtendWith(MockitoExtension::class)

class UserTest {

    private val userModel = UserModel(
        1,
        "myusername",
        "mypassword",
        BigDecimal(100),
        Role.SELLER
    )

    @DisplayName("user model to user")
    @Test
    fun `transform user model to user`() {

        val toUser = userModel.toUser()

        assertEquals(userModel.id, toUser.id)
        assertEquals(userModel.username, toUser.username)
        assertEquals(userModel.password, toUser.password)
        assertEquals(userModel.deposit, toUser.deposit)
        assertEquals(userModel.role, Role.valueOf(toUser.role))
    }
}
