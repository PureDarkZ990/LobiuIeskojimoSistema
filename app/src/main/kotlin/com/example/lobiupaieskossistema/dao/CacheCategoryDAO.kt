package com.example.lobiupaieskossistema.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.lobiupaieskossistema.DatabaseHelper
import com.example.lobiupaieskossistema.database.CacheCategoryTable
import com.example.lobiupaieskossistema.models.CacheCategory
class CacheCategoryDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun addCacheCategory(cacheCategory: CacheCategory): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(CacheCategoryTable.CACHE_ID, cacheCategory.cacheId)
            put(CacheCategoryTable.CATEGORY_ID, cacheCategory.categoryId)
        }
        return db.insert(CacheCategoryTable.TABLE_NAME, null, values)
    }

    fun getAllCacheCategories(): List<CacheCategory> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            CacheCategoryTable.TABLE_NAME,
            null, null, null, null, null, null
        )
        val cacheCategories = mutableListOf<CacheCategory>()
        with(cursor) {
            while (moveToNext()) {
                val cacheCategory = CacheCategory(
                    cacheId = getInt(getColumnIndexOrThrow(CacheCategoryTable.CACHE_ID)),
                    categoryId = getInt(getColumnIndexOrThrow(CacheCategoryTable.CATEGORY_ID))
                )
                cacheCategories.add(cacheCategory)
            }
        }
        cursor.close()
        return cacheCategories
    }
    fun findCacheCategoryById(cacheId: Int, categoryId: Int): CacheCategory? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            CacheCategoryTable.TABLE_NAME,
            null,
            "${CacheCategoryTable.CACHE_ID} = ? AND ${CacheCategoryTable.CATEGORY_ID} = ?",
            arrayOf(cacheId.toString(), categoryId.toString()),
            null, null, null
        )
        var cacheCategory: CacheCategory? = null
        if (cursor.moveToFirst()) {
            cacheCategory = CacheCategory(
                cacheId = cursor.getInt(cursor.getColumnIndexOrThrow(CacheCategoryTable.CACHE_ID)),
                categoryId = cursor.getInt(cursor.getColumnIndexOrThrow(CacheCategoryTable.CATEGORY_ID))
            )
        }
        cursor.close()
        return cacheCategory
    }
    fun updateCacheCategory(cacheCategory: CacheCategory): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(CacheCategoryTable.CACHE_ID, cacheCategory.cacheId)
            put(CacheCategoryTable.CATEGORY_ID, cacheCategory.categoryId)
        }
        return db.update(CacheCategoryTable.TABLE_NAME, values, "${CacheCategoryTable.CACHE_ID} = ? AND ${CacheCategoryTable.CATEGORY_ID} = ?", arrayOf(cacheCategory.cacheId.toString(), cacheCategory.categoryId.toString()))
    }

    fun deleteCacheCategory(cacheId: Int, categoryId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(CacheCategoryTable.TABLE_NAME, "${CacheCategoryTable.CACHE_ID} = ? AND ${CacheCategoryTable.CATEGORY_ID} = ?", arrayOf(cacheId.toString(), categoryId.toString()))
    }
}