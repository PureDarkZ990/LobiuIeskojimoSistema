package com.example.lobiupaieskossistema.caches

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.lobiupaieskossistema.MainActivity
import com.example.lobiupaieskossistema.R
import com.example.lobiupaieskossistema.dao.ThemeDAO
import com.example.lobiupaieskossistema.dao.UserDAO
import com.example.lobiupaieskossistema.data.CacheData
import com.example.lobiupaieskossistema.data.ThemeData
import com.example.lobiupaieskossistema.models.Cache
import com.example.lobiupaieskossistema.models.Theme

class ManageThemeActivity : AppCompatActivity() {
    private var cacheId: Int = -1
    private var cache: Cache? = null
    private var theme: Theme? = null
    private var themeId = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.manage_theme)
        cacheId = intent.getIntExtra("cacheId", -1)
        cache = CacheData.get(cacheId)
        ThemeData.initialize(this)
        theme = ThemeData.findThemeByCacheId(cacheId)
        val themeDescriptionInput: EditText = findViewById(R.id.themeDescriptionInput)
        val timerTypeRadioGroup: RadioGroup = findViewById(R.id.timerTypeRadioGroup)
        val hourPicker: NumberPicker = findViewById(R.id.hourPicker)
        val dayPicker: NumberPicker = findViewById(R.id.dayPicker)
        val monthPicker: NumberPicker = findViewById(R.id.monthPicker)
        val yearPicker: NumberPicker = findViewById(R.id.yearPicker)
        val minutePicker: NumberPicker = findViewById(R.id.minutePicker)
        val zoneHourPicker: NumberPicker = findViewById(R.id.zoneHourPicker)
        val absoluteTimePickerLayout: LinearLayout = findViewById(R.id.absoluteTimePickerLayout)
        val zoneEnterTimePickerLayout: LinearLayout = findViewById(R.id.zoneEnterTimePickerLayout)
        val submitThemeButton: Button = findViewById(R.id.submitThemeButton)

        // Set min and max values programmatically
        hourPicker.minValue = 0
        hourPicker.maxValue = 23
        dayPicker.minValue = 0
        dayPicker.maxValue = 30
        monthPicker.minValue = 0
        monthPicker.maxValue = 11
        yearPicker.minValue = 0
        yearPicker.maxValue = 1
        minutePicker.minValue = 1
        minutePicker.maxValue = 59
        zoneHourPicker.minValue = 0
        zoneHourPicker.maxValue = 2

        // Populate fields with existing theme data
        theme?.let {
            themeDescriptionInput.setText(it.description)
            themeId = it.id
            if (it.absoluteTime > 0) {
                timerTypeRadioGroup.check(R.id.absoluteTimeRadioButton)
                absoluteTimePickerLayout.visibility = View.VISIBLE
                zoneEnterTimePickerLayout.visibility = View.GONE

                val totalSeconds = it.absoluteTime
                yearPicker.value = totalSeconds / (365 * 24 * 3600)
                monthPicker.value = (totalSeconds % (365 * 24 * 3600)) / (30 * 24 * 3600)
                dayPicker.value = (totalSeconds % (30 * 24 * 3600)) / (24 * 3600)
                hourPicker.value = (totalSeconds % (24 * 3600)) / 3600
            } else {
                timerTypeRadioGroup.check(R.id.zoneEnterRadioButton)
                absoluteTimePickerLayout.visibility = View.GONE
                zoneEnterTimePickerLayout.visibility = View.VISIBLE

                val totalSeconds = it.time
                zoneHourPicker.value = totalSeconds / 3600
                minutePicker.value = (totalSeconds % 3600) / 60
            }
        }

        timerTypeRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.absoluteTimeRadioButton -> {
                    absoluteTimePickerLayout.visibility = View.VISIBLE
                    zoneEnterTimePickerLayout.visibility = View.GONE
                }
                R.id.zoneEnterRadioButton -> {
                    absoluteTimePickerLayout.visibility = View.GONE
                    zoneEnterTimePickerLayout.visibility = View.VISIBLE
                }
            }
        }

        submitThemeButton.setOnClickListener {
            val themeDescription = themeDescriptionInput.text.toString()
            val selectedTimerType = timerTypeRadioGroup.checkedRadioButtonId

            val totalSeconds = when (selectedTimerType) {
                R.id.absoluteTimeRadioButton -> {
                    val hours = hourPicker.value
                    val days = dayPicker.value * 24
                    val months = monthPicker.value * 30 * 24
                    val years = yearPicker.value * 365 * 24
                    (hours + days + months + years) * 3600
                }
                R.id.zoneEnterRadioButton -> {
                    val minutes = minutePicker.value
                    val hours = zoneHourPicker.value
                    (hours * 3600) + (minutes * 60)
                }
                else -> 0
            }
            val endingTime = if (selectedTimerType == R.id.absoluteTimeRadioButton) {
                System.currentTimeMillis() + totalSeconds * 1000
            } else {
                0L
            }
            val theme = when (selectedTimerType) {
                R.id.absoluteTimeRadioButton -> Theme(
                    id = themeId,
                    description = themeDescription,
                    cacheId = cacheId,
                    absoluteTime = totalSeconds,
                    endingTime = endingTime,
                    time = 0
                )
                R.id.zoneEnterRadioButton -> Theme(
                    id = themeId,
                    description = themeDescription,
                    absoluteTime = 0,
                    cacheId = cacheId,
                    endingTime = 0L,
                    time = totalSeconds
                )
                else -> Theme(
                    id = themeId,
                    description = themeDescription,
                    cacheId = cacheId
                )
            }
            if(themeId==-1) {
                println(theme)
                ThemeData.add(theme)
            }
            else{
                ThemeData.update(theme)
            }
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}