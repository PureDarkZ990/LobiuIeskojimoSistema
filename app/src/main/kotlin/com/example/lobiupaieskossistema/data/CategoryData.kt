
package com.example.lobiupaieskossistema.data

import android.content.Context
import com.example.lobiupaieskossistema.dao.CategoryDAO
import com.example.lobiupaieskossistema.models.Category

object CategoryData {
    private lateinit var categoryDAO: CategoryDAO
    fun initialize(context: Context) {
        categoryDAO = CategoryDAO(context)
        addHardcodedEntries()
    }

    private fun addHardcodedEntries() {
        val userList = listOf(
           Category(0,"Urbanistinis"),
            Category(1,"Natūralus"),
                Category(2,"Humoristinis")
        )
        userList.forEach { categoryDAO.addCategory(it) }
    }

    fun get(id: Int): Category? {
        return categoryDAO.findCategoryById(id)
    }

    fun update(user: Category) {
        categoryDAO.updateCategory(user)
    }

    fun delete(id: Int) {
        categoryDAO.deleteCategory(id)
    }

    fun add(cache: Category): Long {
       return categoryDAO.addCategory(cache)
    }

    fun getAll(): List<Category> {
        return categoryDAO.getAllCategories()
    }
}