package com.example.lobiupaieskossistema.data

import android.content.Context
import com.example.lobiupaieskossistema.dao.UserDAO
import com.example.lobiupaieskossistema.models.User

object UserData {
    private lateinit var userDAO: UserDAO

    fun initialize(context: Context) {
        userDAO = UserDAO(context)
        addHardcodedEntries()
    }

    private fun addHardcodedEntries() {
        val userList = listOf(
            User(1, "User 1", "hashedPassword1", "user1@example.com", "2023-01-01", 1, 1, null, 0, 0, 0, 0, 0, null, null, null, null, null, null),
            User(2, "User 2", "hashedPassword2", "user2@example.com", "2023-01-01", 1, 1, null, 0, 0, 0, 0, 0, null, null, null, null, null, null)
        )
        println("Adding hardcoded entries to UserDAO "+userList)
        userList.forEach { userDAO.addUser(it) }
        println("Added hardcoded entries to UserDAO "+ userDAO.getAllUsers())

    }

    fun get(id: Int): User? {
        return userDAO.findUserById(id)
    }

    fun update(user: User) {
        userDAO.updateUser(user)
    }

    fun delete(id: Int) {
        userDAO.deleteUser(id)
    }

    fun add(cache: User) {
        userDAO.addUser(cache)
    }

    fun getAll(): List<User> {
        return userDAO.getAllUsers()
    }
}