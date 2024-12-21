package com.example.lobiupaieskossistema.database

import com.example.lobiupaieskossistema.database.CacheTable.XCOORDINATE
import com.example.lobiupaieskossistema.database.CacheTable.YCOORDINATE

object UserGroupTable {
    const val TABLE_NAME = "user_groups"
    const val USER_ID = "user_id"
    const val GROUP_ID = "group_id"

    const val CREATE_TABLE = """
        CREATE TABLE $TABLE_NAME (
            $USER_ID INTEGER,
            $GROUP_ID INTEGER,
            PRIMARY KEY ($USER_ID, $GROUP_ID),
            FOREIGN KEY($USER_ID) REFERENCES ${UserTable.TABLE_NAME}(${UserTable.ID}),
            FOREIGN KEY($GROUP_ID) REFERENCES ${GroupTable.TABLE_NAME}(${GroupTable.ID}),
            UNIQUE($USER_ID, $GROUP_ID)
        )
    """
}