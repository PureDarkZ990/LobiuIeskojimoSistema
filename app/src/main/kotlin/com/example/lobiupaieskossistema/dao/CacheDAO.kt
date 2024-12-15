package com.example.lobiupaieskossistema.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.lobiupaieskossistema.DatabaseHelper
import com.example.lobiupaieskossistema.database.CacheTable
import com.example.lobiupaieskossistema.models.Cache

class CacheDAO(context: Context) {

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
            put(CacheTable.DIFFICULTY, cache.difficulty)
            put(CacheTable.APPROVED, cache.approved)
            put(CacheTable.CREATED_AT, cache.createdAt)
            put(CacheTable.UPDATED_AT, cache.updatedAt)
            put(CacheTable.PRIVATE, cache.private)
            put(CacheTable.THEME_ID, cache.themeId)
            put(CacheTable.CREATOR_ID, cache.creatorId)
        }
        return db.insert(CacheTable.TABLE_NAME, null, values)
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
}