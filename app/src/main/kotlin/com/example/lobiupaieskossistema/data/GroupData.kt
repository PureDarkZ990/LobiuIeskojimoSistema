package com.example.lobiupaieskossistema.data

import android.content.Context
import com.example.lobiupaieskossistema.dao.GroupDAO
import com.example.lobiupaieskossistema.models.Group
import com.example.lobiupaieskossistema.models.User

object GroupData {
    private lateinit var groupDAO: GroupDAO

    fun initialize(context: Context) {
        groupDAO = GroupDAO(context)
        addHardcodedEntries()
    }
    private fun addHardcodedEntries() {
        val groupList = listOf(
            Group(1, "Group 1", "My description", null, null, null, 0, null, "2023-01-01", "2023-01-01"),
            Group(2, "Group 2", "My description", null, null, null, 0, null, "2023-01-01", "2023-01-01")
        )
        groupList.forEach { groupDAO.addGroup(it) }
    }

    fun get(id: Int): Group? {
        return groupDAO.findGroupById(id)
    }

    fun update(group: Group) {
        groupDAO.updateGroup(group)
    }

    fun delete(id: Int) {
        groupDAO.deleteGroup(id)
    }

    fun add(group: Group) {
        groupDAO.addGroup(group)
    }

    fun getAll(): List<Group> {
        return groupDAO.getAllGroups()
    }
}