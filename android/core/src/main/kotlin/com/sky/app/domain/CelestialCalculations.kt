package com.sky.app.domain

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Faithful port of the web app's sky.js calculations so the Android app
 * produces identical output to the original webpage.
 */
object CelestialCalculations {

    data class TimeHM(val hours: Int, val minutes: Int)

    data class LunarInfo(
        // Raw lunar day in the 1..29.53 cycle (used for the moon visualization).
        val lunarDay: Double,
        // Rounded value shown as "יום N".
        val displayDay: Int,
        val phaseName: String,
        // 0..100 position in the cycle, used to draw the lit fraction.
        val normalizedPercent: Double
    )

    data class SeasonInfo(
        val name: String,
        val elapsedDays: Int,
        val totalDays: Int,
        val remainingDays: Int
    )

    data class SunTimes(
        val sunrise: TimeHM,
        val sunset: TimeHM,
        val dayLength: Double,   // hours
        val nightLength: Double  // hours
    )

    data class SeasonalHour(
        val hourNumber: Int,
        val isDaytime: Boolean,
        val hourLengthMinutes: Double,
        val minutesIntoHour: Int
    )

    data class TideTimes(
        val nextHigh: TimeHM,
        val nextLow: TimeHM
    )

		 private const val MS_PER_DAY = 1000.0 * 60 * 60 * 24												
// --- Lunar ------------------------------------------------------------

/**
 * Precise mean synodic month (days).
 * Using the modern value instead of the old rounded 29.53.
 */
private const val LUNAR_CYCLE = 29.530588853

/**
 * Julian Day of a carefully chosen reference new moon (near J2000).
 * Residual error stays ≤ ~0.35 day from 2000–2035 — the practical limit
 * of any pure-mean model.
 */
private const val KNOWN_NEW_MOON_JD = 2451550.19

/**
 * Convert LocalDateTime + ZoneId → Julian Day (UTC).
 * Classic algorithm, accurate enough for lunar-age purposes.
 */
private fun toJulianDay(now: LocalDateTime, zone: ZoneId): Double {
    val utc = now.atZone(zone).withZoneSameInstant(ZoneOffset.UTC)
    var y = utc.year
    var m = utc.monthValue
    val dayFraction = utc.dayOfMonth +
            (utc.hour + utc.minute / 60.0 + utc.second / 3600.0) / 24.0

    if (m <= 2) {
        y -= 1
        m += 12
    }
    val a = y / 100
    val b = 2 - a + a / 4
    return floor(365.25 * (y + 4716)) +
           floor(30.6001 * (m + 1)) +
           dayFraction + b - 1524.5
}

/**
 * Lunar day / phase / normalized position.
 * Replaces the old fixed-reference method that was drifting ~2 days late.
 */
fun calculateLunarInfo(now: LocalDateTime, zone: ZoneId = ZoneId.systemDefault()): LunarInfo {
    val jd = toJulianDay(now, zone)
    var daysSinceNew = jd - KNOWN_NEW_MOON_JD

    // Bring into [0, LUNAR_CYCLE)
    var age = daysSinceNew % LUNAR_CYCLE
    if (age < 0) age += LUNAR_CYCLE

    // 1-based lunar day (1.0 … 29.53…)
    val lunarDay = age + 1.0

    val normalized = (age / LUNAR_CYCLE) * 100.0

    return LunarInfo(
        lunarDay = lunarDay,
        // Most traditional displays use floor-style day numbering.
        // Change to .roundToInt() if you prefer the old visual behaviour.
        displayDay = floor(lunarDay).toInt().coerceIn(1, 30),
        phaseName = getLunarPhase(lunarDay),
        normalizedPercent = normalized
    )
}

private fun getLunarPhase(lunarDay: Double): String = when {
    lunarDay < 1.5 -> HebrewStrings.NEW_MOON
    lunarDay < 7 -> HebrewStrings.WAXING_CRESCENT
    lunarDay < 8.5 -> HebrewStrings.FIRST_QUARTER
    lunarDay < 14 -> HebrewStrings.WAXING_GIBBOUS
    lunarDay < 16 -> HebrewStrings.FULL_MOON
    lunarDay < 22 -> HebrewStrings.WANING_GIBBOUS
    lunarDay < 23.5 -> HebrewStrings.LAST_QUARTER
    else -> HebrewStrings.WANING_CRESCENT
}
    // --- Season -----------------------------------------------------------

