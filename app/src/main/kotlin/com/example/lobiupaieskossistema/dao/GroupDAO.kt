package com.example.lobiupaieskossistema.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.lobiupaieskossistema.DatabaseHelper
import com.example.lobiupaieskossistema.models.Group
import com.example.lobiupaieskossistema.database.GroupTable

class GroupDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun addGroup(group: Group): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(GroupTable.NAME, group.name)
            put(GroupTable.DESCRIPTION, group.description)
            put(GroupTable.ACTIVITY, group.activity)
            put(GroupTable.XACTIVITY, group.xActivity)
            put(GroupTable.YACTIVITY, group.yActivity)
            put(GroupTable.TOTAL_FOUND_CACHES, group.totalFoundCaches)
            put(GroupTable.CREATOR_ID, group.creatorId)
            put(GroupTable.CREATED_AT, group.createdAt)
            put(GroupTable.UPDATED_AT, group.updatedAt)
        }
        return db.insert(GroupTable.TABLE_NAME, null, values)
    }

    fun getAllGroups(): List<Group> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            GroupTable.TABLE_NAME,
            null, null, null, null, null, null
        )
        val groups = mutableListOf<Group>()
        with(cursor) {
            while (moveToNext()) {
                val group = Group(
                    id = getInt(getColumnIndexOrThrow(GroupTable.ID)),
                    name = getString(getColumnIndexOrThrow(GroupTable.NAME)),
                    description = getString(getColumnIndexOrThrow(GroupTable.DESCRIPTION)),
                    activity = getString(getColumnIndexOrThrow(GroupTable.ACTIVITY)),
                    xActivity = getDouble(getColumnIndexOrThrow(GroupTable.XACTIVITY)),
                    yActivity = getDouble(getColumnIndexOrThrow(GroupTable.YACTIVITY)),
                    totalFoundCaches = getInt(getColumnIndexOrThrow(GroupTable.TOTAL_FOUND_CACHES)),
                    creatorId = getInt(getColumnIndexOrThrow(GroupTable.CREATOR_ID)),
                    createdAt = getString(getColumnIndexOrThrow(GroupTable.CREATED_AT)),
                    updatedAt = getString(getColumnIndexOrThrow(GroupTable.UPDATED_AT))
                )
                groups.add(group)
            }
        }
        cursor.close()
        return groups
    }
    fun findGroupById(groupId: Int): Group? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            GroupTable.TABLE_NAME,
            null,
            "${GroupTable.ID} = ?",
            arrayOf(groupId.toString()),
            null, null, null
        )
        var group: Group? = null
        if (cursor.moveToFirst()) {
            group = Group(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(GroupTable.ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(GroupTable.NAME)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(GroupTable.DESCRIPTION)),
                activity = cursor.getString(cursor.getColumnIndexOrThrow(GroupTable.ACTIVITY)),
                xActivity = cursor.getDouble(cursor.getColumnIndexOrThrow(GroupTable.XACTIVITY)),
                yActivity = cursor.getDouble(cursor.getColumnIndexOrThrow(GroupTable.YACTIVITY)),
                totalFoundCaches = cursor.getInt(cursor.getColumnIndexOrThrow(GroupTable.TOTAL_FOUND_CACHES)),
                creatorId = cursor.getInt(cursor.getColumnIndexOrThrow(GroupTable.CREATOR_ID)),
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow(GroupTable.CREATED_AT)),
                updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(GroupTable.UPDATED_AT))
            )
        }
        cursor.close()
        return group
    }
    fun updateGroup(group: Group): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(GroupTable.NAME, group.name)
            put(GroupTable.DESCRIPTION, group.description)
            put(GroupTable.ACTIVITY, group.activity)
            put(GroupTable.XACTIVITY, group.xActivity)
            put(GroupTable.YACTIVITY, group.yActivity)
            put(GroupTable.TOTAL_FOUND_CACHES, group.totalFoundCaches)
            put(GroupTable.CREATOR_ID, group.creatorId)
            put(GroupTable.CREATED_AT, group.createdAt)
            put(GroupTable.UPDATED_AT, group.updatedAt)
        }
        return db.update(GroupTable.TABLE_NAME, values, "${GroupTable.ID} = ?", arrayOf(group.id.toString()))
    }

    fun deleteGroup(groupId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(GroupTable.TABLE_NAME, "${GroupTable.ID} = ?", arrayOf(groupId.toString()))
    }
}