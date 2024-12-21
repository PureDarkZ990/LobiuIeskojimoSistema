package com.example.lobiupaieskossistema.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.lobiupaieskossistema.DatabaseHelper
import com.example.lobiupaieskossistema.models.Status
import com.example.lobiupaieskossistema.database.StatusTable

class StatusDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun addStatus(status: Status): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(StatusTable.TABLE_NAME, status.name)
        }
        return db.insert(StatusTable.TABLE_NAME, null, values)
    }

    fun getAllStatus(): List<Status> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            StatusTable.TABLE_NAME,
            null, null, null, null, null, null
        )
        val statuses = mutableListOf<Status>()
        with(cursor) {
            while (moveToNext()) {
                val status = Status(
                    id = getInt(getColumnIndexOrThrow(StatusTable.ID)),
                    name = getString(getColumnIndexOrThrow(StatusTable.TABLE_NAME))
                )
                statuses.add(status)
            }
        }
        cursor.close()
        return statuses
    }
    fun findStatusById(statusId: Int): Status? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            StatusTable.TABLE_NAME,
            null,
            "${StatusTable.ID} = ?",
            arrayOf(statusId.toString()),
            null, null, null
        )
        var status: Status? = null
        if (cursor.moveToFirst()) {
            status = Status(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(StatusTable.ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(StatusTable.TABLE_NAME))
            )
        }
        cursor.close()
        return status
    }
    fun updateStatus(status: Status): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(StatusTable.TABLE_NAME, status.name)
        }
        return db.update(StatusTable.TABLE_NAME, values, "${StatusTable.ID} = ?", arrayOf(status.id.toString()))
    }

    fun deleteStatus(statusId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(StatusTable.TABLE_NAME, "${StatusTable.ID} = ?", arrayOf(statusId.toString()))
    }
}