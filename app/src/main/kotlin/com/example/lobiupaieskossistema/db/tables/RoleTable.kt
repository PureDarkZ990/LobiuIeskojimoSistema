package com.example.lobiupaieskossistema.database

object RoleTable {
    // Roles table
    const val TABLE_NAME = "roles"
    const val ID = "id"
    const val NAME = "name"
    const val DEFAULT_ROLE = "regular"
    const val ADMIN_ROLE = "admin"

    const val CREATE_TABLE = """
        CREATE TABLE $TABLE_NAME (
            $ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $NAME TEXT UNIQUE NOT NULL CHECK($NAME IN ('$ADMIN_ROLE', '$DEFAULT_ROLE'))
        );
    """
}
