package com.example.lobiupaieskossistema.database

object CacheCategoryTable {
    // Cache Categories table
    const val TABLE_NAME = "cache_categories"
    const val CACHE_ID = "cache_id"
    const val CATEGORY_ID = "category_id"

    const val CREATE_TABLE = """
    CREATE TABLE $TABLE_NAME (
        $CACHE_ID INTEGER,
        $CATEGORY_ID INTEGER,
        FOREIGN KEY($CACHE_ID) REFERENCES ${CacheTable.TABLE_NAME}(${CacheTable.ID}),
        FOREIGN KEY($CATEGORY_ID) REFERENCES ${CategoryTable.TABLE_NAME}(${CategoryTable.ID})
    )
"""
}