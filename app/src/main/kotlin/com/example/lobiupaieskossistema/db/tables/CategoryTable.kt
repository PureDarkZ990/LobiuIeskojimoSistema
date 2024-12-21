package com.example.lobiupaieskossistema.database

object CategoryTable {
    // Categories table
    const val TABLE_NAME = "categories"
    const val ID = "id"
    const val NAME = "name"
    const val DESCRIPTION = "description"

    const val CREATE_TABLE = """
        CREATE TABLE $TABLE_NAME (
            $ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $NAME TEXT UNIQUE NOT NULL,
            $DESCRIPTION TEXT DEFAULT 'My category'
        )
    """
}