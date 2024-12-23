package com.example.lobiupaieskossistema.caches

import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.example.lobiupaieskossistema.R
import com.example.lobiupaieskossistema.data.UserCacheData
import com.example.lobiupaieskossistema.models.Cache
import com.example.lobiupaieskossistema.models.UserCache
import com.example.lobiupaieskossistema.utils.SessionManager

class CacheInteractionHandler(
    private val context: Context,
    private val sessionManager: SessionManager // Use actual SessionManager instance
) {

    fun showPasswordPrompt(cache: Cache) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.password_prompt, null)
        val passwordInput = dialogView.findViewById<EditText>(R.id.passwordInput)

        val dialog = AlertDialog.Builder(context)
            .setTitle("Enter Password")
            .setView(dialogView)
            .setPositiveButton("Submit") { _, _ ->
                val enteredPassword = passwordInput.text?.toString()
                if (enteredPassword == cache.password.orEmpty()) {
                    showSuccessMessage(cache)
                } else {
                    showError()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()

        dialog.show()
    }

    private fun showSuccessMessage(cache: Cache) {
        AlertDialog.Builder(context)
            .setTitle("Success")
            .setMessage("Cache ${cache.name} found successfully!")
            .setPositiveButton("OK") { _, _ ->
                showRatingPrompt(cache)
            }
            .show()
    }

    private fun showError() {
        AlertDialog.Builder(context)
            .setTitle("Error")
            .setMessage("Incorrect password. Please try again.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun showRatingPrompt(cache: Cache) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.rating_prompt, null)

        val dialog = AlertDialog.Builder(context)
            .setTitle("Rate Cache")
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.show()

        listOf(
            R.id.ratingButton1 to 1,
            R.id.ratingButton2 to 2,
            R.id.ratingButton3 to 3,
            R.id.ratingButton4 to 4,
            R.id.ratingButton5 to 5
        ).forEach { (buttonId, rating) ->
            dialogView.findViewById<Button>(buttonId)?.setOnClickListener {
                updateUserCacheRating(cache, rating.toDouble()) // Use Double for rating
                dialog.dismiss()
                showConfirmation(rating)
            }
        }
    }

    private fun updateUserCacheRating(cache: Cache, rating: Double) {
        val userId = sessionManager.getUserId() // Use SessionManager to get user ID
        val userCache = UserCacheData.get(cache.id, userId)

        if (userCache != null) {
            // Update existing record
            userCache.rating = rating
            UserCacheData.update(userCache)
        } else {
            // Add new record
            val newUserCache = UserCache(
                userId = userId,
                cacheId = cache.id,
                rating = rating,
                found = 1 // Mark as found
            )
            UserCacheData.add(newUserCache)
        }
    }

    private fun showConfirmation(rating: Int) {
        AlertDialog.Builder(context)
            .setTitle("Thank You!")
            .setMessage("You rated this cache $rating/5.")
            .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
