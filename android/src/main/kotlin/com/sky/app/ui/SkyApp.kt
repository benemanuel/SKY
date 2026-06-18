package com.sky.app.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.sky.app.domain.CyclePalette
import com.sky.app.ui.theme.Inst
import com.sky.app.viewmodel.SkyViewModel
import java.time.ZoneId
import kotlin.math.min

/**
 * The whole screen is the instrument: four concentric notched rings
 * (season / lunar-29 / week / hour), colored per colors.txt. No text.
 */
@Composable
fun SkyApp(viewModel: SkyViewModel) {
    val currentDateTime by viewModel.currentDateTime.collectAsState()
    val lat by viewModel.latitude.collectAsState()
    val lon by viewModel.longitude.collectAsState()

    val rings = remember(currentDateTime.hour, currentDateTime.minute, lat, lon) {
        CyclePalette.rings(currentDateTime, ZoneId.systemDefault(), lat, lon)
    }

    // Use the device location if it's already granted; otherwise the Jerusalem
    // default stands. No on-screen control — the rings are the whole UI.
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val granted =
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED
        if (granted) viewModel.useDeviceLocation()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(Inst.ink, Inst.inkMid, Inst.ink2))),
        contentAlignment = Alignment.Center
    ) {
        CycleRings(
            rings,
            Modifier
                .fillMaxSize()
                .padding(20.dp)
                .aspectRatio(1f)
        )
    }
}

@Composable
private fun CycleRings(rings: List<CyclePalette.Ring>, modifier: Modifier) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val rMax = min(cx, cy)
        val factors = floatArrayOf(0.92f, 0.78f, 0.64f, 0.50f)
        val stroke = rMax * 0.07f
        rings.forEachIndexed { idx, ring ->
            if (idx < factors.size) notchedRing(ring, Offset(cx, cy), rMax * factors[idx], stroke)
        }
    }
}

private fun DrawScope.notchedRing(ring: CyclePalette.Ring, center: Offset, radius: Float, stroke: Float) {
    val segAngle = 360f / ring.segments
    val gap = segAngle * 0.12f
    val sweepFull = segAngle - gap
    val elapsed = ring.fraction.coerceIn(0f, 1f) * ring.segments
    val topLeft = Offset(center.x - radius, center.y - radius)
    val arcSize = Size(radius * 2, radius * 2)
    val st = Stroke(width = stroke, cap = StrokeCap.Butt)

    for (i in 0 until ring.segments) {
        val segStart = -90f + i * segAngle + gap / 2f
        drawArc(Inst.faint, segStart, sweepFull, false, topLeft, arcSize, style = st)
        val fill = (elapsed - i).coerceIn(0f, 1f)
        if (fill > 0f) {
            drawArc(Color(ring.colors[i]), segStart, sweepFull * fill, false, topLeft, arcSize, style = st)
        }
    }
}
