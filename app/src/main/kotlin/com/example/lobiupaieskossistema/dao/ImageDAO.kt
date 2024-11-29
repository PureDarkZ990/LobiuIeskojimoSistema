package com.example.lobiupaieskossistema.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.lobiupaieskossistema.DatabaseHelper
import com.example.lobiupaieskossistema.models.Image
import com.example.lobiupaieskossistema.database.ImageTable

class ImageDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun addImage(image: Image): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(ImageTable.PATH, image.path)
            put(ImageTable.CACHE_ID, image.cacheId)
            put(ImageTable.DESCRIPTION, image.description)
        }
        return db.insert(ImageTable.TABLE_NAME, null, values)
    }

    fun getAllImages(): List<Image> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            ImageTable.TABLE_NAME,
            null, null, null, null, null, null
        )
        val images = mutableListOf<Image>()
        with(cursor) {
            while (moveToNext()) {
                val image = Image(
                    id = getInt(getColumnIndexOrThrow(ImageTable.ID)),
                    path = getString(getColumnIndexOrThrow(ImageTable.PATH)),
                    cacheId = getInt(getColumnIndexOrThrow(ImageTable.CACHE_ID)),
                    description = getString(getColumnIndexOrThrow(ImageTable.DESCRIPTION))
                )
                images.add(image)
            }
        }
        cursor.close()
        return images
    }
    fun findImageById(imageId: Int): Image? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            ImageTable.TABLE_NAME,
            null,
            "${ImageTable.ID} = ?",
            arrayOf(imageId.toString()),
            null, null, null
        )
        var image: Image? = null
        if (cursor.moveToFirst()) {
            image = Image(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(ImageTable.ID)),
                path = cursor.getString(cursor.getColumnIndexOrThrow(ImageTable.PATH)),
                cacheId = cursor.getInt(cursor.getColumnIndexOrThrow(ImageTable.CACHE_ID)),
                description = cursor.getString(cursor.getColumnIndexOrThrow(ImageTable.DESCRIPTION))
            )
        }
        cursor.close()
        return image
    }
    fun updateImage(image: Image): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(ImageTable.PATH, image.path)
            put(ImageTable.CACHE_ID, image.cacheId)
            put(ImageTable.DESCRIPTION, image.description)
        }
        return db.update(ImageTable.TABLE_NAME, values, "${ImageTable.ID} = ?", arrayOf(image.id.toString()))
    }

    fun deleteImage(imageId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(ImageTable.TABLE_NAME, "${ImageTable.ID} = ?", arrayOf(imageId.toString()))
    }
}