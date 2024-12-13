package com.example.lobiupaieskossistema

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class NewGroupActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.newgroup)

    }
}