package com.example.lobiupaieskossistema.data

import android.content.Context
import com.example.lobiupaieskossistema.dao.CacheGroupDAO
import com.example.lobiupaieskossistema.models.CacheGroup

object CacheGroupData {
    private lateinit var cachegroupDAO: CacheGroupDAO

    fun initialize(context: Context) {
        cachegroupDAO = CacheGroupDAO(context)
    }

    fun add(cacheGroup: CacheGroup): Long {
        return cachegroupDAO.addCacheGroup(cacheGroup)
    }

    fun getAll(): List<CacheGroup> {
        return cachegroupDAO.getAllCacheGroups()
    }

    fun delete(cacheId: Int, cachegroupId: Int) {
        cachegroupDAO.deleteCacheGroup(cacheId, cachegroupId)
    }
}