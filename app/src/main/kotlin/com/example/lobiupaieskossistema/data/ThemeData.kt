package com.example.lobiupaieskossistema.data

import android.content.Context
import com.example.lobiupaieskossistema.dao.ThemeDAO
import com.example.lobiupaieskossistema.models.Theme

object ThemeData {
    private lateinit var themeDAO: ThemeDAO

    fun initialize(context: Context) {
        themeDAO = ThemeDAO(context)
    }

    fun add(theme: Theme): Long {
        return themeDAO.addTheme(theme)
    }
    fun update(theme: Theme) {
        themeDAO.updateTheme(theme)
    }
    fun findThemeByCacheId(cacheId: Int): Theme? {
        return themeDAO.findThemeByCacheId(cacheId)
    }
    fun getAll(): List<Theme> {
        return themeDAO.getAllThemes()
    }

    fun delete(themedId: Int) {
        themeDAO.deleteTheme(themedId)
    }
}