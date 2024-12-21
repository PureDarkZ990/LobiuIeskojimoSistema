package com.example.lobiupaieskossistema.models

data class UserCache(
    val userId: Int,
    val cacheId: Int,
    val found: Int = 0,
    val rating: Double?,
    val available: Int = 1
)