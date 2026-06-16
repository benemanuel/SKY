package com.sky.app.domain

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import kotlin.math.*

object CelestialCalculations {

    data class LunarInfo(
        val dayOfCycle: Int,
        val phaseName: String,
        val illumination: Float
    )

    data class SeasonInfo(
        val name: String,
        val daysElapsed: Int,
        val daysRemaining: Int
    )

    data class SunTimes(
        val sunrise: Long,
        val sunset: Long,
        val dayLength: Long
    )

    // Reference new moon: January 6, 2000
    private val REFERENCE_NEW_MOON = LocalDate.of(2000, 1, 6)
    private const val LUNAR_CYCLE_DAYS = 29.53058867

    fun calculateLunarInfo(now: LocalDateTime): LunarInfo {
        val daysSinceReference = now.toLocalDate().toEpochDay() - REFERENCE_NEW_MOON.toEpochDay()
        val dayInCycle = (daysSinceReference % LUNAR_CYCLE_DAYS.toLong()).toInt()

        val illumination = when {
            dayInCycle < 7 -> (dayInCycle / 7f) * 0.5f
            dayInCycle < 15 -> 0.5f + ((dayInCycle - 7) / 7.5f) * 0.5f
            dayInCycle < 22 -> 1f - ((dayInCycle - 15) / 7f) * 0.5f
            else -> (1f - ((dayInCycle - 22) / 7.5f)) * 0.5f
        }

        val phaseName = when (dayInCycle) {
            in 0..3 -> "🌑 New Moon"
            in 4..10 -> "🌒 Waxing Crescent"
            in 11..14 -> "🌓 First Quarter"
            in 15..21 -> "🌔 Waxing Gibbous"
            in 22..24 -> "🌕 Full Moon"
            in 25..28 -> "🌖 Waning Gibbous"
            else -> "🌗 Waning Crescent"
        }

        return LunarInfo(dayInCycle, phaseName, illumination.coerceIn(0f, 1f))
    }

    fun calculateSeasonInfo(now: LocalDateTime): SeasonInfo {
        val month = now.month
        val dayOfMonth = now.dayOfMonth
        val dayOfYear = now.toLocalDate().dayOfYear

        val (seasonName, springDay, summerDay, autumnDay, winterDay) = when {
            (month == Month.MARCH && dayOfMonth >= 21) ||
            (month in Month.APRIL..Month.MAY) ||
            (month == Month.JUNE && dayOfMonth < 21) -> {
                Triple("Spring", 80, Triple("🌱", 172, 79))
            }
            (month == Month.JUNE && dayOfMonth >= 21) ||
            (month in Month.JULY..Month.AUGUST) ||
            (month == Month.SEPTEMBER && dayOfMonth < 23) -> {
                Triple("Summer", 172, Triple("☀️", 265, 92))
            }
            (month == Month.SEPTEMBER && dayOfMonth >= 23) ||
            (month in Month.OCTOBER..Month.NOVEMBER) ||
            (month == Month.DECEMBER && dayOfMonth < 21) -> {
                Triple("Autumn", 265, Triple("🍂", 355, 89))
            }
            else -> {
                Triple("Winter", 355, Triple("❄️", 79, 84))
            }
        }

        val seasonDays = when (seasonName) {
            "Spring" -> 93
            "Summer" -> 94
            "Autumn" -> 89
            else -> 89
        }

        val startDay = when (seasonName) {
            "Spring" -> 80
            "Summer" -> 172
            "Autumn" -> 265
            else -> 355
        }

        val daysElapsed = (dayOfYear - startDay).coerceAtLeast(0)
        val daysRemaining = (seasonDays - daysElapsed).coerceAtLeast(0)

        return SeasonInfo(seasonName, daysElapsed, daysRemaining)
    }

    fun calculateSunTimes(now: LocalDateTime, latitude: Double, longitude: Double): SunTimes {
        val julianDay = toJulianDay(now.toLocalDate())
        val timeOfDayFraction = (now.hour + now.minute / 60.0 + now.second / 3600.0) / 24.0

        val sunriseTime = calculateSunrise(julianDay, latitude, longitude)
        val sunsetTime = calculateSunset(julianDay, latitude, longitude)

        val sunriseMilestones = now.toLocalDate().atTime(
            (sunriseTime * 24).toInt(),
            ((sunriseTime * 24 * 60) % 60).toInt()
        ).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()

        val sunsetMilestones = now.toLocalDate().atTime(
            (sunsetTime * 24).toInt(),
            ((sunsetTime * 24 * 60) % 60).toInt()
        ).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()

        val dayLength = (sunsetTime - sunriseTime) * 24 * 60 * 60 * 1000

        return SunTimes(sunriseMilestones, sunsetMilestones, dayLength.toLong())
    }

    private fun toJulianDay(date: LocalDate): Double {
        val a = (14 - date.monthValue) / 12
        val y = date.year + 4800 - a
        val m = date.monthValue + 12 * a - 3
        return date.dayOfMonth + (153 * m + 2) / 5 + 365 * y + y / 4 - y / 100 + y / 400 - 32045.0
    }

    private fun calculateSunrise(jd: Double, latitude: Double, longitude: Double): Double {
        return calculateSunPosition(jd, latitude, longitude, true)
    }

    private fun calculateSunset(jd: Double, latitude: Double, longitude: Double): Double {
        return calculateSunPosition(jd, latitude, longitude, false)
    }

    private fun calculateSunPosition(jd: Double, latitude: Double, longitude: Double, isSunrise: Boolean): Double {
        val n = jd - 2451545.0 + 0.0008
        val j = n - longitude / 360.0

        val m = (357.5291 + 0.98560028 * j) % 360.0
        val c = (1.9146 - 0.004817 * j - 0.000014 * j * j) * sin(Math.toRadians(m)) +
                (0.019993 - 0.000101 * j) * sin(Math.toRadians(2 * m)) +
                0.00029 * sin(Math.toRadians(3 * m))

        val lambd = (280.4665 + 36000.76983 * j + 0.0003032 * j * j + c) % 360.0

        val jTransit = 2451545.5 + j + 0.0053 * sin(Math.toRadians(m)) - 0.0069 * sin(Math.toRadians(2 * lambd))
        val delta = Math.toDegrees(asin(sin(Math.toRadians(lambd)) * sin(Math.toRadians(23.4393))))

        val cosH = (-sin(Math.toRadians(0.833)) - sin(Math.toRadians(latitude)) * sin(Math.toRadians(delta))) /
                (cos(Math.toRadians(latitude)) * cos(Math.toRadians(delta)))

        return if (cosH > 1 || cosH < -1) {
            0.5 // Polar day or night
        } else {
            val h = Math.toDegrees(acos(cosH)) / 360.0
            val offset = if (isSunrise) jTransit - h else jTransit + h
            ((offset - floor(offset)) * 24).toDouble() / 24.0
        }
    }
}
