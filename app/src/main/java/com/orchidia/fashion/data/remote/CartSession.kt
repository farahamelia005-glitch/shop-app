package com.orchidia.fashion.data.remote

import android.content.Context
import java.util.UUID

object CartSession {

    private const val PREFS = "orchidia_cart_session"
    private const val KEY_SESSION_ID = "session_id"

    fun getSessionId(context: Context): String {
        val prefs = context.getSharedPreferences(
            PREFS,
            Context.MODE_PRIVATE
        )

        val existing = prefs.getString(KEY_SESSION_ID, null)

        if (existing != null) {
            return existing
        }

        val newId = UUID.randomUUID().toString()

        prefs.edit()
            .putString(KEY_SESSION_ID, newId)
            .apply()

        return newId
    }
}