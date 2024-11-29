package com.example.lobiupaieskossistema.models

data class User(
    val id: Int,
    val name: String
)

data class Group(
    val id: Int,
    val name: String,
    val users: List<User>
)