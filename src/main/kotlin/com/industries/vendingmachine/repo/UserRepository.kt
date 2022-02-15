package com.industries.vendingmachine.repo

import com.industries.vendingmachine.dto.User
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, String>{

    @Query("select * from users")
    fun findMessages(): List<User>
}