package com.industries.vendingmachine.controller

import com.industries.vendingmachine.exception.UserException
import com.industries.vendingmachine.model.Role
import com.industries.vendingmachine.model.UserModel
import com.industries.vendingmachine.service.UserService
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
@RequestMapping("user")
class UserController(val userService: UserService) {

    companion object {
        private val klogger = KotlinLogging.logger { }
        const val JSON = "application/json"
    }

    @GetMapping(produces = [JSON])
    fun getUsers(): ResponseEntity<List<UserModel>> {
        return ResponseEntity(userService.getUsers(), HttpStatus.OK)
    }

    @GetMapping(path = ["/{id}"], produces = [JSON])
    fun getUser(@PathVariable id: Long): ResponseEntity<UserModel> {
        return ResponseEntity(userService.getUser(id), HttpStatus.OK)
    }

    @PostMapping(consumes = [JSON], produces = [JSON])
    fun createUser(@RequestBody user: UserModel): ResponseEntity<UserModel> {
        val createUser = userService.createUser(user)
        return ResponseEntity(createUser, HttpStatus.CREATED)
    }

    @PutMapping(consumes = [JSON], produces = [JSON])
    fun updateUser(@RequestBody userModel: UserModel): ResponseEntity<UserModel> {
        val userModelFromDB = userService.getUser(userModel.id)
        val newModel = userModelFromDB.copy(
            username = userModel.username,
            password = userModel.password,
            role = userModel.role,
            deposit = userModel.deposit
        )
        return ResponseEntity(newModel, HttpStatus.OK)
    }

    @DeleteMapping
    fun deleteAllUsers() {
        userService.deleteAll()
    }

    @PutMapping("/{id}", consumes = [JSON], produces = [JSON])
    fun resetDeposit(@PathVariable id: Long): ResponseEntity<UserModel> {
        val user = userService.getUser(id)
        if (user.role == Role.SELLER) {
            klogger.info { "Cannot reset SELLER deposit with id $id" }
            throw UserException("Cannot reset SELLER deposit with id $id")
        }
        val userWithReturns = user.copy(
            deposit = BigDecimal(0.0)
        )
        val updatedUser = userService.updateUser(userWithReturns)
        return ResponseEntity(updatedUser, HttpStatus.OK)
    }
}