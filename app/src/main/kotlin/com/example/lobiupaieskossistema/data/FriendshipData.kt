package com.example.lobiupaieskossistema.data

import android.content.Context
import com.example.lobiupaieskossistema.dao.FriendshipDAO
import com.example.lobiupaieskossistema.models.Friendship
import com.example.lobiupaieskossistema.models.FriendshipStatus

object FriendshipData {
    private lateinit var friendshipDAO: FriendshipDAO

    fun initialize(context: Context) {
        if (!::friendshipDAO.isInitialized) {
            friendshipDAO = FriendshipDAO(context)
            addHardcodedEntries()
        }
    }

    private fun addHardcodedEntries() {
        val friendshipList = listOf(
            Friendship(1, 1, 2, FriendshipStatus.ACCEPTED.value, "2023-01-01"),
            Friendship(2, 1, 3, FriendshipStatus.ACCEPTED.value, "2023-01-01"),
            Friendship(3, 2, 4, FriendshipStatus.ACCEPTED.value, "2023-01-01"),
            Friendship(4, 3, 5, FriendshipStatus.PENDING.value, "2023-01-01"),
            Friendship(5, 6, 1, FriendshipStatus.REJECTED.value, "2023-01-01"),
            Friendship(6, 7, 8, FriendshipStatus.ACCEPTED.value, "2023-01-01"),
            Friendship(7, 9, 10, FriendshipStatus.ACCEPTED.value, "2023-01-01"),
            Friendship(8, 11, 12, FriendshipStatus.PENDING.value, "2023-01-01"),
            Friendship(9, 13, 14, FriendshipStatus.ACCEPTED.value, "2023-01-01"),
            Friendship(10, 15, 16, FriendshipStatus.BLOCKED.value, "2023-01-01")
        )

        friendshipList.forEach { friendshipDAO.addFriendship(it) }
    }

    fun get(id: Int): Friendship? {
        return friendshipDAO.findFriendshipById(id)
    }

    fun update(friendship: Friendship) {
        friendshipDAO.updateFriendship(friendship)
    }

    fun delete(id: Int) {
        friendshipDAO.deleteFriendship(id)
    }

    fun add(friendship: Friendship) {
        friendshipDAO.addFriendship(friendship)
    }

    fun getAll(): List<Friendship> {
        return friendshipDAO.getAllFriendships()
    }

    fun getFriendships(userId: Int): List<Friendship> {
        return friendshipDAO.getFriendshipsByUserId(userId)
    }

    fun getPendingRequests(userId: Int): List<Friendship> {
        return getFriendships(userId).filter { it.statusValue == FriendshipStatus.PENDING.value }
    }

    fun getBlockedFriendships(userId: Int): List<Friendship> {
        return getFriendships(userId).filter { it.statusValue == FriendshipStatus.BLOCKED.value }
    }

    fun areFriends(userId1: Int, userId2: Int): Boolean {
        val friendship = getFriendships(userId1).find {
            (it.userId == userId1 && it.friendId == userId2) ||
                    (it.userId == userId2 && it.friendId == userId1)
        }
        return friendship?.statusValue == FriendshipStatus.ACCEPTED.value
    }

    fun acceptFriendRequest(friendshipId: Int) {
        val friendship = get(friendshipId)
        friendship?.let {
            update(it.copyWithStatus(FriendshipStatus.ACCEPTED))
        }
    }

    fun rejectFriendRequest(friendshipId: Int) {
        val friendship = get(friendshipId)
        friendship?.let {
            update(it.copyWithStatus(FriendshipStatus.REJECTED))
        }
    }

    fun blockUser(friendshipId: Int, blockerId: Int) {
        val friendship = get(friendshipId)
        friendship?.let {
            val blockedFriendship = it.copy(
                statusValue = FriendshipStatus.BLOCKED.value,
                blockedBy = blockerId,
                updatedAt = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(java.util.Date())
            )
            update(blockedFriendship)
        }
    }

    fun unblockUser(friendshipId: Int) {
        val friendship = get(friendshipId)
        friendship?.let {
            val unblockedFriendship = it.copy(
                statusValue = FriendshipStatus.ACCEPTED.value,
                blockedBy = null,
                blockReason = null,
                updatedAt = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    .format(java.util.Date())
            )
            update(unblockedFriendship)
        }
    }
}

