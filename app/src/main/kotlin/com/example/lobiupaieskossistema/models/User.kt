package com.example.lobiupaieskossistema.models

import androidx.annotation.Nullable

data class User(
    val id: Int = 0,
    val username: String,
    val hashedPassword: String,
    val email: String,
    val bio: String,
    val createdAt: String,
    val roleId: Int?=0,
    val statusId: Int?=1,
    val lastLogin: String?=null,
    val points: Int? = 0,
    val soundEnabled: Int? = 0,
    val vibrationEnabled: Int? = 0,
    val darkMode: Int? = 0,
    val private: Int? = 0,
    val location: String? = null,
    val phone: String? = null,
    val imageId: Int? = null,
    val updatedAt: String? = null,
    val token: String? = null,
    val tokenExpire: String? = null
)