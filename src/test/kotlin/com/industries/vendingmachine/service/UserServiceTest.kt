package com.industries.vendingmachine.service

import com.industries.vendingmachine.dto.User
import com.industries.vendingmachine.exception.UserException
import com.industries.vendingmachine.exception.UserNotFoundException
import com.industries.vendingmachine.model.Role
import com.industries.vendingmachine.model.UserModel
import com.industries.vendingmachine.repo.UserRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.*

@DisplayName("User service tests")
class UserServiceTest {
    private val userRepository: UserRepository = mockk()
    private val userService: UserService = UserService(userRepository)

    private val userModel = UserModel(
        1,
        "myusername",
        "mypassword",
        BigDecimal(100),
        Role.SELLER
    )

    private val user = User(
        1,
        "myusername",
        "mypassword",
        BigDecimal(100),
        Role.SELLER.name
    )

    @DisplayName("Get list of users")
    @Test
    fun `list of users is returned successfully`() {

        every { userRepository.findUsers() } returns listOf(user)

        val users = userService.getUsers()

        assertEquals(1, users.size)
        val userFromDb = users[0]
        assertEquals(user.id, userFromDb.id)
        assertEquals(user.username, userFromDb.username)
        assertEquals("*******", userFromDb.password)
        assertEquals(user.deposit, userFromDb.deposit)
        assertEquals(user.role, userFromDb.role.name)
    }

    @DisplayName("Error thrown when list of users requested")
    @Test
    fun `UserException is thrown when fetching from database fails`() {

        every { userRepository.findUsers() } throws UserException("Error getting data")

        assertThrows<UserException> { userService.getUsers() }
    }

    @DisplayName("User created successfully")
    @Test
    fun `user created successfully`() {

        val copyUser = user.copy(id = 0)
        every { userRepository.save(copyUser) } returns user

        val createdUser = userService.createUser(userModel)

        assertEquals(user.id, createdUser.id)
        assertEquals(user.username, createdUser.username)
        assertEquals("*******", createdUser.password)
        assertEquals(user.deposit, createdUser.deposit)
        assertEquals(user.role, createdUser.role.name)
    }

    @DisplayName("Error is thrown when createUser fails")
    @Test
    fun `UserException is thrown when we fail to create user`() {

        val copyUser = user.copy(id = 0)
        every { userRepository.save(copyUser) } throws Exception("db failure")

        assertThrows<UserException> { userService.createUser(userModel) }
    }

    @DisplayName("Update user successful")
    @Test
    fun `update user successful`() {

        val copyUser = user.copy(username = "hello")
        every { userRepository.save(copyUser) } returns copyUser

        val copyUserModel = userModel.copy(username = "hello")
        val updateUser = userService.updateUser(copyUserModel)

        assertEquals(copyUser.id, updateUser.id)
        assertEquals(copyUser.username, updateUser.username)
        assertEquals("*******", updateUser.password)
        assertEquals(copyUser.deposit, updateUser.deposit)
        assertEquals(copyUser.role, updateUser.role.name)
    }

    @DisplayName("update user fails")
    @Test
    fun `update user fails`() {

        every { userRepository.save(user) } throws Exception("db down")

        assertThrows<UserException> { userService.updateUser(userModel) }
    }

    @DisplayName("delete all users")
    @Test
    fun `delete all users`() {

        every { userService.deleteAll() } returns Unit

        userService.deleteAll()
        verify(exactly = 1) { userRepository.deleteAll() }
    }

    @DisplayName("delete all users fails")
    @Test
    fun `exception thrown when deleting all users`() {
        every { userService.deleteAll() } throws Exception("db error")
        assertThrows<UserException> { userService.deleteAll() }
    }

    @DisplayName(" get one user by id")
    @Test
    fun `get user by id`() {

        every { userRepository.findById(user.id) } returns Optional.of(user)

        val returnedUser = userService.getUser(user.id)

        assertEquals(user.id, returnedUser.id)
        assertEquals(user.username, returnedUser.username)
        assertEquals("*******", returnedUser.password)
        assertEquals(user.deposit, returnedUser.deposit)
        assertEquals(user.role, returnedUser.role.name)
    }

    @DisplayName("Could not fetch user by id")
    @Test
    fun `Error fetching user by id`() {

        every { userRepository.findById(user.id) } throws UserNotFoundException("use not found")

        assertThrows<UserException> { userService.getUser(user.id) }
    }

    @DisplayName("user does not exist in db")
    @Test
    fun `user not found in db`() {
        every { userRepository.findById(user.id) } returns Optional.empty()

        assertThrows<UserException> { userService.getUser(user.id) }
    }
}