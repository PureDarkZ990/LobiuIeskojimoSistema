package com.example.lobiupaieskossistema.caches

import android.app.AlertDialog
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.lobiupaieskossistema.R
import com.example.lobiupaieskossistema.data.UserCacheData
import com.example.lobiupaieskossistema.models.Cache
import com.example.lobiupaieskossistema.utils.SessionManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class CacheInfoWindowAdapter(
    private val context: Context,
    private val isUserWithinRadius: (Cache) -> Boolean,
    private val onTreasureFound: (Cache) -> Unit // Callback to refresh map or perform post-success actions
) : GoogleMap.InfoWindowAdapter {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private val sessionManager = SessionManager(context) // For accessing the logged-in user ID

    override fun getInfoWindow(marker: Marker): View? = null

    override fun getInfoContents(marker: Marker): View {
        val view = inflater.inflate(R.layout.custom_info_window, null)
        val cache = marker.tag as? Cache

        if (cache == null) {
            Log.e("CacheInfoWindowAdapter", "Marker tag is null or not a Cache")
            return view
        }

        // Populate views
        view.findViewById<TextView>(R.id.infoWindowTitle).text = cache.name ?: "No Title"
        view.findViewById<TextView>(R.id.infoWindowDescription).text = cache.description ?: "No Description"
        view.findViewById<TextView>(R.id.infoWindowTheme).text = "Theme: ${cache.themeId ?: "No theme"}"
        view.findViewById<TextView>(R.id.infoWindowRating).text = "Rating: ${cache.rating ?: "No rating"}"
        view.findViewById<TextView>(R.id.infoWindowDifficulty).text = "Difficulty: ${cache.difficulty ?: "Unknown"}"
        view.findViewById<TextView>(R.id.infoWindowCreator).text = "Creator: ${cache.creatorId ?: "Unknown"}"

        // Configure button functionality
        val button = view.findViewById<Button>(R.id.submitTreasureButton)
        if (isUserWithinRadius(cache)) {
            button.visibility = View.VISIBLE
            button.setOnClickListener {
                showPasswordPrompt(cache)
            }
        } else {
            button.visibility = View.GONE
        }

        return view
    }

    private fun showPasswordPrompt(cache: Cache) {
        val dialogView = inflater.inflate(R.layout.dialog_password_prompt, null)
        val passwordInput = dialogView.findViewById<EditText>(R.id.passwordInput)

        AlertDialog.Builder(context)
            .setTitle("Enter Password")
            .setView(dialogView)
            .setPositiveButton("Submit") { _, _ ->
                val enteredPassword = passwordInput.text.toString()
                if (enteredPassword == cache.password) {
                    Toast.makeText(context, "Treasure found!", Toast.LENGTH_SHORT).show()
                    markTreasureAsFound(cache)
                } else {
                    Toast.makeText(context, "Incorrect password. Try again.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun markTreasureAsFound(cache: Cache) {
        val userId = sessionManager.getUserId()
        if (userId != null) {
            // Update treasure availability in the database
            UserCacheData.updateAvailability(cache.id, userId, isAvailable = 0)

            // Trigger the callback to refresh the map or perform other actions
            onTreasureFound(cache)

            Toast.makeText(context, "Treasure marked as found!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Error: User not logged in.", Toast.LENGTH_SHORT).show()
        }
    }
}
