package com.example.groupproject.model

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class LocalStorage(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("RunAppPrefs", Context.MODE_PRIVATE)

    fun saveLoggedInUser(username: String) {
        prefs.edit { putString("loggedInUser", username) }
    }

    fun getLoggedInUser(): String? {
        return prefs.getString("loggedInUser", null)
    }

    fun saveDistanceUnit(unit: String) {
        prefs.edit { putString("distanceUnit", unit) }
    }

    fun getDistanceUnit(): String {
        return prefs.getString("distanceUnit", "miles") ?: "miles"
    }
}