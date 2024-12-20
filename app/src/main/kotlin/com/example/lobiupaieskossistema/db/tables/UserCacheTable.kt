package com.example.lobiupaieskossistema.database

object UserCacheTable {
    // User Caches table
    const val TABLE_NAME = "user_caches"
    const val USER_ID = "user_id"
    const val CACHE_ID = "cache_id"
    const val FOUND = "found"
    const val RATING = "rating"
    const val AVAILABLE = "available"

    const val CREATE_TABLE = """
            CREATE TABLE $TABLE_NAME (
                $USER_ID INTEGER,
                $CACHE_ID INTEGER,
                $FOUND INTEGER DEFAULT 0,
                $RATING REAL,
                $AVAILABLE INTEGER DEFAULT 1,
                PRIMARY KEY ($USER_ID, $CACHE_ID),
                FOREIGN KEY($USER_ID) REFERENCES ${UserTable.TABLE_NAME}(${UserTable.ID}),
                FOREIGN KEY($CACHE_ID) REFERENCES ${CacheTable.TABLE_NAME}(${CacheTable.ID}),
                 UNIQUE($USER_ID, $CACHE_ID)
            )
        """
}