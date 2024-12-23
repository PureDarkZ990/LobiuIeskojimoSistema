package com.example.lobiupaieskossistema.models

data class Group(
    val id: Int = 0,
    val name: String,
    val description: String = "My description",
    val activity: String?,
    val xActivity: Double?,
    val yActivity: Double?,
    val totalFoundCaches: Int? = 0,
    val creatorId: Int?,
    val createdAt: String?,
    val updatedAt: String?
)

