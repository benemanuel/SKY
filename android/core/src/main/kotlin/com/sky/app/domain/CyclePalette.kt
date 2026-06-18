package com.sky.app.domain

import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Shared color wheel (colors.md) and the four-ring model used by the watch
 * face, the widget, and the phone app. Colors are ARGB Ints so every surface
 * (Compose, android.graphics, Wear Canvas) can consume them.
 *
 *   season — 3 segments (thirds)
 *   lunar  — 4 segments (quarters of the ~29.5-day cycle)
 *   week   — 7 segments (days)
 *   hour   — 12 segments (temporal day/night hours)
 */
object CyclePalette {

    // 12-hue color wheel
    val RED = 0xFFE53935.toInt()
    val RED_ORANGE = 0xFFFF5722.toInt()
    val ORANGE = 0xFFFF9800.toInt()
    val YELLOW_ORANGE = 0xFFFFB300.toInt()
    val YELLOW = 0xFFFFEB3B.toInt()
    val YELLOW_GREEN = 0xFFC0CA33.toInt()
    val GREEN = 0xFF4CAF50.toInt()
    val BLUE_GREEN = 0xFF26A69A.toInt()
    val BLUE = 0xFF2196F3.toInt()
    val BLUE_VIOLET = 0xFF6A4FC9.toInt()
    val VIOLET = 0xFF9C27B0.toInt()
    val RED_VIOLET = 0xFFD81B60.toInt()

    // Hour ring (00:00..11:00) and lunar-day wheel.
    val WHEEL = intArrayOf(
        BLUE_VIOLET, VIOLET, RED_VIOLET, RED, RED_ORANGE, ORANGE,
        YELLOW_ORANGE, YELLOW, YELLOW_GREEN, GREEN, BLUE_GREEN, BLUE
    )

    // Weekday colors, index 0 = Sunday .. 6 = Saturday (no week mapping in colors.md).
    val WEEKDAY = intArrayOf(
        0xFFE53935.toInt(), // Sunday — red
        0xFFF4D03F.toInt(), // Monday — yellow
        0xFFF06292.toInt(), // Tuesday — pink
        0xFF57C84D.toInt(), // Wednesday — green
        0xFFFF9800.toInt(), // Thursday — orange
        0xFF4FC3F7.toInt(), // Friday — light blue
        0xFF9575CD.toInt()  // Saturday — purple
    )

    fun seasonTriple(name: String): IntArray = when (name) {
        HebrewStrings.SPRING -> intArrayOf(VIOLET, RED_VIOLET, RED)
        HebrewStrings.SUMMER -> intArrayOf(RED_ORANGE, ORANGE, YELLOW_ORANGE)
        HebrewStrings.FALL -> intArrayOf(YELLOW, YELLOW_GREEN, GREEN)
        else -> intArrayOf(BLUE_GREEN, BLUE, BLUE_VIOLET) // winter
    }

    fun lunarColor(displayDay: Int): Int = WHEEL[(displayDay - 1).mod(12)]

    /** One ring: how many notched segments, current fill 0..1, and per-segment colors. */
    class Ring(val segments: Int, val fraction: Float, val colors: IntArray)

    /** The four rings, ordered outer → inner: season, lunar, week, hour. */
    fun rings(now: LocalDateTime, zone: ZoneId, lat: Double, lon: Double): List<Ring> {
        val lunar = CelestialCalculations.calculateLunarInfo(now, zone)
        val season = CelestialCalculations.calculateSeason(now, zone)
        val sun = CelestialCalculations.calculateSunTimes(now, lat, lon, zone)
        val hour = CelestialCalculations.calculateSeasonalHour(now, sun)
        val dow = CelestialCalculations.dayOfWeekSundayZero(now)

        val seasonFrac = if (season.totalDays > 0) season.elapsedDays.toFloat() / season.totalDays else 0f
        val lunarFrac = (lunar.normalizedPercent / 100.0).toFloat()
        val dayFraction = (now.hour * 60 + now.minute) / 1440f
        val weekFrac = (dow + dayFraction) / 7f
        val hourFrac = if (hour.hourLengthMinutes > 0) {
            (((hour.hourNumber - 1) + hour.minutesIntoHour / hour.hourLengthMinutes) / 12.0).toFloat()
        } else {
            hour.hourNumber / 12f
        }

        return listOf(
            Ring(3, seasonFrac, seasonTriple(season.name)),
            // Lunar: 29 day-segments, each colored by that day's wheel color.
            Ring(29, lunarFrac, IntArray(29) { i -> WHEEL[i % 12] }),
            Ring(7, weekFrac, WEEKDAY),
            Ring(12, hourFrac, WHEEL)
        )
    }
}
