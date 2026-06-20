package com.sky.app.wear

import androidx.wear.watchface.style.UserStyleSchema
import androidx.wear.watchface.style.UserStyleSetting
import androidx.wear.watchface.style.UserStyleSetting.ListUserStyleSetting
import androidx.wear.watchface.style.UserStyleSetting.ListUserStyleSetting.ListOption
import androidx.wear.watchface.style.WatchFaceLayer

/**
 * User-configurable styling for the SKY watch face. Currently one setting,
 * "Center", that decides what (if anything) fills the empty middle inside the
 * four cycle rings. The system watch-face editor renders the picker for free.
 *
 * Option icons are intentionally null for now (placeholders); the editor falls
 * back to the display name.
 */
object SkyStyle {

    val CENTER_SETTING_ID = UserStyleSetting.Id("center")

    // Option ids — shared between the schema and the renderer's branch.
    const val CENTER_NONE = "none"
    const val CENTER_TIME = "time"
    const val CENTER_MINI_RINGS = "mini_rings"
    const val CENTER_TWO_RINGS = "two_rings"

    fun schema(): UserStyleSchema = UserStyleSchema(
        listOf(
            ListUserStyleSetting(
                /* id */ CENTER_SETTING_ID,
                /* displayName */ "Center",
                /* description */ "What to show in the middle",
                /* icon */ null,
                /* options */ listOf(
                    ListOption(UserStyleSetting.Option.Id(CENTER_NONE), "None", "Empty center", null),
                    ListOption(UserStyleSetting.Option.Id(CENTER_TIME), "Time", "Digital time", null),
                    ListOption(UserStyleSetting.Option.Id(CENTER_MINI_RINGS), "Mini rings", "Concentric hour and minute rings", null),
                    ListOption(UserStyleSetting.Option.Id(CENTER_TWO_RINGS), "Two rings", "Side-by-side hour and minute rings", null)
                ),
                /* affectsLayers */ listOf(WatchFaceLayer.BASE)
            )
        )
    )
}
