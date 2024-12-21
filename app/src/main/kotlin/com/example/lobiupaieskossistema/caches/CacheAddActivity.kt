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
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.lobiupaieskossistema.LogInActivity
import com.example.lobiupaieskossistema.R
import com.example.lobiupaieskossistema.caches.EditCacheActivity.Companion
import com.example.lobiupaieskossistema.data.CacheData
import com.example.lobiupaieskossistema.data.CategoryData
import com.example.lobiupaieskossistema.data.CacheCategoryData
import com.example.lobiupaieskossistema.data.CacheGroupData
import com.example.lobiupaieskossistema.data.GroupData
import com.example.lobiupaieskossistema.data.UserCacheData
import com.example.lobiupaieskossistema.data.UserData
import com.example.lobiupaieskossistema.data.UserGroupData
import com.example.lobiupaieskossistema.models.Cache
import com.example.lobiupaieskossistema.models.CacheCategory
import com.example.lobiupaieskossistema.models.CacheGroup
import com.example.lobiupaieskossistema.models.Category
import com.example.lobiupaieskossistema.models.UserCache
import com.example.lobiupaieskossistema.models.UserGroup
import com.example.lobiupaieskossistema.utils.SessionManager
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
    private lateinit var difficultyLabel: TextView
    private lateinit var difficultySeekBar: SeekBar
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
    private var selectedCategoryId: Int = -1
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLatitude: Double = 0.0
    private var currentLongitude: Double = 0.0
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.add_cache)

        sessionManager = SessionManager(this)
        if (!sessionManager.isLoggedIn()) {
            val intent = Intent(this, LogInActivity::class.java)
            startActivity(intent)
            finish()
        }
        UserData.initialize(this)
        GroupData.initialize(this)
        CategoryData.initialize(this)
        CacheCategoryData.initialize(this)
        CacheGroupData.initialize(this)
        UserCacheData.initialize(this)
        UserGroupData.initialize(this)


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation()

        cacheImageView = findViewById(R.id.cacheImageView)
        selectImageButton = findViewById(R.id.selectImageButton)
        cacheNameInput = findViewById(R.id.cacheNameInput)
        cacheDescriptionInput = findViewById(R.id.cacheDescriptionInput)
        zoneRadiusSeekBar = findViewById(R.id.zoneRadiusSeekBar)
        zoneRadiusLabel = findViewById(R.id.zoneRadiusLabel)
        difficultyLabel = findViewById(R.id.difficultyLabel)
        difficultySeekBar = findViewById(R.id.difficultySeekBar)
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
        var categories = CategoryData.getAll()

        val userAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, users.map { it.username })
        userListView.adapter = userAdapter
        userListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        val groupAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, groups.map { it.name })
        groupListView.adapter = groupAdapter
        groupListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories.map { it.name })
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter


        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedCategoryId = categories[position].id
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedCategoryId = -1
            }
        }
        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }
        addNewCategoryCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                newCategoryInput.visibility = EditText.VISIBLE
                addCategoryButton.visibility = Button.VISIBLE
                categoryDescriptionLabel.visibility = TextView.VISIBLE
                categoryDescriptionInput.visibility = EditText.VISIBLE
            } else {
                newCategoryInput.visibility = EditText.GONE
                addCategoryButton.visibility = Button.GONE
                categoryDescriptionLabel.visibility = TextView.GONE
                categoryDescriptionInput.visibility = EditText.GONE
            }
        }
        addCategoryButton.setOnClickListener {
            val newCategoryName = newCategoryInput.text.toString()
            val newCategoryDescription = categoryDescriptionInput.text.toString()
            if (newCategoryName.isNotEmpty()) {
                val newCategory = Category(0, newCategoryName, newCategoryDescription)
                val categoryId=CategoryData.add(newCategory)
                (categorySpinner.adapter as ArrayAdapter<String>).add(newCategoryName)
                newCategoryInput.text.clear()
                categoryDescriptionInput.text.clear()
                newCategoryInput.visibility = EditText.GONE
                addCategoryButton.visibility = Button.GONE
                categoryDescriptionLabel.visibility = TextView.GONE
                categoryDescriptionInput.visibility = EditText.GONE
                addNewCategoryCheckbox.isChecked = false
                categories = CategoryData.getAll()
            }
        }
        assignGroupButton.setOnClickListener {
            groupListView.visibility = ListView.VISIBLE
            userListView.visibility = ListView.GONE
        }

        assignPersonButton.setOnClickListener {
            userListView.visibility = ListView.VISIBLE
            groupListView.visibility = ListView.GONE
        }
        privateCacheCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                assignGroupButton.visibility = Button.VISIBLE
                assignPersonButton.visibility = Button.VISIBLE
            } else {
                assignGroupButton.visibility = Button.GONE
                assignPersonButton.visibility = Button.GONE
                userListView.visibility = ListView.GONE
                groupListView.visibility = ListView.GONE
            }
        }


        zoneRadiusSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val radius = progress
                zoneRadiusLabel.text = "Zone Radius: ${radius}m"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        val defaultDifficulty = 2.5
        difficultySeekBar.progress = (defaultDifficulty * 100).toInt()
        difficultyLabel.text = "Difficulty Rating: $defaultDifficulty"

        difficultySeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val rating = progress / 100.0
                difficultyLabel.text = "Rating: ${rating}"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
        submitCacheButton.setOnClickListener {
            val name = cacheNameInput.text.toString()
            if (name.isEmpty()) {
                Toast.makeText(this, "Cache name cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val description = cacheDescriptionInput.text.toString()
            val zoneRadius = zoneRadiusSeekBar.progress + 5
            val difficulty = difficultySeekBar.progress.toString().toDoubleOrNull() ?: 0.0

            val selectedUsers = userListView.checkedItemPositions
                .let { positions -> users.filterIndexed { index, _ -> positions[index] } }

            val selectedGroups = groupListView.checkedItemPositions
                .let { positions -> groups.filterIndexed { index, _ -> positions[index] } }
            if (currentLatitude == 0.0 && currentLongitude == 0.0) {
                Toast.makeText(this, "Unable to fetch current location. Please try again.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val cache = Cache(
                name = name,
                description = description.ifEmpty { null },
                xCoordinate = currentLatitude,
                yCoordinate = currentLongitude,
                zoneRadius = zoneRadius,
                difficulty = difficulty / 100,
                createdAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                updatedAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
                private = if (privateCacheCheckbox.isChecked) 1 else 0,
                creatorId = sessionManager.getUserId()
            )

            val cacheId = CacheData.add(cache)
            val cacheCategory = CacheCategory(cacheId.toInt(), selectedCategoryId)
            CacheCategoryData.add(cacheCategory)
            if(privateCacheCheckbox.isChecked){
                if (selectedUsers.isNotEmpty()) {
                    for (user in selectedUsers) {
                        UserCacheData.add(UserCache(user.id, cacheId.toInt(), 0, null, 1))
                    }
                }
                if (selectedGroups.isNotEmpty()) {
                    for (group in selectedGroups) {
                        CacheGroupData.add(CacheGroup(cacheId.toInt(), group.id))
                    }
                }
            }
            selectedImageUri?.let { uri ->
                saveImageToInternalStorage(this, (cacheImageView.drawable as BitmapDrawable).bitmap, "cache-images", "${cacheId}.png")
            }

            finish()
        }
        cacheImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, EditCacheActivity.REQUEST_IMAGE_PICK)
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
            } ?: run {
                Toast.makeText(this, "Unable to fetch current location. Please try again.", Toast.LENGTH_SHORT).show()
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