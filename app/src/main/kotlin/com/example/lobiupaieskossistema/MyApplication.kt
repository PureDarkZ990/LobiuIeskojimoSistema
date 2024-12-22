package com.example.lobiupaieskossistema

import android.app.Application
import com.example.lobiupaieskossistema.data.CacheData
import com.example.lobiupaieskossistema.data.GroupData
import com.example.lobiupaieskossistema.data.UserCacheData
import com.example.lobiupaieskossistema.data.UserData

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize shared resources
        UserData.initialize(this)
        CacheData.initialize(this)
        UserCacheData.initialize(this)
        GroupData.initialize(this)
    }
}
