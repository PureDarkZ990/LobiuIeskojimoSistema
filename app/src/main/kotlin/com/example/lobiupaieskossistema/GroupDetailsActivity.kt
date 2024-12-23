package com.example.lobiupaieskossistema

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.lobiupaieskossistema.database.GroupTable

class GroupDetailsActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var groupName: EditText
    private lateinit var groupDescription: EditText
    private lateinit var saveButton: Button
    private var groupId: Int = 0
    private var isCreator: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.grout_list)

        dbHelper = DatabaseHelper(this)
        groupName = findViewById(R.id.groupName)
        groupDescription = findViewById(R.id.groupDescription)
        saveButton = findViewById(R.id.saveButton)

        groupId = intent.getIntExtra("groupId", 0)
        isCreator = intent.getBooleanExtra("isCreator", false)

        loadGroupDetails()

        saveButton.isEnabled = isCreator
        saveButton.setOnClickListener {
            dbHelper.updateGroup(
                groupId,
                groupName.text.toString(),
                groupDescription.text.toString()
            )
        }
    }

    private fun loadGroupDetails() {
        val cursor = dbHelper.getGroupDetails(groupId)
        if (cursor.moveToFirst()) {
            groupName.setText(cursor.getString(cursor.getColumnIndexOrThrow(GroupTable.NAME)))
            groupDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow(GroupTable.DESCRIPTION)))
        }
        cursor.close()
    }
}