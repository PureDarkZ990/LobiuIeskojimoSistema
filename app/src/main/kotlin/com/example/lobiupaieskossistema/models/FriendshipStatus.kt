package com.example.lobiupaieskossistema.models

enum class FriendshipStatus(val value: Int) {
    PENDING(0),      // Laukiama patvirtinimo
    ACCEPTED(1),     // Patvirtinta
    REJECTED(2),     // Atmesta
    BLOCKED(3);      // Užblokuota

    companion object {
        fun fromInt(value: Int): FriendshipStatus {
            return values().find { it.value == value } ?: PENDING
        }
    }
}