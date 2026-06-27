package com.sky.app.data

import android.app.Application
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.sky.app.domain.CenterStyle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Phone-side bridge to the watch face "Center" setting over the Wearable Data Layer.
 *
 * Writes are persisted as a DataItem so the watch picks them up even if it was
 * unreachable at the time ([com.sky.app.wear] mirrors the value into its own
 * SharedPreferences on receipt). The current value and whether a watch is connected
 * are exposed as flows for the UI.
 */
class WatchSettingsRepository(application: Application) {

    private val dataClient: DataClient = Wearable.getDataClient(application)
    private val nodeClient = Wearable.getNodeClient(application)

    private val _centerOption = MutableStateFlow(CenterStyle.DEFAULT)
    val centerOption: StateFlow<String> = _centerOption.asStateFlow()

    private val _watchConnected = MutableStateFlow(false)
    val watchConnected: StateFlow<Boolean> = _watchConnected.asStateFlow()

    private val listener = DataClient.OnDataChangedListener { events: DataEventBuffer ->
        for (event in events) {
            if (event.type == DataEvent.TYPE_CHANGED &&
                event.dataItem.uri.path == CenterStyle.PATH
            ) {
                readInto(event.dataItem)
            }
        }
    }

    /** Begin observing the Data Layer; call from a lifecycle-aware start. */
    fun start() {
        dataClient.addListener(listener)
        refreshWatchConnected()
        // Seed the current value from any already-synced DataItem.
        dataClient.dataItems.addOnSuccessListener { buffer ->
            try {
                buffer.firstOrNull { it.uri.path == CenterStyle.PATH }?.let { readInto(it) }
            } finally {
                buffer.release()
            }
        }
    }

    fun stop() {
        dataClient.removeListener(listener)
    }

    fun refreshWatchConnected() {
        nodeClient.connectedNodes.addOnSuccessListener { nodes ->
            _watchConnected.value = nodes.isNotEmpty()
        }
    }

    /** Push a new center choice to the watch. */
    fun setCenterOption(id: String) {
        if (!CenterStyle.isValid(id)) return
        _centerOption.value = id
        val request = PutDataMapRequest.create(CenterStyle.PATH).apply {
            dataMap.putString(CenterStyle.KEY, id)
            // Force a distinct DataItem each time so repeated picks still sync.
            dataMap.putLong("timestamp", System.currentTimeMillis())
        }.asPutDataRequest().setUrgent()
        dataClient.putDataItem(request)
    }

    private fun readInto(item: com.google.android.gms.wearable.DataItem) {
        val id = DataMapItem.fromDataItem(item).dataMap.getString(CenterStyle.KEY)
        if (CenterStyle.isValid(id)) {
            _centerOption.value = id!!
        }
    }
}
