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
import com.sky.app.domain.CyclePalette
import java.time.ZonedDateTime
import kotlin.math.min

/**
 * Abstract watch face: four concentric notched rings (season / lunar / week /
 * hour), each filled clockwise from the top by progress, colored per colors.txt.
 * No text. Ring data + colors come from the shared :core CyclePalette.
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
        strokeCap = Paint.Cap.BUTT
    }

    private val radii = floatArrayOf(0.92f, 0.78f, 0.64f, 0.50f)

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

    companion object {
        private val INK = 0xFF0B1026.toInt()
        private val FAINT = 0x1FECE3D0
        private val DIM = 0xFF9AA0B5.toInt()
    }
}
