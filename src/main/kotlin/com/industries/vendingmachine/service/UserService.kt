package com.industries.vendingmachine.service

import com.industries.vendingmachine.dto.User
import com.industries.vendingmachine.exception.UserException
import com.industries.vendingmachine.exception.UserNotFoundException
import com.industries.vendingmachine.model.UserModel
import com.industries.vendingmachine.repo.UserRepository
import mu.KotlinLogging
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service

@Service
class UserService(val db: UserRepository) {

    companion object {
        private val klogger = KotlinLogging.logger { }
    }

    fun getUsers(): List<UserModel> {
        try {
            val usersFromDB = db.findMessages()
            return usersFromDB.map {
                it.toUserModel()
            }
        } catch (exception: Exception) {
            klogger.error(exception) { "Error getting data from db" }
            throw UserException("Error getting data from db", exception);
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
        } catch (exception: Exception) {
            klogger.warn (exception) { "User with id ${userModel.id} already exists" }
            throw UserException("User with id ${userModel.id} already exists", exception)
        }
    }

    fun updateUser(userModel: UserModel): UserModel {
        try {
            val user = userModel.toUser()
            return db.save(user).toUserModel()
        } catch (exception: Exception) {

            when(exception){
                is DuplicateKeyException -> {
                   klogger.warn (exception) { "User with id ${userModel.id} already exists" }
                   throw UserException("User with id ${userModel.id} already exists", exception)
                }
                else -> {
                    klogger.warn (exception) {"Something went wrong when updating user with id ${userModel.id}" }
                    throw UserException("Something went wrong when updating user with id ${userModel.id}", exception)
                }
            }
        }
    }

    fun deleteAll() {
        try {
            db.deleteAll()
        } catch (exception :Exception){
            klogger.warn (exception) {"Something went wrong when deleting users" }
            throw UserException("Something went wrong when deleting users", exception)
        }
    }

    fun getUser(id: Long): UserModel {
        try {
            val user = db.findById(id)
            if(user.isPresent)
                return user.get().toUserModel()
            throw UserNotFoundException("User does not exist with id $id" )
        } catch (exception: Exception) {
            klogger.warn (exception) {"Something went wrong when finding user with id $id" }
            throw UserException("Something went wrong when finding user with id $id", exception)
        }
    }
}