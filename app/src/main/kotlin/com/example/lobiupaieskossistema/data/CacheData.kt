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
            Cache(1, "Cache 1", "Description 1", 54.9057, 23.9659, 100, 0, 4.5, 3.0, 1, "2023-01-01", "2023-01-01", 0, 1, null, null,"123"),
            Cache(2, "Cache 2", "Description 2", 54.9000, 23.9100, 100, 0, 3.8, 2.0, 0, "2023-01-01", "2023-01-01", 1, 0, null, null,"123"),
           Cache(3, "test123", "Dtestukas", 54.89846086828864, 23.957918353352756, 1000,0, 4.0, 1.0, 1, "2023-01-01", "2023-01-01", 0,1, null  , null,"123"),
            Cache(4, "Cache 4", "Description 4", 54.8970, 23.9000, 100, 1, 1.1, 1.4, 0, "2024-08-09", "2024-12-15", 0, 44, null, null,"123"),
            Cache(5, "Cache 5", "Description 5", 54.8955, 23.9055, 120, 1, 3.0, 4.3, 0, "2024-11-02", "2024-12-23", 0, 11, null, null,"123"),
            Cache(6, "Cache 6", "Description 6", 54.8990, 23.9040, 84, 0, 4.2, 2.2, 1, "2024-05-24", "2024-12-04", 0, 34, null, null,"123"),
            Cache(7, "Cache 7", "Description 7", 54.8965, 23.9020, 111, 0, 2.0, 5.0, 1, "2024-10-05", "2024-11-26", 0, 2, null, null,"123"),
            Cache(8, "Cache 8", "Description 8", 54.8980, 23.9010, 145, 0, 4.1, 2.2, 1, "2024-06-06", "2024-12-12", 0, 26, null, null,"123"),
            Cache(9, "Cache 9", "Description 9", 54.8995, 23.9025, 124, 1, 2.3, 4.3, 0, "2024-05-23", "2024-11-25", 0, 2, null, null,"123"),
            Cache(10, "Cache 10", "Description 10", 54.8950, 23.9060, 97, 0, 1.9, 1.8, 0, "2024-01-18", "2024-12-23", 0, 6, null, null,"123"),
            Cache(11, "Cache 11", "Description 11", 54.9020, 23.9050, 116, 0, 2.5, 3.2, 1, "2024-06-17", "2024-12-16", 0, 3, null, null,"123"),
            Cache(12, "Cache 12", "Description 12", 54.9005, 23.9065, 62, 0, 1.6, 4.3, 1, "2024-07-02", "2024-12-01", 0, 31, null, null,"123"),
            Cache(13, "Cache 13", "Description 13", 54.9010, 23.9030, 111, 0, 2.7, 2.3, 0, "2024-08-09", "2024-11-29", 0, 17, null, null,"123"),
            Cache(14, "Cache 14", "Description 14", 54.9000, 23.9025, 86, 0, 1.6, 3.0, 0, "2024-10-26", "2024-12-09", 0, 6, null, null,"123"),
            Cache(15, "Cache 15", "Description 15", 54.8980, 23.9060, 119, 1, 4.3, 2.1, 0, "2024-05-18", "2024-12-07", 1, 9, null, null,"123"),
            Cache(16, "Cache 16", "Description 16", 54.8995, 23.9035, 71, 1, 4.7, 2.8, 1, "2024-05-16", "2024-12-05", 0, 15, null, null,"123"),
            Cache(17, "Cache 17", "Description 17", 54.9000, 23.9020, 82, 0, 2.3, 3.4, 1, "2024-01-15", "2024-11-28", 0, 22, null, null,"123"),
            Cache(18, "Cache 18", "Description 18", 54.8950, 23.9050, 98, 0, 4.7, 4.9, 0, "2024-09-03", "2024-12-04", 0, 9, null, null,"123"),
            Cache(19, "Cache 19", "Description 19", 54.8990, 23.9020, 76, 1, 4.1, 4.2, 1, "2024-01-03", "2024-12-22", 0, 50, null, null,"123"),
            Cache(20, "Cache 20", "Description 20", 54.8975, 23.9005, 57, 0, 2.4, 4.1, 1, "2024-09-18", "2024-11-30", 0, 29, null, null,"123"),
            Cache(21, "Cache 21", "Description 21", 54.9025, 23.9045, 120, 1, 1.5, 4.8, 1, "2024-09-07", "2024-12-09", 0, 33, null, null,"123"),
            Cache(22, "Cache 22", "Description 22", 54.8995, 23.9030, 118, 1, 2.8, 1.6, 1, "2024-08-03", "2024-11-24", 0, 20, null, null,"123"),
            Cache(23, "Cache 23", "Description 23", 54.9000, 23.9050, 128, 0, 4.9, 3.1, 0, "2024-01-13", "2024-11-23", 0, 23, null, null,"123")
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