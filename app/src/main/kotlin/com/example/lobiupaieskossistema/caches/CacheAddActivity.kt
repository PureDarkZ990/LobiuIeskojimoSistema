package com.example.lobiupaieskossistema.caches

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.lobiupaieskossistema.R
import com.example.lobiupaieskossistema.data.CacheData
import com.example.lobiupaieskossistema.data.CategoryData
import com.example.lobiupaieskossistema.data.GroupData
import com.example.lobiupaieskossistema.data.UserData
import com.example.lobiupaieskossistema.models.Cache
import com.example.lobiupaieskossistema.models.Category
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class CacheAddActivity : AppCompatActivity() {

    private lateinit var cacheImageView: ImageView
    private lateinit var selectImageButton: Button
    private lateinit var cacheNameInput: EditText
    private lateinit var cacheDescriptionInput: EditText
    private lateinit var zoneRadiusSeekBar: SeekBar
    private lateinit var zoneRadiusLabel: TextView
    private lateinit var difficultyInput: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var addNewCategoryCheckbox: CheckBox
    private lateinit var newCategoryInput: EditText
    private lateinit var addCategoryButton: Button
    private lateinit var categoryDescriptionLabel: TextView
    private lateinit var categoryDescriptionInput: EditText
    private lateinit var privateCacheCheckbox: CheckBox
    private lateinit var assignGroupButton: Button
    private lateinit var assignPersonButton: Button
    private lateinit var submitCacheButton: Button
    private lateinit var userListView: ListView
    private lateinit var groupListView: ListView
    private var selectedImageUri: Uri? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.add_cache)

        UserData.initialize(this)
        GroupData.initialize(this)
        CategoryData.initialize(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation()

        cacheImageView = findViewById(R.id.cacheImageView)
        selectImageButton = findViewById(R.id.selectImageButton)
        cacheNameInput = findViewById(R.id.cacheNameInput)
        cacheDescriptionInput = findViewById(R.id.cacheDescriptionInput)
        zoneRadiusSeekBar = findViewById(R.id.zoneRadiusSeekBar)
        zoneRadiusLabel = findViewById(R.id.zoneRadiusLabel)
        difficultyInput = findViewById(R.id.difficultyInput)
        categorySpinner = findViewById(R.id.categorySpinner)
        addNewCategoryCheckbox = findViewById(R.id.addNewCategoryCheckbox)
        newCategoryInput = findViewById(R.id.newCategoryInput)
        addCategoryButton = findViewById(R.id.addCategoryButton)
        categoryDescriptionLabel = findViewById(R.id.categoryDescriptionLabel)
        categoryDescriptionInput = findViewById(R.id.categoryDescriptionInput)
        privateCacheCheckbox = findViewById(R.id.privateCacheCheckbox)
        assignGroupButton = findViewById(R.id.assignGroupButton)
        assignPersonButton = findViewById(R.id.assignPersonButton)
        submitCacheButton = findViewById(R.id.submitCacheButton)
        userListView = findViewById(R.id.userListView)
        groupListView = findViewById(R.id.groupListView)

        val users = UserData.getAll()
        val groups = GroupData.getAll()
        val categories = CategoryData.getAll()

        val userAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, users.map { it.username })
        userListView.adapter = userAdapter
        userListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        val groupAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, groups.map { it.name })
        groupListView.adapter = groupAdapter
        groupListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories.map { it.name })
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter

        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        addNewCategoryCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                newCategoryInput.visibility = EditText.VISIBLE
                addCategoryButton.visibility = Button.VISIBLE
                categoryDescriptionLabel.visibility = EditText.VISIBLE
                categoryDescriptionInput.visibility = EditText.VISIBLE
            } else {
                newCategoryInput.visibility = EditText.GONE
                addCategoryButton.visibility = Button.GONE
                categoryDescriptionLabel.visibility = EditText.GONE
                categoryDescriptionInput.visibility = EditText.GONE
            }
        }

        privateCacheCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                assignGroupButton.visibility = Button.VISIBLE
                assignPersonButton.visibility = Button.VISIBLE
            } else {
                assignGroupButton.visibility = Button.GONE
                assignPersonButton.visibility = Button.GONE
            }
        }

        zoneRadiusSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val radius = progress + 5
                zoneRadiusLabel.text = "Zone Radius: ${radius}m"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        submitCacheButton.setOnClickListener {
            val name = cacheNameInput.text.toString()
            val description = cacheDescriptionInput.text.toString()
            val zoneRadius = zoneRadiusSeekBar.progress + 5
            val difficulty = difficultyInput.text.toString().toDoubleOrNull() ?: 0.0
            val category = if (addNewCategoryCheckbox.isChecked) {
                val newCategoryName = newCategoryInput.text.toString()
                val newCategoryDescription = categoryDescriptionInput.text.toString()
                val newCategory = Category(0, newCategoryName, newCategoryDescription)
                CategoryData.add(newCategory)
                newCategoryName
            } else {
                categorySpinner.selectedItem.toString()
            }
            val isPublic = !privateCacheCheckbox.isChecked
            val private = privateCacheCheckbox.isChecked

            val selectedUsers = userListView.checkedItemPositions
                .let { positions -> users.filterIndexed { index, _ -> positions[index] } }

            val selectedGroups = groupListView.checkedItemPositions
                .let { positions -> groups.filterIndexed { index, _ -> positions[index] } }

            val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

            val newCache = Cache(
                name = name,
                description = description,
                xCoordinate = currentLatitude,
                yCoordinate = currentLongitude,
                zoneRadius = zoneRadius,
                rating = 0.0,
                difficulty = difficulty,
                approved = 0,
                createdAt = currentTime,
                updatedAt = currentTime,
                private = if (private) 1 else 0,
                themeId = null,
                creatorId = null
            )

            CacheData.add(newCache)

            selectedImageUri?.let { uri ->
                saveImageToInternalStorage(this, (cacheImageView.drawable as BitmapDrawable).bitmap, "cache-images", "${newCache.id}.png")
            }

            finish()
        }
    }

    private fun getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_LOCATION_PERMISSION)
        } else {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    currentLatitude = it.latitude
                    currentLongitude = it.longitude
                }
            }
        }
    }

    private fun saveImageToInternalStorage(context: Context, bitmap: Bitmap, directoryName: String, imageName: String): String? {
        val directory = File(context.filesDir, directoryName)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, imageName)
        var fileOutputStream: FileOutputStream? = null
        return try {
            fileOutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream)
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        } finally {
            fileOutputStream?.close()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            cacheImageView.setImageURI(selectedImageUri)
            cacheImageView.visibility = ImageView.VISIBLE
        }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
        private const val REQUEST_LOCATION_PERMISSION = 2
    }
}