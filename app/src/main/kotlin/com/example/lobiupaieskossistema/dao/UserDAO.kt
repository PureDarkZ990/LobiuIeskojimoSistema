package com.example.lobiupaieskossistema.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.lobiupaieskossistema.DatabaseHelper
import com.example.lobiupaieskossistema.database.UserTable
import com.example.lobiupaieskossistema.database.RoleTable
import com.example.lobiupaieskossistema.models.User

class UserDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun addUser(user: User): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(UserTable.NAME, user.username)
            put(UserTable.HASHED_PASSWORD, user.hashedPassword)
            put(UserTable.EMAIL, user.email)
            put(UserTable.CREATED_AT, user.createdAt)
            put(UserTable.ROLE_ID, user.roleId)
            put(UserTable.STATUS_ID, user.statusId)
            put(UserTable.LAST_LOGIN, user.lastLogin)
            put(UserTable.POINTS, user.points)
            put(UserTable.SOUND_ENABLED, user.soundEnabled)
            put(UserTable.VIBRATION_ENABLED, user.vibrationEnabled)
            put(UserTable.DARK_MODE, user.darkMode)
            put(UserTable.PRIVATE, user.private)
            put(UserTable.LOCATION, user.location)
            put(UserTable.PHONE, user.phone)
            put(UserTable.IMAGE, user.imageId)
            put(UserTable.UPDATED_AT, user.updatedAt)
            put(UserTable.TOKEN, user.token)
            put(UserTable.TOKEN_EXPIRE, user.tokenExpire)
        }
        return db.insert(UserTable.TABLE_NAME, null, values)
    }

    private fun getDefaultRoleId(): Int {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            RoleTable.TABLE_NAME,
            arrayOf(RoleTable.ID),
            "${RoleTable.NAME}  = ?",
            arrayOf(RoleTable.DEFAULT_ROLE),
            null, null, null
        )
        var roleId = -1
        if (cursor.moveToFirst()) {
            roleId = cursor.getInt(cursor.getColumnIndexOrThrow(RoleTable.ID))
        }
        cursor.close()
        return roleId
    }

    fun getAllUsers(): List<User> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            UserTable.TABLE_NAME,
            null, null, null, null, null, null
        )
        val users = mutableListOf<User>()
        with(cursor) {
            while (moveToNext()) {
                val user = User(
                    id = getInt(getColumnIndexOrThrow(UserTable.ID)),
                    username = getString(getColumnIndexOrThrow(UserTable.NAME)),
                    hashedPassword = getString(getColumnIndexOrThrow(UserTable.HASHED_PASSWORD)),
                    email = getString(getColumnIndexOrThrow(UserTable.EMAIL)),
                    createdAt = getString(getColumnIndexOrThrow(UserTable.CREATED_AT)),
                    roleId = getInt(getColumnIndexOrThrow(UserTable.ROLE_ID)),
                    statusId = getInt(getColumnIndexOrThrow(UserTable.STATUS_ID)),
                    lastLogin = getString(getColumnIndexOrThrow(UserTable.LAST_LOGIN)),
                    points = getInt(getColumnIndexOrThrow(UserTable.POINTS)),
                    soundEnabled = getInt(getColumnIndexOrThrow(UserTable.SOUND_ENABLED)),
                    vibrationEnabled = getInt(getColumnIndexOrThrow(UserTable.VIBRATION_ENABLED)),
                    darkMode = getInt(getColumnIndexOrThrow(UserTable.DARK_MODE)),
                    private = getInt(getColumnIndexOrThrow(UserTable.PRIVATE)),
                    location = getString(getColumnIndexOrThrow(UserTable.LOCATION)),
                    phone = getString(getColumnIndexOrThrow(UserTable.PHONE)),
                    imageId = getInt(getColumnIndexOrThrow(UserTable.IMAGE)),
                    updatedAt = getString(getColumnIndexOrThrow(UserTable.UPDATED_AT)),
                    token = getString(getColumnIndexOrThrow(UserTable.TOKEN)),
                    tokenExpire = getString(getColumnIndexOrThrow(UserTable.TOKEN_EXPIRE))
                )
                users.add(user)
            }
        }
        cursor.close()
        return users
    }

    fun findUserById(userId: Int): User? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            UserTable.TABLE_NAME,
            null,
            "${UserTable.ID} = ?",
            arrayOf(userId.toString()),
            null, null, null
        )
        var user: User? = null
        if (cursor.moveToFirst()) {
            user = User(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.ID)),
                username = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.NAME)),
                hashedPassword = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.HASHED_PASSWORD)),
                email = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.EMAIL)),
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.CREATED_AT)),
                roleId = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.ROLE_ID)),
                statusId = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.STATUS_ID)),
                lastLogin = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.LAST_LOGIN)),
                points = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.POINTS)),
                soundEnabled = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.SOUND_ENABLED)),
                vibrationEnabled = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.VIBRATION_ENABLED)),
                darkMode = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.DARK_MODE)),
                private = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.PRIVATE)),
                location = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.LOCATION)),
                phone = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.PHONE)),
                imageId = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.IMAGE)),
                updatedAt = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.UPDATED_AT)),
                token = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.TOKEN)),
                tokenExpire = cursor.getString(cursor.getColumnIndexOrThrow(UserTable.TOKEN_EXPIRE))
            )
        }
        cursor.close()
        return user
    }

    fun updateUser(user: User): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(UserTable.NAME, user.username)
            put(UserTable.HASHED_PASSWORD, user.hashedPassword)
            put(UserTable.EMAIL, user.email)
            put(UserTable.CREATED_AT, user.createdAt)
            put(UserTable.ROLE_ID, user.roleId)
            put(UserTable.STATUS_ID, user.statusId)
            put(UserTable.LAST_LOGIN, user.lastLogin)
            put(UserTable.POINTS, user.points)
            put(UserTable.SOUND_ENABLED, user.soundEnabled)
            put(UserTable.VIBRATION_ENABLED, user.vibrationEnabled)
            put(UserTable.DARK_MODE, user.darkMode)
            put(UserTable.PRIVATE, user.private)
            put(UserTable.LOCATION, user.location)
            put(UserTable.PHONE, user.phone)
            put(UserTable.IMAGE, user.imageId)
            put(UserTable.UPDATED_AT, user.updatedAt)
            put(UserTable.TOKEN, user.token)
            put(UserTable.TOKEN_EXPIRE, user.tokenExpire)
        }
        return db.update(UserTable.TABLE_NAME, values, "${UserTable.ID} = ?", arrayOf(user.id.toString()))
    }

    fun deleteUser(userId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(UserTable.TABLE_NAME, "${UserTable.ID} = ?", arrayOf(userId.toString()))
    }
}