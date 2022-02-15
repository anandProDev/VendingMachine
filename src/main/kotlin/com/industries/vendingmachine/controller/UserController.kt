package com.industries.vendingmachine.controller

import com.industries.vendingmachine.model.Role
import com.industries.vendingmachine.model.UserModel
import com.industries.vendingmachine.service.UserService
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

@RestController
class UserController(val userService: UserService) {

    @GetMapping
    fun getUsers(): List<UserModel> {
        return userService.getUsers()
    }

    @PostMapping
    fun createUser(@RequestBody user: UserModel) {
        userService.createUser(user)
    }

    @DeleteMapping
    fun deleteAllUsers(){
        userService.deleteAll()
    }
}