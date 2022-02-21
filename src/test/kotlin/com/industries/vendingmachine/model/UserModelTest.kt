package com.industries.vendingmachine.model

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import java.math.BigDecimal

@DisplayName("user model test")
class UserModelTest {
    @DisplayName("transform user model to dto")
    fun `transform usermodel to dto`() {
        val user = UserModel(
            1,
            "myusername",
            "mypassword",
            BigDecimal(100),
            Role.SELLER
        )

        val toUser = user.toUser()

        assertEquals(user.id, toUser.id)
        assertEquals(user.username, toUser.username)
        assertEquals("*******", toUser.password)
        assertEquals(user.deposit, toUser.deposit)
        assertEquals(user.role, toUser.role)
    }
}