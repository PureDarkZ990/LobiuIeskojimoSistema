package com.example.lobiupaieskossistema.data

import android.content.Context
import com.example.lobiupaieskossistema.dao.UserCacheDAO
import com.example.lobiupaieskossistema.models.UserCache

object UserCacheData {
    private lateinit var userCacheDAO: UserCacheDAO

    fun initialize(context: Context) {
        userCacheDAO = UserCacheDAO(context)
    }

    fun add(userCache: UserCache): Long {
        return userCacheDAO.addUserCache(userCache)
    }

    fun getAll(): List<UserCache> {
        return userCacheDAO.getAllUserCaches()
    }

    fun delete(cacheId: Int, cacheUserId: Int) {
        userCacheDAO.deleteUserCache(cacheId, cacheUserId)
    }
}