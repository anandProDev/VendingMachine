package com.industries.vendingmachine.repo

import com.industries.vendingmachine.dto.User
import org.springframework.data.jdbc.repository.query.Query
import org.springframework.data.repository.CrudRepository
import java.math.BigDecimal
import java.util.*

interface UserRepository : CrudRepository<User, Long>{

    @Query("select * from users")
    fun findMessages(): List<User>

    @Query("DELETE FROM users WHERE id = #{id}")
    override fun deleteById(id: Long)

    @Query(
        "INSERT INTO users(id, username, password,deposit, role) " +
                " VALUES (#{id}, #{username}, #{password}, #{deposit}, #{role})"
    )
    fun insert(user: User): Long
}
