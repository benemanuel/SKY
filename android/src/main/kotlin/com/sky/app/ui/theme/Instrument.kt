package com.sky.app.ui.theme

import androidx.compose.ui.graphics.Color
import com.sky.app.domain.HebrewStrings

/** Palette for the "Night Instrument" redesign: brass engraving on midnight. */
object Inst {
    val ink = Color(0xFF0B1026)
    val inkMid = Color(0xFF0E1430)
    val ink2 = Color(0xFF131A38)
    val panel = Color(0xFF2A3258)

    val brass = Color(0xFFC9A66B)
    val brassBright = Color(0xFFE8C987)
    val parchment = Color(0xFFECE3D0)
    val muted = Color(0xFF6E769A)

    val dayAccent = Color(0xFFE8C987)   // sun / daytime hours
    val nightAccent = Color(0xFF8FA4D8) // moonlight / night hours

    val hairline = Color(0x40C9A66B)
    val faint = Color(0x1FECE3D0)

    fun seasonAccent(name: String): Color = when (name) {
        HebrewStrings.SPRING -> Color(0xFF9CCF9F)
        HebrewStrings.SUMMER -> Color(0xFFE8C987)
        HebrewStrings.FALL -> Color(0xFFD8945A)
        else -> Color(0xFF8FA4D8)
    }
}
