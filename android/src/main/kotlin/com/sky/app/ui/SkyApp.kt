package com.sky.app.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Icon
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.sky.app.data.UpdateInfo
import com.sky.app.domain.CenterStyle
import com.sky.app.domain.CyclePalette
import com.sky.app.ui.theme.Inst
import com.sky.app.viewmodel.SkyViewModel
import java.time.ZoneId
import kotlin.math.min

/**
 * The whole screen is the instrument: four concentric notched rings
 * (season / lunar-29 / week / hour), colored per colors.md. No text.
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
    val watchConnected by viewModel.watchConnected.collectAsState()
    val watchCenter by viewModel.watchCenterOption.collectAsState()
    val updateInfo by viewModel.updateInfo.collectAsState()

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.refreshWatchConnected()
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

        if (updateInfo?.updateAvailable == true) {
            UpdateBanner(
                info = updateInfo!!,
                onDismiss = viewModel::dismissUpdate,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(16.dp)
            )
        }

        if (watchConnected) {
            WatchFaceSettings(
                selected = watchCenter,
                onSelect = viewModel::setWatchCenterOption,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            )
        }
    }
}

@Composable
private fun UpdateBanner(
    info: UpdateInfo,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Inst.inkMid,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        ) {
            Icon(
                Icons.Filled.SystemUpdate,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp),
            )
            Column(modifier = Modifier
                .weight(1f)
                .clickable {
                   safeOpenUrl(context, info.releaseUrl)
                }
            ) {
                Text(
                    text = "Update available: ${info.versionName}",
                    color = Color.White,
                    fontSize = 14.sp,
                )
                if (info.notes.isNotEmpty()) {
                    Text(text = info.notes, color = Color.White, fontSize = 12.sp)
                }
            }
            Icon(
                Icons.Filled.Close,
                contentDescription = "Dismiss",
                tint = Color.White,
                modifier = Modifier
                    .size(20.dp)
                    .clickable { onDismiss() },
            )
        }
    }
}

/**
 * Editor for the paired watch face's "Center" option. Collapsed to a small watch
 * chip; expands to a radio list that pushes the choice over the Data Layer. Shown
 * only when a watch with the SKY app is connected.
 */
@Composable
private fun WatchFaceSettings(
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Inst.inkMid,
    ) {
        Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(vertical = 4.dp),
            ) {
                Icon(
                    Icons.Filled.Watch,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(20.dp),
                )
                Text(
                    text = "Watch face center",
                    color = Color.White,
                    fontSize = 14.sp,
                )
            }

            if (expanded) {
                CenterStyle.OPTIONS.forEach { (id, label) ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(id) }
                            .padding(vertical = 2.dp),
                    ) {
                        RadioButton(
                            selected = id == selected,
                            onClick = { onSelect(id) },
                        )
                        Spacer(Modifier.size(4.dp))
                        Text(text = label, color = Color.White, fontSize = 14.sp)
                    }
                }
            }
        }
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

private fun safeOpenUrl(context: android.content.Context, url: String) {
    if (url.isBlank()) return

    val uri = try {
        val safeUrl = url.trim()
        // Enforce HTTPS (preferred for updates); allow HTTP only if needed
        if (!safeUrl.startsWith("https://") && !safeUrl.startsWith("http://")) {
            Log.w("SkyApp", "Blocked non-HTTP(S) URL: $safeUrl")
            return
        }
        // Optional: Restrict to your trusted update domain(s)
        // if (!safeUrl.startsWith("https://your-update-domain.com/")) return

        Uri.parse(safeUrl)
    } catch (e: Exception) {
        Log.e("SkyApp", "Invalid URL: $url", e)
        return
    }

    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
    } catch (e: android.content.ActivityNotFoundException) {
        Log.w("SkyApp", "No activity found to handle URL: $url")
    } catch (e: Exception) {
        Log.e("SkyApp", "Failed to open URL", e)
    }
}