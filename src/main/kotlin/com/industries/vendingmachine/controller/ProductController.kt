package com.industries.vendingmachine.controller

import com.industries.vendingmachine.exception.UserException
import com.industries.vendingmachine.model.UserModel
import com.industries.vendingmachine.service.UserService
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("user")
class UserController(val userService: UserService) {

    companion object {
        private val klogger = KotlinLogging.logger { }
    }

    @GetMapping(produces = ["application/json"])
    fun getUsers(): ResponseEntity<List<UserModel>> {
        return ResponseEntity(userService.getUsers(), HttpStatus.OK)
    }

    @PostMapping(consumes = ["application/json"])
    fun createUser(@RequestBody user: UserModel): ResponseEntity<UserModel> {
        val createUser = userService.createUser(user)
        return ResponseEntity(createUser, HttpStatus.CREATED)
    }

    @PutMapping(consumes = ["application/json"])
    fun updateUser(@RequestBody userModel: UserModel): ResponseEntity<UserModel> {

        try {
            val userModelFromDB = userService.findById(userModel.id)

            val newModel = userModelFromDB.copy(
                username = userModel.username,
                password = userModel.password,
                role = userModel.role,
                deposit = userModel.deposit
            )
            return ResponseEntity(newModel, HttpStatus.OK)
        } catch (e: Exception) {
            throw UserException(e.message, e)
        }
    }

    @DeleteMapping
    fun deleteAllUsers(){
        userService.deleteAll()
    }
}