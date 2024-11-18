package com.example.lobiupaieskossistema

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class GroupsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.groups)

        val registerButton: Button = findViewById(R.id.addNewGroupButton)
        registerButton.setOnClickListener {
            val intent = Intent(this, NewGroupActivity::class.java)
            startActivity(intent)
        }
    }
}