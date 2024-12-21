package com.example.lobiupaieskossistema.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.lobiupaieskossistema.DatabaseHelper
import com.example.lobiupaieskossistema.models.NotificationUser
import com.example.lobiupaieskossistema.database.NotificationUserTable
class NotificationUserDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun addNotificationUser(notificationUser: NotificationUser): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(NotificationUserTable.NOTIFICATION_ID, notificationUser.notificationId)
            put(NotificationUserTable.USER_ID, notificationUser.userId)
            put(NotificationUserTable.READ, notificationUser.read)
        }
        return db.insert(NotificationUserTable.TABLE_NAME, null, values)
    }

    fun getAllNotificationUsers(): List<NotificationUser> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            NotificationUserTable.TABLE_NAME,
            null, null, null, null, null, null
        )
        val notificationUsers = mutableListOf<NotificationUser>()
        with(cursor) {
            while (moveToNext()) {
                val notificationUser = NotificationUser(
                    notificationId = getInt(getColumnIndexOrThrow(NotificationUserTable.NOTIFICATION_ID)),
                    userId = getInt(getColumnIndexOrThrow(NotificationUserTable.USER_ID)),
                    read = getInt(getColumnIndexOrThrow(NotificationUserTable.READ))
                )
                notificationUsers.add(notificationUser)
            }
        }
        cursor.close()
        return notificationUsers
    }
    fun findNotificationUserById(notificationId: Int, userId: Int): NotificationUser? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            NotificationUserTable.TABLE_NAME,
            null,
            "${NotificationUserTable.NOTIFICATION_ID} = ? AND ${NotificationUserTable.USER_ID} = ?",
            arrayOf(notificationId.toString(), userId.toString()),
            null, null, null
        )
        var notificationUser: NotificationUser? = null
        if (cursor.moveToFirst()) {
            notificationUser = NotificationUser(
                notificationId = cursor.getInt(cursor.getColumnIndexOrThrow(NotificationUserTable.NOTIFICATION_ID)),
                userId = cursor.getInt(cursor.getColumnIndexOrThrow(NotificationUserTable.USER_ID)),
                read = cursor.getInt(cursor.getColumnIndexOrThrow(NotificationUserTable.READ))
            )
        }
        cursor.close()
        return notificationUser
    }
    fun updateNotificationUser(notificationUser: NotificationUser): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(NotificationUserTable.NOTIFICATION_ID, notificationUser.notificationId)
            put(NotificationUserTable.USER_ID, notificationUser.userId)
            put(NotificationUserTable.READ, notificationUser.read)
        }
        return db.update(NotificationUserTable.TABLE_NAME, values, "${NotificationUserTable.NOTIFICATION_ID} = ? AND ${NotificationUserTable.USER_ID} = ?", arrayOf(notificationUser.notificationId.toString(), notificationUser.userId.toString()))
    }

    fun deleteNotificationUser(notificationId: Int, userId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(NotificationUserTable.TABLE_NAME, "${NotificationUserTable.NOTIFICATION_ID} = ? AND ${NotificationUserTable.USER_ID} = ?", arrayOf(notificationId.toString(), userId.toString()))
    }
}