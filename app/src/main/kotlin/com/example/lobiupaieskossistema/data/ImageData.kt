package com.example.lobiupaieskossistema.data

import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import com.example.lobiupaieskossistema.dao.ImageDAO
import com.example.lobiupaieskossistema.models.Image

object ImageData {
    private lateinit var imageDAO: ImageDAO

    fun initialize(context: Context) {
        imageDAO = ImageDAO(context)
        addHardcodedEntries()
    }

    private fun addHardcodedEntries() {
        val imageList = listOf(
            Image(0, "image1", 0,null),
        )
        imageList.forEach { imageDAO.addImage(it) }
    }

    fun get(id: Int): Image? {
        return imageDAO.findImageById(id)
    }

    fun update(image: Image) {
        imageDAO.updateImage(image)
    }

    fun delete(id: Int) {
        imageDAO.deleteImage(id)
    }

    fun add(image: Image) {
        imageDAO.addImage(image)
    }

    fun getAll(): List<Image> {
        return imageDAO.getAllImages()
    }

    fun saveImageToInternalStorage(context: Context, bitmap: Bitmap, directoryName: String, imageName: String): String? {
        val directory = File(context.filesDir, directoryName)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, "$imageName.png")
        var fileOutputStream: FileOutputStream? = null
        return try {
            fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            fileOutputStream?.close()
        }
    }

    fun saveImageToExternalStorage(context: Context, bitmap: Bitmap, directoryName: String, imageName: String): String? {
        val directory = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), directoryName)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, "$imageName.png")
        var fileOutputStream: FileOutputStream? = null
        return try {
            fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            fileOutputStream?.close()
        }
    }
}