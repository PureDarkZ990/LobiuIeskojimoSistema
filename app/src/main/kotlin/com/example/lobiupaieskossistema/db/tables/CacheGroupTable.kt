package com.example.lobiupaieskossistema.database

object CacheGroupTable {
    // Cache Groups table
    const val TABLE_NAME = "cache_groups"
    const val CACHE_ID = "cache_id"
    const val GROUP_ID = "group_id"

    const val CREATE_TABLE = """
        CREATE TABLE $TABLE_NAME (
            $CACHE_ID INTEGER,
            $GROUP_ID INTEGER,
            PRIMARY KEY ($CACHE_ID, $GROUP_ID),
            FOREIGN KEY($CACHE_ID) REFERENCES ${CacheTable.TABLE_NAME}(${CacheTable.ID}),
            FOREIGN KEY($GROUP_ID) REFERENCES ${GroupTable.TABLE_NAME}(${GroupTable.ID}),
            UNIQUE($CACHE_ID, $GROUP_ID)
        )
    """
}