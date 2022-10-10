package com.babanomania.pdfscanner.utils

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.babanomania.pdfscanner.R


fun Context.applyTheme(pref: String? = null) {
    val themePref = pref ?: PreferenceManager
        .getDefaultSharedPreferences(this)
        .getString(getString(R.string.setting_theme_key), getString(R.string.setting_theme_default))

    val themes = resources.getStringArray(R.array.theme_values)

    val theme = when (themePref) {
        themes[0] -> {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        themes[1] -> {
            AppCompatDelegate.MODE_NIGHT_NO
        }
        themes[2] -> {
            AppCompatDelegate.MODE_NIGHT_YES
        }
        else -> {
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    }

    AppCompatDelegate.setDefaultNightMode(theme)
}
