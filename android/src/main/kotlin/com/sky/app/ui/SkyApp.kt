package com.sky.app.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.sky.app.R
import com.sky.app.domain.CelestialCalculations
import com.sky.app.domain.HebrewStrings
import com.sky.app.ui.theme.Inst
import com.sky.app.viewmodel.SkyViewModel
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

@Composable
fun SkyApp(viewModel: SkyViewModel) {
    val currentDateTime by viewModel.currentDateTime.collectAsState()
    val lunarInfo by viewModel.lunarInfo.collectAsState()
    val seasonInfo by viewModel.seasonInfo.collectAsState()
    val seasonalHour by viewModel.seasonalHour.collectAsState()
    val tides by viewModel.tides.collectAsState()

    val dayOfWeek = CelestialCalculations.dayOfWeekSundayZero(currentDateTime)

    val context = LocalContext.current
    fun hasLocationPermission(): Boolean =
        ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        if (result.values.any { it }) viewModel.useDeviceLocation()
    }

    // On first launch, use the device location automatically if already allowed.
    LaunchedEffect(Unit) {
        if (hasLocationPermission()) viewModel.useDeviceLocation()
    }

    fun requestLocation() {
        if (hasLocationPermission()) {
            viewModel.useDeviceLocation()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(listOf(Inst.ink, Inst.inkMid, Inst.ink2))
            )
    ) {
        // Fills one screen on normal devices; on short screens or with a large
        // accessibility font, the content scrolls instead of being clipped.
        Column(
            modifier = Modifier
                .widthIn(max = 520.dp)
                .align(Alignment.TopCenter)
                .verticalScroll(rememberScrollState())
                .heightIn(min = maxHeight)
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            TitleBlock()

            Spacer(Modifier.height(32.dp))
            MoonHero(lunarInfo, tides)
            Spacer(Modifier.height(32.dp))

            EngravedRule()
            Spacer(Modifier.height(18.dp))
            // Three nested cycles, shown with the same etched-scale grammar:
            // the hour within the day, the day within the week, the third within the season.
            HourScale(seasonalHour)
            Spacer(Modifier.height(22.dp))
            WeekScale(dayOfWeek)
            Spacer(Modifier.height(22.dp))
            SeasonScale(seasonInfo)
        }

        IconButton(
            onClick = { requestLocation() },
            modifier = Modifier.align(Alignment.TopStart).padding(4.dp)
        ) {
            Icon(Icons.Filled.LocationOn, contentDescription = "השתמש במיקום שלי", tint = Inst.muted)
        }
    }
}

// --- Title -------------------------------------------------------------

@Composable
private fun TitleBlock() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = HebrewStrings.TITLE,
            fontFamily = FontFamily.Serif,
            fontSize = 17.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.Medium,
            color = Inst.brassBright,
            letterSpacing = 0.5.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        Spacer(Modifier.height(12.dp))
        Box(
            modifier = Modifier
                .width(120.dp)
                .height(1.dp)
                .background(Inst.hairline)
        )
    }
}

// --- Week scale (the day within the week) -----------------------------

@Composable
private fun WeekScale(dayOfWeek: Int) {
    // dayOfWeek is 0..6 (Sunday..Saturday); the current day is the 1-based position.
    val current = dayOfWeek + 1
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(HebrewStrings.WEEKDAYS[dayOfWeek], fontFamily = FontFamily.Serif, fontSize = 16.sp, color = Inst.brass)
            Text(ltr("$current / 7"), fontFamily = FontFamily.Monospace, fontSize = 14.sp, color = Inst.muted)
        }
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            for (i in 0 until 7) {
                // A day is discrete: fully filled up to and including today.
                val fill = if (i <= dayOfWeek) 1f else 0f
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Inst.faint)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fill)
                            .background(Inst.brass)
                    )
                }
            }
        }
    }
}

// --- Moon hero (the signature) ----------------------------------------

@Composable
private fun MoonHero(lunar: CelestialCalculations.LunarInfo, tides: CelestialCalculations.TideTimes) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        MoonDial(lunar.normalizedPercent)
        Spacer(Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.Bottom, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "${lunar.displayDay}",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Light,
                fontSize = 60.sp,
                color = Inst.parchment
            )
            Text(
                text = HebrewStrings.DAY,
                fontFamily = FontFamily.Serif,
                fontSize = 18.sp,
                color = Inst.brass,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
        Text(
            text = lunar.phaseName,
            fontFamily = FontFamily.Serif,
            fontSize = 15.sp,
            color = Inst.muted,
            modifier = Modifier.padding(top = 2.dp)
        )
        Spacer(Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
            TideReadout(R.drawable.ic_tide_high, formatTime(tides.nextHigh))
            TideReadout(R.drawable.ic_tide_low, formatTime(tides.nextLow))
        }
    }
}

@Composable
private fun TideReadout(iconRes: Int, time: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = Inst.brass,
            modifier = Modifier.size(18.dp)
        )
        Text(time, fontFamily = FontFamily.Monospace, fontSize = 15.sp, color = Inst.parchment)
    }
}

