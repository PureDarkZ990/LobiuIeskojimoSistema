package com.example.lobiupaieskossistema.data

import android.content.Context
import com.example.lobiupaieskossistema.dao.CacheCategoryDAO
import com.example.lobiupaieskossistema.models.CacheCategory

object CacheCategoryData {
    private lateinit var cacheCategoryDAO: CacheCategoryDAO

    fun initialize(context: Context) {
        cacheCategoryDAO = CacheCategoryDAO(context)
    }
    fun get(cacheId: Int, categoryId: Int): CacheCategory? {
        return cacheCategoryDAO.findCacheCategoryById(cacheId, categoryId)
    }
    fun add(cacheCategory: CacheCategory): Long {
        return cacheCategoryDAO.addCacheCategory(cacheCategory)
    }

    fun getAll(): List<CacheCategory> {
        return cacheCategoryDAO.getAllCacheCategories()
    }
    fun update(cacheCategory: CacheCategory, categoryId: Int) {
        cacheCategoryDAO.updateCacheCategory(cacheCategory,categoryId)
    }
    fun delete(cacheId: Int, categoryId: Int) {
        cacheCategoryDAO.deleteCacheCategory(cacheId, categoryId)
    }
}