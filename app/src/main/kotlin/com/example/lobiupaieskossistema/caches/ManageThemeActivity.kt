package com.example.lobiupaieskossistema.caches

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.lobiupaieskossistema.R
import com.example.lobiupaieskossistema.dao.ThemeDAO
import com.example.lobiupaieskossistema.data.CacheData
import com.example.lobiupaieskossistema.models.Cache
import com.example.lobiupaieskossistema.models.Theme

class ManageThemeActivity : AppCompatActivity() {
    private var cacheId: Int = -1
    private var cache: Cache? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manage_theme)

        cacheId = intent.getIntExtra("cacheId", -1)
        cache = CacheData.get(cacheId)

        val themeNameInput: EditText = findViewById(R.id.themeNameInput)
        val themeDescriptionInput: EditText = findViewById(R.id.themeDescriptionInput)
        val timerTypeRadioGroup: RadioGroup = findViewById(R.id.timerTypeRadioGroup)
        val timeInput: EditText = findViewById(R.id.timeInput)
        val submitThemeButton: Button = findViewById(R.id.submitThemeButton)

        submitThemeButton.setOnClickListener {
            val themeName = themeNameInput.text.toString()
            val themeDescription = themeDescriptionInput.text.toString()
            val time = timeInput.text.toString().toIntOrNull() ?: 0
            val selectedTimerType = timerTypeRadioGroup.checkedRadioButtonId

            val theme = when (selectedTimerType) {
                R.id.absoluteTimeRadioButton -> Theme(
                    description = themeDescription,
                    cacheId = cacheId,
                    absoluteTime = time,
                    time = 0
                )
                R.id.zoneEnterRadioButton -> Theme(
                    description = themeDescription,
                    absoluteTime = 0,
                    cacheId = cacheId,
                    time = time
                )
                else -> Theme(
                    description = themeDescription,
                    cacheId = cacheId,
                    absoluteTime = 0,
                    time = 0
                )
            }

            ThemeDAO(this).addTheme(theme)
            finish()
        }
    }
}