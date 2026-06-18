package com.sky.app.widget

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import androidx.compose.ui.graphics.toArgb
import com.sky.app.ui.theme.Inst
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Renders the moon dial to a Bitmap for the home-screen widget, mirroring the
 * in-app MoonDial drawing (brass rim + degree ticks + lit phase fraction).
 * Widgets are RemoteViews and cannot host a Compose Canvas, so we bake an image.
 */
object MoonBitmap {

    fun render(sizePx: Int, normalizedPercent: Double): Bitmap {
        val bmp = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888)
        val c = Canvas(bmp)
        val cx = sizePx / 2f
        val cy = sizePx / 2f
        val rim = sizePx / 2f * 0.94f
        val moonR = rim * 0.78f
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)

        // Brass rim ticks (long every 90°).
        for (i in 0 until 24) {
            val a = (i * (2 * PI / 24)).toFloat() - (PI / 2).toFloat()
            val long = i % 6 == 0
            val inner = rim - if (long) sizePx * 0.05f else sizePx * 0.025f
            paint.color = (if (long) Inst.brass else Inst.hairline).toArgb()
            paint.strokeWidth = if (long) sizePx * 0.012f else sizePx * 0.006f
            c.drawLine(cx + inner * cos(a), cy + inner * sin(a), cx + rim * cos(a), cy + rim * sin(a), paint)
        }

        // Unlit body.
        paint.style = Paint.Style.FILL
        paint.color = Inst.panel.toArgb()
        c.drawCircle(cx, cy, moonR, paint)

        // Lit fraction, clipped to the moon disc.
        c.save()
        c.clipPath(Path().apply { addCircle(cx, cy, moonR, Path.Direction.CW) })
        val lit = Inst.parchment.toArgb()
        when {
            normalizedPercent < 3 || normalizedPercent > 97 -> {
                paint.color = lit
                paint.alpha = 26
                c.drawCircle(cx, cy, moonR, paint)
                paint.alpha = 255
            }
            normalizedPercent < 47 -> {
                val w = (moonR * 2 * (normalizedPercent * 2 / 100.0)).toFloat()
                paint.color = lit
                c.drawRect(cx + moonR - w, cy - moonR, cx + moonR, cy + moonR, paint)
            }
            normalizedPercent <= 53 -> {
                paint.color = lit
                c.drawCircle(cx, cy, moonR, paint)
            }
            else -> {
                paint.color = lit
                c.drawCircle(cx, cy, moonR, paint)
                val w = (moonR * 2 * ((normalizedPercent - 50) * 2 / 100.0)).toFloat()
                paint.color = Inst.ink2.toArgb()
                c.drawRect(cx - moonR, cy - moonR, cx - moonR + w, cy + moonR, paint)
            }
        }
        c.restore()

        // Crisp moon edge.
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = sizePx * 0.01f
        paint.color = Inst.brass.toArgb()
        paint.alpha = 128
        c.drawCircle(cx, cy, moonR, paint)

        return bmp
    }
}
