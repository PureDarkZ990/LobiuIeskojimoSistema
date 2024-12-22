package com.example.lobiupaieskossistema

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.lobiupaieskossistema.utils.SessionManager

class ProfileActivity : AppCompatActivity() {
    private lateinit var sessionManager: SessionManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.profile)

        val logoutButton: Button = findViewById(R.id.logoutButton)
        val settingsButton: Button = findViewById(R.id.settingsButton)
        val editButton: Button = findViewById(R.id.EditButton) // New Edit button
        val returnToMapButton: Button = findViewById(R.id.returnToMap) // Return to Map button

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn()) {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }


        logoutButton.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        settingsButton.setOnClickListener {
            showSettingsDialog()
        }

        editButton.setOnClickListener {
            showEditProfileDialog()
        }

        returnToMapButton.setOnClickListener {
            returnToMap()
        }
    }

    private fun returnToMap() {
        val intent = Intent(this, MainActivity::class.java) // Replace with your MainActivity class
        startActivity(intent)
        finish()
    }

    private fun showLogoutConfirmationDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.logout_confirmation, null)

        val dialog = Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val confirmButton: Button = dialog.findViewById(R.id.confirmButton)
        val cancelButton: Button = dialog.findViewById(R.id.cancelButton)
        confirmButton.setOnClickListener {
            sessionManager.logout()
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
            dialog.dismiss()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showSettingsDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.settings_change, null)

        val dialog = Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val okButton: Button = dialog.findViewById(R.id.cancelButton)
        val deleteAccountButton: Button = dialog.findViewById(R.id.deleteAccountButton)

        okButton.setOnClickListener {
            dialog.dismiss()
        }

        deleteAccountButton.setOnClickListener {
            showDeleteAccountConfirmationDialog()
        }

        dialog.show()
    }

    private fun showDeleteAccountConfirmationDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.deletion_confirmation, null)

        val dialog = Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val confirmButton: Button = dialog.findViewById(R.id.confirmDeletionButton)
        val cancelButton: Button = dialog.findViewById(R.id.cancelButton)

        confirmButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
            dialog.dismiss()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun showEditProfileDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.edit_profile, null)

        val dialog = Dialog(this)
        dialog.setContentView(dialogView)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val profileImage: ImageView = dialog.findViewById(R.id.profileImage)
        val uploadImageButton: Button = dialog.findViewById(R.id.uploadImageButton)
        val usernameField: EditText = dialog.findViewById(R.id.usernameField)
        val bioField: EditText = dialog.findViewById(R.id.bioField)

        val saveButton: Button = dialog.findViewById(R.id.saveButton)
        val cancelButton: Button = dialog.findViewById(R.id.cancelButton)

        saveButton.setOnClickListener {
            dialog.dismiss()
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
}
