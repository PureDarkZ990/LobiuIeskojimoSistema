package com.example.lobiupaieskossistema.database

object ThemeTable {
    // Themes table
    const val TABLE_NAME = "themes"
    const val ID = "id"
    const val CACHE_ID = "cache_id"
    const val DESCRIPTION = "description"
    const val ABSOLUTE_TIME = "abs_time"
    const val ENDING_TIME = "ending_time"
    const val TIME = "time"

    const val CREATE_TABLE = """
        CREATE TABLE $TABLE_NAME (
            $ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $CACHE_ID INTEGER UNIQUE,
            $DESCRIPTION TEXT,
            $ABSOLUTE_TIME INTEGER DEFAULT 0,
            $ENDING_TIME INTEGER DEFAULT 0,
            $TIME INTEGER DEFAULT 0,
            FOREIGN KEY($CACHE_ID) REFERENCES ${CacheTable.TABLE_NAME}(${CacheTable.ID})
        );
    """
}
