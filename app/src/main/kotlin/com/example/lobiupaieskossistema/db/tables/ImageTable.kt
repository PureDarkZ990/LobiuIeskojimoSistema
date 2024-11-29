package com.example.lobiupaieskossistema.database

object ImageTable {
    // Images table
    const val TABLE_NAME = "images"
    const val ID = "id"
    const val PATH = "path"
    const val CACHE_ID = "cache_id"
    const val DESCRIPTION = "description"

    const val CREATE_TABLE = """
        CREATE TABLE $TABLE_NAME (
            $ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $PATH TEXT DEFAULT 'default_image.png',
            $CACHE_ID INTEGER,
            $DESCRIPTION TEXT,
            FOREIGN KEY($CACHE_ID) REFERENCES ${CacheTable.TABLE_NAME}(${CacheTable.ID})
        )
    """
}