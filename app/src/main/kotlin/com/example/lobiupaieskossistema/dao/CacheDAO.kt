package com.example.lobiupaieskossistema.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.lobiupaieskossistema.DatabaseHelper
import com.example.lobiupaieskossistema.database.CacheTable
import com.example.lobiupaieskossistema.models.Cache
import com.example.lobiupaieskossistema.models.UserCache
import kotlin.math.abs
import kotlin.math.sqrt

class CacheDAO(private val context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun addCache(cache: Cache): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(CacheTable.NAME, cache.name)
            put(CacheTable.DESCRIPTION, cache.description)
            put(CacheTable.XCOORDINATE, cache.xCoordinate)
            put(CacheTable.YCOORDINATE, cache.yCoordinate)
            put(CacheTable.ZONE, cache.zoneRadius)
            put(CacheTable.RATING, cache.rating)
            put(CacheTable.SHOWN, cache.shown)
            put(CacheTable.DIFFICULTY, cache.difficulty)
            put(CacheTable.APPROVED, cache.approved)
            put(CacheTable.CREATED_AT, cache.createdAt)
            put(CacheTable.UPDATED_AT, cache.updatedAt)
            put(CacheTable.PRIVATE, cache.private)
            put(CacheTable.THEME_ID, cache.themeId)
            put(CacheTable.CREATOR_ID, cache.creatorId)
        }
        val id= db.insert(CacheTable.TABLE_NAME, null, values)
        if(id!=-1L) {
            cache.creatorId?.let {
                UserCache(
                    it.toInt(),
                    id.toInt(),
                    1,
                    5.0,
                    1
                )
            }?.let {
                UserCacheDAO(context).addUserCache(
                    it
                )
            }
            for(admins in UserDAO(context).getAllAdmins()){
                UserCacheDAO(context).addUserCache(
                    UserCache(
                        admins.id,
                        id.toInt(),
                        0,
                        null,
                        1
                    )
                )
            }
            if(cache.private==0) {
                val userList = UserDAO(context).getAllUsers()
                for (user in userList) {
                    if(user.id==cache.creatorId)
                        continue
                    UserCacheDAO(context).addUserCache(UserCache(user.id, id.toInt(), 0, null, 1))
                }
            }else{
                val userList = UserDAO(context).getAllAdmins()
                for (user in userList) {
                    val userCache = UserCache(user.id, id.toInt(), 0, null, 1)
                    UserCacheDAO(context).addUserCache(userCache)
                }
            }

        }
        return id
    }

    fun getAllCaches(): List<Cache> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            CacheTable.TABLE_NAME,
            null, null, null, null, null, null
        )
        val caches = mutableListOf<Cache>()
        with(cursor) {
            while (moveToNext()) {
                val cache = Cache(
                    id = getInt(getColumnIndexOrThrow(CacheTable.ID)),
                    name = getString(getColumnIndexOrThrow(CacheTable.NAME)),
                    description = getString(getColumnIndexOrThrow(CacheTable.DESCRIPTION)),
                    xCoordinate = getDouble(getColumnIndexOrThrow(CacheTable.XCOORDINATE)),
                    yCoordinate = getDouble(getColumnIndexOrThrow(CacheTable.YCOORDINATE)),
                    zoneRadius = getInt(getColumnIndexOrThrow(CacheTable.ZONE)),
                    rating = getDouble(getColumnIndexOrThrow(CacheTable.RATING)),
                    difficulty = getDouble(getColumnIndexOrThrow(CacheTable.DIFFICULTY)),
                    approved = getInt(getColumnIndexOrThrow(CacheTable.APPROVED)),
                    shown = getInt(getColumnIndexOrThrow(CacheTable.SHOWN)),
                    createdAt = getString(getColumnIndexOrThrow(CacheTable.CREATED_AT)),
                    updatedAt = getString(getColumnIndexOrThrow(CacheTable.UPDATED_AT)),
                    private = getInt(getColumnIndexOrThrow(CacheTable.PRIVATE)),
                    themeId = getInt(getColumnIndexOrThrow(CacheTable.THEME_ID)),
                    creatorId = getInt(getColumnIndexOrThrow(CacheTable.CREATOR_ID))
                )
                caches.add(cache)
            }
        }
        cursor.close()
        return caches
    }
    fun findCacheById(cacheId: Int): Cache? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            CacheTable.TABLE_NAME,
            null,
            "${CacheTable.ID} = ?",
            arrayOf(cacheId.toString()),
            null, null, null
        )
        var cache: Cache? = null
        if (cursor.moveToFirst()) {
            cache = Cache(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(CacheTable.ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(CacheTable.NAME)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(CacheTable.DESCRIPTION)),
                xCoordinate = cursor.getDouble(cursor.getColumnIndexOrThrow(CacheTable.XCOORDINATE)),
                yCoordinate = cursor.getDouble(cursor.getColumnIndexOrThrow(CacheTable.YCOORDINATE)),
                zoneRadius = cursor.getInt(cursor.getColumnIndexOrThrow(CacheTable.ZONE)),
                rating = cursor.getDouble(cursor.getColumnIndexOrThrow(CacheTable.RATING)),
                difficulty = cursor.getDouble(cursor.getColumnIndexOrThrow(CacheTable.DIFFICULTY)),
                approved = cursor.getInt(cursor.getColumnIndexOrThrow(CacheTable.APPROVED)),
                shown = cursor.getInt(cursor.getColumnIndexOrThrow(CacheTable.SHOWN)),
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow(CacheTable.CREATED_AT)),
                updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(CacheTable.UPDATED_AT)),
                private = cursor.getInt(cursor.getColumnIndexOrThrow(CacheTable.PRIVATE)),
                themeId = cursor.getInt(cursor.getColumnIndexOrThrow(CacheTable.THEME_ID)),
                creatorId = cursor.getInt(cursor.getColumnIndexOrThrow(CacheTable.CREATOR_ID))
            )
        }
        cursor.close()
        return cache
    }

    fun updateCache(cache: Cache): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(CacheTable.NAME, cache.name)
            put(CacheTable.DESCRIPTION, cache.description)
            put(CacheTable.XCOORDINATE, cache.xCoordinate)
            put(CacheTable.YCOORDINATE, cache.yCoordinate)
            put(CacheTable.ZONE, cache.zoneRadius)
            put(CacheTable.RATING, cache.rating)
            put(CacheTable.DIFFICULTY, cache.difficulty)
            put(CacheTable.APPROVED, cache.approved)
            put(CacheTable.SHOWN, cache.shown)
            put(CacheTable.CREATED_AT, cache.createdAt)
            put(CacheTable.UPDATED_AT, cache.updatedAt)
            put(CacheTable.PRIVATE, cache.private)
            put(CacheTable.THEME_ID, cache.themeId)
            put(CacheTable.CREATOR_ID, cache.creatorId)
        }
        return db.update(CacheTable.TABLE_NAME, values, "${CacheTable.ID} = ?", arrayOf(cache.id.toString()))
    }

    fun deleteCache(cacheId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(CacheTable.TABLE_NAME, "${CacheTable.ID} = ?", arrayOf(cacheId.toString()))
    }

    fun getUserCaches(creatorId: Int): List<Cache> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            CacheTable.TABLE_NAME,
            null,
            "${CacheTable.CREATOR_ID} = ?",
            arrayOf(creatorId.toString()),
            null, null, null
        )
        val caches = mutableListOf<Cache>()
        with(cursor) {
            while (moveToNext()) {
                val cache = Cache(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(CacheTable.ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(CacheTable.NAME)),
                    description = cursor.getString(cursor.getColumnIndexOrThrow(CacheTable.DESCRIPTION)),
                    xCoordinate = cursor.getDouble(cursor.getColumnIndexOrThrow(CacheTable.XCOORDINATE)),
                    yCoordinate = cursor.getDouble(cursor.getColumnIndexOrThrow(CacheTable.YCOORDINATE)),
                    zoneRadius = cursor.getInt(cursor.getColumnIndexOrThrow(CacheTable.ZONE)),
                    rating = cursor.getDouble(cursor.getColumnIndexOrThrow(CacheTable.RATING)),
                    difficulty = cursor.getDouble(cursor.getColumnIndexOrThrow(CacheTable.DIFFICULTY)),
                    approved = cursor.getInt(cursor.getColumnIndexOrThrow(CacheTable.APPROVED)),
                    shown = cursor.getInt(cursor.getColumnIndexOrThrow(CacheTable.SHOWN)),
                    createdAt = cursor.getString(cursor.getColumnIndexOrThrow(CacheTable.CREATED_AT)),
                    updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(CacheTable.UPDATED_AT)),
                    private = cursor.getInt(cursor.getColumnIndexOrThrow(CacheTable.PRIVATE)),
                    themeId = cursor.getInt(cursor.getColumnIndexOrThrow(CacheTable.THEME_ID)),
                    creatorId = cursor.getInt(cursor.getColumnIndexOrThrow(CacheTable.CREATOR_ID))
                )
                caches.add(cache)
            }
        }
        cursor.close()
        return caches

    }

    fun getRecommendedCaches(userId: Int, currentLatitude: Double, currentLongitude: Double): List<Cache> {

        // Calculate weights for each cache
        val userCacheDAO = UserCacheDAO(context)
        val userCaches=userCacheDAO.getAllUserCaches()
        val caches = getAllCaches().filter {
                it.creatorId!=userId
                userCaches.find { cache ->  cache.cacheId == it.id&&
                                            cache.userId==userId }?.
                                            available==1

        }
        val foundCaches = userCaches.filter {
            it.found == 1&&
            it.userId==userId&&
            userId!=findCacheById(it.cacheId)?.creatorId
        }
        val averageDifficulty = foundCaches.map { it.rating ?: 0.0 }.average()
        val commonCategories = foundCaches.groupBy { it.cacheId }.mapValues { it.value.size }

        val userFoundCaches = foundCaches.filter { it.userId == userId }
        val themedCachesCount = userFoundCaches.count { cache -> caches.find { it.id == cache.cacheId }?.themeId != null }
        val nonThemedCachesCount = userFoundCaches.size - themedCachesCount
        val prefersThemed = themedCachesCount > nonThemedCachesCount

        val weightedCaches = caches.map { cache ->
            val distance = sqrt(
                (cache.xCoordinate - currentLatitude) * (cache.xCoordinate - currentLatitude) +
                        (cache.yCoordinate - currentLongitude) * (cache.yCoordinate - currentLongitude)
            )
            val distanceWeight = if (distance <= 10) 1.0 else 1.0 / distance

            val difficultyWeight = 1.0 / (1.0 + abs((cache.difficulty ?: 0.0) - averageDifficulty))

            val categoryWeight = (commonCategories[cache.id] ?: 0) / foundCaches.size.toDouble()

            val themeWeight = if (prefersThemed) {
                if (cache.themeId != null) 1.0 else 0.5
            } else {
                if (cache.themeId == null) 1.0 else 0.5
            }

            val totalWeight = distanceWeight + difficultyWeight + categoryWeight + themeWeight

            cache to totalWeight
        }

        return weightedCaches.sortedByDescending { it.second }.map { it.first }
    }
}