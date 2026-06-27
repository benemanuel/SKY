package com.sky.app.domain

/**
 * Single source of truth for the watch face "Center" option, shared between the
 * phone app (`:app`) and the watch app (`:wear`).
 *
 * The watch stores the chosen id in its own SharedPreferences and renders from it;
 * the phone edits it remotely over the Wearable Data Layer. Both sides agree on the
 * option ids, the Data Layer [PATH], and the [KEY] via this object so they never
 * drift apart.
 */
object CenterStyle {

    const val NONE = "none"
    const val TIME = "time"
    const val MINI_RINGS = "mini_rings"
    const val TWO_RINGS = "two_rings"

    const val DEFAULT = NONE

    /** Options in display order, as (id, label) pairs. */
    val OPTIONS = listOf(
        NONE to "None",
        TIME to "Time",
        MINI_RINGS to "Mini rings",
        TWO_RINGS to "Two rings",
    )

    /** True if [id] is one of the known option ids. */
    fun isValid(id: String?): Boolean = id != null && OPTIONS.any { it.first == id }

    /** Data Layer DataItem path carrying the center choice between phone and watch. */
    const val PATH = "/sky/center"

    /** Key within the DataItem's data map holding the center option id. */
    const val KEY = "center"
}
