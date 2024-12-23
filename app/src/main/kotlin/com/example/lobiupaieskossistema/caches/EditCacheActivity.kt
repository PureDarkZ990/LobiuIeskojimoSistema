package com.example.lobiupaieskossistema.caches

import  android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.lobiupaieskossistema.MainActivity
import com.example.lobiupaieskossistema.R
import com.example.lobiupaieskossistema.dao.CacheGroupDAO
import com.example.lobiupaieskossistema.dao.UserCacheDAO
import com.example.lobiupaieskossistema.data.CacheCategoryData
import com.example.lobiupaieskossistema.data.CacheData
import com.example.lobiupaieskossistema.data.CacheGroupData
import com.example.lobiupaieskossistema.data.CategoryData
import com.example.lobiupaieskossistema.data.GroupData
import com.example.lobiupaieskossistema.data.UserCacheData
import com.example.lobiupaieskossistema.data.UserData
import com.example.lobiupaieskossistema.data.UserGroupData
import com.example.lobiupaieskossistema.models.Cache
import com.example.lobiupaieskossistema.models.CacheCategory
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
    private lateinit var difficultyLabel: TextView
    private lateinit var difficultySeekBar: SeekBar
    private var cacheId: Int = -1
    private var selectedCategoryId: Int = -1
    private var cache: Cache? = null
    private lateinit var selectImageButton: Button
    private lateinit var cacheImageView: ImageView
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_cache)

        UserData.initialize(this)
        GroupData.initialize(this)
        CategoryData.initialize(this)
        CacheGroupData.initialize(this)
        CacheCategoryData.initialize(this)
        UserCacheData.initialize(this)
        UserGroupData.initialize(this)

        cacheId = intent.getIntExtra("cacheId", -1)
        cache = CacheData.get(cacheId)

        cacheNameInput = findViewById(R.id.cacheNameInput)
        cacheDescriptionInput = findViewById(R.id.cacheDescriptionInput)
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
        difficultySeekBar = findViewById(R.id.difficultySeekBar)
        difficultyLabel = findViewById(R.id.difficultyLabel)
        selectImageButton = findViewById(R.id.selectImageButton)


        val users = UserData.getAll().filter{
            cache?.creatorId!=it.id &&
            !UserData.isAdministrator(it.id)
        }
        val groups = GroupData.getAll()
        var categories = CategoryData.getAll()

        val userAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, users.map { it.username })
        userListView.adapter = userAdapter
        userListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE

        val groupAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, groups.map { it.name })
        groupListView.adapter = groupAdapter
        groupListView.choiceMode = ListView.CHOICE_MODE_MULTIPLE
        if(cache?.private==1){
            assignGroupButton.visibility = Button.VISIBLE
            assignPersonButton.visibility = Button.VISIBLE
        }
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories.map { it.name })
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter

        selectImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        cacheImageView.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }

        cache?.let {
            cacheNameInput.setText(it.name)
            cacheDescriptionInput.setText(it.description)
            privateCacheCheckbox.isChecked = it.private == 1

            val imagePath = File(filesDir, "cache-images/${it.id}.png")
            if (imagePath.exists()) {
                val bitmap = BitmapFactory.decodeFile(imagePath.absolutePath)
                cacheImageView.setImageBitmap(bitmap)
                cacheImageView.visibility = View.VISIBLE
            }

            val assignedCategory = CacheCategoryData.getAll().find { cacheCategory -> cacheCategory.cacheId == it.id }
            assignedCategory?.let { category ->
                val categoryPosition = categories.indexOfFirst { it.id == category.categoryId }
                if (categoryPosition != -1) {
                    categorySpinner.setSelection(categoryPosition)
                }
            }

            val assignedUsers = UserCacheData.getAll().filter { userCache -> userCache.cacheId == it.id && userCache.available==1 }
            for (i in 0 until userAdapter.count) {
                val user = users[i]
                if (assignedUsers.any { userCache -> userCache.userId == user.id &&userCache.available==1}) {
                    userListView.setItemChecked(i, true)
                }
            }

            val assignedGroups = CacheGroupData.getAll().filter { cacheGroup -> cacheGroup.cacheId == it.id }
            for (i in 0 until groupAdapter.count) {
                val group = groups[i]
                if (assignedGroups.any { cacheGroup -> cacheGroup.groupId == group.id }) {
                    groupListView.setItemChecked(i, true)
                }
            }
        }

        assignGroupButton.setOnClickListener {
            showSelectionDialog(groups.map { it.name }, groupListView)
        }

        assignPersonButton.setOnClickListener {
            showSelectionDialog(users.map { it.username }, userListView)
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
        categorySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                selectedCategoryId = categories[position].id
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedCategoryId = -1
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

        addCategoryButton.setOnClickListener {
            val newCategoryName = newCategoryInput.text.toString()
            val newCategoryDescription = categoryDescriptionInput.text.toString()
            if (newCategoryName.isNotEmpty()) {
                val newCategory = Category(0, newCategoryName, newCategoryDescription)
                val categoryId = CategoryData.add(newCategory)
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
        cache?.let {
            cacheNameInput.setText(it.name)
            cacheDescriptionInput.setText(it.description)
            privateCacheCheckbox.isChecked = it.private == 1
            difficultySeekBar.progress = it.difficulty?.times(100)?.toInt() ?: 250
            difficultyLabel.text = "Difficulty Rating: ${difficultySeekBar.progress/100.0}"
            val assignedUsers = UserCacheData.getAll().filter { userCache -> userCache.cacheId == it.id }
            for (i in 0 until userAdapter.count) {
                val user = users[i]
                if (assignedUsers.any { userCache -> userCache.userId == user.id &&userCache.available==1}) {
                    userListView.setItemChecked(i, true)
                }
            }

            val assignedGroups = CacheGroupData.getAll().filter { cacheGroup -> cacheGroup.cacheId == it.id }
            for (i in 0 until groupAdapter.count) {
                val group = groups[i]
                if (assignedGroups.any { cacheGroup -> cacheGroup.groupId == group.id }) {
                    groupListView.setItemChecked(i, true)
                }
            }

        }


        assignGroupButton.setOnClickListener {
            showSelectionDialog(groups.map { it.name }, groupListView)
        }

        assignPersonButton.setOnClickListener {
            showSelectionDialog(users.map { it.username }, userListView)
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
            val difficulty = difficultySeekBar.progress.toString().toDoubleOrNull() ?: 0.0

            val selectedUsers = userListView.checkedItemPositions
                .let { positions -> users.filterIndexed { index, _ -> positions[index] } }

            val selectedGroups = groupListView.checkedItemPositions
                .let { positions -> groups.filterIndexed { index, _ -> positions[index] } }

            val private = privateCacheCheckbox.isChecked

            cache?.let {
                it.name = name
                it.description = description
                it.private = if (private) 1 else 0
                it.difficulty = difficulty / 100
                it.updatedAt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())
                CacheData.update(it)
                CacheCategoryData.getAll().find { cacheCategory -> cacheCategory.cacheId == it.id }
                    ?.let { it1 -> CacheCategory(it.id, it1.categoryId) }
                    ?.let { it2 -> CacheCategoryData.update(it2, selectedCategoryId) }
                val userCacheDAO = UserCacheDAO(this)
                val cacheGroupDAO = CacheGroupDAO(this)

                if (privateCacheCheckbox.isChecked) {
                    // Delete all cache groups
                    val allCacheGroups = CacheGroupData.getAll().filter { cacheGroup -> cacheGroup.cacheId == it.id }
                    allCacheGroups.forEach { cacheGroup ->
                        // Check if the group is in the selected groups list
                        if (selectedGroups.none { group -> group.id == cacheGroup.groupId }) {
                            cacheGroupDAO.deleteCacheGroup(cacheGroup.cacheId, cacheGroup.groupId)
                        }
                    }
                    val allUserCaches = userCacheDAO.getAllUserCaches().filter { userCache -> userCache.cacheId == it.id&&userCache.userId!=it.creatorId }
                    // Update all user caches to available
                    allUserCaches.forEach { userCache ->
                        // Check if the user is in the selected users list
                        if (selectedUsers.any { user -> user.id == userCache.userId }) {
                            userCache.available = 1
                            userCacheDAO.updateUserCache(userCache)
                        } // If not, set available to 0
                         else {
                            if (userCache.found == 1) {
                               // If the user has found the cache, set available to 1
                                userCache.available = 1
                            } else {
                                userCache.available = 0
                            }
                            userCacheDAO.updateUserCache(userCache)
                        }
                    }

                    // Add new user caches to the selected users list
                    selectedUsers.forEach { user ->
                        if (UserCacheData.getAll().none { userCache -> userCache.userId == user.id && userCache.cacheId == it.id }) {
                            userCacheDAO.addUserCache(UserCache(user.id, it.id))
                        }
                    }
                    // Add new cache groups to the selected groups list
                    selectedGroups.forEach { group ->
                        if (CacheGroupData.getAll().none { cacheGroup -> cacheGroup.groupId == group.id && cacheGroup.cacheId == it.id }) {
                            cacheGroupDAO.addCacheGroup(CacheGroup(it.id, group.id))
                        }
                    }
                } else {
                    val allUsers = UserData.getAll()
                    for(user in allUsers){
                        if(user.id!=cache?.creatorId) {
                            userCacheDAO.addUserCache(UserCache(user.id, it.id))
                        }
                    }
                }
                selectedImageUri?.let { uri ->
                    val bitmap = (cacheImageView.drawable as BitmapDrawable).bitmap
                    saveImageToInternalStorage(this, bitmap, "cache-images", "${it.id}")
                }

                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
                finish()
            }
        }
    }
    private fun showSelectionDialog(items: List<String>, listView: ListView) {
        val dialogView = layoutInflater.inflate(R.layout.user_group_selection, null)
        val selectionListView: ListView = dialogView.findViewById(R.id.selectionListView)
        val closeButton: Button = dialogView.findViewById(R.id.closeButton)
        val selectAllButton: Button = dialogView.findViewById(R.id.selectAllButton)
        val deselectAllButton: Button = dialogView.findViewById(R.id.deselectAllButton)
        val searchInput: EditText = dialogView.findViewById(R.id.searchInput)

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice, items)
        selectionListView.adapter = adapter

        // Set the checked items based on the original ListView
        for (i in 0 until listView.count) {
            if (listView.isItemChecked(i)) {
                selectionListView.setItemChecked(i, true)
            }
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        closeButton.setOnClickListener {
            // Update the original ListView with the selected items
            for (i in 0 until selectionListView.count) {
                listView.setItemChecked(i, selectionListView.isItemChecked(i))
            }
            dialog.dismiss()
        }

        selectAllButton.setOnClickListener {
            for (i in 0 until selectionListView.count) {
                selectionListView.setItemChecked(i, true)
            }
        }

        deselectAllButton.setOnClickListener {
            for (i in 0 until selectionListView.count) {
                selectionListView.setItemChecked(i, false)
            }
        }

        searchInput.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                adapter.filter.filter(s)
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        dialog.show()
    }
    private fun saveImageToInternalStorage(context: Context, bitmap: Bitmap, directoryName: String, imageName: String): String? {
        val directory = File(context.filesDir, directoryName)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, "$imageName.png")
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
        const val REQUEST_IMAGE_PICK = 1
    }
}