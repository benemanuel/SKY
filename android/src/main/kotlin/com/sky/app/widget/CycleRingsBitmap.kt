package com.sky.app.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.graphics.toArgb
import com.sky.app.domain.CyclePalette
import com.sky.app.ui.theme.Inst

/**
 * Four concentric notched rings (season / lunar / week / hour) baked to a
 * Bitmap for the widget — the same design as the watch face. Colors per
 * colors.txt via the shared CyclePalette.
 */
object CycleRingsBitmap {

    private val RADIUS = floatArrayOf(0.86f, 0.70f, 0.54f, 0.38f) // outer → inner, of half-size

    fun render(sizePx: Int, rings: List<CyclePalette.Ring>): Bitmap {
        val bmp = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        val center = sizePx / 2f
        val track = Inst.faint.toArgb()

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeCap = Paint.Cap.BUTT
            strokeWidth = sizePx * 0.055f
        }

        rings.forEachIndexed { idx, ring ->
            if (idx >= RADIUS.size) return@forEachIndexed
            val r = center * RADIUS[idx]
            val rect = RectF(center - r, center - r, center + r, center + r)
            val segAngle = 360f / ring.segments
            val gap = segAngle * 0.12f
            val sweepFull = segAngle - gap
            val elapsed = ring.fraction.coerceIn(0f, 1f) * ring.segments

            for (i in 0 until ring.segments) {
                val segStart = -90f + i * segAngle + gap / 2f
                paint.color = track
                c.drawArc(rect, segStart, sweepFull, false, paint)
                val fill = (elapsed - i).coerceIn(0f, 1f)
                if (fill > 0f) {
                    paint.color = ring.colors[i]
                    c.drawArc(rect, segStart, sweepFull * fill, false, paint)
                }
            }
        }
        return bmp
    }
}
