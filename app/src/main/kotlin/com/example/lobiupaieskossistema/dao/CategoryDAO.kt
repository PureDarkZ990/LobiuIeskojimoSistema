package com.example.lobiupaieskossistema.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.lobiupaieskossistema.DatabaseHelper
import com.example.lobiupaieskossistema.models.Category
import com.example.lobiupaieskossistema.database.CategoryTable

class CategoryDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun addCategory(category: Category): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(CategoryTable.NAME, category.name)
            put(CategoryTable.DESCRIPTION, category.description)
        }
        return db.insert(CategoryTable.TABLE_NAME, null, values)
    }
    
    fun getAllCategories(): List<Category> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            CategoryTable.TABLE_NAME,
            null, null, null, null, null, null
        )
        val categories = mutableListOf<Category>()
        with(cursor) {
            while (moveToNext()) {
                val category = Category(
                    id = getInt(getColumnIndexOrThrow(CategoryTable.ID)),
                    name = getString(getColumnIndexOrThrow(CategoryTable.NAME)),
                    description = getString(getColumnIndexOrThrow(CategoryTable.DESCRIPTION))
                )
                categories.add(category)
            }
        }
        cursor.close()
        return categories
    }
    fun findCategoryById(categoryId: Int): Category? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            CategoryTable.TABLE_NAME,
            null,
            "${CategoryTable.ID} = ?",
            arrayOf(categoryId.toString()),
            null, null, null
        )
        var category: Category? = null
        if (cursor.moveToFirst()) {
            category = Category(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(CategoryTable.ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.NAME)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(CategoryTable.DESCRIPTION))
            )
        }
        cursor.close()
        return category
    }

    fun updateCategory(category: Category): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(CategoryTable.NAME, category.name)
            put(CategoryTable.DESCRIPTION, category.description)
        }
        return db.update(CategoryTable.TABLE_NAME, values, "${CategoryTable.ID} = ?", arrayOf(category.id.toString()))
    }

    fun deleteCategory(categoryId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(CategoryTable.TABLE_NAME, "${CategoryTable.ID} = ?", arrayOf(categoryId.toString()))
    }
}