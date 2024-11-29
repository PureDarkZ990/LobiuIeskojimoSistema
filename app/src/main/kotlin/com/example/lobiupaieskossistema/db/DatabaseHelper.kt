package com.example.lobiupaieskossistema

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.lobiupaieskossistema.database.*

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("PRAGMA foreign_keys=ON;")
        db.execSQL(RoleTable.CREATE_TABLE)
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
        onCreate(db)
    }

    companion object {
        private const val DATABASE_NAME = "treasure_hunt.db"
        private const val DATABASE_VERSION = 1
    }
}