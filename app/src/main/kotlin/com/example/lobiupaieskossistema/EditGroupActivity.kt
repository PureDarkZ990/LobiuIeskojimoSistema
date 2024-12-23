package com.example.lobiupaieskossistema

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lobiupaieskossistema.dao.GroupDAO
import com.example.lobiupaieskossistema.models.Group

class EditGroupActivity : AppCompatActivity() {

    private lateinit var groupDAO: GroupDAO
    private lateinit var groupNameEditText: EditText
    private lateinit var groupDescriptionEditText: EditText
    private lateinit var saveGroupButton: Button
    private lateinit var deleteGroupButton: Button
    private var groupId: Int = -1
    private var creatorId: Int = -1  // Store the creator ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_group)

        groupDAO = GroupDAO(this)

        // Initialize UI components
        groupNameEditText = findViewById(R.id.groupNameEditText)
        groupDescriptionEditText = findViewById(R.id.groupDescriptionEditText)
        saveGroupButton = findViewById(R.id.saveGroupButton)
        deleteGroupButton = findViewById(R.id.deleteGroupButton)

        // Get the group ID from the intent
        groupId = intent.getIntExtra("groupId", -1)

        // Load the group details if groupId is valid
        if (groupId != -1) {
            loadGroupDetails(groupId)
        }

        // Set click listeners
        saveGroupButton.setOnClickListener { saveGroup() }
        deleteGroupButton.setOnClickListener { deleteGroup() }
    }

    private fun loadGroupDetails(groupId: Int) {
        val group = groupDAO.findGroupById(groupId)

        if (group != null) {
            // Populate the EditText fields with the group details
            groupNameEditText.setText(group.name)
            groupDescriptionEditText.setText(group.description)

            // Set the creatorId
            creatorId = group.creatorId!!

            // Show the delete button only if the logged-in user is the creator
            val loggedInUserId = 1 // Replace with actual logic to get logged-in user ID
            if (creatorId == loggedInUserId) {
                deleteGroupButton.visibility = View.VISIBLE
            }
        } else {
            // Show an error if group is not found
            Toast.makeText(this, "Group not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveGroup() {
        val newName = groupNameEditText.text.toString()
        val newDescription = groupDescriptionEditText.text.toString()

        // Validate the input
        if (newName.isEmpty() || newDescription.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedGroup = Group(
            id = groupId,
            name = newName,
            description = newDescription,
            activity = "",  // Assuming activity is not updated here
            xActivity = 0.0,
            yActivity = 0.0,
            totalFoundCaches = 0,
            creatorId = creatorId,
            createdAt = "",  // Assuming you don't update createdAt here
            updatedAt = ""   // Assuming you set updatedAt at some later stage
        )

        // Update the group in the database
        val rowsUpdated = groupDAO.updateGroup(updatedGroup)

        if (rowsUpdated > 0) {
            Toast.makeText(this, "Group updated successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Failed to update group", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteGroup() {
        val rowsDeleted = groupDAO.deleteGroup(groupId)

        if (rowsDeleted > 0) {
            Toast.makeText(this, "Group deleted successfully", Toast.LENGTH_SHORT).show()
            finish()  // Close the activity
        } else {
            Toast.makeText(this, "Failed to delete group", Toast.LENGTH_SHORT).show()
        }
    }
}