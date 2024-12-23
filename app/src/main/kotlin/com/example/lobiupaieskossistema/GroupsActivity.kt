package com.example.lobiupaieskossistema

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.SimpleCursorAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.lobiupaieskossistema.database.GroupTable
import kotlinx.coroutines.*

class GroupsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var userGroupList: ListView
    private lateinit var allGroupList: ListView
    private lateinit var createGroupButton: Button
    private var loggedInUserId = 1 // Example logged-in user ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.groups)

        // Initialize the database helper and views
        dbHelper = DatabaseHelper(this)
        userGroupList = findViewById(R.id.userGroupList)
        allGroupList = findViewById(R.id.allGroupList)
        createGroupButton = findViewById(R.id.createGroupButton)

        // Load groups
        loadUserGroups()
        loadAllGroups()

        // Handle Create Group button click
        createGroupButton.setOnClickListener {
            openCreateGroupActivity()
        }

        // Handle user's group click
        userGroupList.setOnItemClickListener { _, _, position, id ->
            val intent = Intent(this, GroupDetailsActivity::class.java)
            intent.putExtra("groupId", id.toInt())
            intent.putExtra("isCreator", true) // Assume creator status
            startActivity(intent)
        }
    }

    // Open activity to create a new group
    private fun openCreateGroupActivity() {
        val intent = Intent(this, CreateGroupActivity::class.java)
        startActivity(intent)
    }

    // Load the user's groups (with async query)
    private fun loadUserGroups() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val cursor = withContext(Dispatchers.IO) {
                    dbHelper.getUserGroups(loggedInUserId)
                }
                if (cursor != null && cursor.count > 0) {
                    val adapter = SimpleCursorAdapter(
                        this@GroupsActivity,
                        android.R.layout.simple_list_item_2,
                        cursor,
                        arrayOf(GroupTable.ID, GroupTable.NAME),
                        intArrayOf(android.R.id.text1, android.R.id.text2),
                        0
                    )
                    userGroupList.adapter = adapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // Load all available groups (with async query)
    private fun loadAllGroups() {
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val cursor = withContext(Dispatchers.IO) {
                    dbHelper.getAllGroups()
                }
                if (cursor != null && cursor.count > 0) {
                    val adapter = SimpleCursorAdapter(
                        this@GroupsActivity,
                        android.R.layout.simple_list_item_2,
                        cursor,
                        arrayOf(GroupTable.ID, GroupTable.NAME),
                        intArrayOf(android.R.id.text1, android.R.id.text2),
                        0
                    )
                    allGroupList.adapter = adapter
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}