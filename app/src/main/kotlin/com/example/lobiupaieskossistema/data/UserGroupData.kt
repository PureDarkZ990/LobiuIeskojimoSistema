package com.example.lobiupaieskossistema.data

import android.content.Context
import com.example.lobiupaieskossistema.dao.UserGroupDAO
import com.example.lobiupaieskossistema.models.User
import com.example.lobiupaieskossistema.models.UserGroup

object UserGroupData {
    private lateinit var userGroupDAO: UserGroupDAO

    fun initialize(context: Context) {
        userGroupDAO = UserGroupDAO(context)
        addHardcodedEntries()
    }
    private fun addHardcodedEntries() {
        val userList = listOf(
            UserGroup(1, 1),
            UserGroup(1, 2),
            UserGroup(3, 3),
            UserGroup(4, 4)
        )
        userList.forEach { userGroupDAO.addUserGroup(it) }

    }
    fun get(userId: Int, groupId: Int): UserGroup? {
        return userGroupDAO.findUserGroupById(userId, groupId)
    }
    fun add(userGroup: UserGroup): Long {
        return userGroupDAO.addUserGroup(userGroup)
    }

    fun getAll(): List<UserGroup> {
        return userGroupDAO.getAllUserGroups()
    }

    fun delete(userId: Int, groupId: Int) {
        userGroupDAO.deleteUserGroup(userId, groupId)
    }
}