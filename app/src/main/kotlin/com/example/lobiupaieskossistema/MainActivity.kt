package com.example.lobiupaieskossistema

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import java.io.InputStream

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.profile)

        val imageView: ImageView = findViewById(R.id.profileImage)

        try {
            val assetManager = assets
            val inputStream: InputStream = assetManager.open("default_profile_image.jpg")
            val bitmap: Bitmap = BitmapFactory.decodeStream(inputStream)
            imageView.setImageBitmap(bitmap)
        } catch (e: IOException) {
            e.printStackTrace() // Handle the exception if the image is not found
        }
    }
}