package com.example.lobiupaieskossistema.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.lobiupaieskossistema.DatabaseHelper
import com.example.lobiupaieskossistema.models.Role
import com.example.lobiupaieskossistema.database.RoleTable

class RoleDAO(context: Context) {

    private val dbHelper = DatabaseHelper(context)

    fun addRole(role: Role): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(RoleTable.NAME, role.name)
        }
        return db.insert(RoleTable.TABLE_NAME, null, values)
    }

    fun getAllRoles(): List<Role> {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            RoleTable.TABLE_NAME,
            null, null, null, null, null, null
        )
        val roles = mutableListOf<Role>()
        with(cursor) {
            while (moveToNext()) {
                val role = Role(
                    id = getInt(getColumnIndexOrThrow(RoleTable.ID)),
                    name = getString(getColumnIndexOrThrow(RoleTable.NAME))
                )
                roles.add(role)
            }
        }
        cursor.close()
        return roles
    }
    fun findRoleById(roleId: Int): Role? {
        val db = dbHelper.readableDatabase
        val cursor: Cursor = db.query(
            RoleTable.TABLE_NAME,
            null,
            "${RoleTable.ID} = ?",
            arrayOf(roleId.toString()),
            null, null, null
        )
        var role: Role? = null
        if (cursor.moveToFirst()) {
            role = Role(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(RoleTable.ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(RoleTable.NAME))
            )
        }
        cursor.close()
        return role
    }
    fun updateRole(role: Role): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(RoleTable.NAME, role.name)
        }
        return db.update(RoleTable.TABLE_NAME, values, "${RoleTable.ID} = ?", arrayOf(role.id.toString()))
    }

    fun deleteRole(roleId: Int): Int {
        val db = dbHelper.writableDatabase
        return db.delete(RoleTable.TABLE_NAME, "${RoleTable.ID} = ?", arrayOf(roleId.toString()))
    }
}