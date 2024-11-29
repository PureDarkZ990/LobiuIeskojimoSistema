package com.example.lobiupaieskossistema

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lobiupaieskossistema.models.Cache

class ManageThemeActivity : AppCompatActivity() {

    private var cacheId: Int = -1
    private var cache: Cache? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manage_theme)

        cacheId = intent.getIntExtra("cacheId", -1)
        cache = CacheData.getCacheById(cacheId)

        // Implement theme management functionality here
    }
}