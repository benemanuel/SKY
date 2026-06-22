package com.sky.app.wear

import android.content.Context
import android.content.SharedPreferences

/**
 * Center-display options for the SKY watch face, plus a tiny SharedPreferences
 * helper to persist the user's choice.
 *
 * Wear OS 5 (e.g. Galaxy Watch 7) does not surface the system "Customize" editor
 * for code-based watch faces, so the choice is stored in our own preferences and
 * changed from a launchable on-watch settings app ([SkySettingsActivity]) rather
 * than the watch-face picker. The renderer reads it (and listens for changes).
 */
object SkyStyle {

    const val PREFS = "sky_watchface"
    const val KEY_CENTER = "center"

    const val CENTER_NONE = "none"
    const val CENTER_TIME = "time"
    const val CENTER_MINI_RINGS = "mini_rings"
    const val CENTER_TWO_RINGS = "two_rings"

    /** Options in display order, as (id, label) pairs. */
    val CENTER_OPTIONS = listOf(
        CENTER_NONE to "None",
        CENTER_TIME to "Time",
        CENTER_MINI_RINGS to "Mini rings",
        CENTER_TWO_RINGS to "Two rings",
    )

    fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun centerOption(context: Context): String =
        prefs(context).getString(KEY_CENTER, CENTER_NONE) ?: CENTER_NONE

    fun setCenterOption(context: Context, id: String) {
        prefs(context).edit().putString(KEY_CENTER, id).apply()
    }
}
