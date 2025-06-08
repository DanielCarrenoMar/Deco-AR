package com.app.homear.core.utils

import android.content.Context

class SharedPreferenceHelper(
    private val context: Context,
) {
    companion object {
        private const val PREF_NAME = "HOMEAR_PREF"
    }

    fun saveStringData(key: String, value: String?) {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString(key, value)
            apply()
        }
    }

    fun getStringData(key: String): String? {
        val sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getString(key, null)
    }
}