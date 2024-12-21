package com.example.lobiupaieskossistema.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "user_session"
        private const val KEY_USERNAME = "username"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_ROLE = "user_role"
        private const val KEY_LOGIN_TIME = "login_time"
        private const val SESSION_DURATION = 5 * 24 * 60 * 60 * 1000
    }

    fun createLoginSession(username: String, userId: Int, userRole: Int) {
        val editor = prefs.edit()
        editor.putString(KEY_USERNAME, username)
        editor.putInt(KEY_USER_ID, userId)
        editor.putInt(KEY_USER_ROLE, userRole)
        editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis())
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        val loginTime = prefs.getLong(KEY_LOGIN_TIME, 0)
        return prefs.contains(KEY_USERNAME) && (System.currentTimeMillis() - loginTime) < SESSION_DURATION
    }

    fun logout() {
        val editor = prefs.edit()
        editor.clear()
        editor.apply()
    }

    fun getUsername(): String? {
        return prefs.getString(KEY_USERNAME, null)
    }

    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }

    fun getRole(): Int {
        return prefs.getInt(KEY_USER_ROLE, -1)
    }
}