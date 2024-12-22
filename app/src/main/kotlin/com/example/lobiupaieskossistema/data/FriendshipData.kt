package com.example.lobiupaieskossistema.data

import android.content.Context
import com.example.lobiupaieskossistema.dao.FriendshipDAO
import com.example.lobiupaieskossistema.models.Friendship

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
            // Sukuriame keletą pavyzdinių draugysčių tarp vartotojų
            Friendship(1, 1, 2, 1, "2023-01-01"), // user1 ir user2 yra draugai
            Friendship(2, 1, 3, 1, "2023-01-01"), // user1 ir user3 yra draugai
            Friendship(3, 2, 4, 1, "2023-01-01"), // user2 ir admin yra draugai
            Friendship(4, 3, 5, 0, "2023-01-01"), // user3 siuntė užklausą user4
            Friendship(5, 6, 1, 2, "2023-01-01"), // user5 atmetė user1 užklausą
            Friendship(6, 7, 8, 1, "2023-01-01"), // user6 ir user7 yra draugai
            Friendship(7, 9, 10, 1, "2023-01-01"), // user8 ir user9 yra draugai
            Friendship(8, 11, 12, 0, "2023-01-01"), // user10 siuntė užklausą user11
            Friendship(9, 13, 14, 1, "2023-01-01"), // user12 ir user13 yra draugai
            Friendship(10, 15, 16, 3, "2023-01-01") // user14 užblokavo user15
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
        return friendshipDAO.getPendingFriendRequests(userId)
    }

    fun areFriends(userId1: Int, userId2: Int): Boolean {
        return friendshipDAO.checkFriendshipStatus(userId1, userId2) == 1
    }

    fun getBlockedUsers(userId: Int): List<Friendship> {
        return friendshipDAO.getBlockedFriendships(userId)
    }

    fun acceptFriendRequest(friendshipId: Int) {
        friendshipDAO.updateFriendshipStatus(friendshipId, 1)
    }

    fun rejectFriendRequest(friendshipId: Int) {
        friendshipDAO.updateFriendshipStatus(friendshipId, 2)
    }

    fun blockUser(friendshipId: Int, blockerId: Int) {
        friendshipDAO.blockFriendship(friendshipId, blockerId)
    }

    fun unblockUser(friendshipId: Int) {
        friendshipDAO.unblockFriendship(friendshipId)
    }
}