/** Moon disc with phase, set inside a brass astrolabe rim with degree ticks. */
@Composable
private fun MoonDial(normalizedPercent: Double) {
    Canvas(modifier = Modifier.size(168.dp)) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val rim = size.minDimension / 2f
        val moonR = rim * 0.74f

        // Brass rim + 24 degree ticks (the astrolabe scale).
        drawCircle(color = Inst.hairline, radius = rim, style = Stroke(width = 2f))
        for (i in 0 until 24) {
            val a = (i * (2 * PI / 24)).toFloat() - (PI / 2).toFloat()
            val long = i % 6 == 0
            val inner = rim - if (long) 10f else 5f
            drawLine(
                color = if (long) Inst.brass else Inst.hairline,
                start = Offset(cx + inner * cos(a), cy + inner * sin(a)),
                end = Offset(cx + rim * cos(a), cy + rim * sin(a)),
                strokeWidth = if (long) 2f else 1f
            )
        }

        // Unlit moon body.
        drawCircle(color = Inst.panel, radius = moonR, center = Offset(cx, cy))

        val lit = Inst.parchment
        val rect = Rect(cx - moonR, cy - moonR, cx + moonR, cy + moonR)
        val moonPath = Path().apply { addOval(rect) }
        clipPath(moonPath) {
            when {
                normalizedPercent < 3 || normalizedPercent > 97 -> {
                    drawCircle(color = lit.copy(alpha = 0.10f), radius = moonR, center = Offset(cx, cy))
                }
                normalizedPercent < 47 -> {
                    val frac = (normalizedPercent * 2 / 100.0).toFloat()
                    val w = moonR * 2 * frac
                    drawRect(color = lit, topLeft = Offset(cx + moonR - w, cy - moonR), size = Size(w, moonR * 2))
                }
                normalizedPercent <= 53 -> {
                    drawCircle(color = lit, radius = moonR, center = Offset(cx, cy))
                }
                else -> {
                    drawCircle(color = lit, radius = moonR, center = Offset(cx, cy))
                    val frac = ((normalizedPercent - 50) * 2 / 100.0).toFloat()
                    val w = moonR * 2 * frac
                    drawRect(color = Inst.ink2, topLeft = Offset(cx - moonR, cy - moonR), size = Size(w, moonR * 2))
                }
            }
        }
        // Crisp moon edge.
        drawCircle(color = Inst.brass.copy(alpha = 0.5f), radius = moonR, center = Offset(cx, cy), style = Stroke(width = 1.5f))
    }
}

// --- Hour scale (12 etched temporal hours) ----------------------------

@Composable
private fun HourScale(hour: CelestialCalculations.SeasonalHour) {
    val accent = if (hour.isDaytime) Inst.dayAccent else Inst.nightAccent
    val label = if (hour.isDaytime) HebrewStrings.DAY_HOUR else HebrewStrings.NIGHT_HOUR

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, fontFamily = FontFamily.Serif, fontSize = 16.sp, color = accent)
            Text(
                ltr("${hour.hourNumber} / 12"),
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                color = Inst.muted
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            for (h in 1..12) {
                val fill = when {
                    h < hour.hourNumber -> 1f
                    h == hour.hourNumber ->
                        if (hour.hourLengthMinutes > 0)
                            (hour.minutesIntoHour / hour.hourLengthMinutes).toFloat().coerceIn(0f, 1f)
                        else 0f
                    else -> 0f
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(10.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Inst.faint)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(fill)
                            .background(accent)
                    )
                }
            }
        }
    }
}

// --- Season scale (3 tekufah thirds) ----------------------------------

@Composable
private fun SeasonScale(season: CelestialCalculations.SeasonInfo) {
    val accent = Inst.seasonAccent(season.name)
    val pct = if (season.totalDays > 0)
        min(season.elapsedDays.toFloat() / season.totalDays * 100f, 100f) else 0f

    val s1: Float
    val s2: Float
    val s3: Float
    when {
        pct <= 33.33f -> { s1 = min(pct * 3f, 100f); s2 = 0f; s3 = 0f }
        pct <= 66.66f -> { s1 = 100f; s2 = min((pct - 33.33f) * 3f, 100f); s3 = 0f }
        else -> { s1 = 100f; s2 = 100f; s3 = min((pct - 66.66f) * 3f, 100f) }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(season.name, fontFamily = FontFamily.Serif, fontSize = 16.sp, color = accent)
            Text(
                ltr("${season.elapsedDays} / ${season.totalDays}"),
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                color = Inst.muted
            )
        }
        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SeasonSegment(s1, accent, Modifier.weight(1f))
            SeasonSegment(s2, accent, Modifier.weight(1f))
            SeasonSegment(s3, accent, Modifier.weight(1f))
        }
        Spacer(Modifier.height(6.dp))
        Text(
            "${season.remainingDays} ${HebrewStrings.DAYS_REMAINING}",
            fontFamily = FontFamily.SansSerif,
            fontSize = 12.sp,
            color = Inst.muted
        )
    }
}

@Composable
private fun SeasonSegment(fillPercent: Float, accent: Color, modifier: Modifier) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .clip(RoundedCornerShape(2.dp))
            .background(Inst.faint)
    ) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(fillPercent / 100f)
                .background(accent)
        )
    }
}

// --- Shared bits ------------------------------------------------------

@Composable
private fun EngravedRule() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Inst.hairline)
    )
}

private fun formatTime(t: CelestialCalculations.TimeHM): String =
    ltr("%02d:%02d".format(t.hours, t.minutes))

/**
 * Wraps a string in a Unicode LTR isolate so numeric readouts like "6 / 12"
 * keep left-to-right order inside the right-to-left layout (otherwise bidi
 * reordering swaps the two numbers).
 */
private fun ltr(s: String): String = "⁦$s⁩"
