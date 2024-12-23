package com.example.lobiupaieskossistema.data

import android.content.Context
import com.example.lobiupaieskossistema.dao.UserDAO
import com.example.lobiupaieskossistema.models.User
import com.example.lobiupaieskossistema.utils.EncryptionUtils
import com.example.lobiupaieskossistema.utils.SessionManager

object UserData {
    private lateinit var userDAO: UserDAO

    fun initialize(context: Context) {
        if (!::userDAO.isInitialized) {
            userDAO = UserDAO(context)
            addHardcodedEntries()
        }
    }

    private fun addHardcodedEntries() {
        val passwords= mutableListOf("user1","user2","user3","admin")
        for(i in passwords.indices){
            passwords[i] = EncryptionUtils.hashPassword(passwords[i])
        }
        val userList = listOf(
            User(1, "user1", passwords[0], "user1@example.com","bio is empty", "2023-01-01", 0, 1, null, 0, 0, 0, 0, 0, null, null, null, null, null, null),
            User(2, "user2", passwords[1], "user2@example.com","bio is empty", "2023-01-01", 0, 1, null, 0, 0, 0, 0, 0, null, null, null, null, null, null),
            User(3, "user3", passwords[2], "user3@example.com","bio is empty", "2023-01-01", 0, 1, null, 0, 0, 0, 0, 0, null, null, null, null, null, null),
            User(4, "admin", passwords[3], "user4@example.com","bio is empty", "2023-01-01", 1, 1, null, 0, 0,0,0,0,null,null,null,null,null,null),
            User(5, "user4", passwords[0], "user5@example.com","bio is empty", "2023-01-01" ,0, 1, null, 0, 0, 0, 0, 0, null, null, null, null, null, null),
            User(6, "user5", passwords[1], "user6@example.com", "bio is empty","2023-01-01", 0, 1, null, 0, 0, 0, 0, 0, null, null, null, null, null, null),
            User(7, "user6", passwords[2], "user7@example.com", "bio is empty","2023-01-01", 0, 1, null, 0, 0, 0, 0, 0, null, null, null, null, null, null),
            User(8, "user7", passwords[0], "user8@example.com", "bio is empty","2023-01-01" , 0, 1, null, 0, 0, 0, 0, 0, null, null, null, null, null, null),
            User(9, "user8", passwords[1], "user9@example.com", "bio is empty","2023-01-01" , 0, 1, null, 0, 0, 0, 0, 0, null, null, null, null, null, null),
            User(10, "user9", passwords[2], "user10@example.com", "bio is empty","2023-01-01" , 0, 1, null, 0, 0, 0, 0, 0, null, null, null, null, null, null),
            User(11, "user10", passwords[0], "user11@example.com", "bio is empty","2023-01-01" , 0, 1, null, 0, 0, 0, 0, 0, null, null, null, null, null, null),
            User(12, "user11", passwords[1], "user12@example.com", "bio is empty","2023-01-01" , 0, 1, null, 0, 0, 0, 0, 0, null, null, null, null, null, null),
            User(13, "user12", passwords[2], "user13@example.com", "bio is empty","2023-01-01" , 0, 1, null, 0, 0, 0, 0, 0, null, null, null, null, null, null),
            User(14, "user13", passwords[2], "user14@example.com", "bio is empty","2023-01-01" , 0, 1, null, 0, 0, 0, 0, 0, null, null, null, null, null, null),
            User(15, "user14", passwords[0], "user15@example.com", "bio is empty","2023-01-01" , 0, 1, null, 0, 0, 0, 0, 0, null, null, null, null, null, null),
            User(16, "user15", passwords[1], "user16@example.com", "bio is empty","2023-01-01" , 0, 1, null, 0, 0, 0, 0, 0, null, null, null, null, null, null),
            User(17, "user16", passwords[2], "user17@example.com","bio is empty","2023-01-01" , 0, 1, null, 0, 0, 0, 0, 0, null, null, null, null, null, null),
            User(18, "user17", passwords[0], "user18@example.com", "bio is empty","2023-01-01" , 0, 1, null, 0, 0, 0, 0, 0, null, null, null, null, null, null),
            User(19, "user18", passwords[1], "user19@example.com", "bio is empty","2023-01-01" , 0, 1, null, 0, 0, 0, 0, 0, null, null, null, null, null, null)
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
    fun isAdministrator(id: Int): Boolean {
        return userDAO.isAdministrator(id)
    }
    fun getAll(): List<User> {
        return userDAO.getAllUsers()
    }
}