package com.example.lobiupaieskossistema.caches;

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lobiupaieskossistema.LogInActivity
import com.example.lobiupaieskossistema.R
import com.example.lobiupaieskossistema.caches.CacheAdapter
import com.example.lobiupaieskossistema.data.CacheData
import com.example.lobiupaieskossistema.models.Cache
import com.example.lobiupaieskossistema.utils.SessionManager

class ManageCacheActivity : AppCompatActivity() {

    private lateinit var cacheRecyclerView: RecyclerView
    private lateinit var cacheAdapter: CacheAdapter
    private lateinit var cacheList: MutableList<Cache>
    private lateinit var sessionManager: SessionManager
    private var userRoleId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.manage_caches)

        sessionManager = SessionManager(this)

        if (!sessionManager.isLoggedIn()) {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }

        userRoleId = sessionManager.getRole()
        if (userRoleId == 0) {
            cacheList = CacheData.getAllUserCaches(sessionManager.getUserId()).toMutableList()
        }
        else if(userRoleId == 1) {
            cacheList = CacheData.getAll().toMutableList()
        }
        cacheRecyclerView = findViewById(R.id.cacheListRecyclerView)
        cacheRecyclerView.layoutManager = LinearLayoutManager(this)
        cacheAdapter = CacheAdapter(cacheList, this, userRoleId)
        cacheRecyclerView.adapter = cacheAdapter
    }
}