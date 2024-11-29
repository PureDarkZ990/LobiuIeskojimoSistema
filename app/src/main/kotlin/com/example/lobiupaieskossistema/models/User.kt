package com.example.lobiupaieskossistema.models

data class User(
    val id: Int = 0,
    val username: String,
    val hashedPassword: String,
    val email: String,
    val createdAt: String,
    val roleId: Int,
    val statusId: Int,
    val lastLogin: String?,
    val points: Int = 0,
    val soundEnabled: Int = 0,
    val vibrationEnabled: Int = 0,
    val darkMode: Int = 0,
    val private: Int = 0,
    val location: String?,
    val phone: String?,
    val imageId: Int?,
    val updatedAt: String?,
    val token: String?,
    val tokenExpire: String?
)