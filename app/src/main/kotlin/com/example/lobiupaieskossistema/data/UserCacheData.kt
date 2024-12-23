package com.example.lobiupaieskossistema.data

import android.content.ContentValues
import android.content.Context
import android.util.Log
import com.example.lobiupaieskossistema.DatabaseHelper
import com.example.lobiupaieskossistema.dao.UserCacheDAO
import com.example.lobiupaieskossistema.models.UserCache

object UserCacheData {
    private lateinit var userCacheDAO: UserCacheDAO
    private lateinit var databaseHelper: DatabaseHelper

    fun initialize(context: Context) {
        userCacheDAO = UserCacheDAO(context)
        databaseHelper = DatabaseHelper(context)
    }

    fun add(userCache: UserCache): Any {
        return userCacheDAO.addUserCache(userCache)
    }

    fun getAll(): List<UserCache> {
        return userCacheDAO.getAllUserCaches()
    }

    fun get(cacheId: Int, userId: Int): UserCache? {
        return userCacheDAO.findUserCacheById(userId, cacheId)
    }

    fun update(userCache: UserCache) {
        userCacheDAO.updateUserCache(userCache)
    }

    fun delete(userId: Int, cacheId: Int) {
        userCacheDAO.deleteUserCache(userId, cacheId)
    }

    fun updateAvailability(cacheId: Int, userId: Int, isAvailable: Int) {
        val db = databaseHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put("available", isAvailable)
        }
        val rowsUpdated = db.update(
            "user_caches",
            contentValues,
            "cache_id = ? AND user_id = ?",
            arrayOf(cacheId.toString(), userId.toString())
        )
        if (rowsUpdated > 0) {
            Log.d("UserCacheData", "Cache $cacheId updated successfully for user $userId.")
        } else {
            Log.e("UserCacheData", "Failed to update cache $cacheId for user $userId.")
        }
    }
}
