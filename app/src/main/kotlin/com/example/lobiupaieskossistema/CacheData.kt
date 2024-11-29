package com.example.lobiupaieskossistema

import com.example.lobiupaieskossistema.models.Cache
import com.example.lobiupaieskossistema.models.Group
import com.example.lobiupaieskossistema.models.User

object CacheData{
private val userList= mutableListOf(
    User(0,"NAME1"),User(1,"NAME2"),
    User(2,"NAME3"),User(3,"NAME4")
)
private val cacheList = mutableListOf(
    Cache("Cache 1", "Description 1", true, "High", "4.5",
        true, 54.6872, 25.2797, 500, null, false, userList, emptyList()),
    Cache("Cache 2", "Description 2", false, "Medium", "3.8",
        false, 54.6872, 25.2797, 300, null, true, emptyList(), emptyList()),
    Cache("Cache 3", "Description 3", true, "Low", "4.0", true,
        54.6872, 25.2797, 200, null, false, emptyList(), emptyList())
)
fun getCacheById(id: Int): Cache? {
    return if (id in cacheList.indices) cacheList[id] else null
}

fun updateCache(cache: Cache) {
    val index = cacheList.indexOfFirst { it.id == cache.id }
    if (index != -1) {
        cacheList[index] = cache
    }
}

fun deleteCache(id: Int) {
    if (id in cacheList.indices) {
        cacheList.removeAt(id)
    }
}

fun addCache(cache: Cache) {
    cacheList.add(cache)
}

fun getAllCaches(): List<Cache> {
    return cacheList
}
}