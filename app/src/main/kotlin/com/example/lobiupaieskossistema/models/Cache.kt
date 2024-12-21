package com.example.lobiupaieskossistema.models

data class Cache(
    val id: Int = 0,
    var name: String,
    var description: String? = "My cache",
    var xCoordinate: Double,
    var yCoordinate: Double,
    var zoneRadius: Int = 100,
    var found: Int = 0,
    var rating: Double? =0.0,
    var difficulty: Double?,
    var approved: Int? = 0,
    var createdAt: String?,
    var updatedAt: String?,
    var private: Int = 0,
    var shown: Int? = 1,
    var themeId: Int? = null,
    var creatorId: Int?
)