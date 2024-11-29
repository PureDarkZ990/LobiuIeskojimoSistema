package com.example.lobiupaieskossistema.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.lobiupaieskossistema.DatabaseHelper
import com.example.lobiupaieskossistema.models.Notification
import com.example.lobiupaieskossistema.database.NotificationTable

class NotificationDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun addNotification(notification: Notification): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(NotificationTable.MESSAGE, notification.message)
            put(NotificationTable.DATE, notification.date)
        }
        return db.insert(NotificationTable.TABLE_NAME, null, values)
    }

    fun getAllNotifications(): List<Notification> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            NotificationTable.TABLE_NAME,
            null, null, null, null, null, null
        )
        val notifications = mutableListOf<Notification>()
        with(cursor) {
            while (moveToNext()) {
                val notification = Notification(
                    id = getInt(getColumnIndexOrThrow(NotificationTable.ID)),
                    message = getString(getColumnIndexOrThrow(NotificationTable.MESSAGE)),
                    date = getString(getColumnIndexOrThrow(NotificationTable.DATE))
                )
                notifications.add(notification)
            }
        }
        cursor.close()
        return notifications
    }
    fun findNotificationById(notificationId: Int): Notification? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            NotificationTable.TABLE_NAME,
            null,
            "${NotificationTable.ID} = ?",
            arrayOf(notificationId.toString()),
            null, null, null
        )
        var notification: Notification? = null
        if (cursor.moveToFirst()) {
            notification = Notification(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(NotificationTable.ID)),
                message = cursor.getString(cursor.getColumnIndexOrThrow(NotificationTable.MESSAGE)),
                date = cursor.getString(cursor.getColumnIndexOrThrow(NotificationTable.DATE))
            )
        }
        cursor.close()
        return notification
    }
    fun updateNotification(notification: Notification): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(NotificationTable.MESSAGE, notification.message)
            put(NotificationTable.DATE, notification.date)
        }
        return db.update(NotificationTable.TABLE_NAME, values, "${NotificationTable.ID} = ?", arrayOf(notification.id.toString()))
    }

    fun deleteNotification(notificationId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(NotificationTable.TABLE_NAME, "${NotificationTable.ID} = ?", arrayOf(notificationId.toString()))
    }
}