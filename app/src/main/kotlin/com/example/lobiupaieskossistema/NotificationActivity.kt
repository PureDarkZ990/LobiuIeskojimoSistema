package com.example.lobiupaieskossistema

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.media.MediaPlayer
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.lobiupaieskossistema.database.NotificationTable
import com.example.lobiupaieskossistema.database.NotificationUserTable
import com.example.lobiupaieskossistema.database.UserTable

class NotificationActivity : AppCompatActivity() {

    private lateinit var database: SQLiteDatabase
    private var userId: Int = -1 // Pass this dynamically to target the user
    private var soundEnabled: Boolean = false
    private var vibrationEnabled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notification)

        database = DatabaseHelper(this).writableDatabase

        userId = intent.getIntExtra("USER_ID", -1)
        if (userId == -1) {
            finish()
            return
        }

        // Load user preferences for sound and vibration
        loadUserPreferences()

        // Display the notification
        displayNotification()
    }

    private fun loadUserPreferences() {
        val cursor = database.query(
            UserTable.TABLE_NAME,
            arrayOf(UserTable.SOUND_ENABLED, UserTable.VIBRATION_ENABLED),
            "${UserTable.ID} = ?",
            arrayOf(userId.toString()),
            null,
            null,
            null
        )

        if (cursor.moveToFirst()) {
            soundEnabled = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.SOUND_ENABLED)) == 1
            vibrationEnabled = cursor.getInt(cursor.getColumnIndexOrThrow(UserTable.VIBRATION_ENABLED)) == 1
        }

        cursor.close()
    }

    private fun displayNotification() {
        val cursor = database.rawQuery(
            """
            SELECT n.${NotificationTable.MESSAGE}, n.${NotificationTable.DATE}, nu.${NotificationUserTable.READ}
            FROM ${NotificationTable.TABLE_NAME} n
            JOIN ${NotificationUserTable.TABLE_NAME} nu
            ON n.${NotificationTable.ID} = nu.${NotificationUserTable.NOTIFICATION_ID}
            WHERE nu.${NotificationUserTable.USER_ID} = ? AND nu.${NotificationUserTable.READ} = 0
            """,
            arrayOf(userId.toString())
        )

        if (cursor.moveToFirst()) {
            val message = cursor.getString(cursor.getColumnIndexOrThrow(NotificationTable.MESSAGE))
            val date = cursor.getString(cursor.getColumnIndexOrThrow(NotificationTable.DATE))

            // Update UI with fetched data
            val iconView = findViewById<ImageView>(R.id.notificationIcon)
            val messageView = findViewById<TextView>(R.id.notificationMessage)
            val okButton = findViewById<Button>(R.id.okButton)

            messageView.text = message

            // Trigger sound and vibration if enabled
            if (soundEnabled) {
                playSound()
            }
            if (vibrationEnabled) {
                triggerVibration()
            }

            // Mark notification as read on button click
            okButton.setOnClickListener {
                markNotificationAsRead(cursor.getInt(cursor.getColumnIndexOrThrow(NotificationUserTable.NOTIFICATION_ID)))
                cursor.close()
                finish()
            }
        } else {
            // No new notifications
            finish()
        }
    }

    private fun playSound() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.notification_sound)
        mediaPlayer.setOnCompletionListener { it.release() }
        mediaPlayer.start()
    }

    private fun triggerVibration() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                vibrator.vibrate(500) // Fallback for older versions
            }
        }
    }

    private fun markNotificationAsRead(notificationId: Int) {
        val values = ContentValues().apply {
            put(NotificationUserTable.READ, 1)
        }

        database.update(
            NotificationUserTable.TABLE_NAME,
            values,
            "${NotificationUserTable.NOTIFICATION_ID} = ? AND ${NotificationUserTable.USER_ID} = ?",
            arrayOf(notificationId.toString(), userId.toString())
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        database.close() // Close database to prevent leaks
    }
}
