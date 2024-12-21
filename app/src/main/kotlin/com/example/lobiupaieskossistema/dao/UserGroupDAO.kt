package com.example.lobiupaieskossistema.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.lobiupaieskossistema.DatabaseHelper
import com.example.lobiupaieskossistema.models.UserGroup
import com.example.lobiupaieskossistema.database.UserGroupTable

class UserGroupDAO(private val context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun addUserGroup(userGroup: UserGroup): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(UserGroupTable.USER_ID, userGroup.userId)
            put(UserGroupTable.GROUP_ID, userGroup.groupId)
        }
        return db.insert(UserGroupTable.TABLE_NAME, null, values)
    }
    fun getAllUserGroups(): List<UserGroup> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            UserGroupTable.TABLE_NAME,
            null, null, null, null, null, null
        )
        val userGroups = mutableListOf<UserGroup>()
        with(cursor) {
            while (moveToNext()) {
                val cacheGroup = UserGroup(
                    userId = getInt(getColumnIndexOrThrow(UserGroupTable.USER_ID)),
                    groupId = getInt(getColumnIndexOrThrow(UserGroupTable.GROUP_ID))
                )
                userGroups.add(cacheGroup)
            }
        }
        cursor.close()
        return userGroups
    }
    fun getAllUsersInGroup(groupId: Int): List<Int> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            UserGroupTable.TABLE_NAME,
            arrayOf(UserGroupTable.USER_ID),
            "${UserGroupTable.GROUP_ID} = ?",
            arrayOf(groupId.toString()),
            null, null, null
        )
        val userIds = mutableListOf<Int>()
        with(cursor) {
            while (moveToNext()) {
                val userId = getInt(getColumnIndexOrThrow(UserGroupTable.USER_ID))
                userIds.add(userId)
            }
        }
        cursor.close()
        return userIds
    }
    fun findUserGroupById(userId: Int, groupId: Int): UserGroup? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            UserGroupTable.TABLE_NAME,
            null,
            "${UserGroupTable.USER_ID} = ? AND ${UserGroupTable.GROUP_ID} = ?",
            arrayOf(userId.toString(), groupId.toString()),
            null, null, null
        )
        var userGroup: UserGroup? = null
        if (cursor.moveToFirst()) {
            userGroup = UserGroup(
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(UserGroupTable.USER_ID)),
                groupId = cursor.getInt(cursor.getColumnIndexOrThrow(UserGroupTable.GROUP_ID))
            )
        }
        cursor.close()
        return userGroup
    }
    fun updateUserGroup(userGroup: UserGroup): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(UserGroupTable.USER_ID, userGroup.userId)
            put(UserGroupTable.GROUP_ID, userGroup.groupId)
        }
        return db.update(UserGroupTable.TABLE_NAME, values, "${UserGroupTable.USER_ID} = ? AND ${UserGroupTable.GROUP_ID} = ?", arrayOf(userGroup.userId.toString(), userGroup.groupId.toString()))
    }

    fun deleteUserGroup(userId: Int, groupId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(UserGroupTable.TABLE_NAME, "${UserGroupTable.USER_ID} = ? AND ${UserGroupTable.GROUP_ID} = ?", arrayOf(userId.toString(), groupId.toString()))
    }
}