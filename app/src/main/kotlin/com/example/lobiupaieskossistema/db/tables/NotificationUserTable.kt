package com.example.lobiupaieskossistema.database

object NotificationUserTable {
    // Notification Users table
    const val TABLE_NAME = "notification_users"
    const val NOTIFICATION_ID = "notification_id"
    const val USER_ID = "user_id"
    const val READ = "read"

    const val CREATE_TABLE = """
            CREATE TABLE $TABLE_NAME (
                $NOTIFICATION_ID INTEGER,
                $USER_ID INTEGER,
                $READ INTEGER,
                PRIMARY KEY ($NOTIFICATION_ID, $USER_ID),
                FOREIGN KEY($NOTIFICATION_ID) REFERENCES ${NotificationTable.TABLE_NAME}(${NotificationTable.ID}),
                FOREIGN KEY($USER_ID) REFERENCES ${UserTable.TABLE_NAME}(${UserTable.ID})
            )
        """
}