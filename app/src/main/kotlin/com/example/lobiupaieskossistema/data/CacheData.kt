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
            Cache(1, "Cache 1", "Description 1", 54.6872, 25.2797, 100,0, 4.5, 3.0, 1, "2023-01-01", "2023-01-01", 0, 1,null, null),
            Cache(2, "Cache 2", "Description 2", 54.9872, 25.3797, 100, 0, 3.8, 2.0, 0, "2023-01-01", "2023-01-01", 1, 0,null, null),
           Cache(3, "test123", "Dtestukas", 54.89846086828864, 23.957918353352756, 1000,0, 4.0, 1.0, 1, "2023-01-01", "2023-01-01", 0,1, null  , null),
            Cache(4, "Cache 4", "Description 4", 54.6779, 25.2821, 100, 1, 1.1, 1.4, 0, "2024-08-09", "2024-12-15", 0, 44, null, null),
            Cache(5, "Cache 5", "Description 5", 54.7005, 25.2716, 120, 1, 3.0, 4.3, 0, "2024-11-02", "2024-12-23", 0, 11, null, null),
            Cache(6, "Cache 6", "Description 6", 54.6885, 25.2769, 84, 0, 4.2, 2.2, 1, "2024-05-24", "2024-12-04", 0, 34, null, null),
            Cache(7, "Cache 7", "Description 7", 54.6764, 25.2813, 111, 0, 2.0, 5.0, 1, "2024-10-05", "2024-11-26", 0, 2, null, null),
            Cache(8, "Cache 8", "Description 8", 54.6724, 25.2947, 145, 0, 4.1, 2.2, 1, "2024-06-06", "2024-12-12", 0, 26, null, null),
            Cache(9, "Cache 9", "Description 9", 54.7036, 25.2938, 124, 1, 2.3, 4.3, 0, "2024-05-23", "2024-11-25", 0, 2, null, null),
            Cache(10, "Cache 10", "Description 10", 54.6739, 25.2903, 97, 0, 1.9, 1.8, 0, "2024-01-18", "2024-12-23", 0, 6, null, null),
            Cache(11, "Cache 11", "Description 11", 54.6848, 25.2934, 116, 0, 2.5, 3.2, 1, "2024-06-17", "2024-12-16", 0, 3, null, null),
            Cache(12, "Cache 12", "Description 12", 54.6836, 25.2718, 62, 0, 1.6, 4.3, 1, "2024-07-02", "2024-12-01", 0, 31, null, null),
            Cache(13, "Cache 13", "Description 13", 54.6942, 25.2858, 111, 0, 2.7, 2.3, 0, "2024-08-09", "2024-11-29", 0, 17, null, null),
            Cache(14, "Cache 14", "Description 14", 54.6741, 25.2807, 86, 0, 1.6, 3.0, 0, "2024-10-26", "2024-12-09", 0, 6, null, null),
            Cache(15, "Cache 15", "Description 15", 54.6938, 25.2649, 119, 1, 4.3, 2.1, 0, "2024-05-18", "2024-12-07", 1, 9, null, null),
            Cache(16, "Cache 16", "Description 16", 54.6830, 25.2721, 71, 1, 4.7, 2.8, 1, "2024-05-16", "2024-12-05", 0, 15, null, null),
            Cache(17, "Cache 17", "Description 17", 54.6940, 25.2609, 82, 0, 2.3, 3.4, 1, "2024-01-15", "2024-11-28", 0, 22, null, null),
            Cache(18, "Cache 18", "Description 18", 54.6827, 25.2836, 98, 0, 4.7, 4.9, 0, "2024-09-03", "2024-12-04", 0, 9, null, null),
            Cache(19, "Cache 19", "Description 19", 54.6726, 25.2816, 76, 1, 4.1, 4.2, 1, "2024-01-03", "2024-12-22", 0, 50, null, null),
            Cache(20, "Cache 20", "Description 20", 54.7041, 25.2639, 57, 0, 2.4, 4.1, 1, "2024-09-18", "2024-11-30", 0, 29, null, null),
            Cache(21, "Cache 21", "Description 21", 54.7072, 25.2893, 120, 1, 1.5, 4.8, 1, "2024-09-07", "2024-12-09", 0, 33, null, null),
            Cache(22, "Cache 22", "Description 22", 54.7021, 25.2614, 118, 1, 2.8, 1.6, 1, "2024-08-03", "2024-11-24", 0, 20, null, null),
            Cache(23, "Cache 23", "Description 23", 54.6789, 25.2830, 128, 0, 4.9, 3.1, 0, "2024-01-13", "2024-11-23", 0, 23, null, null)
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