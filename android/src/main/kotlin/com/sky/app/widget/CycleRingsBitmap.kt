package com.sky.app.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import androidx.compose.ui.graphics.toArgb
import com.sky.app.ui.theme.Inst

/**
 * Minimalist graphic of the three nested cycles as concentric progress rings:
 * outer = season, middle = week, inner = (temporal) hour. Each ring fills
 * clockwise from the top by its progress, in the cycle's accent color.
 * Widgets can't host a Compose Canvas, so this is baked to a Bitmap.
 */
object CycleRingsBitmap {

    fun render(
        sizePx: Int,
        seasonFrac: Float,
        seasonColor: Int,
        weekFrac: Float,
        weekColor: Int,
        hourFrac: Float,
        hourColor: Int
    ): Bitmap {
        val bmp = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        val center = sizePx / 2f
        val stroke = sizePx * 0.075f

        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.STROKE
            strokeWidth = stroke
            strokeCap = Paint.Cap.ROUND
        }
        val track = Inst.faint.toArgb()

        val radii = floatArrayOf(sizePx * 0.40f, sizePx * 0.285f, sizePx * 0.17f)
        val fracs = floatArrayOf(seasonFrac, weekFrac, hourFrac)
        val colors = intArrayOf(seasonColor, weekColor, hourColor)

        for (i in 0..2) {
            val r = radii[i]
            val rect = RectF(center - r, center - r, center + r, center + r)
            paint.color = track
            c.drawArc(rect, 0f, 360f, false, paint)
            paint.color = colors[i]
            c.drawArc(rect, -90f, fracs[i].coerceIn(0f, 1f) * 360f, false, paint)
        }
        return bmp
    }
}
