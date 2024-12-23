package com.example.lobiupaieskossistema

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.lobiupaieskossistema.dao.GroupDAO
import com.example.lobiupaieskossistema.models.Group
import java.text.SimpleDateFormat
import java.util.*

class CreateGroupActivity : AppCompatActivity() {

    private lateinit var groupNameEditText: EditText
    private lateinit var groupDescriptionEditText: EditText
    private lateinit var saveGroupButton: Button
    private lateinit var groupDAO: GroupDAO

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_group)

        groupNameEditText = findViewById(R.id.groupNameEditText)
        groupDescriptionEditText = findViewById(R.id.groupDescriptionEditText)
        saveGroupButton = findViewById(R.id.saveGroupButton)
        groupDAO = GroupDAO(this)

        saveGroupButton.setOnClickListener {
            val groupName = groupNameEditText.text.toString()
            val groupDescription = groupDescriptionEditText.text.toString()
            val userId = 1 // Assuming user ID is 1 for this example (use the logged-in user's ID in a real app)
            val createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
            val updatedAt = createdAt

            val newGroup = Group(
                id = 0, // ID will be auto-generated
                name = groupName,
                description = groupDescription,
                activity = "",
                xActivity = 0.0,
                yActivity = 0.0,
                totalFoundCaches = 0,
                creatorId = userId,
                createdAt = createdAt,
                updatedAt = updatedAt
            )

            groupDAO.addGroup(newGroup)
            finish() // Close the activity after saving the group
        }
    }
}