package com.example.lobiupaieskossistema.models

data class Image(
    val id: Int = 0,
    val path: String = "default_image.png",
    val cacheId: Int?,
    val description: String?
)