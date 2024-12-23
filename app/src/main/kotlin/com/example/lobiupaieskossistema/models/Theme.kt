package com.example.lobiupaieskossistema.models

data class Theme(
    val id: Int = 0,
    val cacheId: Int,
    val description: String,
    val absoluteTime: Int = 0,
    val time: Int = 0,
    val endingTime: Long = 0L
)