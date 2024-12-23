package com.example.lobiupaieskossistema.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.lobiupaieskossistema.DatabaseHelper
import com.example.lobiupaieskossistema.database.FriendshipTable
import com.example.lobiupaieskossistema.models.Friendship
import com.example.lobiupaieskossistema.models.FriendshipStatus

class FriendshipDAO(private val context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun addFriendship(friendship: Friendship): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(FriendshipTable.USER_ID, friendship.userId)
            put(FriendshipTable.FRIEND_ID, friendship.friendId)
            put(FriendshipTable.STATUS, friendship.status.value)
            put(FriendshipTable.CREATED_AT, friendship.createdAt)
            put(FriendshipTable.UPDATED_AT, friendship.updatedAt)
            put(FriendshipTable.BLOCKED_BY, friendship.blockedBy)
            put(FriendshipTable.BLOCK_REASON, friendship.blockReason)
            put(FriendshipTable.LAST_INTERACTION, friendship.lastInteraction)
        }
        return db.insert(FriendshipTable.TABLE_NAME, null, values)
    }

    fun findFriendshipById(friendshipId: Int): Friendship? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            FriendshipTable.TABLE_NAME,
            null,
            "${FriendshipTable.ID} = ?",
            arrayOf(friendshipId.toString()),
            null, null, null
        )
        var friendship: Friendship? = null
        if (cursor.moveToFirst()) {
            friendship = cursor.toFriendship()
        }
        cursor.close()
        return friendship
    }

    fun getAllFriendships(): List<Friendship> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            FriendshipTable.TABLE_NAME,
            null, null, null, null, null, null
        )
        val friendships = mutableListOf<Friendship>()
        while (cursor.moveToNext()) {
            friendships.add(cursor.toFriendship())
        }
        cursor.close()
        return friendships
    }

    fun getFriendshipsByUserId(userId: Int): List<Friendship> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            FriendshipTable.TABLE_NAME,
            null,
            "${FriendshipTable.USER_ID} = ? OR ${FriendshipTable.FRIEND_ID} = ?",
            arrayOf(userId.toString(), userId.toString()),
            null, null, null
        )
        val friendships = mutableListOf<Friendship>()
        while (cursor.moveToNext()) {
            friendships.add(cursor.toFriendship())
        }
        cursor.close()
        return friendships
    }

    fun getPendingFriendRequests(userId: Int): List<Friendship> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            FriendshipTable.TABLE_NAME,
            null,
            "${FriendshipTable.FRIEND_ID} = ? AND ${FriendshipTable.STATUS} = 0",
            arrayOf(userId.toString()),
            null, null, null
        )
        val friendships = mutableListOf<Friendship>()
        while (cursor.moveToNext()) {
            friendships.add(cursor.toFriendship())
        }
        cursor.close()
        return friendships
    }

    fun updateFriendshipStatus(friendshipId: Int, status: FriendshipStatus) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(FriendshipTable.STATUS, status.value)
            put(FriendshipTable.UPDATED_AT, System.currentTimeMillis().toString())
        }
        db.update(
            FriendshipTable.TABLE_NAME,
            values,
            "${FriendshipTable.ID} = ?",
            arrayOf(friendshipId.toString())
        )
    }

    fun updateFriendship(friendship: Friendship): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(FriendshipTable.USER_ID, friendship.userId)
            put(FriendshipTable.FRIEND_ID, friendship.friendId)
            put(FriendshipTable.STATUS, friendship.statusValue) // Naudojame statusValue vietoj status
            put(FriendshipTable.UPDATED_AT, friendship.updatedAt)
            put(FriendshipTable.BLOCKED_BY, friendship.blockedBy)
            put(FriendshipTable.BLOCK_REASON, friendship.blockReason)
            put(FriendshipTable.LAST_INTERACTION, friendship.lastInteraction)
        }
        return db.update(
            FriendshipTable.TABLE_NAME,
            values,
            "${FriendshipTable.ID} = ?",
            arrayOf(friendship.id.toString())
        )
    }

    fun deleteFriendship(friendshipId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(
            FriendshipTable.TABLE_NAME,
            "${FriendshipTable.ID} = ?",
            arrayOf(friendshipId.toString())
        )
    }

    fun checkFriendshipStatus(userId1: Int, userId2: Int): Int? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            FriendshipTable.TABLE_NAME,
            arrayOf(FriendshipTable.STATUS),
            "(${FriendshipTable.USER_ID} = ? AND ${FriendshipTable.FRIEND_ID} = ?) OR " +
                    "(${FriendshipTable.USER_ID} = ? AND ${FriendshipTable.FRIEND_ID} = ?)",
            arrayOf(userId1.toString(), userId2.toString(), userId2.toString(), userId1.toString()),
            null, null, null
        )
        var status: Int? = null
        if (cursor.moveToFirst()) {
            status = cursor.getInt(cursor.getColumnIndexOrThrow(FriendshipTable.STATUS))
        }
        cursor.close()
        return status
    }

    fun updateFriendshipStatus(friendshipId: Int, newStatus: Int): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(FriendshipTable.STATUS, newStatus)
            put(FriendshipTable.UPDATED_AT, System.currentTimeMillis().toString())
        }
        return db.update(
            FriendshipTable.TABLE_NAME,
            values,
            "${FriendshipTable.ID} = ?",
            arrayOf(friendshipId.toString())
        )
    }

    fun blockFriendship(friendshipId: Int, blockerId: Int) {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(FriendshipTable.STATUS, FriendshipStatus.BLOCKED.value)
            put(FriendshipTable.BLOCKED_BY, blockerId)
            put(FriendshipTable.UPDATED_AT, System.currentTimeMillis().toString())
        }
        db.update(
            FriendshipTable.TABLE_NAME,
            values,
            "${FriendshipTable.ID} = ?",
            arrayOf(friendshipId.toString())
        )
    }

    private fun Cursor.toFriendship(): Friendship {
        return Friendship(
            id = getInt(getColumnIndexOrThrow(FriendshipTable.ID)),
            userId = getInt(getColumnIndexOrThrow(FriendshipTable.USER_ID)),
            friendId = getInt(getColumnIndexOrThrow(FriendshipTable.FRIEND_ID)),
            statusValue = getInt(getColumnIndexOrThrow(FriendshipTable.STATUS)),
            createdAt = getString(getColumnIndexOrThrow(FriendshipTable.CREATED_AT)),
            updatedAt = getString(getColumnIndexOrThrow(FriendshipTable.UPDATED_AT)),
            blockedBy = getInt(getColumnIndexOrThrow(FriendshipTable.BLOCKED_BY)),
            blockReason = getString(getColumnIndexOrThrow(FriendshipTable.BLOCK_REASON)),
            lastInteraction = getString(getColumnIndexOrThrow(FriendshipTable.LAST_INTERACTION))
        )
    }

    fun getBlockedFriendships(userId: Int): List<Friendship> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            FriendshipTable.TABLE_NAME,
            null,
            "(${FriendshipTable.USER_ID} = ? OR ${FriendshipTable.FRIEND_ID} = ?) AND " +
                    "${FriendshipTable.STATUS} = 3", // 3 = blocked
            arrayOf(userId.toString(), userId.toString()),
            null, null, null
        )
        val friendships = mutableListOf<Friendship>()
        while (cursor.moveToNext()) {
            friendships.add(cursor.toFriendship())
        }
        cursor.close()
        return friendships
    }

    fun unblockFriendship(friendshipId: Int): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(FriendshipTable.STATUS, 1) // 1 = accepted/active
            putNull(FriendshipTable.BLOCKED_BY)
            putNull(FriendshipTable.BLOCK_REASON)
            put(FriendshipTable.UPDATED_AT, System.currentTimeMillis().toString())
        }
        return db.update(
            FriendshipTable.TABLE_NAME,
            values,
            "${FriendshipTable.ID} = ?",
            arrayOf(friendshipId.toString())
        )
    }

}
