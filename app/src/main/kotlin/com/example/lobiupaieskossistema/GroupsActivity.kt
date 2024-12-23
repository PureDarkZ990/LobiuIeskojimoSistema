/*package com.example.lobiupaieskossistema

import android.os.Bundle
import android.widget.ListView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.lobiupaieskossistema.dao.GroupDAO
import com.example.lobiupaieskossistema.models.Group

class GroupsActivity : AppCompatActivity() {

    private lateinit var groupListView: ListView
    private lateinit var createGroupButton: Button
    private lateinit var groupDAO: GroupDAO

    private val loggedInUserId = 1 // For example, you should retrieve this from shared preferences or session

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.groups)

        groupListView = findViewById(R.id.groupListView)
        createGroupButton = findViewById(R.id.createGroupButton)

        // Initialize the DAO to interact with the database
        groupDAO = GroupDAO(this)

        // Load and display the list of groups
        loadGroupList()

        // Set a listener for the "Create New Group" button
        createGroupButton.setOnClickListener {
            // TODO: Start the CreateGroupActivity
            // Example:
            // val intent = Intent(this, CreateGroupActivity::class.java)
            // startActivity(intent)
            Toast.makeText(this, "Create New Group Clicked", Toast.LENGTH_SHORT).show()
        }
    }

    // Method to load groups from the database and display them in the ListView
    private fun loadGroupList() {
        // Fetch groups from the database using GroupDAO
        val groups = groupDAO.getAllGroups()

        // Check if groups exist
        if (groups.isNotEmpty()) {
            // Create the custom adapter
            val adapter = GroupAdapter(this, groups, loggedInUserId)
            groupListView.adapter = adapter
        } else {
            // Show a message if there are no groups
            Toast.makeText(this, "No groups found", Toast.LENGTH_SHORT).show()
        }
    }

}*/