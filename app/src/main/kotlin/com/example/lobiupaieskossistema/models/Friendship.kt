package com.example.lobiupaieskossistema.models
import com.example.lobiupaieskossistema.models.FriendshipStatus

data class Friendship(
    val id: Int = 0,
    val userId: Int,
    val friendId: Int,
    val statusValue: Int = FriendshipStatus.PENDING.value,
    val createdAt: String,
    val updatedAt: String? = null,
    val blockedBy: Int? = null,
    val blockReason: String? = null,
    val lastInteraction: String? = null
) {
    val status: FriendshipStatus
        get() = FriendshipStatus.fromInt(statusValue)

    fun copyWithStatus(newStatus: FriendshipStatus): Friendship {
        return copy(statusValue = newStatus.value)
    }
}
