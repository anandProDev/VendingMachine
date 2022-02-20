package com.industries.vendingmachine.service

import com.industries.vendingmachine.dto.User
import com.industries.vendingmachine.exception.UserException
import com.industries.vendingmachine.model.UserModel
import com.industries.vendingmachine.repo.UserRepository
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import kotlin.random.Random.Default.nextLong

@Service
class UserService(val db: UserRepository) {

    companion object {
        private val klogger = KotlinLogging.logger { }
    }

    fun getUsers(): List<UserModel> {
        val usersFromDB = db.findMessages()
        return usersFromDB.map {
            it.toUserModel()
        }
    }

    fun createUser(userModel: UserModel): UserModel {
        try {
            val user = User(
                username = userModel.username,
                password = userModel.password,
                deposit = userModel.deposit,
                role = userModel.role.name
            )
            return db.save(user).toUserModel()
        } catch (e: Exception) {
            klogger.warn (e) { "User with id ${userModel.id} already exists" }
            throw UserException("User with id ${userModel.id} already exists", e)
        }
    }

    fun updateUser(userModel: UserModel): UserModel {
        try {
            val user = userModel.toUser()
            return db.save(user).toUserModel()
        } catch (e: Exception) {
            klogger.warn (e) {"User with id ${userModel.id} could not be updated exists" }
            throw UserException("User with id ${userModel.id} could not be updated exists", e)
        }
    }

    fun deleteAll() {
        db.deleteAll()
    }

    fun findById(id: Long): UserModel {
        val user = db.findById(id)
        if(user.isPresent)
            return user.get().toUserModel()
        throw UserException("User does not exist")
    }
}