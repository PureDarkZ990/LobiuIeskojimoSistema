package com.example.lobiupaieskossistema.database

object UserTable {
    // Users table
    const val TABLE_NAME= "users"
    const val ID = "id"
    const val NAME = "username"
    const val HASHED_PASSWORD = "hashed_password"
    const val EMAIL = "email"
    const val CREATED_AT = "created_at"
    const val ROLE_ID = "role_id"
    const val STATUS_ID = "status_id"
    const val LAST_LOGIN = "last_login"
    const val POINTS = "points"
    const val SOUND_ENABLED = "sound_enabled"
    const val VIBRATION_ENABLED = "vibration_enabled"
    const val DARK_MODE = "dark_mode"
    const val PRIVATE = "private"
    const val LOCATION = "location"
    const val PHONE = "phone"
    const val IMAGE = "image_id"
    const val UPDATED_AT = "updated_at"
    const val TOKEN = "token"
    const val TOKEN_EXPIRE = "token_expire"

    const val CREATE_TABLE = """
    CREATE TABLE $TABLE_NAME (
        $ID INTEGER PRIMARY KEY AUTOINCREMENT,
        $NAME TEXT UNIQUE NOT NULL,
        $HASHED_PASSWORD TEXT NOT NULL,
        $EMAIL TEXT UNIQUE NOT NULL,
        $CREATED_AT TEXT,
        $ROLE_ID INTEGER DEFAULT 1,
        $STATUS_ID INTEGER,
        $LAST_LOGIN TEXT,
        $POINTS INTEGER,
        $SOUND_ENABLED INTEGER DEFAULT 0,
        $VIBRATION_ENABLED INTEGER DEFAULT 0,
        $DARK_MODE INTEGER DEFAULT 0,
        $PRIVATE INTEGER DEFAULT 0,
        $LOCATION TEXT,
        $PHONE TEXT UNIQUE,
        $IMAGE INTEGER,
        $UPDATED_AT TEXT,
        $TOKEN TEXT,
        $TOKEN_EXPIRE TEXT,
        FOREIGN KEY($ROLE_ID) REFERENCES roles(id),
        FOREIGN KEY($STATUS_ID) REFERENCES status(id)
    )
"""
}