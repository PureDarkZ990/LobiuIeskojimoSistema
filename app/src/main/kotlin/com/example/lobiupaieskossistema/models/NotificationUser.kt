package com.example.lobiupaieskossistema.models

data class NotificationUser(
    val notificationId: Int,
    val userId: Int,
    val read: Int = 0
)