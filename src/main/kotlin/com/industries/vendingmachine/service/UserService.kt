package com.industries.vendingmachine.service

import com.industries.vendingmachine.dto.User
import com.industries.vendingmachine.model.Role
import com.industries.vendingmachine.model.UserModel
import com.industries.vendingmachine.repo.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(val db: UserRepository) {

    fun getUsers(): List<UserModel> {
        val usersFromDB = db.findMessages()
        return usersFromDB.map {
            UserModel(
                username = it.username,
                password = "********",
                deposit = it.deposit,
                role = Role.valueOf(it.role)
            )
        }
    }

    fun createUser(userModel: UserModel): User {
        val user = User(
            username = userModel.username,
            password = userModel.password,
            deposit = userModel.deposit,
            role = userModel.role.name
        )
        return db.save(user)
    }

    fun updateUser(userModel: UserModel) : User {
        val user = User(
            username = userModel.username,
            password = userModel.password,
            deposit = userModel.deposit,
            role = userModel.role.name
        )
        return db.save(user);
    }

    fun deleteAll() {
        db.deleteAll()
    }
}