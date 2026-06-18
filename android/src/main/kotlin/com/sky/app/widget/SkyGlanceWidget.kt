package com.sky.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalSize
import androidx.glance.background
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.SizeMode
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.sky.app.domain.CelestialCalculations
import com.sky.app.domain.HebrewStrings
import com.sky.app.ui.theme.Inst
import java.time.LocalDateTime

private val SMALL = DpSize(110.dp, 110.dp)
private val WIDE = DpSize(250.dp, 110.dp)

// Default coordinates: Jerusalem (same as the app default). The widget can't
// prompt for runtime location permission, so it uses this for sun/hour math.
private const val LAT = 31.7781
private const val LON = 35.2360

private data class WidgetData(
    val normalizedPercent: Double,
    val lunarDay: Int,
    val hourFrac: Float,
    val hourColor: Color,
    val weekFrac: Float,
    val seasonFrac: Float,
    val seasonColor: Color
)

class SkyGlanceWidget : GlanceAppWidget() {

    override val sizeMode = SizeMode.Responsive(setOf(SMALL, WIDE))

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val data = computeData()
        provideContent {
            val wide = LocalSize.current.width >= 200.dp
            WidgetBody(data, wide)
        }
    }

    private fun computeData(): WidgetData {
        val now = LocalDateTime.now()
        val lunar = CelestialCalculations.calculateLunarInfo(now)
        val season = CelestialCalculations.calculateSeason(now)
        val sun = CelestialCalculations.calculateSunTimes(now, LAT, LON)
        val hour = CelestialCalculations.calculateSeasonalHour(now, sun)
        val dow = CelestialCalculations.dayOfWeekSundayZero(now)

        val hourFrac = if (hour.hourLengthMinutes > 0) {
            ((hour.hourNumber - 1) + hour.minutesIntoHour / hour.hourLengthMinutes).toFloat() / 12f
        } else {
            hour.hourNumber / 12f
        }
        val weekFrac = (dow + 1) / 7f
        val seasonFrac = if (season.totalDays > 0) season.elapsedDays.toFloat() / season.totalDays else 0f

        return WidgetData(
            normalizedPercent = lunar.normalizedPercent,
            lunarDay = lunar.displayDay,
            hourFrac = hourFrac,
            hourColor = if (hour.isDaytime) Inst.dayAccent else Inst.nightAccent,
            weekFrac = weekFrac,
            seasonFrac = seasonFrac,
            seasonColor = Inst.seasonAccent(season.name)
        )
    }
}

class SkyWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget = SkyGlanceWidget()
}

@Composable
private fun WidgetBody(data: WidgetData, wide: Boolean) {
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Inst.ink)
            .cornerRadius(16.dp)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        if (wide) {
            Row(verticalAlignment = Alignment.Vertical.CenterVertically) {
                MoonBlock(data)
                Spacer(GlanceModifier.width(16.dp))
                CycleRings(data, displayDp = 84)
            }
        } else {
            CycleRings(data, displayDp = 104)
        }
    }
}

@Composable
private fun CycleRings(data: WidgetData, displayDp: Int) {
    Image(
        provider = ImageProvider(
            CycleRingsBitmap.render(
                sizePx = 240,
                seasonFrac = data.seasonFrac,
                seasonColor = data.seasonColor.toArgb(),
                weekFrac = data.weekFrac,
                weekColor = Inst.brass.toArgb(),
                hourFrac = data.hourFrac,
                hourColor = data.hourColor.toArgb()
            )
        ),
        contentDescription = "מחזורי שעה, שבוע ועונה",
        modifier = GlanceModifier.size(displayDp.dp)
    )
}

@Composable
private fun MoonBlock(data: WidgetData) {
    Column(horizontalAlignment = Alignment.Horizontal.CenterHorizontally) {
        Image(
            provider = ImageProvider(MoonBitmap.render(220, data.normalizedPercent)),
            contentDescription = null,
            modifier = GlanceModifier.size(80.dp)
        )
        Spacer(GlanceModifier.height(4.dp))
        Text(
            "${HebrewStrings.DAY} ${data.lunarDay}",
            style = TextStyle(color = ColorProvider(Inst.parchment), fontSize = 15.sp, fontWeight = FontWeight.Bold)
        )
    }
}
