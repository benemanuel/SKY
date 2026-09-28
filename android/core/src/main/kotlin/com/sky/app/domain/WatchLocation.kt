package com.sky.app.domain

import kotlin.math.roundToLong

/**
 * Shared contract for pushing the phone's location to the watch face over the
 * Wearable Data Layer. The watch has no location permission of its own (and polling
 * GPS on the watch would cost battery), so the phone sends its fix and the watch
 * persists it; until one arrives the watch uses the Jerusalem [DEFAULT_LAT]/[DEFAULT_LON].
 */
object WatchLocation {

    /** Jerusalem — matches the web app and phone defaults. */
    const val DEFAULT_LAT = 31.7781
    const val DEFAULT_LON = 35.2360

    /** Data Layer DataItem path carrying the location from phone to watch. */
    const val PATH = "/sky/location"

    const val KEY_LAT = "lat"
    const val KEY_LON = "lon"

    /**
     * Coordinates are rounded to 0.01° (~1 km) before sending. Sunrise/sunset shift by
     * only seconds over that distance, and identical DataItems are not re-delivered by
     * the Data Layer, so GPS jitter doesn't wake the watch.
     */
    fun round(value: Double): Double = (value * 100.0).roundToLong() / 100.0

    fun isValid(lat: Double, lon: Double): Boolean =
        !lat.isNaN() && !lon.isNaN() && lat in -90.0..90.0 && lon in -180.0..180.0
}
