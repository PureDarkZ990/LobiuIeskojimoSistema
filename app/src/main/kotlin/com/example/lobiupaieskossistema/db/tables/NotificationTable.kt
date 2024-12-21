package com.example.lobiupaieskossistema.database

object NotificationTable {
    // Notifications table
    const val TABLE_NAME = "notifications"
    const val ID = "id"
    const val MESSAGE = "message"
    const val DATE = "date"

    const val CREATE_TABLE = """
        CREATE TABLE $TABLE_NAME (
            $ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $MESSAGE TEXT,
            $DATE TEXT
        )
    """
}