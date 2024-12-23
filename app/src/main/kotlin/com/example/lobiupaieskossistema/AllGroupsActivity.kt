package com.example.lobiupaieskossistema

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import com.example.lobiupaieskossistema.dao.GroupDAO
import com.example.lobiupaieskossistema.models.Group

class AllGroupsActivity : AppCompatActivity() {

    private lateinit var groupListView: ListView
    private lateinit var addGroupButton: Button
    private lateinit var groupDAO: GroupDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_groups)

        groupListView = findViewById(R.id.groupListView)
        addGroupButton = findViewById(R.id.addGroupButton)
        groupDAO = GroupDAO(this)

        // Fetch all groups
        val groups = groupDAO.getAllGroups()
        val adapter = GroupAdapter(this, groups) // Custom adapter to display group data
        groupListView.adapter = adapter

        // Handle the click event to create a new group
        addGroupButton.setOnClickListener {
            val intent = Intent(this, CreateGroupActivity::class.java)
            startActivity(intent)
        }

        // Handle the click event to view/edit a specific group
        groupListView.setOnItemClickListener { _, _, position, _ ->
            val group = groups[position]
            val intent = Intent(this, EditGroupActivity::class.java)
            intent.putExtra("groupId", group.id)
            startActivity(intent)
        }
    }
}