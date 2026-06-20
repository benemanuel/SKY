package com.sky.app.wear

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.text.format.DateFormat
import android.view.SurfaceHolder
import androidx.wear.watchface.CanvasType
import androidx.wear.watchface.DrawMode
import androidx.wear.watchface.Renderer
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.style.CurrentUserStyleRepository
import com.sky.app.domain.CyclePalette
import androidx.wear.watchface.style.UserStyleSetting
import java.time.ZonedDateTime
import kotlin.math.min

/**
 * Abstract watch face: four concentric notched rings (season / lunar / week /
 * hour), each filled clockwise from the top by progress, colored per colors.md.
 * Ring data + colors come from the shared :core CyclePalette.
 *
 * The empty middle is configurable via the "Center" user style (see [SkyStyle]):
 * None (default, no text), a digital Time readout, two concentric 12-segment mini
 * rings (hour + minute), or the same two rings side by side. 12/24h follows the
 * system setting.
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

    private val styleRepository = currentUserStyleRepository

    class SkyAssets : Renderer.SharedAssets {
        override fun onDestroy() {}
    }

    override suspend fun createSharedAssets(): SkyAssets = SkyAssets()

    // Jerusalem default (a watch face can't request the location permission).
    private val lat = 31.7781
    private val lon = 35.2360

    private val arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.BUTT
    }

    private val radii = floatArrayOf(0.92f, 0.78f, 0.64f, 0.50f)

    // Paints for the optional center sub-dials.
    private val centerStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeCap = Paint.Cap.BUTT
    }
    private val centerText = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    override fun render(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime, sharedAssets: SkyAssets) {
        val ambient = renderParameters.drawMode == DrawMode.AMBIENT
        canvas.drawColor(if (ambient) Color.BLACK else INK)

        val cx = bounds.exactCenterX()
        val cy = bounds.exactCenterY()
        val r = min(bounds.width(), bounds.height()) / 2f

        val rings = CyclePalette.rings(zonedDateTime.toLocalDateTime(), zonedDateTime.zone, lat, lon)
        arcPaint.strokeWidth = r * 0.07f
        rings.forEachIndexed { idx, ring ->
            drawNotchedRing(canvas, cx, cy, r * radii[idx], ring, ambient)
        }

        drawCenter(canvas, cx, cy, r, zonedDateTime, ambient)
    }

    /** Draws the user-selected center content (if any) in the empty middle. */
    private fun drawCenter(canvas: Canvas, cx: Float, cy: Float, r: Float, time: ZonedDateTime, ambient: Boolean) {
        val option = (styleRepository.userStyle.value[SkyStyle.CENTER_SETTING_ID]
                as? UserStyleSetting.ListUserStyleSetting.ListOption)?.id?.value
            ?: SkyStyle.CENTER_NONE

        val is24h = DateFormat.is24HourFormat(context)
        val hour12 = ((time.hour + 11) % 12) + 1
        val hourValue = if (is24h) time.hour else hour12

        when (option) {
            SkyStyle.CENTER_TIME ->
                drawCenterText(canvas, cx, cy, r * 0.22f, "%d:%02d".format(hourValue, time.minute), ambient)

            SkyStyle.CENTER_MINI_RINGS -> {
                // Two concentric 12-segment rings. Outer = hour (position on a 12-hour
                // dial); inner = minute in 5-minute chunks, so it advances only every
                // 5 minutes.
                val stroke = r * 0.05f
                drawMiniRing(canvas, cx, cy, r * 0.34f, stroke, hour12, ambient)
                drawMiniRing(canvas, cx, cy, r * 0.22f, stroke, time.minute / 5, ambient)
            }

            SkyStyle.CENTER_TWO_RINGS -> {
                // Same 12-segment rings as Mini rings, but side by side: hour (left),
                // minute (right). Minute advances only every 5 minutes.
                val rad = r * 0.18f
                val off = r * 0.20f
                val stroke = rad * 0.18f
                drawMiniRing(canvas, cx - off, cy, rad, stroke, hour12, ambient)
                drawMiniRing(canvas, cx + off, cy, rad, stroke, time.minute / 5, ambient)
            }
            // CENTER_NONE → draw nothing.
        }
    }

    override fun renderHighlightLayer(canvas: Canvas, bounds: Rect, zonedDateTime: ZonedDateTime, sharedAssets: SkyAssets) {
        // No editable elements.
    }

    private fun drawNotchedRing(canvas: Canvas, cx: Float, cy: Float, radius: Float, ring: CyclePalette.Ring, ambient: Boolean) {
        val rect = RectF(cx - radius, cy - radius, cx + radius, cy + radius)
        val segAngle = 360f / ring.segments
        val gap = segAngle * 0.12f
        val sweepFull = segAngle - gap
        val elapsed = ring.fraction.coerceIn(0f, 1f) * ring.segments

        for (i in 0 until ring.segments) {
            val segStart = -90f + i * segAngle + gap / 2f
            if (!ambient) {
                arcPaint.color = FAINT
                canvas.drawArc(rect, segStart, sweepFull, false, arcPaint)
            }
            val fillFrac = (elapsed - i).coerceIn(0f, 1f)
            if (fillFrac > 0f) {
                arcPaint.color = if (ambient) DIM else ring.colors[i]
                canvas.drawArc(rect, segStart, sweepFull * fillFrac, false, arcPaint)
            }
        }
    }

    /** Single line of centered text (the "Time" option). */
    private fun drawCenterText(canvas: Canvas, cx: Float, cy: Float, textSize: Float, text: String, ambient: Boolean) {
        centerText.color = if (ambient) DIM else Color.WHITE
        centerText.textSize = textSize
        val baseline = cy - (centerText.descent() + centerText.ascent()) / 2f
        canvas.drawText(text, cx, baseline, centerText)
    }

    /** A 12-segment notched ring with [lit] whole segments filled clockwise from top. */
    private fun drawMiniRing(canvas: Canvas, cx: Float, cy: Float, radius: Float, strokeWidth: Float, lit: Int, ambient: Boolean) {
        val rect = RectF(cx - radius, cy - radius, cx + radius, cy + radius)
        val segments = 12
        val segAngle = 360f / segments
        val gap = segAngle * 0.12f
        val sweep = segAngle - gap
        centerStroke.strokeWidth = strokeWidth
        for (i in 0 until segments) {
            val segStart = -90f + i * segAngle + gap / 2f
            if (!ambient) {
                centerStroke.color = FAINT
                canvas.drawArc(rect, segStart, sweep, false, centerStroke)
            }
            if (i < lit) {
                centerStroke.color = if (ambient) DIM else CyclePalette.WHEEL[i % 12]
                canvas.drawArc(rect, segStart, sweep, false, centerStroke)
            }
        }
    }

    companion object {
        private val INK = 0xFF0B1026.toInt()
        private val FAINT = 0x1FECE3D0
        private val DIM = 0xFF9AA0B5.toInt()
    }
}
