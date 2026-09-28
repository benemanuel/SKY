package com.sky.app.wear

import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import com.sky.app.domain.CenterStyle
import com.sky.app.domain.WatchLocation

/**
 * Receives settings pushed from the phone over the Wearable Data Layer and mirrors
 * them into [SkyStyle]'s SharedPreferences:
 *  - the "Center" choice ([CenterStyle.PATH])
 *  - the phone's location ([WatchLocation.PATH]), used for sunrise/sunset and the
 *    temporal-hour ring instead of the Jerusalem default.
 * The running watch face redraws automatically via its preference-change listener.
 *
 * DataItems (rather than one-off messages) are used so values still sync when the
 * watch was unreachable at the moment the phone changed them.
 */
class WatchSettingsListenerService : WearableListenerService() {

    override fun onDataChanged(events: DataEventBuffer) {
        for (event in events) {
            if (event.type != DataEvent.TYPE_CHANGED) continue
            val item = event.dataItem
            val map = DataMapItem.fromDataItem(item).dataMap

            when (item.uri.path) {
                CenterStyle.PATH -> {
                    val id = map.getString(CenterStyle.KEY)
                    if (CenterStyle.isValid(id)) {
                        SkyStyle.setCenterOption(this, id!!)
                    }
                }

                WatchLocation.PATH -> {
                    if (map.containsKey(WatchLocation.KEY_LAT) && map.containsKey(WatchLocation.KEY_LON)) {
                        val lat = map.getDouble(WatchLocation.KEY_LAT)
                        val lon = map.getDouble(WatchLocation.KEY_LON)
                        if (WatchLocation.isValid(lat, lon)) {
                            SkyStyle.setLocation(this, lat, lon)
                        }
                    }
                }
            }
        }
    }
}