    fun calculateSeason(now: LocalDateTime, zone: ZoneId = ZoneId.systemDefault()): SeasonInfo {
        val year = now.year

        // Northern-hemisphere approximations, matching sky.js (local midnight).
        val springEquinox = LocalDateTime.of(year, 3, 20, 0, 0)
        val summerSolstice = LocalDateTime.of(year, 6, 21, 0, 0)
        val fallEquinox = LocalDateTime.of(year, 9, 22, 0, 0)
        val winterSolstice = LocalDateTime.of(year, 12, 21, 0, 0)

        val name: String
        val seasonStart: LocalDateTime
        val seasonEnd: LocalDateTime

        if (now >= winterSolstice || now < springEquinox) {
            name = HebrewStrings.WINTER
            if (now >= winterSolstice) {
                // Late December: winter runs into next year's spring equinox.
                seasonStart = winterSolstice
                seasonEnd = LocalDateTime.of(year + 1, 3, 20, 0, 0)
            } else {
                // Jan 1 – Mar 19: winter began at last year's solstice.
                seasonStart = LocalDateTime.of(year - 1, 12, 21, 0, 0)
                seasonEnd = springEquinox
            }
        } else if (now < summerSolstice) {
            name = HebrewStrings.SPRING
            seasonStart = springEquinox
            seasonEnd = summerSolstice
        } else if (now < fallEquinox) {
            name = HebrewStrings.SUMMER
            seasonStart = summerSolstice
            seasonEnd = fallEquinox
        } else {
            name = HebrewStrings.FALL
            seasonStart = fallEquinox
            seasonEnd = winterSolstice
        }

        val startMs = seasonStart.atZone(zone).toInstant().toEpochMilli()
        val endMs = seasonEnd.atZone(zone).toInstant().toEpochMilli()
        val nowMs = now.atZone(zone).toInstant().toEpochMilli()

        val totalDays = ((endMs - startMs) / MS_PER_DAY).roundToInt()
        val elapsedDays = ceil((nowMs - startMs) / MS_PER_DAY).toInt()
        val remainingDays = totalDays - elapsedDays

        return SeasonInfo(name, elapsedDays, totalDays, remainingDays)
    }

    // --- Sun times --------------------------------------------------------

