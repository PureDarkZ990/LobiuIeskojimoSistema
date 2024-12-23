package com.example.lobiupaieskossistema.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.lobiupaieskossistema.DatabaseHelper
import com.example.lobiupaieskossistema.models.CacheGroup
import com.example.lobiupaieskossistema.database.CacheGroupTable
import com.example.lobiupaieskossistema.models.UserCache

class CacheGroupDAO(private val context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun addCacheGroup(cacheGroup: CacheGroup): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(CacheGroupTable.CACHE_ID, cacheGroup.cacheId)
            put(CacheGroupTable.GROUP_ID, cacheGroup.groupId)
        }
        val result = db.insert(CacheGroupTable.TABLE_NAME, null, values)
        if (result.toInt() != -1) {
            val usersInGroup = UserGroupDAO(context).getAllUsersInGroup(cacheGroup.groupId)
            usersInGroup.forEach { userId ->
                val userCache = UserCache(userId, cacheGroup.cacheId, 0, null, 1)
                UserCacheDAO(context).addUserCache(userCache)
            }
        }

        return result
    }

    fun getAllCacheGroups(): List<CacheGroup> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            CacheGroupTable.TABLE_NAME,
            null, null, null, null, null, null
        )
        val cacheGroups = mutableListOf<CacheGroup>()
        with(cursor) {
            while (moveToNext()) {
                val cacheGroup = CacheGroup(
                    cacheId = getInt(getColumnIndexOrThrow(CacheGroupTable.CACHE_ID)),
                    groupId = getInt(getColumnIndexOrThrow(CacheGroupTable.GROUP_ID))
                )
                cacheGroups.add(cacheGroup)
            }
        }
        cursor.close()
        return cacheGroups
    }

    fun findCacheGroupById(cacheId: Int, groupId: Int): CacheGroup? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            CacheGroupTable.TABLE_NAME,
            null,
            "${CacheGroupTable.CACHE_ID} = ? AND ${CacheGroupTable.GROUP_ID} = ?",
            arrayOf(cacheId.toString(), groupId.toString()),
            null, null, null
        )
        var cacheGroup: CacheGroup? = null
        if (cursor.moveToFirst()) {
            cacheGroup = CacheGroup(
                cacheId = cursor.getInt(cursor.getColumnIndexOrThrow(CacheGroupTable.CACHE_ID)),
                groupId = cursor.getInt(cursor.getColumnIndexOrThrow(CacheGroupTable.GROUP_ID))
            )
        }
        cursor.close()
        return cacheGroup
    }

    fun updateCacheGroup(cacheGroup: CacheGroup): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(CacheGroupTable.CACHE_ID, cacheGroup.cacheId)
            put(CacheGroupTable.GROUP_ID, cacheGroup.groupId)
        }
        return db.update(CacheGroupTable.TABLE_NAME, values, "${CacheGroupTable.CACHE_ID} = ? AND ${CacheGroupTable.GROUP_ID} = ?", arrayOf(cacheGroup.cacheId.toString(), cacheGroup.groupId.toString()))
    }

    fun deleteCacheGroup(cacheId: Int, groupId: Int): Int {
        val db = dbHelper.writableDatabase
        deleteUsersFromCacheGroup(cacheId, groupId)
        return  db.delete(CacheGroupTable.TABLE_NAME, "${CacheGroupTable.CACHE_ID} = ? AND ${CacheGroupTable.GROUP_ID} = ?", arrayOf(cacheId.toString(), groupId.toString()))
    }
    private fun deleteUsersFromCacheGroup(cacheId: Int, groupId: Int){
        val db = dbHelper.writableDatabase
        val usersInGroup = UserGroupDAO(context).getAllUsersInGroup(groupId).filter { userId -> userId != 0 }
        for (userId in usersInGroup) {
            val userCache = UserCacheDAO(context).findUserCacheById(userId, cacheId)
            if (userCache != null&&userId!=CacheDAO(context).findCacheById(cacheId)?.creatorId) {
                userCache.available = 0
                UserCacheDAO(context).updateUserCache(userCache)
            }
        }
    }
}