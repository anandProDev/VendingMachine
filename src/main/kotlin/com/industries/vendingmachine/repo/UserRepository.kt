package com.industries.vendingmachine.repo

import com.industries.vendingmachine.dto.User
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, Long> {

    @Query("select * from users")
    fun findUsers(): List<User>

    @Query(
        "INSERT INTO users(id, username, password,deposit, role) " +
                " VALUES (#{id}, #{username}, #{password}, #{deposit}, #{role})"
    )
    fun insert(user: User): Long
}
