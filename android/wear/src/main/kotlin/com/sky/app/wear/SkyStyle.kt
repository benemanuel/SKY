package com.sky.app.wear

import android.content.Context
import android.content.SharedPreferences
import com.sky.app.domain.CenterStyle

/**
 * Center-display options for the SKY watch face, plus a tiny SharedPreferences
 * helper to persist the user's choice.
 *
 * Wear OS 5 (e.g. Galaxy Watch 7) does not surface the system "Customize" editor
 * for code-based watch faces, so the choice is stored in our own preferences and
 * changed either from a launchable on-watch settings app ([SkySettingsActivity]) or
 * remotely from the phone over the Wearable Data Layer ([WatchSettingsListenerService]).
 * The renderer reads it (and listens for changes).
 *
 * The option ids/labels and the Data Layer path/key live in [CenterStyle] in `:core`
 * so the phone and watch agree on them.
 */
object SkyStyle {

    const val PREFS = "sky_watchface"
    const val KEY_CENTER = "center"

    const val CENTER_NONE = CenterStyle.NONE
    const val CENTER_TIME = CenterStyle.TIME
    const val CENTER_MINI_RINGS = CenterStyle.MINI_RINGS
    const val CENTER_TWO_RINGS = CenterStyle.TWO_RINGS

    /** Options in display order, as (id, label) pairs. */
    val CENTER_OPTIONS = CenterStyle.OPTIONS

    fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun centerOption(context: Context): String =
        prefs(context).getString(KEY_CENTER, CenterStyle.DEFAULT) ?: CenterStyle.DEFAULT

    fun setCenterOption(context: Context, id: String) {
        prefs(context).edit().putString(KEY_CENTER, id).apply()
    }
}
