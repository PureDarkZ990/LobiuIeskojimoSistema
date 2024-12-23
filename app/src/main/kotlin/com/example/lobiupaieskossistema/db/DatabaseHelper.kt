package com.example.lobiupaieskossistema

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.lobiupaieskossistema.database.*
import android.database.Cursor

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("PRAGMA foreign_keys=ON;")
        db.execSQL(RoleTable.CREATE_TABLE)
        db.execSQL("INSERT INTO ${RoleTable.TABLE_NAME} (${RoleTable.NAME}) VALUES ('admin')")
        db.execSQL("INSERT INTO ${RoleTable.TABLE_NAME} (${RoleTable.NAME}) VALUES ('regular')")
        db.execSQL(StatusTable.CREATE_TABLE)
        db.execSQL(NotificationTable.CREATE_TABLE)
        db.execSQL(ThemeTable.CREATE_TABLE)
        db.execSQL(UserTable.CREATE_TABLE)
        db.execSQL(ImageTable.CREATE_TABLE)
        db.execSQL(CacheTable.CREATE_TABLE)
        db.execSQL(CategoryTable.CREATE_TABLE)
        db.execSQL(GroupTable.CREATE_TABLE)
        db.execSQL(UserCacheTable.CREATE_TABLE)
        db.execSQL(NotificationUserTable.CREATE_TABLE)
        db.execSQL(CacheCategoryTable.CREATE_TABLE)
        db.execSQL(CacheGroupTable.CREATE_TABLE)
       db.execSQL(UserGroupTable.CREATE_TABLE)
        db.execSQL(FriendshipTable.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${UserTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${UserCacheTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${NotificationUserTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${CacheCategoryTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${CacheGroupTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${ImageTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${CacheTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${CategoryTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${GroupTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${RoleTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${StatusTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${NotificationTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${ThemeTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${UserGroupTable.TABLE_NAME}")
        db.execSQL("DROP TABLE IF EXISTS ${FriendshipTable.TABLE_NAME}")
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "treasure_hunt.db"
        private const val DATABASE_VERSION = 4
    }


    fun getAllGroups(): Cursor {
        val db = readableDatabase
        return db.query(
            GroupTable.TABLE_NAME,
            arrayOf(GroupTable.ID, GroupTable.NAME),
            null, null, null, null, null
        )
    }

    // Method to get groups for a specific user
    fun getUserGroups(userId: Int): Cursor {
        val db = readableDatabase
        val selection = "${GroupTable.CREATOR_ID} = ?"
        val selectionArgs = arrayOf(userId.toString())
        return db.query(
            GroupTable.TABLE_NAME,
            arrayOf(GroupTable.ID, GroupTable.NAME),
            selection,
            selectionArgs,
            null,
            null,
            null
        )
    }
    fun getGroupDetails(groupId: Int): Cursor {
        val db = readableDatabase
        val selection = "${GroupTable.ID} = ?"
        val selectionArgs = arrayOf(groupId.toString())

        // Query the database to get the group details
        return db.query(
            GroupTable.TABLE_NAME,
            arrayOf(GroupTable.ID, GroupTable.NAME, GroupTable.DESCRIPTION),
            selection,
            selectionArgs,
            null,
            null,
            null
        )
    }

    fun updateGroup(groupId: Int, newGroupName: String, newGroupDescription: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(GroupTable.NAME, newGroupName)
            put(GroupTable.DESCRIPTION, newGroupDescription)
        }

        // Update the group in the database and return the number of rows affected
        val selection = "${GroupTable.ID} = ?"
        val selectionArgs = arrayOf(groupId.toString())
        return db.update(GroupTable.TABLE_NAME, values, selection, selectionArgs)
    }

    // Method to create a new group
    fun createGroup(groupName: String, groupDescription: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(GroupTable.NAME, groupName)
            put(GroupTable.DESCRIPTION, groupDescription)
            put(GroupTable.CREATOR_ID, 1) // Assuming the logged-in user has ID 1 for simplicity
        }

        // Insert the new group into the database and return the new row's ID
        val index = db.insert(GroupTable.TABLE_NAME, null, values)
        if(index==-1L) {
            println("NOT ADEDED")
        }else{
            println("ADDED ${index}")
        }
        return index
    }
}