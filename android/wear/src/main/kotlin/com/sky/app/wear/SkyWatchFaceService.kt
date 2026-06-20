package com.sky.app.wear

import android.view.SurfaceHolder
import androidx.wear.watchface.WatchFace
import androidx.wear.watchface.WatchFaceService
import androidx.wear.watchface.WatchFaceType
import androidx.wear.watchface.WatchState
import androidx.wear.watchface.ComplicationSlotsManager
import androidx.wear.watchface.style.CurrentUserStyleRepository
import androidx.wear.watchface.style.UserStyleSchema

/** Entry point for the SKY code-based watch face. */
class SkyWatchFaceService : WatchFaceService() {

    override fun createUserStyleSchema(): UserStyleSchema = SkyStyle.schema()

    override suspend fun createWatchFace(
        surfaceHolder: SurfaceHolder,
        watchState: WatchState,
        complicationSlotsManager: ComplicationSlotsManager,
        currentUserStyleRepository: CurrentUserStyleRepository
    ): WatchFace {
        val renderer = SkyRenderer(
            context = applicationContext,
            surfaceHolder = surfaceHolder,
            currentUserStyleRepository = currentUserStyleRepository,
            watchState = watchState
        )
        return WatchFace(WatchFaceType.DIGITAL, renderer)
    }
}
