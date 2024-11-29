package com.example.lobiupaieskossistema

object CategoryData {
    private val categories = mutableListOf("Urbanistinis", "Natūralus", "Humoristinis")

    fun getAllCategories(): List<String> {
        return categories
    }

    fun addCategory(category: String) {
        categories.add(category)
    }
}