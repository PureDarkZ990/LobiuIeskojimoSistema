package com.example.lobiupaieskossistema.caches

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.lobiupaieskossistema.R
import com.example.lobiupaieskossistema.data.CacheData
import com.example.lobiupaieskossistema.models.Cache

class ManageThemeActivity : AppCompatActivity() {

    private var cacheId: Int = -1
    private var cache: Cache? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manage_theme)

        cacheId = intent.getIntExtra("cacheId", -1)
        cache = CacheData.get(cacheId)

        // Implement theme management functionality here
    }
}