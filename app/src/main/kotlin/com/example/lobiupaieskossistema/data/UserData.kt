package com.example.lobiupaieskossistema.data

import android.content.Context
import com.example.lobiupaieskossistema.dao.UserDAO
import com.example.lobiupaieskossistema.models.User
import com.example.lobiupaieskossistema.utils.EncryptionUtils

object UserData {
    private lateinit var userDAO: UserDAO

    fun initialize(context: Context) {
        userDAO = UserDAO(context)
        addHardcodedEntries()
    }

    private fun addHardcodedEntries() {
        val passwords= mutableListOf("user1","user2","user3","admin")
        for(i in passwords.indices){
            passwords[i] = EncryptionUtils.hashPassword(passwords[i])
        }
        val userList = listOf(
            User(1, "user1", passwords[0], "user1@example.com", "2023-01-01","bio is empty", 0, 1, null, 0, 0, 0, 0, 0, null, null, null, null, null, null),
            User(2, "user2", passwords[1], "user2@example.com", "2023-01-01","bio is empty", 0, 1, null, 0, 0, 0, 0, 0, null, null, null, null, null, null),
            User(3, "user3", passwords[2], "user3@example.com", "2023-01-01","bio is empty", 0, 1, null, 0, 0, 0, 0, 0, null, null, null, null, null, null),
            User(4, "admin", passwords[3], "user4@example.com", "2023-01-01", "bio is empty",1, 1, null, 0, 0,0,0,0,null,null,null,null,null,null)
        )
        userList.forEach { userDAO.addUser(it) }

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