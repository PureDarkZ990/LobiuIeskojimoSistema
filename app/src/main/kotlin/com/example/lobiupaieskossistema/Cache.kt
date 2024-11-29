package com.example.lobiupaieskossistema.models

data class Cache(
    var name: String,
    var description: String,
    var isPublic: Boolean,
    var complexity: String,
    var rating: String,
    var isApproved: Boolean,
    var latitude: Double,
    var longitude: Double,
    var zoneRadius: Int,
    var imageUri: String?,
    var private: Boolean,
    var assignedUsers: List<User>,
    var assignedGroups: List<Group>,
    val id: Int = nextId++
) {
    companion object {
        private var nextId = 0
    }
}