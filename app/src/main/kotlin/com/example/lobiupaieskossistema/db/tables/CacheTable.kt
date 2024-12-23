package com.example.lobiupaieskossistema.database

object CacheTable {
    const val TABLE_NAME = "caches"
    const val ID = "id"
    const val NAME = "name"
    const val DESCRIPTION = "description"
    const val XCOORDINATE = "xcoordinate"
    const val YCOORDINATE = "ycoordinate"
    const val RATING = "rating"
    const val DIFFICULTY = "difficulty"
    const val APPROVED = "approved"
    const val CREATED_AT = "created_at"
    const val UPDATED_AT = "updated_at"
    const val PRIVATE = "private"
    const val THEME_ID = "theme_id"
    const val CREATOR_ID = "user_id"
    const val ZONE = "zone"
    const val SHOWN = "shown"
    const val PASSWORD = "password"

    const val CREATE_TABLE = """
    CREATE TABLE $TABLE_NAME (
        $ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $NAME TEXT NOT NULL,
        $DESCRIPTION TEXT DEFAULT 'My cache',
        $XCOORDINATE REAL NOT NULL,
        $YCOORDINATE REAL NOT NULL,
        $ZONE INTEGER DEFAULT 100,
        $RATING REAL,
        $DIFFICULTY REAL,
        $SHOWN INTEGER DEFAULT 1,
        $APPROVED INTEGER DEFAULT 0,
        $CREATED_AT TEXT,
        $UPDATED_AT TEXT,
        $PRIVATE INTEGER DEFAULT 0,
        $THEME_ID INTEGER,
        $CREATOR_ID INTEGER,
        $PASSWORD TEXT,
        UNIQUE($XCOORDINATE, $YCOORDINATE)
    )
"""}