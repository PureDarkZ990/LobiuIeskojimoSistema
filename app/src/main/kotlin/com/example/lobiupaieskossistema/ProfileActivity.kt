package com.example.lobiupaieskossistema

import android.app.Dialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import android.widget.CheckBox
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.example.lobiupaieskossistema.database.CacheTable
import com.example.lobiupaieskossistema.database.UserTable
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

        sessionManager = SessionManager(this)
        databaseHelper = DatabaseHelper(this)

        applyDarkModePreference()
        setContentView(R.layout.profile)
        supportActionBar?.hide()

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

    private fun applyDarkModePreference() {
        val userId = sessionManager.getUserId()
        val db = databaseHelper.readableDatabase

        val cursor = db.query(
            "users",
            arrayOf("dark_mode"),
            "id = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            val darkModeEnabled = cursor.getInt(cursor.getColumnIndexOrThrow("dark_mode")) == 1
            if (darkModeEnabled) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
        cursor.close()
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

                currentDialogImageView?.setImageURI(imageUri)
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

        // Query to get user details
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

            // Query to count the number of found caches for the user
            val foundCursor = db.rawQuery(
                "SELECT COUNT(*) FROM user_caches WHERE user_id = ? AND found = 1",
                arrayOf(userId.toString())
            )

            val amountFound = if (foundCursor.moveToFirst()) {
                foundCursor.getInt(0) // Get the count from the first column
            } else {
                0
            }
            foundCursor.close()

            // Update the UI
            findViewById<TextView>(R.id.userName).text = username
            findViewById<TextView>(R.id.bio).text = bio
            findViewById<TextView>(R.id.textView3).text = "Last seen: $lastLogin"
            findViewById<TextView>(R.id.textView2).text = "Email: $email"
            findViewById<TextView>(R.id.textView1).text = "Found: $amountFound"

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
                R.id.menu_test_notification -> {
                    testNotification()
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

    private fun testNotification() {
        val notificationMessage = "This is a test notification message."
        val notificationDate = "2024-12-23"

        val db = databaseHelper.writableDatabase

        // Insert notification into the notifications table
        val notificationValues = ContentValues().apply {
            put("message", notificationMessage)
            put("date", notificationDate)
        }
        val notificationId = db.insert("notifications", null, notificationValues)

        // Link the notification to the current user in the notification_users table
        val userId = sessionManager.getUserId()
        val userNotificationValues = ContentValues().apply {
            put("notification_id", notificationId.toInt())
            put("user_id", userId)
            put("read", 0) // Unread
        }
        db.insert("notification_users", null, userNotificationValues)

        // Fetch user preferences for sound and vibration
        val cursor = db.query(
            UserTable.TABLE_NAME,
            arrayOf(UserTable.SOUND_ENABLED, UserTable.VIBRATION_ENABLED),
            "${UserTable.ID} = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )

        var soundEnabled = false
        var vibrationEnabled = false
        if (cursor.moveToFirst()) {
            soundEnabled = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.SOUND_ENABLED)) == 1
            vibrationEnabled = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.VIBRATION_ENABLED)) == 1
        }
        cursor.close()
        db.close()

        // Play sound and vibrate if enabled
        if (soundEnabled) {
            playSound()
        }
        if (vibrationEnabled) {
            triggerVibration()
        }

        // Display the notification dialog
        val dialog = Dialog(this)
        val dialogView = layoutInflater.inflate(R.layout.notification, null)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val messageView = dialogView.findViewById<TextView>(R.id.notificationMessage)
        val okButton = dialogView.findViewById<Button>(R.id.okButton)

        messageView.text = notificationMessage
        okButton.setOnClickListener { dialog.dismiss() }

        dialog.show()
    }




    private fun playSound() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.notification_sound)
        mediaPlayer.setOnCompletionListener { it.release() }
        mediaPlayer.start()
    }
    private fun triggerVibration() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(500) // Fallback for older versions
            }
        }
    }



    private fun showSettingsDialog() {
        val dialogView = layoutInflater.inflate(R.layout.settings_change, null)
        val dialog = Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val darkModeCheckbox = dialogView.findViewById<CheckBox>(R.id.darkModeCheckbox)
        val vibrationCheckbox = dialogView.findViewById<CheckBox>(R.id.vibrationCheckbox)
        val soundCheckbox = dialogView.findViewById<CheckBox>(R.id.soundCheckbox)
        val userId = sessionManager.getUserId()
        val db = databaseHelper.readableDatabase

        // Load current dark mode setting
        val cursor = db.query(
            "users",
            arrayOf("dark_mode","sound_enabled", "vibration_enabled"),
            "id = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            darkModeCheckbox.isChecked = cursor.getInt(cursor.getColumnIndexOrThrow("dark_mode")) == 1
            vibrationCheckbox.isChecked = cursor.getInt(cursor.getColumnIndexOrThrow("sound_enabled")) == 1
            soundCheckbox.isChecked = cursor.getInt(cursor.getColumnIndexOrThrow("vibration_enabled")) == 1
        }
        cursor.close()

        dialog.findViewById<Button>(R.id.cancelButton).setOnClickListener {
            val isDarkModeEnabled = darkModeCheckbox.isChecked
            val isVibrationEnabled = vibrationCheckbox.isChecked
            val isSoundEnabled = soundCheckbox.isChecked

            val values = ContentValues().apply {
                put("dark_mode", if (isDarkModeEnabled) 1 else 0)
                put("sound_enabled", if (isSoundEnabled) 1 else 0)
                put("vibration_enabled", if (isVibrationEnabled) 1 else 0)
            }

            db.update("users", values, "id = ?", arrayOf(userId.toString()))
            Toast.makeText(this, "Settings updated successfully", Toast.LENGTH_SHORT).show()

            if (isDarkModeEnabled) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            dialog.dismiss()
        }

        dialog.findViewById<Button>(R.id.deleteAccountButton).setOnClickListener {
            showDeleteAccountConfirmationDialog(dialog)
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

        // Load the current profile image into the dialog's ImageView
        val userId = sessionManager.getUserId()
        val imagePath = File(filesDir, "profile-images/$userId.png")
        if (imagePath.exists()) {
            val bitmap = BitmapFactory.decodeFile(imagePath.absolutePath)
            dialogProfileImageView.setImageBitmap(bitmap)
        } else {
            dialogProfileImageView.setImageResource(R.drawable.default_profile_image)
        }

        // Handle image upload
        uploadImageButton.setOnClickListener {
            openImagePickerForDialog(dialogProfileImageView)
        }

        saveButton.setOnClickListener {
            val newUsername = usernameField.text.toString()
            val newBio = bioField.text.toString()

            updateUserProfile(userId, newUsername, newBio)

            findViewById<TextView>(R.id.userName).text = newUsername
            findViewById<TextView>(R.id.bio).text = newBio

            // Update the main profile image after saving changes
            if (imagePath.exists()) {
                val updatedBitmap = BitmapFactory.decodeFile(imagePath.absolutePath)
                profileImageView.setImageBitmap(updatedBitmap)
            }

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

        val defaultLocation = LatLng(54.8985, 23.9036)
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))
    }
    /*
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
        }*/


    private fun loadHeatmap() {
        val userId = sessionManager.getUserId()
        val db = databaseHelper.readableDatabase

        // Query to fetch only the caches found by the user
        val cursor = db.rawQuery(
            """
    SELECT c.xcoordinate, c.ycoordinate, c.difficulty
    FROM ${CacheTable.TABLE_NAME} AS c
    INNER JOIN user_caches AS uc ON c.id = uc.cache_id
    WHERE uc.user_id = ? AND uc.found = 1
    """,
            arrayOf(userId.toString())
        )

        val weightedLocations = mutableListOf<com.google.maps.android.heatmaps.WeightedLatLng>()

        while (cursor.moveToNext()) {
            val latitude = cursor.getDouble(cursor.getColumnIndexOrThrow("xcoordinate"))
            val longitude = cursor.getDouble(cursor.getColumnIndexOrThrow("ycoordinate"))
            val difficulty = cursor.getDouble(cursor.getColumnIndexOrThrow("difficulty"))

            // Calculate weight based on difficulty
            val weight = calculateWeight(difficulty)
            weightedLocations.add(com.google.maps.android.heatmaps.WeightedLatLng(LatLng(latitude, longitude), weight))
        }
        cursor.close()

        // Handle case where there are no found treasures
        if (weightedLocations.isEmpty()) {
            // Display a message and return without adding a heatmap
            Toast.makeText(this, "No found treasures to display on the heatmap.", Toast.LENGTH_SHORT).show()
            return
        }

        // Create and add the heatmap to the map
        val heatmapProvider = HeatmapTileProvider.Builder()
            .weightedData(weightedLocations) // Pass filtered and weighted locations
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