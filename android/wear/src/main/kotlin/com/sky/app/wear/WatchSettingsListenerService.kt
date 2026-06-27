package com.sky.app.wear

import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import com.sky.app.domain.CenterStyle

/**
 * Receives the "Center" choice pushed from the phone over the Wearable Data Layer
 * and mirrors it into [SkyStyle]'s SharedPreferences. The running watch face redraws
 * automatically via its existing preference-change listener.
 *
 * A DataItem (rather than a one-off message) is used so the value still syncs when
 * the watch was unreachable at the moment the phone changed it.
 */
class WatchSettingsListenerService : WearableListenerService() {

    override fun onDataChanged(events: DataEventBuffer) {
        for (event in events) {
            if (event.type != DataEvent.TYPE_CHANGED) continue
            val item = event.dataItem
            if (item.uri.path != CenterStyle.PATH) continue

            val map = DataMapItem.fromDataItem(item).dataMap
            val id = map.getString(CenterStyle.KEY)
            if (CenterStyle.isValid(id)) {
                SkyStyle.setCenterOption(this, id!!)
            }
        }
    }
}
