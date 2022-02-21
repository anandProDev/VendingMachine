package com.industries.vendingmachine.controller

import com.industries.vendingmachine.exception.UserException
import com.industries.vendingmachine.model.Role
import com.industries.vendingmachine.model.UserModel
import com.industries.vendingmachine.service.UserService
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.HttpStatus
import java.math.BigDecimal


@DisplayName("User tests")
@ExtendWith(MockitoExtension::class)
class UserControllerTest {

    private val userService: UserService = mockk()
    private val userController = UserController(userService)

    private val userModel = UserModel(
        1,
        "myusername",
        "mypassword",
        BigDecimal(100),
        Role.BUYER
    )

    @Test
    @DisplayName("returns list of users")
    fun `returnsProductsSuccessfully`() {
        every { userService.getUsers() } returns listOf(userModel)

        val items = userController.getUsers()

        verify(exactly = 1) { userService.getUsers() }
        assertEquals(HttpStatus.OK.value(), items.statusCode.value())
        assertEquals(1, items.body?.size)
        val body = items.body?.get(0)
        assertEquals(userModel, body)
    }

    @Test
    @DisplayName("returns one user based on id")
    fun `givenAndId_returnsUserSuccessfully`() {
        every { userService.getUser(userModel.id) } returns userModel

        val items = userController.getUser(userModel.id)

        verify(exactly = 1) { userService.getUser(userModel.id) }
        assertEquals(HttpStatus.OK.value(), items.statusCode.value())
        assertEquals(userModel, items.body)
    }

    @Test
    @DisplayName("Create user successful")
    fun `createUserIsSuccessful`() {
        every { userService.createUser(userModel) } returns userModel

        val items = userController.createUser(userModel)

        verify(exactly = 1) { userService.createUser(userModel) }
        assertEquals(HttpStatus.CREATED.value(), items.statusCode.value())
        assertEquals(userModel, items.body)
    }

    @Test
    @DisplayName("Update user successful")
    fun `updateUserIsSuccessful`() {

        val updatedModel = userModel.copy(
            username = "hello"
        )
        every { userService.getUser(userModel.id) } returns userModel
        every { userService.updateUser(userModel) } returns updatedModel

        val items = userController.updateUser(userModel)

        verify(exactly = 1) { userService.updateUser(userModel) }
        assertEquals(HttpStatus.OK.value(), items.statusCode.value())
        assertEquals(updatedModel, items.body)
    }

    @Test
    @DisplayName("Delete all users")
    fun `deleteAllUsersIsSuccessful`() {

        every { userService.deleteAll() } returns Unit

        val items = userController.deleteAllUsers()

        verify(exactly = 1) { userService.deleteAll() }
        assertEquals(HttpStatus.NO_CONTENT.value(), items.statusCode.value())
    }

    @Test
    @DisplayName("Reset user deposit")
    fun `reset user deposit successful`() {

        val updatedModel = userModel.copy(
            deposit = BigDecimal(0.0)
        )
        every { userService.getUser(userModel.id) } returns userModel
        every { userService.updateUser(updatedModel) } returns updatedModel

        val items = userController.resetDeposit(userModel.id)

        verify(exactly = 1) { userService.getUser(updatedModel.id) }
        verify(exactly = 1) { userService.updateUser(updatedModel) }
        assertEquals(HttpStatus.OK.value(), items.statusCode.value())
    }

    @Test
    @DisplayName("Reset user deposit")
    fun `throws UserException when seller tries to reset deposit`() {

        val updatedModel = userModel.copy(
            deposit = BigDecimal(0.0),
            role = Role.SELLER
        )
        every { userService.getUser(userModel.id) } returns updatedModel

        assertThrows<UserException> {
            userController.resetDeposit(userModel.id)
        }
    }
}