package com.sky.app.wear

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.view.SurfaceHolder
import androidx.wear.watchface.CanvasType
import androidx.wear.watchface.DrawMode
import androidx.wear.watchface.Renderer
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import com.sky.app.domain.CelestialCalculations
import com.sky.app.domain.HebrewStrings
import java.time.ZonedDateTime
import kotlin.math.min

/**
 * Abstract watch face: four concentric notched rings, each divided into its
 * cycle's count and filled clockwise from the top by progress. No text.
 *   outer  season — 3 notches (thirds)
 *          lunar  — 4 notches (quarters of the ~29.5-day cycle)
 *          week   — 7 notches (days), each day its own color
 *   inner  hour   — 12 notches (temporal hours; day vs night color)
 */
class SkyRenderer(
    private val context: Context,
    surfaceHolder: SurfaceHolder,
    currentUserStyleRepository: CurrentUserStyleRepository,
    watchState: WatchState
) : Renderer.CanvasRenderer2<SkyRenderer.SkyAssets>(
    surfaceHolder,
    currentUserStyleRepository,
    watchState,
    CanvasType.HARDWARE,
    interactiveDrawModeUpdateDelayMillis = 60_000L,
    clearWithBackgroundTintBeforeRenderingHighlightLayer = false
) {

    class SkyAssets : Renderer.SharedAssets {
        override fun onDestroy() {}
    }

    override suspend fun createSharedAssets(): SkyAssets = SkyAssets()

    // Jerusalem default (a watch face can't request the location permission).
    private val lat = 31.7781
    private val lon = 35.2360

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.BUTT // crisp notches between segments
    }

    override fun render(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime, sharedAssets: SkyAssets) {
        val ambient = renderParameters.drawMode == DrawMode.AMBIENT
        canvas.drawColor(if (ambient) Color.BLACK else INK)

        val cx = bounds.exactCenterX()
        val cy = bounds.exactCenterY()
        val r = min(bounds.width(), bounds.height()) / 2f

        val now = zonedDateTime.toLocalDateTime()
        val zone = zonedDateTime.zone
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
        val seasonColors = seasonTriple(season.name)
        // Lunar day color from colors.txt (Day N → 12-hue wheel, repeating).
        val lunarColor = WHEEL[(lunar.displayDay - 1).mod(12)]

        arcPaint.strokeWidth = r * 0.07f
        drawNotchedRing(canvas, cx, cy, r * 0.92f, 3, seasonFrac, ambient) { i -> seasonColors[i] }
        drawNotchedRing(canvas, cx, cy, r * 0.78f, 4, lunarFrac, ambient) { lunarColor }
        drawNotchedRing(canvas, cx, cy, r * 0.64f, 7, weekFrac, ambient) { i -> WEEKDAY[i] }
        drawNotchedRing(canvas, cx, cy, r * 0.50f, 12, hourFrac, ambient) { i -> WHEEL[i] }
    }

    override fun renderHighlightLayer(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime, sharedAssets: SkyAssets) {
        // No editable elements.
    }

    private inline fun drawNotchedRing(
        canvas: Canvas,
        cx: Float,
        cy: Float,
        radius: Float,
        segments: Int,
        frac: Float,
        ambient: Boolean,
        colorFor: (Int) -> Int
    ) {
        val rect = RectF(cx - radius, cy - radius, cx + radius, cy + radius)
        val segAngle = 360f / segments
        val gap = segAngle * 0.12f          // the notch
        val sweepFull = segAngle - gap
        val elapsed = frac.coerceIn(0f, 1f) * segments

        for (i in 0 until segments) {
            val segStart = -90f + i * segAngle + gap / 2f
            if (!ambient) {
                arcPaint.color = FAINT
                canvas.drawArc(rect, segStart, sweepFull, false, arcPaint)
            }
            val fillFrac = (elapsed - i).coerceIn(0f, 1f)
            if (fillFrac > 0f) {
                arcPaint.color = if (ambient) DIM else colorFor(i)
                canvas.drawArc(rect, segStart, sweepFull * fillFrac, false, arcPaint)
            }
        }
    }

    private fun seasonTriple(name: String): IntArray = when (name) {
        HebrewStrings.SPRING -> intArrayOf(VIOLET, RED_VIOLET, RED)
        HebrewStrings.SUMMER -> intArrayOf(RED_ORANGE, ORANGE, YELLOW_ORANGE)
        HebrewStrings.FALL -> intArrayOf(YELLOW, YELLOW_GREEN, GREEN)
        else -> intArrayOf(BLUE_GREEN, BLUE, BLUE_VIOLET) // winter
    }

    companion object {
        private val INK = 0xFF0B1026.toInt()
        private val FAINT = 0x1FECE3D0
        private val DIM = 0xFF9AA0B5.toInt()

        // 12-hue color wheel (colors.txt)
        private val RED = 0xFFE53935.toInt()
        private val RED_ORANGE = 0xFFFF5722.toInt()
        private val ORANGE = 0xFFFF9800.toInt()
        private val YELLOW_ORANGE = 0xFFFFB300.toInt()
        private val YELLOW = 0xFFFFEB3B.toInt()
        private val YELLOW_GREEN = 0xFFC0CA33.toInt()
        private val GREEN = 0xFF4CAF50.toInt()
        private val BLUE_GREEN = 0xFF26A69A.toInt()
        private val BLUE = 0xFF2196F3.toInt()
        private val BLUE_VIOLET = 0xFF6A4FC9.toInt()
        private val VIOLET = 0xFF9C27B0.toInt()
        private val RED_VIOLET = 0xFFD81B60.toInt()

        // Hour ring (00:00..11:00) and lunar-day wheel, per colors.txt.
        private val WHEEL = intArrayOf(
            BLUE_VIOLET, VIOLET, RED_VIOLET, RED, RED_ORANGE, ORANGE,
            YELLOW_ORANGE, YELLOW, YELLOW_GREEN, GREEN, BLUE_GREEN, BLUE
        )

        // Weekday colors, index 0 = Sunday .. 6 = Saturday.
        // (colors.txt has no week mapping; keeping the previously chosen colors.)
        private val WEEKDAY = intArrayOf(
            0xFFE53935.toInt(), // Sunday — red
            0xFFF4D03F.toInt(), // Monday — yellow
            0xFFF06292.toInt(), // Tuesday — pink
            0xFF57C84D.toInt(), // Wednesday — green
            0xFFFF9800.toInt(), // Thursday — orange
            0xFF4FC3F7.toInt(), // Friday — light blue
            0xFF9575CD.toInt()  // Saturday — purple
        )
    }
}
