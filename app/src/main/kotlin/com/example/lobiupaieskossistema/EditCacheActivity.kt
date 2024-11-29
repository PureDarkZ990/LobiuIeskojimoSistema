package com.example.lobiupaieskossistema

import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.lobiupaieskossistema.models.Cache
import com.example.lobiupaieskossistema.models.Group
import com.example.lobiupaieskossistema.models.User

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
    private val users = listOf(User(1, "User 1"), User(2, "User 2")) // Example users
    private val groups = listOf(Group(1, "Group 1", users), Group(2, "Group 2", users)) // Example groups

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_cache)

        cacheId = intent.getIntExtra("cacheId", -1)
        cache = CacheData.getCacheById(cacheId)

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

        val userAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, users.map { it.name })
        userListView.adapter = userAdapter
        userListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        val groupAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, groups.map { it.name })
        groupListView.adapter = groupAdapter
        groupListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, CategoryData.getAllCategories())
        categorySpinner.adapter = categoryAdapter

        cache?.let {
            cacheNameInput.setText(it.name)
            cacheDescriptionInput.setText(it.description)
            zoneRadiusInput.setText(it.zoneRadius.toString())
            categorySpinner.setSelection(CategoryData.getAllCategories().indexOf(it.complexity))
            privateCacheCheckbox.isChecked = it.private
            it.imageUri?.let { uri ->
                cacheImageView.setImageURI(Uri.parse(uri))
            }
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

        assignGroupButton.setOnClickListener {
            groupListView.visibility = ListView.VISIBLE
        }

        assignPersonButton.setOnClickListener {
            userListView.visibility = ListView.VISIBLE
        }

        addCategoryButton.setOnClickListener {
            val newCategory = newCategoryInput.text.toString()
            if (newCategory.isNotEmpty()) {
                CategoryData.addCategory(newCategory)
                (categorySpinner.adapter as ArrayAdapter<String>).notifyDataSetChanged()
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
                newCategoryInput.text.toString()
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
                it.complexity = category
                it.isPublic = isPublic
                it.private = private
                it.assignedUsers = selectedUsers
                it.assignedGroups = selectedGroups
                CacheData.updateCache(it)
                finish()
            }
        }
    }
}