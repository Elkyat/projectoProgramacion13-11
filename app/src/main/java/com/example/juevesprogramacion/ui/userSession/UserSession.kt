package com.example.juevesprogramacion.data.local

import android.content.Context

class UserSession(context: Context) {
    private val prefs = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)

    fun saveUser(email: String, password: String) {
        prefs.edit()
            .putString("userEmail", email)
            .putString("userPassword", password)
            .putBoolean("isGuest", false)
            .putBoolean("isLoggedIn", true)
            .apply()
    }

    fun setGuestMode() {
        prefs.edit()
            .clear()
            .putBoolean("isGuest", true)
            .putBoolean("isLoggedIn", true)
            .apply()
    }

    fun isGuest(): Boolean = prefs.getBoolean("isGuest", false)

    fun setLoggedIn(loggedIn: Boolean) {
        prefs.edit().putBoolean("isLoggedIn", loggedIn).apply()
    }

    fun isLoggedIn(): Boolean = prefs.getBoolean("isLoggedIn", false)

    fun getEmail(): String? = prefs.getString("userEmail", null)

    fun getPassword(): String? = prefs.getString("userPassword", null)

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
