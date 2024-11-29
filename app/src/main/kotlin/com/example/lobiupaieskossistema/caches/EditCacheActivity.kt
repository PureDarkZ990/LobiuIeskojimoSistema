package com.example.lobiupaieskossistema.caches

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.lobiupaieskossistema.R
import com.example.lobiupaieskossistema.dao.CacheGroupDAO
import com.example.lobiupaieskossistema.dao.UserCacheDAO
import com.example.lobiupaieskossistema.data.CacheData
import com.example.lobiupaieskossistema.data.CategoryData
import com.example.lobiupaieskossistema.data.GroupData
import com.example.lobiupaieskossistema.data.UserData
import com.example.lobiupaieskossistema.models.Cache
import com.example.lobiupaieskossistema.models.CacheGroup
import com.example.lobiupaieskossistema.models.Category
import com.example.lobiupaieskossistema.models.UserCache
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class EditCacheActivity : AppCompatActivity() {

    private lateinit var cacheNameInput: EditText
    private lateinit var cacheDescriptionInput: EditText
    private lateinit var zoneRadiusInput: EditText
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
    private lateinit var cacheImageView: ImageView
    private var cacheId: Int = -1
    private var cache: Cache? = null
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_cache)

        UserData.initialize(this)
        GroupData.initialize(this)
        CategoryData.initialize(this)

        cacheId = intent.getIntExtra("cacheId", -1)
        cache = CacheData.get(cacheId)

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
        cacheImageView = findViewById(R.id.cacheImageView)

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

        cache?.let {
            cacheNameInput.setText(it.name)
            cacheDescriptionInput.setText(it.description)
            zoneRadiusInput.setText(it.zoneRadius.toString())
            privateCacheCheckbox.isChecked = it.private == 1
            // Set other fields as needed
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

        privateCacheCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                assignGroupButton.visibility = Button.VISIBLE
                assignPersonButton.visibility = Button.VISIBLE
            } else {
                assignGroupButton.visibility = Button.GONE
                assignPersonButton.visibility = Button.GONE
            }
        }

        addCategoryButton.setOnClickListener {
            val newCategoryName = newCategoryInput.text.toString()
            val newCategoryDescription = categoryDescriptionInput.text.toString()
            if (newCategoryName.isNotEmpty()) {
                val newCategory = Category(0, newCategoryName, newCategoryDescription)
                CategoryData.add(newCategory)
                (categorySpinner.adapter as ArrayAdapter<String>).add(newCategoryName)
                newCategoryInput.text.clear()
                categoryDescriptionInput.text.clear()
                newCategoryInput.visibility = EditText.GONE
                addCategoryButton.visibility = Button.GONE
                categoryDescriptionLabel.visibility = TextView.GONE
                categoryDescriptionInput.visibility = EditText.GONE
                addNewCategoryCheckbox.isChecked = false
            }
        }

        submitCacheButton.setOnClickListener {
            val name = cacheNameInput.text.toString()
            val description = cacheDescriptionInput.text.toString()
            val zoneRadius = zoneRadiusInput.text.toString().toIntOrNull() ?: 0
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

            cache?.let {
                it.name = name
                it.description = description
                it.zoneRadius = zoneRadius
                it.private = if (private) 1 else 0
                CacheData.update(it)

                // Update UserCache and CacheGroup
                val userCacheDAO = UserCacheDAO(this)
                val cacheGroupDAO = CacheGroupDAO(this)
                selectedUsers.forEach { user ->
                    userCacheDAO.addUserCache(UserCache(user.id, it.id,0,null,1))
                }
                selectedGroups.forEach { group ->
                    cacheGroupDAO.addCacheGroup(CacheGroup(it.id, group.id))
                }

                selectedImageUri?.let { uri ->
                    saveImageToInternalStorage(this, (cacheImageView.drawable as BitmapDrawable).bitmap, "cache-images", "${it.id}.png")
                }

                finish()
            }
        }

        cacheImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
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
    }
}