    fun calculateSunTimes(
        now: LocalDateTime,
        lat: Double,
        lon: Double,
        zone: ZoneId = ZoneId.systemDefault()
    ): SunTimes {
        val nowInstant = now.atZone(zone).toInstant()
        val timeZoneOffset = zone.rules.getOffset(nowInstant).totalSeconds / 3600.0

        val utc = nowInstant.atZone(ZoneOffset.UTC)
        val year = utc.year
        val startOfYearMs = LocalDate.of(year, 1, 1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        val n = floor((nowInstant.toEpochMilli() - startOfYearMs) / MS_PER_DAY) + 1

        val delta = 23.45 * sin(2 * PI * (n - 80) / 365)
        val b = 2 * PI * (n - 81) / 365
        val e = (9.87 * sin(2 * b) - 7.53 * cos(b) - 1.5 * sin(b)) / 60

        val latRad = lat * PI / 180
        val deltaRad = delta * PI / 180
        val cosOmega = (sin(-0.8333 * PI / 180) - sin(latRad) * sin(deltaRad)) /
            (cos(latRad) * cos(deltaRad))

        if (cosOmega > 1) {
            return SunTimes(TimeHM(0, 0), TimeHM(0, 0), 0.0, 24.0)
        }
        if (cosOmega < -1) {
            return SunTimes(TimeHM(0, 0), TimeHM(0, 0), 24.0, 0.0)
        }

        val omega = acos(cosOmega) * 180 / PI
        val dayLength = 2 * (omega / 15)
        val nightLength = 24 - dayLength

        val sunriseUtc = 12 - (omega / 15) - e - (lon / 15)
        val sunsetUtc = 12 + (omega / 15) - e - (lon / 15)

        return SunTimes(
            sunrise = hoursToTime(sunriseUtc, timeZoneOffset),
            sunset = hoursToTime(sunsetUtc, timeZoneOffset),
            dayLength = dayLength,
            nightLength = nightLength
        )
    }

    private fun hoursToTime(hours: Double, timeZoneOffset: Double): TimeHM {
        var adjusted = hours + timeZoneOffset
        while (adjusted < 0) adjusted += 24
        while (adjusted >= 24) adjusted -= 24
        val h = floor(adjusted).toInt()
        val m = floor((adjusted - h) * 60).toInt()
        return TimeHM(h, m)
    }

    // --- Seasonal (temporal) hour ----------------------------------------

    fun calculateSeasonalHour(now: LocalDateTime, sunTimes: SunTimes): SeasonalHour {
        val currentTimeInHours = now.hour + now.minute / 60.0
        val sunriseInHours = sunTimes.sunrise.hours + sunTimes.sunrise.minutes / 60.0
        val sunsetInHours = sunTimes.sunset.hours + sunTimes.sunset.minutes / 60.0

        val isDaytime = currentTimeInHours >= sunriseInHours && currentTimeInHours < sunsetInHours

        if (isDaytime) {
            val dayHourLength = sunTimes.dayLength / 12
            val hoursSinceSunrise = currentTimeInHours - sunriseInHours
            val hourNumber = floor(hoursSinceSunrise / dayHourLength).toInt() + 1
            val hourStart = sunriseInHours + (hourNumber - 1) * dayHourLength
            val minutesIntoHour = floor((currentTimeInHours - hourStart) * 60).toInt()
            return SeasonalHour(hourNumber, true, dayHourLength * 60, minutesIntoHour.coerceAtLeast(0))
        }

        val nightHourLength = sunTimes.nightLength / 12
        var hourNumber: Int
        if (currentTimeInHours >= sunsetInHours) {
            val hoursSinceSunset = currentTimeInHours - sunsetInHours
            hourNumber = floor(hoursSinceSunset / nightHourLength).toInt() + 1
        } else {
            val adjustedTime = currentTimeInHours + 24 - sunsetInHours
            hourNumber = floor(adjustedTime / nightHourLength).toInt() + 1
            if (hourNumber > 12) hourNumber = 1
        }

        val hourStart: Double
        if (currentTimeInHours >= sunsetInHours) {
            hourStart = sunsetInHours + (hourNumber - 1) * nightHourLength
        } else {
            val hoursFromMidnightToSunset = 24 - sunsetInHours
            val effectiveStart = (hourNumber - 1) * nightHourLength - hoursFromMidnightToSunset
            var hs = if (effectiveStart >= 0) effectiveStart else effectiveStart + 24
            if (currentTimeInHours < hs) hs -= 24
            hourStart = hs
        }

        val minutesIntoHour = floor((currentTimeInHours - hourStart) * 60).toInt()
        return SeasonalHour(hourNumber, false, nightHourLength * 60, minutesIntoHour.coerceAtLeast(0))
    }

    // --- Tides (from moon transit) ---------------------------------------

    fun calculateTides(now: LocalDateTime, lon: Double, zone: ZoneId = ZoneId.systemDefault()): TideTimes {
        val nowInstant = now.atZone(zone).toInstant()
        val jd = nowInstant.toEpochMilli() / 86400000.0 + 2440587.5

        val t = (jd - 2451545.0) / 36525
        val lunarLongitude = 218.316 + 481267.881342 * t + 6.289 * sin(134.963 + 477198.8676 * t)
        val lunarRA = (lunarLongitude % 360) / 15

        val lmst = 280.46061837 + 360.98564736629 * (jd - 2451545.0) + lon
        val lst = (lmst % 360) / 15

        var first = lst - lunarRA
        if (first < 0) first += 24
        val second = (first + 12.42) % 24

        val highTide1 = atTimeToday(now, first, zone)
        val highTide2 = atTimeToday(now, second, zone)
        val lowTide1 = highTide1.plusMillis((6.21 * 3600 * 1000).toLong())
        val lowTide2 = highTide2.plusMillis((6.21 * 3600 * 1000).toLong())

        val nowMs = nowInstant.toEpochMilli()
        val nextHigh = if (nowMs < highTide1.toEpochMilli()) highTide1 else highTide2
        val nextLow = if (nowMs < lowTide1.toEpochMilli()) lowTide1 else lowTide2

        return TideTimes(instantToTime(nextHigh, zone), instantToTime(nextLow, zone))
    }

    private fun atTimeToday(now: LocalDateTime, hoursFraction: Double, zone: ZoneId): Instant {
        val h = floor(hoursFraction).toInt()
        val m = floor((hoursFraction % 1) * 60).toInt()
        return now.toLocalDate().atTime(h.coerceIn(0, 23), m.coerceIn(0, 59))
            .atZone(zone).toInstant()
    }

    private fun instantToTime(instant: Instant, zone: ZoneId): TimeHM {
        val z = instant.atZone(zone)
        return TimeHM(z.hour, z.minute)
    }

    // --- Week -------------------------------------------------------------

    /** Day of week with Sunday = 0 .. Saturday = 6, matching JS Date.getDay(). */
    fun dayOfWeekSundayZero(now: LocalDateTime): Int = now.dayOfWeek.value % 7
}
