package com.example.lobiupaieskossistema

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lobiupaieskossistema.adapters.CacheAdapter
import com.example.lobiupaieskossistema.models.Cache

class ManageCacheActivity : AppCompatActivity() {

    private lateinit var cacheRecyclerView: RecyclerView
    private lateinit var cacheAdapter: CacheAdapter
    private lateinit var cacheList: MutableList<Cache>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.manage_caches)

        cacheList = CacheData.getAllCaches().toMutableList()

        cacheRecyclerView = findViewById(R.id.cacheListRecyclerView)
        cacheRecyclerView.layoutManager = LinearLayoutManager(this)
        cacheAdapter = CacheAdapter(cacheList, this)
        cacheRecyclerView.adapter = cacheAdapter
    }
}