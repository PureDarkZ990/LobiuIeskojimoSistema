package com.example.lobiupaieskossistema.database

object StatusTable {
    // Status table
    const val TABLE_NAME = "status"
    const val ID = "id"
    const val NAME = "name"

    const val CREATE_TABLE = """
        CREATE TABLE $TABLE_NAME (
            $ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $NAME TEXT UNIQUE CHECK($NAME IN ('active', 'removed', 'deactivated'))
        )
    """
}