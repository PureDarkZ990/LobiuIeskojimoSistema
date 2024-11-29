package com.example.lobiupaieskossistema.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.lobiupaieskossistema.DatabaseHelper
import com.example.lobiupaieskossistema.models.Theme
import com.example.lobiupaieskossistema.database.ThemeTable

class ThemeDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun addTheme(theme: Theme): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(ThemeTable.DESCRIPTION, theme.description)
            put(ThemeTable.ABSOLUTE_TIME, theme.absoluteTime)
            put(ThemeTable.TIME, theme.time)
        }
        return db.insert(ThemeTable.TABLE_NAME, null, values)
    }

    fun getAllThemes(): List<Theme> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            ThemeTable.TABLE_NAME,
            null, null, null, null, null, null
        )
        val themes = mutableListOf<Theme>()
        with(cursor) {
            while (moveToNext()) {
                val theme = Theme(
                    id = getInt(getColumnIndexOrThrow(ThemeTable.ID)),
                    description = getString(getColumnIndexOrThrow(ThemeTable.DESCRIPTION)),
                    absoluteTime = getInt(getColumnIndexOrThrow(ThemeTable.ABSOLUTE_TIME)),
                    time = getInt(getColumnIndexOrThrow(ThemeTable.TIME))
                )
                themes.add(theme)
            }
        }
        cursor.close()
        return themes
    }
    fun findThemeById(themeId: Int): Theme? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            ThemeTable.TABLE_NAME,
            null,
            "${ThemeTable.ID} = ?",
            arrayOf(themeId.toString()),
            null, null, null
        )
        var theme: Theme? = null
        if (cursor.moveToFirst()) {
            theme = Theme(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(ThemeTable.ID)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(ThemeTable.DESCRIPTION)),
                absoluteTime = cursor.getInt(cursor.getColumnIndexOrThrow(ThemeTable.ABSOLUTE_TIME)),
                time = cursor.getInt(cursor.getColumnIndexOrThrow(ThemeTable.TIME))
            )
        }
        cursor.close()
        return theme
    }
    fun updateTheme(theme: Theme): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(ThemeTable.DESCRIPTION, theme.description)
            put(ThemeTable.ABSOLUTE_TIME, theme.absoluteTime)
            put(ThemeTable.TIME, theme.time)
        }
        return db.update(ThemeTable.TABLE_NAME, values, "${ThemeTable.ID} = ?", arrayOf(theme.id.toString()))
    }

    fun deleteTheme(themeId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(ThemeTable.TABLE_NAME, "${ThemeTable.ID} = ?", arrayOf(themeId.toString()))
    }
}