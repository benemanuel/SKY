package com.sky.app.widget

import android.content.Context
import androidx.compose.runtime.Composable
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
import com.sky.app.domain.CyclePalette
import com.sky.app.domain.HebrewStrings
import com.sky.app.ui.theme.Inst
import java.time.LocalDateTime
import java.time.ZoneId

private val SMALL = DpSize(110.dp, 110.dp)
private val WIDE = DpSize(250.dp, 110.dp)

// Default coordinates: Jerusalem (a widget can't request the location permission).
private const val LAT = 31.7781
private const val LON = 35.2360

private class WidgetData(
    val rings: List<CyclePalette.Ring>,
    val normalizedPercent: Double,
    val lunarDay: Int
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
        val zone = ZoneId.systemDefault()
        val lunar = CelestialCalculations.calculateLunarInfo(now, zone)
        return WidgetData(
            rings = CyclePalette.rings(now, zone, LAT, LON),
            normalizedPercent = lunar.normalizedPercent,
            lunarDay = lunar.displayDay
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
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        if (wide) {
            Row(verticalAlignment = Alignment.Vertical.CenterVertically) {
                MoonBlock(data)
                Spacer(GlanceModifier.width(16.dp))
                Rings(data, displayDp = 92)
            }
        } else {
            Rings(data, displayDp = 108)
        }
    }
}

@Composable
private fun Rings(data: WidgetData, displayDp: Int) {
    Image(
        provider = ImageProvider(CycleRingsBitmap.render(sizePx = 240, rings = data.rings)),
        contentDescription = "מחזורי עונה, ירח, שבוע ושעה",
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
