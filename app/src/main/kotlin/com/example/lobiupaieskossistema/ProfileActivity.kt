package com.example.lobiupaieskossistema

import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.lobiupaieskossistema.database.CacheTable
import com.example.lobiupaieskossistema.utils.SessionManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.TileOverlayOptions
import com.google.maps.android.heatmaps.HeatmapTileProvider
import java.io.File
import java.io.FileOutputStream

class ProfileActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var sessionManager: SessionManager
    private lateinit var databaseHelper: DatabaseHelper
    private var selectedImageUri: Uri? = null
    private lateinit var profileImageView: ImageView
    private var currentDialogImageView: ImageView? = null
    private lateinit var googleMap: GoogleMap
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.profile)

        sessionManager = SessionManager(this)
        databaseHelper = DatabaseHelper(this)

        if (!sessionManager.isLoggedIn()) {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }

        mapView = findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        profileImageView = findViewById(R.id.profileImage)
        findViewById<ImageButton>(R.id.dropdownMenuButton).setOnClickListener { showPopupMenu(it) }

        loadUserProfile()
    }

    private fun openImagePickerForDialog(imageView: ImageView) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
        this.currentDialogImageView = imageView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            val imageUri = data?.data
            if (imageUri != null) {
                selectedImageUri = imageUri
                saveImageToInternalStorage(imageUri)
            }
        }
    }

    private fun saveImageToInternalStorage(imageUri: Uri) {
        val userId = sessionManager.getUserId()
        val file = File(filesDir, "profile-images/$userId.png")
        file.parentFile?.mkdirs() // Create directory if it doesn’t exist

        try {
            val inputStream = contentResolver.openInputStream(imageUri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()

            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

            updateImagePathInDatabase(userId, file.absolutePath)
            profileImageView.setImageBitmap(bitmap)
        } catch (e: Exception) {
            Log.e("ProfileActivity", "Error saving image: ${e.message}", e)
        }
    }

    private fun updateImagePathInDatabase(userId: Int, path: String) {
        val db = databaseHelper.writableDatabase
        val values = ContentValues().apply {
            put("path", path)
        }

        // Insert or update image path in the "images" table
        val cursor = db.query("images", arrayOf("id"), "id = ?", arrayOf(userId.toString()), null, null, null)
        if (cursor.moveToFirst()) {
            db.update("images", values, "id = ?", arrayOf(userId.toString()))
        } else {
            values.put("id", userId)
            db.insert("images", null, values)
        }
        cursor.close()
        db.close()
    }

    private fun loadUserProfile() {
        val userId = sessionManager.getUserId()
        val db = databaseHelper.readableDatabase

        val cursor = db.query(
            "users",
            arrayOf("username", "bio", "last_login", "email"),
            "id = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            val username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
            val bio = cursor.getString(cursor.getColumnIndexOrThrow("bio"))
            val lastLogin = cursor.getString(cursor.getColumnIndexOrThrow("last_login"))
            val email = cursor.getString(cursor.getColumnIndexOrThrow("email"))

            findViewById<TextView>(R.id.userName).text = username
            findViewById<TextView>(R.id.bio).text = bio
            findViewById<TextView>(R.id.textView1).text = lastLogin
            findViewById<TextView>(R.id.textView2).text = email

            val imagePath = File(filesDir, "profile-images/$userId.png")
            if (imagePath.exists()) {
                val bitmap = BitmapFactory.decodeFile(imagePath.absolutePath)
                profileImageView.setImageBitmap(bitmap)
            } else {
                profileImageView.setImageResource(R.drawable.default_profile_image)
            }
        }

        cursor.close()
        db.close()
        val mapView = findViewById<MapView>(R.id.mapView)
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.profile_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menu_friends -> {
                    val intent = Intent(this, FriendshipActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.menu_edit_profile -> {
                    showEditProfileDialog()
                    true
                }
                R.id.menu_return_to_map -> {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                R.id.menu_settings -> {
                    showSettingsDialog()
                    true
                }
                R.id.menu_logout -> {
                    sessionManager.logout()
                    val intent = Intent(this, LogInActivity::class.java)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> false
            }
        }
        popupMenu.show()
    }

    private fun showSettingsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.settings_change, null)
        val dialog = Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.findViewById<Button>(R.id.deleteAccountButton).setOnClickListener {
            showDeleteAccountConfirmationDialog(dialog)
        }

        dialog.findViewById<Button>(R.id.cancelButton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showDeleteAccountConfirmationDialog(settingsDialog: Dialog) {
        val confirmationDialog = Dialog(this)
        val dialogView = layoutInflater.inflate(R.layout.deletion_confirmation, null)
        confirmationDialog.setContentView(dialogView)
        confirmationDialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val confirmButton = dialogView.findViewById<Button>(R.id.confirmDeletionButton)
        val cancelButton = dialogView.findViewById<Button>(R.id.cancelButton)

        confirmButton.setOnClickListener {
            deleteAccount()
            confirmationDialog.dismiss()
            settingsDialog.dismiss()
        }

        cancelButton.setOnClickListener {
            confirmationDialog.dismiss()
        }

        confirmationDialog.show()
    }

    private fun deleteAccount() {
        val userId = sessionManager.getUserId()
        val db = databaseHelper.writableDatabase
        db.delete("users", "id=?", arrayOf(userId.toString()))
        db.close()

        sessionManager.logout()
        val intent = Intent(this, LogInActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun showEditProfileDialog() {
        val dialogView = layoutInflater.inflate(R.layout.edit_profile, null)
        val dialog = Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val usernameField = dialogView.findViewById<EditText>(R.id.usernameField)
        val bioField = dialogView.findViewById<EditText>(R.id.bioField)
        val saveButton = dialogView.findViewById<Button>(R.id.saveButton)
        val uploadImageButton = dialogView.findViewById<Button>(R.id.uploadImageButton)
        val dialogProfileImageView = dialogView.findViewById<ImageView>(R.id.profileImage)

        usernameField.setText(findViewById<TextView>(R.id.userName).text.toString())
        bioField.setText(findViewById<TextView>(R.id.bio).text.toString())

        uploadImageButton.setOnClickListener {
            openImagePickerForDialog(dialogProfileImageView)
        }

        saveButton.setOnClickListener {
            val userId = sessionManager.getUserId()
            val newUsername = usernameField.text.toString()
            val newBio = bioField.text.toString()

            updateUserProfile(userId, newUsername, newBio)

            findViewById<TextView>(R.id.userName).text = newUsername
            findViewById<TextView>(R.id.bio).text = newBio
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun updateUserProfile(userId: Int, username: String, bio: String) {
        val db = databaseHelper.writableDatabase
        val values = ContentValues().apply {
            put("username", username)
            put("bio", bio)
        }
        db.update("users", values, "id = ?", arrayOf(userId.toString()))
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        setupMap()
        loadHeatmap()
    }

    private fun setupMap() {
        googleMap.uiSettings.isZoomControlsEnabled = true
        googleMap.uiSettings.isMyLocationButtonEnabled = true

        val defaultLocation = LatLng(54.6872, 25.2797)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))
    }

    private fun loadHeatmap() {
        val db = databaseHelper.readableDatabase
        val cursor = db.rawQuery("SELECT xcoordinate, ycoordinate, difficulty FROM ${CacheTable.TABLE_NAME}", null)
        val weightedLocations = mutableListOf<com.google.maps.android.heatmaps.WeightedLatLng>()

        while (cursor.moveToNext()) {
            val latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("xcoordinate"))
            val longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("ycoordinate"))
            val difficulty = cursor.getDouble(cursor.getColumnIndexOrThrow("difficulty"))

            val weight = calculateWeight(difficulty)
            weightedLocations.add(com.google.maps.android.heatmaps.WeightedLatLng(LatLng(latitude, longitude), weight))
        }
        cursor.close()

        val heatmapProvider = HeatmapTileProvider.Builder()
            .weightedData(weightedLocations)
            .build()

        googleMap.addTileOverlay(TileOverlayOptions().tileProvider(heatmapProvider))
    }

    private fun calculateWeight(difficulty: Double): Double {
        return difficulty / 5.0
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

}
