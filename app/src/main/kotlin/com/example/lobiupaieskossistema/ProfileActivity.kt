package com.example.lobiupaieskossistema

import android.app.Dialog
import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.lobiupaieskossistema.utils.SessionManager

class ProfileActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.profile)

        // Initialize session manager and database helper
        sessionManager = SessionManager(this)
        databaseHelper = DatabaseHelper(this)

        // Redirect to login if not logged in
        if (!sessionManager.isLoggedIn()) {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }

        val dropdownMenuButton: ImageButton = findViewById(R.id.dropdownMenuButton)

        // Handle dropdown menu click
        dropdownMenuButton.setOnClickListener {
            showPopupMenu(it)
        }

        // Load user details from database
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val userId = sessionManager.getUserId()
        val db = databaseHelper.readableDatabase
        val cursor = db.query(
            "users",
            arrayOf("username", "bio"),
            "id = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            val username = cursor.getString(cursor.getColumnIndexOrThrow("username"))
            val bio = cursor.getString(cursor.getColumnIndexOrThrow("bio")) ?: "No bio available."

            findViewById<TextView>(R.id.userName).text = username
            findViewById<TextView>(R.id.bio).text = bio
            findViewById<ImageView>(R.id.profileImage).setImageResource(R.drawable.default_profile_image)
        }
        cursor.close()
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(this, view)
        popupMenu.menuInflater.inflate(R.menu.profile_menu, popupMenu.menu)

        popupMenu.setOnMenuItemClickListener { menuItem: MenuItem ->
            when (menuItem.itemId) {
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

    private fun showEditProfileDialog() {
        val dialogView = layoutInflater.inflate(R.layout.edit_profile, null)
        val dialog = Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val usernameField = dialogView.findViewById<EditText>(R.id.usernameField)
        val bioField = dialogView.findViewById<EditText>(R.id.bioField)
        val saveButton = dialogView.findViewById<Button>(R.id.saveButton)

        // Pre-fill current data
        usernameField.setText(findViewById<TextView>(R.id.userName).text.toString())
        bioField.setText(findViewById<TextView>(R.id.bio).text.toString())

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
}
