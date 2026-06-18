package com.sky.app.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/** Colors ported 1:1 from the :root CSS variables in sky.css. */
data class SkyColors(
    val background: Color,
    val cardBg: Color,
    val text: Color,
    val subtext: Color,
    val border: Color,
    val cardHeaderBorder: Color,
    // Season backgrounds (gradients) + text
    val springBg: Brush,
    val springText: Color,
    val summerBg: Brush,
    val summerText: Color,
    val fallBg: Brush,
    val fallText: Color,
    val winterBg: Brush,
    val winterText: Color,
    // Hour (day/night) backgrounds + text
    val dayBg: Brush,
    val dayText: Color,
    val nightBg: Brush,
    val nightText: Color,
    // Hour segments
    val segmentBg: Color,
    val segmentFillDay: Color,
    val segmentFillNight: Color,
    val segmentText: Color,
    // Moon
    val moonBg: Color,
    val moonBorder: Color,
    val moonLight: Color,
    val moonDark: Color,
    // Week circle
    val weekStroke: Color,
    val weekFilled: Color,
    // Progress bar
    val progressTrack: Color,
    val progressFill: Color,
)

private fun gradient(start: Long, end: Long) =
    Brush.linearGradient(listOf(Color(start), Color(end)))

val LightSkyColors = SkyColors(
    background = Color(0xFFFFFFFF),
    cardBg = Color(0xFFF8F9FA),
    text = Color(0xFF212529),
    subtext = Color(0xFF6C757D),
    border = Color(0xFFDEE2E6),
    cardHeaderBorder = Color(0x1A000000),
    springBg = gradient(0xFFE6F7E6, 0xFFC3E6CB),
    springText = Color(0xFF28A745),
    summerBg = gradient(0xFFFFF3CD, 0xFFFFE8A1),
    summerText = Color(0xFFB7791F),
    fallBg = gradient(0xFFFFE5D0, 0xFFFFCC99),
    fallText = Color(0xFFFD7E14),
    winterBg = gradient(0xFFE6F2FF, 0xFFCFE2FF),
    winterText = Color(0xFF0D6EFD),
    dayBg = gradient(0xFFFFF7CC, 0xFFFFE066),
    dayText = Color(0xFFA67C00),
    nightBg = gradient(0xFFE5E9FF, 0xFFC5CAE9),
    nightText = Color(0xFF3949AB),
    segmentBg = Color(0xFFE9ECEF),
    segmentFillDay = Color(0xFF28A745),
    segmentFillNight = Color(0xFF0D6EFD),
    segmentText = Color(0xFF495057),
    moonBg = Color(0xFFE0E0E0),
    moonBorder = Color(0xFFBDBDBD),
    moonLight = Color(0xFFF5F5F5),
    moonDark = Color(0xFF757575),
    weekStroke = Color(0xFFDEE2E6),
    weekFilled = Color(0xFF6C757D),
    progressTrack = Color(0x1A000000),
    progressFill = Color(0x99000000),
)

val DarkSkyColors = SkyColors(
    background = Color(0xFF121212),
    cardBg = Color(0xFF1E1E1E),
    text = Color(0xFFE9ECEF),
    subtext = Color(0xFFADB5BD),
    border = Color(0xFF343A40),
    cardHeaderBorder = Color(0x1AFFFFFF),
    springBg = gradient(0xFF145214, 0xFF0D3D0D),
    springText = Color(0xFF82D98B),
    summerBg = gradient(0xFF705A10, 0xFF4D3C00),
    summerText = Color(0xFFFFD43B),
    fallBg = gradient(0xFF7D3B08, 0xFF592A06),
    fallText = Color(0xFFFF922B),
    winterBg = gradient(0xFF0C326E, 0xFF051D4D),
    winterText = Color(0xFF74C0FC),
    dayBg = gradient(0xFF856404, 0xFF664D03),
    dayText = Color(0xFFFFD43B),
    nightBg = gradient(0xFF1A237E, 0xFF121858),
    nightText = Color(0xFFB1BEE3),
    segmentBg = Color(0xFF343A40),
    segmentFillDay = Color(0xFF82D98B),
    segmentFillNight = Color(0xFF74C0FC),
    segmentText = Color(0xFFCED4DA),
    moonBg = Color(0xFF424242),
    moonBorder = Color(0xFF616161),
    moonLight = Color(0xFFE0E0E0),
    moonDark = Color(0xFF212121),
    weekStroke = Color(0xFF343A40),
    weekFilled = Color(0xFFADB5BD),
    progressTrack = Color(0x1AFFFFFF),
    progressFill = Color(0x99FFFFFF),
)

val LocalSkyColors = staticCompositionLocalOf { LightSkyColors }
