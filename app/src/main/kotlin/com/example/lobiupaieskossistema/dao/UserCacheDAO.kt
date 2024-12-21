package com.example.lobiupaieskossistema.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.lobiupaieskossistema.DatabaseHelper
import com.example.lobiupaieskossistema.database.UserCacheTable
import com.example.lobiupaieskossistema.models.UserCache

class UserCacheDAO(private val context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun addUserCache(userCache: UserCache): Any {
        val db = dbHelper.writableDatabase

        val cursor: Cursor = db.query(
            UserCacheTable.TABLE_NAME,
            null,
            "${UserCacheTable.USER_ID} = ? AND ${UserCacheTable.CACHE_ID} = ?",
            arrayOf(userCache.userId.toString(), userCache.cacheId.toString()),
            null, null, null
        )
        val exists = cursor.moveToFirst()
        cursor.close()

        return if (exists) {
            if(userCache.userId==CacheDAO(context).findCacheById(cacheId = userCache.cacheId)?.creatorId){
                return userCache
            }
            updateUserCache(userCache)
        } else {
            val values = ContentValues().apply {
                put(UserCacheTable.USER_ID, userCache.userId)
                put(UserCacheTable.CACHE_ID, userCache.cacheId)
                put(UserCacheTable.FOUND, userCache.found)
                put(UserCacheTable.RATING, userCache.rating)
                put(UserCacheTable.AVAILABLE, userCache.available)
            }
            db.insert(UserCacheTable.TABLE_NAME, null, values)
        }
    }

    fun getAllUserCaches(): List<UserCache> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            UserCacheTable.TABLE_NAME,
            null, null, null, null, null, null
        )
        val userCaches = mutableListOf<UserCache>()
        with(cursor) {
            while (moveToNext()) {
                val userCache = UserCache(
                    userId = getInt(getColumnIndexOrThrow(UserCacheTable.USER_ID)),
                    cacheId = getInt(getColumnIndexOrThrow(UserCacheTable.CACHE_ID)),
                    found = getInt(getColumnIndexOrThrow(UserCacheTable.FOUND)),
                    rating = getDouble(getColumnIndexOrThrow(UserCacheTable.RATING)),
                    available = getInt(getColumnIndexOrThrow(UserCacheTable.AVAILABLE))
                )
                userCaches.add(userCache)
            }
        }
        cursor.close()
        return userCaches
    }

    fun findUserCacheById(userId: Int, cacheId: Int): UserCache? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            UserCacheTable.TABLE_NAME,
            null,
            "${UserCacheTable.USER_ID} = ? AND ${UserCacheTable.CACHE_ID} = ?",
            arrayOf(userId.toString(), cacheId.toString()),
            null, null, null
        )
        var userCache: UserCache? = null
        if (cursor.moveToFirst()) {
            userCache = UserCache(
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(UserCacheTable.USER_ID)),
                cacheId = cursor.getInt(cursor.getColumnIndexOrThrow(UserCacheTable.CACHE_ID)),
                found = cursor.getInt(cursor.getColumnIndexOrThrow(UserCacheTable.FOUND)),
                rating = cursor.getDouble(cursor.getColumnIndexOrThrow(UserCacheTable.RATING)),
                available = cursor.getInt(cursor.getColumnIndexOrThrow(UserCacheTable.AVAILABLE))
            )
        }
        cursor.close()
        return userCache
    }

    fun updateUserCache(userCache: UserCache): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(UserCacheTable.FOUND, userCache.found)
            put(UserCacheTable.RATING, userCache.rating)
            put(UserCacheTable.AVAILABLE, userCache.available)
        }
        return db.update(UserCacheTable.TABLE_NAME, values, "${UserCacheTable.USER_ID} = ? AND ${UserCacheTable.CACHE_ID} = ?", arrayOf(userCache.userId.toString(), userCache.cacheId.toString()))
    }
    fun deleteUserCache(userId: Int, cacheId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(UserCacheTable.TABLE_NAME, "${UserCacheTable.USER_ID} = ? AND ${UserCacheTable.CACHE_ID} = ?", arrayOf(userId.toString(), cacheId.toString()))
    }
}