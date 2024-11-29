package com.example.lobiupaieskossistema

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.lobiupaieskossistema.models.Cache
import com.example.lobiupaieskossistema.models.Group
import com.example.lobiupaieskossistema.models.User
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class CacheAddActivity : AppCompatActivity() {

    private lateinit var cacheImageView: ImageView
    private lateinit var selectImageButton: Button
    private lateinit var cacheNameInput: EditText
    private lateinit var cacheDescriptionInput: EditText
    private lateinit var zoneRadiusInput: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var addNewCategoryCheckbox: CheckBox
    private lateinit var newCategoryInput: EditText
    private lateinit var addCategoryButton: Button
    private lateinit var categoryDescriptionLabel: EditText
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
    private val users = listOf(User(1, "User 1"), User(2, "User 2")) // Example users
    private val groups = listOf(Group(1, "Group 1", users), Group(2, "Group 2", users)) // Example groups

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.add_cache)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        getLocation()

        cacheImageView = findViewById(R.id.cacheImageView)
        selectImageButton = findViewById(R.id.selectImageButton)
        cacheNameInput = findViewById(R.id.cacheNameInput)
        cacheDescriptionInput = findViewById(R.id.cacheDescriptionInput)
        zoneRadiusInput = findViewById(R.id.zoneRadiusInput)
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

        val userAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, users.map { it.name })
        userListView.adapter = userAdapter
        userListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        val groupAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, groups.map { it.name })
        groupListView.adapter = groupAdapter
        groupListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE

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

        submitCacheButton.setOnClickListener {
            val name = cacheNameInput.text.toString()
            val description = cacheDescriptionInput.text.toString()
            val zoneRadius = zoneRadiusInput.text.toString().toIntOrNull() ?: 0
            val category = if (addNewCategoryCheckbox.isChecked) {
                newCategoryInput.text.toString()
            } else {
                categorySpinner.selectedItem.toString()
            }
            val isPublic = !privateCacheCheckbox.isChecked
            val private = privateCacheCheckbox.isChecked
            val imageUri = selectedImageUri?.toString()

            val selectedUsers = userListView.checkedItemPositions
                .let { positions -> users.filterIndexed { index, _ -> positions[index] } }

            val selectedGroups = groupListView.checkedItemPositions
                .let { positions -> groups.filterIndexed { index, _ -> positions[index] } }

            val newCache = Cache(
                name = name,
                description = description,
                isPublic = isPublic,
                complexity = category,
                rating = "0.0",
                isApproved = false,
                latitude = currentLatitude,
                longitude = currentLongitude,
                zoneRadius = zoneRadius,
                imageUri = imageUri,
                private = private,
                assignedUsers = selectedUsers,
                assignedGroups = selectedGroups
            )

            CacheData.addCache(newCache)
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

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            cacheImageView.setImageURI(selectedImageUri)
        }
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1
        private const val REQUEST_LOCATION_PERMISSION = 2
    }
}