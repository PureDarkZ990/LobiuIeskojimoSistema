package com.example.lobiupaieskossistema.data

import android.content.Context
import com.example.lobiupaieskossistema.dao.CacheDAO
import com.example.lobiupaieskossistema.models.Cache

object CacheData {
    private lateinit var cacheDAO: CacheDAO

    fun initialize(context: Context) {
        cacheDAO = CacheDAO(context)
        addHardcodedEntries()
    }

    private fun addHardcodedEntries() {
        val cacheList = listOf(
            Cache(1, "Cache 1", "Description 1", 54.6872, 25.2797, 1000,0, 4.5, 3.0, 1, "2023-01-01", "2023-01-01", 0, 1,null, null),
            Cache(2, "Cache 2", "Description 2", 54.9872, 25.3797, 1000, 0, 3.8, 2.0, 0, "2023-01-01", "2023-01-01", 1, 0,null, null),
            Cache(3, "Cache 3", "Description 3", 54.5872, 25.1797, 1000,0, 4.0, 1.0, 1, "2023-01-01", "2023-01-01", 0,1, null, null),
            Cache(3, "test123", "Dtestukas", 54.89846086828864, 23.957918353352756, 1000,0, 4.0, 1.0, 1, "2023-01-01", "2023-01-01", 0,1, null  , null)
        )
        cacheList.forEach { cacheDAO.addCache(it) }
    }

    fun get(id: Int): Cache? {
        return cacheDAO.findCacheById(id)
    }

    fun update(cache: Cache) {
        cacheDAO.updateCache(cache)
    }

    fun delete(id: Int) {
        cacheDAO.deleteCache(id)
    }

    fun add(cache: Cache): Long {
        return cacheDAO.addCache(cache)
    }
    fun getAllUserCaches(creatorId:Int): List<Cache> {
        return cacheDAO.getUserCaches(creatorId)
    }
    fun getRecommendedCaches(userId: Int, currentLatitude: Double, currentLongitude: Double): List<Cache> {
        return cacheDAO.getRecommendedCaches(userId, currentLatitude, currentLongitude)
    }
    fun getAll(): List<Cache> {
        return cacheDAO.getAllCaches()
    }
}