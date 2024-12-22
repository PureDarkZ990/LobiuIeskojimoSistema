package com.example.lobiupaieskossistema.database

object FriendshipTable {
    const val TABLE_NAME = "friendships"
    const val ID = "id"
    const val USER_ID = "user_id"
    const val FRIEND_ID = "friend_id"
    const val STATUS = "status"
    const val CREATED_AT = "created_at"
    const val UPDATED_AT = "updated_at"
    const val BLOCKED_BY = "blocked_by"
    const val BLOCK_REASON = "block_reason"
    const val LAST_INTERACTION = "last_interaction"

    val CREATE_TABLE = """
        CREATE TABLE IF NOT EXISTS $TABLE_NAME (
            $ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $USER_ID INTEGER NOT NULL,
            $FRIEND_ID INTEGER NOT NULL,
            $STATUS INTEGER NOT NULL DEFAULT 0,
            $CREATED_AT TEXT NOT NULL,
            $UPDATED_AT TEXT,
            $BLOCKED_BY INTEGER,
            $BLOCK_REASON TEXT,
            $LAST_INTERACTION TEXT,
            FOREIGN KEY ($USER_ID) REFERENCES users(id) ON DELETE CASCADE,
            FOREIGN KEY ($FRIEND_ID) REFERENCES users(id) ON DELETE CASCADE,
            FOREIGN KEY ($BLOCKED_BY) REFERENCES users(id) ON DELETE SET NULL
        )
    """.trimIndent()
}


