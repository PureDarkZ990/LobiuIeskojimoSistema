package com.example.lobiupaieskossistema.database

object GroupTable {
    const val TABLE_NAME = "groups"
    const val ID = "id"
    const val NAME = "name"
    const val DESCRIPTION = "description"
    const val ACTIVITY = "activity"
    const val XACTIVITY = "xactivity"
    const val YACTIVITY = "yactivity"
    const val TOTAL_FOUND_CACHES = "total_caches"
    const val CREATOR_ID = "user_id"
    const val CREATED_AT = "created_at"
    const val UPDATED_AT = "updated_at"

    const val CREATE_TABLE = """
        CREATE TABLE $TABLE_NAME (
            $ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $NAME TEXT NOT NULL,
            $DESCRIPTION TEXT DEFAULT 'My description',
            $ACTIVITY TEXT,
            $XACTIVITY REAL,
            $YACTIVITY REAL,
            $TOTAL_FOUND_CACHES INTEGER DEFAULT 0,
            $CREATOR_ID INTEGER,
            $CREATED_AT TEXT,
            $UPDATED_AT TEXT
        )
    """
}