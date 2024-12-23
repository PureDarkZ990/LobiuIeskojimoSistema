package com.example.lobiupaieskossistema

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class CreateGroupActivity : AppCompatActivity() {

    private lateinit var groupNameEditText: EditText
    private lateinit var groupDescriptionEditText: EditText
    private lateinit var createButton: Button
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        // Initialize views and database helper
        dbHelper = DatabaseHelper(this)
        groupNameEditText = findViewById(R.id.groupNameEditText)
        groupDescriptionEditText = findViewById(R.id.groupDescriptionEditText)
        createButton = findViewById(R.id.createButton)

        // Set up the button click listener
        createButton.setOnClickListener {
            val groupName = groupNameEditText.text.toString().trim()
            val groupDescription = groupDescriptionEditText.text.toString().trim()

            if (groupName.isNotEmpty()) {
                // Create the new group
                createNewGroup(groupName, groupDescription)
            }
        }
    }

    // Create a new group and save it to the database
    private fun createNewGroup(groupName: String, groupDescription: String) {
        val success = dbHelper.createGroup(groupName, groupDescription)

        val value = success.toInt();
        if (value != 0) {
            println("Group created successfully ${dbHelper.getAllGroups()}")
            // Return to GroupsActivity
            finish()
        } else {
            // Show an error message (Toast, Snackbar, etc.)
        }
    }
}